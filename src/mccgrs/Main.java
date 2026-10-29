package mccgrs;

import mccgrs.repository.*;
import mccgrs.scheduler.SLAMonitor;
import mccgrs.service.*;
import mccgrs.ui.ConsoleUI;

/**
 * Bootstrap entry point for the Municipal Civic Complaint & Grievance Redressal System.
 * Coordinates initialization of repositories, services, multi-threaded SLA daemon, and the terminal UI.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("[INFO] Bootstrapping Municipal Civic Complaint & Grievance Redressal System...");

        // 1. Initialize Repositories (Auto-creates data/ directory and loads stored data)
        UserRepository userRepository = new UserRepository();
        LocationRepository locationRepository = new LocationRepository();
        TicketRepository ticketRepository = new TicketRepository();
        FeedbackRepository feedbackRepository = new FeedbackRepository();
        NotificationRepository notificationRepository = new NotificationRepository();
        AuditRepository auditRepository = new AuditRepository();

        // 2. Initialize Services
        AuditService auditService = new AuditService(auditRepository);
        NotificationService notificationService = new NotificationService(notificationRepository);
        AuthenticationService authService = new AuthenticationService(userRepository, auditService);
        PriorityService priorityService = new PriorityService();
        SLAService slaService = new SLAService();
        AssignmentService assignmentService = new AssignmentService(
                userRepository, locationRepository, ticketRepository, notificationService, auditService);
        TicketService ticketService = new TicketService(
                ticketRepository, locationRepository, userRepository,
                priorityService, slaService, auditService, notificationService);
        FeedbackService feedbackService = new FeedbackService(feedbackRepository, ticketService, auditService);
        AnalyticsService analyticsService = new AnalyticsService(ticketRepository, locationRepository);

        // 3. Populate Demo Data on First Run
        DataInitializerService dataInitializer = new DataInitializerService(
                userRepository, locationRepository, ticketRepository, feedbackRepository, auditService);
        dataInitializer.initializeIfEmpty();

        // 4. Start SLA Background Monitoring Daemon (Interval: 10 seconds for live demo responsiveness)
        SLAMonitor slaMonitor = new SLAMonitor(ticketRepository, notificationService, auditService, 10);
        slaMonitor.start();
        System.out.println("[INFO] SLA Background Monitor active (Sweep interval: 10s).");

        // 5. Register Clean JVM Shutdown Hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[INFO] Clean shutdown initiated. Halting SLA background monitor...");
            slaMonitor.stop();
            System.out.println("[INFO] System shutdown complete.");
        }, "MCCGRS-ShutdownHook"));

        // 6. Launch Console UI
        ConsoleUI consoleUI = new ConsoleUI(
                userRepository, locationRepository, ticketRepository, feedbackRepository,
                authService, ticketService, assignmentService, priorityService,
                slaService, analyticsService, feedbackService, notificationService,
                auditService, slaMonitor);

        consoleUI.start();

        // 7. Explicit Shutdown upon normal loop exit
        slaMonitor.stop();
        System.out.println("[INFO] Application exited cleanly.");
    }
}
