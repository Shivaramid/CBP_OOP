package mccgrs;

import mccgrs.enums.ComplaintCategory;
import mccgrs.enums.Priority;
import mccgrs.enums.TicketStatus;
import mccgrs.enums.UserRole;
import mccgrs.exception.AssignmentException;
import mccgrs.exception.AuthenticationException;
import mccgrs.exception.InvalidFeedbackException;
import mccgrs.exception.InvalidStatusTransitionException;
import mccgrs.model.*;
import mccgrs.repository.*;
import mccgrs.scheduler.SLAMonitor;
import mccgrs.service.*;
import mccgrs.util.PasswordUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Automated end-to-end integration and unit verification suite.
 * Validates domain integrity, workflows, multi-threaded SLA daemon, and disk persistence.
 */
public class SystemTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("  STARTING MCC&GRS AUTOMATED COMPREHENSIVE SYSTEM VERIFICATION");
        System.out.println("================================================================");

        try {
            // Setup clean repository and service instances
            UserRepository userRepo = new UserRepository();
            LocationRepository locRepo = new LocationRepository();
            TicketRepository ticketRepo = new TicketRepository();
            FeedbackRepository feedbackRepo = new FeedbackRepository();
            NotificationRepository notifRepo = new NotificationRepository();
            AuditRepository auditRepo = new AuditRepository();

            AuditService auditService = new AuditService(auditRepo);
            NotificationService notifService = new NotificationService(notifRepo);
            AuthenticationService authService = new AuthenticationService(userRepo, auditService);
            PriorityService priorityService = new PriorityService();
            SLAService slaService = new SLAService();
            AssignmentService assignService = new AssignmentService(userRepo, locRepo, ticketRepo, notifService, auditService);
            TicketService ticketService = new TicketService(ticketRepo, locRepo, userRepo, priorityService, slaService, auditService, notifService);
            FeedbackService feedbackService = new FeedbackService(feedbackRepo, ticketService, auditService);
            AnalyticsService analyticsService = new AnalyticsService(ticketRepo, locRepo);

            DataInitializerService initializer = new DataInitializerService(userRepo, locRepo, ticketRepo, feedbackRepo, auditService);
            initializer.initializeIfEmpty();

            // Run Tests
            testAuthenticationAndHashing(authService, userRepo);
            testLocationHierarchy(locRepo);
            testTicketCreationAndIdFormat(ticketService, userRepo);
            testEndToEndTicketLifecycle(ticketService, assignService, feedbackService, userRepo);
            testProhibitedStatusTransition(ticketService, userRepo);
            testCircleAndWardAuthorization(ticketService, assignService, userRepo);
            testReopeningWorkflow(ticketService, userRepo);
            testFeedbackValidation(feedbackService, ticketService, userRepo);
            testRealAnalyticsCalculation(analyticsService, ticketRepo);
            testSlaBreachAndEscalation(ticketService, ticketRepo, notifService, auditService);
            testPersistenceAcrossRestart();

            System.out.println("================================================================");
            System.out.println(String.format("  TEST EXECUTION COMPLETED: %d PASSED, %d FAILED", testsPassed, testsFailed));
            System.out.println("================================================================");

            if (testsFailed > 0) {
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("[FATAL] Test harness failed with unhandled exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition, String failureMessage) {
        if (condition) {
            System.out.println("  [PASS] " + testName);
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + testName + " -> " + failureMessage);
            testsFailed++;
        }
    }

    private static void testAuthenticationAndHashing(AuthenticationService authService, UserRepository userRepo) {
        System.out.println("\n--- 1. Testing Authentication & Cryptographic Hashing ---");

        // Test valid login
        try {
            User admin = authService.login("admin", "admin123");
            assertTrue("Admin login with valid password", admin != null && admin.getRole() == UserRole.ADMINISTRATOR, "Admin failed login");
        } catch (Exception e) {
            assertTrue("Admin login with valid password", false, e.getMessage());
        }

        // Test invalid password
        boolean caughtInvalidPassword = false;
        try {
            authService.login("admin", "wrong_password");
        } catch (AuthenticationException e) {
            caughtInvalidPassword = true;
        }
        assertTrue("Reject invalid password", caughtInvalidPassword, "Failed to reject invalid password");

        // Verify SHA-256 hash not stored as plaintext
        User adminUser = userRepo.findByUsername("admin").orElseThrow();
        assertTrue("Password hashed with SHA-256 (not plaintext)",
                !adminUser.getPasswordHash().equals("admin123") && adminUser.getPasswordHash().length() == 64,
                "Password appears to be plaintext or incorrect hash length");
    }

    private static void testLocationHierarchy(LocationRepository locRepo) {
        System.out.println("\n--- 2. Testing Location Hierarchy (Zone -> Circle -> Ward -> Area -> Street) ---");
        Zone zone = locRepo.findZoneById("ZONE-01").orElse(null);
        assertTrue("Zone exists (Malkajgiri)", zone != null && zone.getName().equalsIgnoreCase("Malkajgiri"), "Zone missing");

        List<Circle> circles = locRepo.findCirclesByZone("ZONE-01");
        assertTrue("Circles exist under Malkajgiri", circles.size() >= 2, "Insufficient circles found");

        Ward ward1 = locRepo.findWardById("WARD-01").orElse(null);
        assertTrue("Ward 1 exists with areas and streets",
                ward1 != null && !ward1.getAreas().isEmpty() && !ward1.getAreas().get(0).getStreets().isEmpty(),
                "Ward hierarchy incomplete");
    }

    private static void testTicketCreationAndIdFormat(TicketService ticketService, UserRepository userRepo) {
        System.out.println("\n--- 3. Testing Ticket Creation & ID Formatting ---");
        Citizen cit = (Citizen) userRepo.findByUsername("citizen_priya").orElseThrow();

        Ticket t = ticketService.createComplaint(
                cit, ComplaintCategory.GARBAGE, "Test garbage overflow on road",
                "ZONE-01", "CIR-01", "WARD-01", "Keesara Main Area", "Main Road");

        assertTrue("Ticket ID matches CMP-YYYY-XXXX format",
                t.getTicketId().matches("^CMP-\\d{4}-\\d{4}$"),
                "Invalid Ticket ID format: " + t.getTicketId());
        assertTrue("Initial status is OPEN", t.getStatus() == TicketStatus.OPEN, "Status is not OPEN");
        assertTrue("SLA deadline calculated based on priority",
                t.getSlaDeadline() != null && t.getSlaDeadline().isAfter(t.getCreatedAt()),
                "SLA deadline invalid");
    }

    private static void testEndToEndTicketLifecycle(TicketService ticketService, AssignmentService assignService,
                                                   FeedbackService feedbackService, UserRepository userRepo) {
        System.out.println("\n--- 4. Testing End-to-End Workflow: OPEN -> ASSIGNED -> ACCEPTED -> IN_PROGRESS -> RESOLVED -> CLOSED ---");
        Citizen cit = (Citizen) userRepo.findByUsername("citizen_priya").orElseThrow();
        GRO gro = (GRO) userRepo.findByUsername("gro_keesara").orElseThrow();
        WardOfficer wo = (WardOfficer) userRepo.findByUsername("wo_keesara1").orElseThrow();
        FieldWorker fw = (FieldWorker) userRepo.findByUsername("fw_ramesh").orElseThrow();

        // 1. Citizen creates ticket
        Ticket t = ticketService.createComplaint(
                cit, ComplaintCategory.ROAD, "Severe pothole requiring immediate asphalt patch",
                "ZONE-01", "CIR-01", "WARD-01", "Keesara Main Area", "Temple Road");
        assertTrue("Step 1: Ticket created as OPEN", t.getStatus() == TicketStatus.OPEN, "Not OPEN");

        // 2. GRO assigns Ward Officer -> ASSIGNED
        assignService.assignTicketToWardOfficer(t, wo, gro);
        assertTrue("Step 2: Status becomes ASSIGNED", t.getStatus() == TicketStatus.ASSIGNED, "Not ASSIGNED");

        // 3. Ward Officer assigns Field Worker
        assignService.assignTicketToFieldWorker(t, fw, wo);
        assertTrue("Step 3: Assigned to worker", fw.getUserId().equals(t.getAssignedFieldWorkerId()), "Worker assignment failed");

        // 4. Field worker accepts -> ACCEPTED
        ticketService.acceptTicket(t.getTicketId(), fw);
        assertTrue("Step 4: Status becomes ACCEPTED", t.getStatus() == TicketStatus.ACCEPTED, "Not ACCEPTED");

        // 5. Field worker starts work -> IN_PROGRESS
        ticketService.startWork(t.getTicketId(), fw);
        assertTrue("Step 5: Status becomes IN_PROGRESS", t.getStatus() == TicketStatus.IN_PROGRESS, "Not IN_PROGRESS");

        // 6. Field worker resolves -> RESOLVED
        ticketService.resolveTicket(t.getTicketId(), "Filled pothole with 40kg bitumen cold mix", fw);
        assertTrue("Step 6: Status becomes RESOLVED", t.getStatus() == TicketStatus.RESOLVED, "Not RESOLVED");

        // 7. Citizen provides feedback -> CLOSED
        feedbackService.submitFeedback(t.getTicketId(), cit, 5, "Excellent road repair!");
        assertTrue("Step 7: Status becomes CLOSED after feedback", t.getStatus() == TicketStatus.CLOSED, "Not CLOSED");
        assertTrue("Feedback rating recorded", t.getFeedbackRating() == 5, "Rating not 5");
    }

    private static void testProhibitedStatusTransition(TicketService ticketService, UserRepository userRepo) {
        System.out.println("\n--- 5. Testing Prohibited Transition Enforcement (e.g. CLOSED -> IN_PROGRESS) ---");
        FieldWorker fw = (FieldWorker) userRepo.findByUsername("fw_ramesh").orElseThrow();

        // Find any closed ticket
        List<Ticket> closed = ticketService.search(null, null, null, null, TicketStatus.CLOSED, null);
        assertTrue("Closed ticket exists for transition test", !closed.isEmpty(), "No closed ticket");

        Ticket t = closed.get(0);
        boolean caughtInvalidTransition = false;
        try {
            ticketService.startWork(t.getTicketId(), fw);
        } catch (InvalidStatusTransitionException e) {
            caughtInvalidTransition = true;
        }
        assertTrue("Prevent illegal transition from CLOSED to IN_PROGRESS", caughtInvalidTransition,
                "System allowed illegal transition from CLOSED to IN_PROGRESS!");
    }

    private static void testCircleAndWardAuthorization(TicketService ticketService, AssignmentService assignService,
                                                       UserRepository userRepo) {
        System.out.println("\n--- 6. Testing Circle and Ward Jurisdictional Boundaries ---");
        GRO groAlwal = (GRO) userRepo.findByUsername("gro_alwal").orElseThrow();
        WardOfficer woKeesara = (WardOfficer) userRepo.findByUsername("wo_keesara1").orElseThrow();

        // Ticket in Keesara Circle
        List<Ticket> keesaraTickets = ticketService.search(null, null, "WARD-01", null, null, null);
        assertTrue("Keesara tickets exist", !keesaraTickets.isEmpty(), "No tickets in Ward 1");

        Ticket keesaraTicket = keesaraTickets.get(0);

        // GRO of Alwal attempting to assign/manage Keesara ticket must be blocked
        boolean blockedCrossCircleGro = false;
        try {
            assignService.assignTicketToWardOfficer(keesaraTicket, woKeesara, groAlwal);
        } catch (AssignmentException e) {
            blockedCrossCircleGro = true;
        }
        assertTrue("GRO of Circle Alwal blocked from modifying Keesara ticket", blockedCrossCircleGro,
                "Cross-circle GRO breach allowed!");
    }

    private static void testReopeningWorkflow(TicketService ticketService, UserRepository userRepo) {
        System.out.println("\n--- 7. Testing Reopen Workflow: RESOLVED -> REOPENED ---");
        Citizen cit = (Citizen) userRepo.findByUsername("citizen_priya").orElseThrow();
        FieldWorker fw = (FieldWorker) userRepo.findByUsername("fw_ramesh").orElseThrow();

        // Create and resolve a ticket
        Ticket t = ticketService.createComplaint(cit, ComplaintCategory.SANITATION, "Debris on sidewalk",
                "ZONE-01", "CIR-01", "WARD-01", "Keesara Main Area", "Main Road");
        t.setAssignedFieldWorkerId(fw.getUserId());
        t.setStatus(TicketStatus.IN_PROGRESS);
        ticketService.resolveTicket(t.getTicketId(), "Sidewalk swept", fw);
        assertTrue("Ticket is RESOLVED", t.getStatus() == TicketStatus.RESOLVED, "Not RESOLVED");

        // Citizen reopens
        ticketService.reopenTicket(t.getTicketId(), "Only half the debris was cleared", cit);
        assertTrue("Status transitioned to REOPENED", t.getStatus() == TicketStatus.REOPENED, "Not REOPENED");
        assertTrue("Reopen count is 1", t.getReopenCount() == 1, "Reopen count mismatch");
    }

    private static void testFeedbackValidation(FeedbackService feedbackService, TicketService ticketService, UserRepository userRepo) {
        System.out.println("\n--- 8. Testing Feedback Rating Bounds (1-5) ---");
        Citizen cit = (Citizen) userRepo.findByUsername("citizen_priya").orElseThrow();

        boolean caughtInvalidRatingHigh = false;
        try {
            feedbackService.submitFeedback("CMP-NON-EXISTENT", cit, 6, "Invalid 6 stars");
        } catch (InvalidFeedbackException e) {
            caughtInvalidRatingHigh = true;
        }
        assertTrue("Reject rating > 5", caughtInvalidRatingHigh, "Accepted rating 6");

        boolean caughtInvalidRatingLow = false;
        try {
            feedbackService.submitFeedback("CMP-NON-EXISTENT", cit, 0, "Invalid 0 stars");
        } catch (InvalidFeedbackException e) {
            caughtInvalidRatingLow = true;
        }
        assertTrue("Reject rating < 1", caughtInvalidRatingLow, "Accepted rating 0");
    }

    private static void testRealAnalyticsCalculation(AnalyticsService analyticsService, TicketRepository ticketRepo) {
        System.out.println("\n--- 9. Testing Dynamic Analytics Calculations (No Hardcoding) ---");
        AnalyticsService.WardMetrics wm = analyticsService.calculateWardMetrics("WARD-01");
        int countInRepo = ticketRepo.findByWardId("WARD-01").size();

        assertTrue("Ward metrics total matches repository count exactly",
                wm.total == countInRepo,
                String.format("Calculated %d != Repo %d", wm.total, countInRepo));
        assertTrue("Resolution rate is mathematically bounded between 0% and 100%",
                wm.resolutionRate >= 0.0 && wm.resolutionRate <= 100.0,
                "Invalid resolution rate: " + wm.resolutionRate);
        assertTrue("SLA compliance rate is mathematically bounded between 0% and 100%",
                wm.slaComplianceRate >= 0.0 && wm.slaComplianceRate <= 100.0,
                "Invalid compliance rate: " + wm.slaComplianceRate);
    }

    private static void testSlaBreachAndEscalation(TicketService ticketService, TicketRepository ticketRepo,
                                                  NotificationService notifService, AuditService auditService) {
        System.out.println("\n--- 10. Testing SLA Monitoring Daemon & Auto-Escalation ---");
        SLAMonitor monitor = new SLAMonitor(ticketRepo, notifService, auditService, 1);

        // Find or create an active ticket
        List<Ticket> active = ticketRepo.findActiveTickets();
        assertTrue("Active tickets exist for SLA sweep test", !active.isEmpty(), "No active tickets");
        Ticket testTicket = active.get(0);

        // Fast-forward its SLA deadline to 2 hours ago
        monitor.simulateBreachForTesting(testTicket);
        assertTrue("Ticket deadline fast-forwarded into the past",
                testTicket.isSlaBreached(), "Ticket is not breached after fast-forward");

        // Execute monitor sweep
        monitor.checkTicketsForSlaBreach();

        Ticket afterSweep = ticketRepo.findById(testTicket.getTicketId()).orElseThrow();
        assertTrue("SLA Monitor auto-escalated breached ticket to ESCALATED",
                afterSweep.getStatus() == TicketStatus.ESCALATED,
                "Ticket status after sweep was: " + afterSweep.getStatus());
        assertTrue("Escalation count was incremented",
                afterSweep.getEscalationCount() >= 1, "Escalation count was not incremented");
    }

    private static void testPersistenceAcrossRestart() {
        System.out.println("\n--- 11. Testing File Persistence Across Simulated JVM Restart ---");

        // Phase 1: Write an explicit marker user and ticket in session 1
        String uniqueUsername = "persist_test_user_" + System.currentTimeMillis();
        String ticketDescription = "Unique persistence verification test complaint at " + LocalDateTime.now();

        UserRepository repo1 = new UserRepository();
        AuditService audit1 = new AuditService(new AuditRepository());
        AuthenticationService auth1 = new AuthenticationService(repo1, audit1);
        TicketRepository ticketRepo1 = new TicketRepository();
        LocationRepository locRepo1 = new LocationRepository();
        TicketService ticketService1 = new TicketService(
                ticketRepo1, locRepo1, repo1, new PriorityService(), new SLAService(),
                audit1, new NotificationService(new NotificationRepository()));

        Citizen createdCitizen = auth1.registerCitizen(
                "Persistence Citizen", uniqueUsername, "pass123", "persist@test.com", "9998887776", "Test Street");

        Ticket createdTicket = ticketService1.createComplaint(
                createdCitizen, ComplaintCategory.WATER, ticketDescription,
                "ZONE-01", "CIR-01", "WARD-01", "Keesara Main Area", "Main Road");

        String savedTicketId = createdTicket.getTicketId();

        // Phase 2: Simulate JVM Shutdown and cold startup with fresh objects
        UserRepository repo2 = new UserRepository();
        TicketRepository ticketRepo2 = new TicketRepository();

        User reloadedUser = repo2.findByUsername(uniqueUsername).orElse(null);
        assertTrue("Citizen persisted and retrieved after fresh reload",
                reloadedUser != null && reloadedUser.getUsername().equals(uniqueUsername),
                "Persisted citizen not found after reload");

        Ticket reloadedTicket = ticketRepo2.findById(savedTicketId).orElse(null);
        assertTrue("Ticket persisted and retrieved after fresh reload",
                reloadedTicket != null && reloadedTicket.getDescription().equals(ticketDescription),
                "Persisted ticket not found after reload");

        // Clean up test citizen and ticket
        repo2.delete(createdCitizen.getUserId());
        ticketRepo2.delete(savedTicketId);
    }
}
