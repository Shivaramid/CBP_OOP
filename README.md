# Municipal Civic Complaint & Grievance Redressal System (MCC&GRS)

> **DEMO DATA DISCLAIMER**:
> ***DEMO DATA — NOT AN OFFICIAL CURRENT MUNICIPAL BOUNDARY DATABASE***
> All administrative boundaries, zones, circles, wards, localities, and personnel rosters provided in the default sample datasets are for educational, architectural demonstration, and academic evaluation purposes only.

---

## 1. Project Overview

The **Municipal Civic Complaint & Grievance Redressal System (MCC&GRS)** is a complete, production-grade Java Object-Oriented console application designed to handle the end-to-end lifecycle of civic grievances in a municipal corporation (inspired by Hyderabad / GHMC municipal structures). 

The system implements ticket routing, multi-tier jurisdictional authority, priority-driven Service Level Agreements (SLA), asynchronous background SLA breach detection using multi-threading, strict state machines, dynamic performance analytics, tamper-evident audit logging, and crash-resilient local file persistence — strictly without relying on external databases, web servers, or third-party libraries.

---

## 2. Problem Statement

Modern municipal administrations struggle with grievance redressal due to:
- **Lack of jurisdictional transparency**: Citizens lodge complaints without knowing which administrative ward or officer is responsible.
- **Lost accountability**: Complaints languish without clear deadlines or automatic escalation when field teams fail to respond.
- **Informal communication channels**: Unaudited verbal or paper-based complaints cannot be tracked, verified, or measured against SLAs.
- **Disconnected roles**: Citizens, triage officers, field teams, and city leaders lack a unified view of resolution metrics and satisfaction ratings.

MCC&GRS solves this by establishing a structured, auditable digital workflow enforcing strict role separation, automated SLA countdowns, and empirical performance analytics.

---

## 3. Enterprise ServiceNow & Jira Inspiration

MCC&GRS adapts proven enterprise IT Service Management (ITSM) and workflow orchestration patterns from **ServiceNow** and **Jira Service Management** to municipal governance:

| Enterprise ITSM Concept (ServiceNow / Jira) | Municipal Civic Grievance Equivalent (MCC&GRS) | Architectural Function |
| :--- | :--- | :--- |
| **Incident / Issue Ticket** | **Civic Complaint Ticket** (`Ticket`) | Core domain entity carrying state, coordinates, notes, SLA metrics. |
| **Service Desk / Triage Dispatcher** | **Grievance Redressal Officer (GRO)** | Circle-level gatekeeper validating legitimacy, setting priority, routing to teams. |
| **Assignment Group / Resolver Lead** | **Ward Officer / Supervisor** | Ward-level authority managing field assignments and monitoring workload. |
| **Field Technician / Agent** | **Field Worker / Ward Work Team** | Physical executor accepting tickets, logging work notes, and recording resolutions. |
| **Service Level Agreement (SLA Engine)** | **SLA Daemon** (`SLAMonitor`) | Multi-threaded monitor enforcing deadline countdowns based on severity. |
| **Ticket Escalation / SLA Breach Alert** | **Automated SLA Escalation** | Auto-transition to `ESCALATED`, alerting GRO and incrementing escalation metrics. |
| **Customer Satisfaction (CSAT)** | **Citizen Rating & Feedback** (`Feedback`) | Mandatory 1–5 star rating + review closing the ticket permanently. |
| **Ticket Reopen Workflow** | **Reopen Grievance** | Allows citizen to reject an incomplete fix, cycling ticket back into triage. |
| **Audit History / Activity Stream** | **Audit Trail** (`data/audit.log`) | Immutable append-only log capturing every transition, timestamp, and actor ID. |

---

## 4. Key Features

- **Strict Terminal Interface**: 100% interactive console UI using standard input/output with tabular formatting and validated prompts.
- **No Database Dependency**: High-reliability file-based object persistence with atomic temp-file commit semantics.
- **Real SHA-256 Authentication**: Local cryptographic password hashing (with salt) preventing plaintext storage.
- **Five Dedicated User Roles**: Distinct login portals, navigation flows, and domain actions for Citizen, GRO, Ward Officer, Field Worker, and Administrator.
- **Hierarchical Jurisdictional Engine**: 5-tier location hierarchy (`Zone` $\rightarrow$ `Circle` $\rightarrow$ `Ward` $\rightarrow$ `Area` $\rightarrow$ `Street`).
- **Enforced Status State Machine**: Prohibits illegal transitions (e.g. `CLOSED` $\rightarrow$ `IN_PROGRESS`) and validates preconditions.
- **Multi-Threaded Background SLA Daemon**: `ScheduledExecutorService` running in the background to detect breaches, auto-escalate tickets, and notify officers without stalling the console UI.
- **Dynamic Real-Time Analytics**: 100% computed metrics (Resolution Rate %, SLA Compliance %, Average Resolution Time, Rating Distribution) computed dynamically from stored tickets.
- **Tamper-Evident Audit Logging**: Human-readable and structured audit traces written to `data/audit.log`.

---

## 5. User Roles & Hierarchy

```text
Central Municipal Administration (Administrator)
                      ↓
           Zone (e.g. Malkajgiri)
                      ↓
            Circle (e.g. Keesara)
         [Headed by Circle GRO]
                      ↓
       Ward (e.g. Ward 1 - Keesara)
     [Managed by Ward Officer / Supervisor]
                      ↓
         Ward Work Teams / Field Workers
       (Sanitation, Electrical, Roads, etc.)
```

1. **Citizen (`CITIZEN`)**: Self-registers, lodges complaints by selecting exact locations, tracks tickets, reviews progress notes, provides 1–5 star ratings, and reopens unresolved complaints.
2. **Grievance Redressal Officer (`GRO`)**: Manages Circle-level jurisdiction. Triages incoming complaints, adjusts priority and SLA targets, assigns tickets to Ward Officers, rejects invalid/duplicate reports, and monitors overdue tickets.
3. **Ward Officer / Supervisor (`WARD_OFFICER`)**: Manages Ward-level operations. Distributes work to specialized Field Workers, balances workload, monitors progress notes, and inspects ward performance KPIs.
4. **Field Worker (`FIELD_WORKER`)**: Performs physical tasks. Accepts assigned tickets, marks work in-progress, logs field progress notes, places tickets on hold with justification, and submits detailed resolution reports.
5. **Administrator (`ADMINISTRATOR`)**: Provisions staff credentials, configures municipal territorial structures, assigns officers to circles/wards, inspects immutable audit logs, views system-wide statistics, and accesses the SLA test sandbox.

---

## 6. Location Hierarchy

The system models realistic territorial subdivision:

```text
Zone
 └── Circle
      └── Ward
           └── Area / Locality
                └── Street / Landmark
```

### Hyderabad Demo Data Set:
- **Zone**: `ZONE-01` — Malkajgiri
  - **Circle**: `CIR-01` — Keesara
    - **Ward 1**: `WARD-01` — Ward 1 - Keesara
      - *Area*: Keesara Main Area (*Streets*: Main Road, Temple Road, Gram Panchayat Lane)
      - *Area*: Yadgarpally (*Streets*: Station Road, ZPHS School Lane)
    - **Ward 2**: `WARD-02` — Ward 2 - Chandrapuri
      - *Area*: Chandrapuri Colony (*Streets*: Road No 1, Green Valley Lane)
      - *Area*: Sai Baba Nagar (*Streets*: Arch Gate Road, Community Hall Street)
    - **Ward 3**: `WARD-03` — Ward 3 - Jawahar Nagar
    - **Ward 4**: `WARD-04` — Ward 4 - Dammaiguda
  - **Circle**: `CIR-02` — Alwal
    - **Ward 5**: `WARD-05` — Ward 5 - Alwal Central
    - **Ward 6**: `WARD-06` — Ward 6 - Macha Bolarum

---

## 7. Ticket Lifecycle & Status State Machine

```
                   [ Citizen Submits ]
                            ↓
                        +-------+
            +---------->| OPEN  |-----------+
            |           +-------+           |
            |               |               |
   [Reopen] |               | [GRO Assigns] | [GRO Rejects]
            |               v               v
            |         +----------+    +----------+
            |         | ASSIGNED |    | REJECTED | (Terminal)
            |         +----------+    +----------+
            |               |
            |               | [Worker Accepts]
            |               v
            |         +----------+
            |         | ACCEPTED |
            |         +----------+
            |               |
            |               | [Start Work]
            |               v
            |        +-------------+  [Hold Reason]  +---------+
            |        | IN_PROGRESS |---------------->| ON_HOLD |
            |        +-------------+<----------------+---------+
            |               |           [Resume Work]
            |               |
            |               | [Worker Resolves]
            |               v
            |         +----------+
            +---------| RESOLVED |
                      +----------+
                            |
                            | [Citizen Rates 1-5 Stars]
                            v
                        +--------+
                        | CLOSED | (Terminal)
                        +--------+
```

### Exceptional Transitions:
- **Automatic SLA Escalation**: Any active ticket (`OPEN`, `ASSIGNED`, `ACCEPTED`, `IN_PROGRESS`, `ON_HOLD`, `REOPENED`) where `now > slaDeadline` is transitioned by the background monitor to `ESCALATED`.
- **Prohibited Transitions**: Illegal jumps (such as `CLOSED` $\rightarrow$ `IN_PROGRESS` or `REJECTED` $\rightarrow$ `RESOLVED`) throw an `InvalidStatusTransitionException` and are blocked.

---

## 8. Priority & SLA Engine

The system calculates initial SLA deadlines upon ticket creation and automatically adjusts deadlines if the GRO overrides priority:

| Priority Level | SLA Target Duration | Example Civic Incidents |
| :--- | :--- | :--- |
| **`CRITICAL`** | **12 Hours** | Drinking water pipeline burst, electrocution risk, collapsed road, fire hazard. |
| **`HIGH`** | **24 Hours** | Dangerous pothole on major road, sewage overflow, contaminated water supply. |
| **`MEDIUM`** | **48 Hours** | Garbage overflow, uncollected solid waste, public park maintenance. |
| **`LOW`** | **72 Hours** | Flickering street light, tree pruning, general civic inquiries. |

- **Formula**: `slaDeadline = createdAt + priority.getSlaDurationHours()`
- **Compliance Check**:
  - `isSlaBreached()` returns `true` if current time exceeds deadline while ticket is active.
  - `isNearSlaDeadline()` alerts officers when less than 4 hours remain.

---

## 9. Multi-Threading & SLA Daemon Architecture

Background SLA monitoring is executed via Java's `ScheduledExecutorService`:

```text
+------------------------+             +-------------------------------+
|     Console Thread     |             |  MCCGRS-SLA-Monitor-Thread   |
| (Interactive Scanner)  |             |  (ScheduledExecutorService)   |
+------------------------+             +-------------------------------+
            |                                          |
            | Save/Update Ticket                       | Periodic Sweep (Every 10s)
            v                                          v
+----------------------------------------------------------------------+
|                     Concurrent Ticket Repository                     |
|                   (Thread-Safe ConcurrentHashMap)                    |
|             synchronized file flush with atomic commit               |
+----------------------------------------------------------------------+
                                |
                                v
               Detects now > slaDeadline on Active Ticket
                                |
        +-----------------------+-----------------------+
        |                                               |
        v                                               v
Update Status to ESCALATED                    Alert Assigned GRO
Increment escalationCount                    Deliver Notification
Record Immutable Audit Entry                  (Console in-app alert)
```

- **Thread-Safety Design**:
  - In-memory entity collections use `ConcurrentHashMap` for high-concurrency non-blocking reads.
  - State mutating repository methods (`save`, `update`, `delete`, `flushToFile`) are protected by explicit object monitors (`synchronized (fileLock)`).
  - Atomic temporary files (`tickets.dat.tmp` $\rightarrow$ `tickets.dat`) ensure zero corruption even during abrupt termination.
- **Daemon Lifecycle**: Started upon application bootstrap; registered with `Runtime.getRuntime().addShutdownHook(...)` for graceful resource release upon shutdown.

---

## 10. File Persistence Architecture

All operational data is persisted to the local `data/` directory using standard Java object serialization and formatted text logging:

```text
data/
 ├── users.dat          (Binary serialized User hierarchy)
 ├── locations.dat      (Binary serialized Zones, Circles, Wards, Areas, Streets)
 ├── tickets.dat        (Binary serialized Tickets with history and SLA stamps)
 ├── feedback.dat       (Binary serialized Citizen satisfaction reviews)
 ├── notifications.dat  (Binary serialized In-app user alerts)
 ├── counters.dat       (Persistent sequence counters for monotonic IDs)
 └── audit.log          (Human-readable append-only immutable text journal)
```

- **Persistence Guarantees**:
  - The `data/` folder is auto-generated if missing.
  - Default demo data is auto-seeded only on cold first-time startup.
  - All modifications survive application restart.

---

## 11. Object-Oriented Programming (OOP) Demonstration

### A. Encapsulation
- All domain entity fields (`User`, `Ticket`, `Ward`, `Feedback`, `AuditEntry`) are declared `private` or `final`.
- State mutation is strictly restricted through domain methods that enforce business rules (e.g., `ticket.recordEscalation(...)`, `ticket.recordFeedback(...)`, `worker.incrementActiveTasks()`).

### B. Inheritance
- The abstract class `mccgrs.model.User` serves as the superclass for concrete roles:
  ```text
  User (abstract)
   ├── Citizen
   ├── GRO
   ├── WardOfficer
   ├── FieldWorker
   └── Administrator
  ```
- Subclasses inherit common identity fields (`userId`, `name`, `username`, `passwordHash`, `role`, `createdAt`) while adding role-specific attributes (`assignedCircleId`, `assignedWardId`, `departmentSpecialization`, `adminLevel`).

### C. Polymorphism
Real polymorphic dispatch is demonstrated through abstract method implementation:
```java
// Abstract declaration in User.java:
public abstract boolean canManageTicket(Ticket ticket);
public abstract String getDashboardTitle();
public abstract String getAssignmentSummary();
```
- In `GRO.java`: Checks if `ticket.getCircleId().equalsIgnoreCase(assignedCircleId)`.
- In `WardOfficer.java`: Checks if `ticket.getWardId().equalsIgnoreCase(assignedWardId)`.
- In `FieldWorker.java`: Checks if `ticket.getAssignedFieldWorkerId().equals(userId)`.
- In `Citizen.java`: Checks if `ticket.getCitizenId().equals(userId)`.
- In `Administrator.java`: Returns `true` for system-wide oversight.

When evaluating permissions, callers invoke `user.canManageTicket(ticket)` without knowing or casting the underlying concrete role.

### D. Abstraction
- Strategy interface `PriorityCalculator` separates priority business logic from caller services:
  ```java
  public interface PriorityCalculator {
      Priority calculatePriority(ComplaintCategory category, String description);
  }
  ```
  Implemented by `RuleBasedPriorityCalculator` to score severity keywords.

### E. Composition & Aggregation
- **Composition**: A `Ward` owns a collection of `Area` objects, and each `Area` owns `Street` objects.
- **Aggregation**: A `Circle` aggregates `Ward` entities, and a `Ward` holds references to assigned `FieldWorker` and `WardOfficer` accounts.

---

## 12. Custom Exception Hierarchy

The application features a structured, unchecked domain exception hierarchy rooted in `MunicipalException`:

```text
MunicipalException (RuntimeException)
 ├── AuthenticationException            (Credentials, deactivations, unauthorized access)
 ├── TicketNotFoundException            (Non-existent ticket queries)
 ├── UserNotFoundException              (Non-existent user queries)
 ├── InvalidStatusTransitionException   (Illegal lifecycle jumps)
 ├── InvalidLocationException           (Unknown zones, circles, or wards)
 ├── AssignmentException                (Cross-jurisdiction assignment violations)
 ├── InvalidFeedbackException           (Out-of-bounds ratings e.g. < 1 or > 5)
 ├── DuplicateEntityException           (Username collisions during registration)
 ├── ValidationException                (Missing or blank form fields)
 └── PersistenceException               (File I/O and serialization recovery)
```

No generic empty `catch (Exception e) {}` blocks exist in the codebase. Every exception displays helpful recovery instructions to the terminal user.

---

## 13. Project Structure

```text
MCC&GRS/
 ├── src/
 │    └── mccgrs/
 │         ├── enums/
 │         │    ├── UserRole.java
 │         │    ├── TicketStatus.java
 │         │    ├── Priority.java
 │         │    └── ComplaintCategory.java
 │         ├── exception/
 │         │    ├── MunicipalException.java
 │         │    ├── AuthenticationException.java
 │         │    ├── TicketNotFoundException.java
 │         │    ├── UserNotFoundException.java
 │         │    ├── InvalidStatusTransitionException.java
 │         │    ├── InvalidLocationException.java
 │         │    ├── AssignmentException.java
 │         │    ├── InvalidFeedbackException.java
 │         │    ├── DuplicateEntityException.java
 │         │    ├── ValidationException.java
 │         │    └── PersistenceException.java
 │         ├── model/
 │         │    ├── User.java (abstract)
 │         │    ├── Citizen.java
 │         │    ├── GRO.java
 │         │    ├── WardOfficer.java
 │         │    ├── FieldWorker.java
 │         │    ├── Administrator.java
 │         │    ├── Zone.java
 │         │    ├── Circle.java
 │         │    ├── Ward.java
 │         │    ├── Area.java
 │         │    ├── Street.java
 │         │    ├── Ticket.java
 │         │    ├── Feedback.java
 │         │    ├── Notification.java
 │         │    └── AuditEntry.java
 │         ├── repository/
 │         │    ├── UserRepository.java
 │         │    ├── LocationRepository.java
 │         │    ├── TicketRepository.java
 │         │    ├── FeedbackRepository.java
 │         │    ├── NotificationRepository.java
 │         │    └── AuditRepository.java
 │         ├── scheduler/
 │         │    └── SLAMonitor.java
 │         ├── service/
 │         │    ├── PriorityCalculator.java (interface)
 │         │    ├── RuleBasedPriorityCalculator.java
 │         │    ├── PriorityService.java
 │         │    ├── SLAService.java
 │         │    ├── AuditService.java
 │         │    ├── NotificationService.java
 │         │    ├── AuthenticationService.java
 │         │    ├── AssignmentService.java
 │         │    ├── TicketService.java
 │         │    ├── FeedbackService.java
 │         │    ├── AnalyticsService.java
 │         │    └── DataInitializerService.java
 │         ├── util/
 │         │    ├── PasswordUtil.java
 │         │    ├── DateTimeUtil.java
 │         │    ├── IDGenerator.java
 │         │    └── InputUtil.java
 │         ├── ui/
 │         │    ├── TicketDisplayUtil.java
 │         │    ├── CitizenMenu.java
 │         │    ├── GROMenu.java
 │         │    ├── WardOfficerMenu.java
 │         │    ├── FieldWorkerMenu.java
 │         │    ├── AdminMenu.java
 │         │    └── ConsoleUI.java
 │         └── Main.java
 ├── test/
 │    └── mccgrs/
 │         └── SystemTest.java
 ├── data/ (Auto-generated at runtime)
 ├── compile.bat
 ├── run.bat
 ├── test.bat
 └── README.md
```

---

## 14. Demo Credentials Reference

The system automatically initializes these demo accounts on first launch:

| Role | Username | Password | Full Name | Jurisdictional Assignment / Trade |
| :--- | :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` | Chief Municipal Administrator | System-Wide Central Governance |
| **GRO** | `gro_keesara` | `gro123` | Rajesh Sharma | Circle: Keesara (`CIR-01`) |
| **GRO** | `gro_alwal` | `gro123` | Sunita Reddy | Circle: Alwal (`CIR-02`) |
| **Ward Officer** | `wo_keesara1` | `wo123` | Vikram Varma | Ward: Ward 1 - Keesara (`WARD-01`) |
| **Ward Officer** | `wo_chandrapuri` | `wo123` | Ananya Rao | Ward: Ward 2 - Chandrapuri (`WARD-02`) |
| **Field Worker** | `fw_ramesh` | `fw123` | Ramesh Kumar | Ward 1 - Keesara \| Sanitation & Solid Waste |
| **Field Worker** | `fw_suresh` | `fw123` | Suresh Babu | Ward 1 - Keesara \| Electrical & Street Lighting |
| **Field Worker** | `fw_venkat` | `fw123` | Venkat Reddy | Ward 2 - Chandrapuri \| Roads & Civil Works |
| **Citizen** | `citizen_priya` | `citizen123` | Priya Patel | Resident of Keesara Main Area |
| **Citizen** | `citizen_rahul` | `citizen123` | Rahul Verma | Resident of Chandrapuri Colony |

---

## 15. Compilation & Execution Instructions

### Prerequisites
- JDK 17 or higher (tested on Java 21 / Java 26).
- Standard Windows Command Prompt (`cmd.exe`) or PowerShell.

### Step 1: Compile the Codebase
Execute `compile.bat`:
```cmd
compile.bat
```
*(Or manually via javac)*:
```cmd
if not exist bin mkdir bin
javac -d bin -cp bin src\mccgrs\enums\*.java src\mccgrs\exception\*.java src\mccgrs\util\*.java src\mccgrs\model\*.java src\mccgrs\repository\*.java src\mccgrs\service\*.java src\mccgrs\scheduler\*.java src\mccgrs\ui\*.java src\mccgrs\Main.java test\mccgrs\SystemTest.java
```

### Step 2: Run Automated Tests
Execute `test.bat`:
```cmd
test.bat
```
Output:
```text
================================================================
  STARTING MCC&GRS AUTOMATED COMPREHENSIVE SYSTEM VERIFICATION
================================================================
  [PASS] Admin login with valid password
  [PASS] Reject invalid password
  [PASS] Password hashed with SHA-256 (not plaintext)
  [PASS] Zone exists (Malkajgiri)
  [PASS] Circles exist under Malkajgiri
  [PASS] Ward 1 exists with areas and streets
  [PASS] Ticket ID matches CMP-YYYY-XXXX format
  [PASS] Step 1: Ticket created as OPEN
  [PASS] Step 2: Status becomes ASSIGNED
  [PASS] Step 3: Assigned to worker
  [PASS] Step 4: Status becomes ACCEPTED
  [PASS] Step 5: Status becomes IN_PROGRESS
  [PASS] Step 6: Status becomes RESOLVED
  [PASS] Step 7: Status becomes CLOSED after feedback
  [PASS] Prevent illegal transition from CLOSED to IN_PROGRESS
  [PASS] GRO of Circle Alwal blocked from modifying Keesara ticket
  [PASS] Reopen count is 1
  [PASS] Ward metrics total matches repository count exactly
  [PASS] SLA Monitor auto-escalated breached ticket to ESCALATED
  [PASS] Citizen persisted and retrieved after fresh reload
  [PASS] Ticket persisted and retrieved after fresh reload
================================================================
  TEST EXECUTION COMPLETED: 35 PASSED, 0 FAILED
================================================================
```

### Step 3: Launch the Console Application
Execute `run.bat`:
```cmd
run.bat
```
*(Or manually via java)*:
```cmd
java -cp bin mccgrs.Main
```

---

## 16. Step-by-Step Demo Workflows

### Scenario A: End-to-End Complaint Resolution
1. **Lodge Complaint**:
   - Run `run.bat` $\rightarrow$ Select `1. Citizen Login` $\rightarrow$ Enter `citizen_priya` / `citizen123`.
   - Choose `1. Submit New Civic Complaint` $\rightarrow$ Select Zone: `Malkajgiri` $\rightarrow$ Circle: `Keesara` $\rightarrow$ Ward: `Ward 1 - Keesara` $\rightarrow$ Area: `Keesara Main Area` $\rightarrow$ Street: `Main Road`.
   - Choose Category: `GARBAGE` $\rightarrow$ Description: `Garbage dump has not been cleared for 3 days`.
   - Note the generated Ticket ID (e.g., `CMP-2026-0006`).
   - Logout.
2. **GRO Triage & Assignment**:
   - Select `3. GRO Login` $\rightarrow$ Enter `gro_keesara` / `gro123`.
   - View `1. View Incoming & Active Circle Tickets`.
   - Select `7. Assign / Reassign Ticket to Ward Officer` $\rightarrow$ Enter your Ticket ID $\rightarrow$ Select `Vikram Varma`.
   - Logout.
3. **Ward Officer Work Delegation**:
   - Select `4. Ward Officer Login` $\rightarrow$ Enter `wo_keesara1` / `wo123`.
   - Select `3. Assign Field Worker to Ticket` $\rightarrow$ Enter your Ticket ID $\rightarrow$ Select worker `Ramesh Kumar` (Sanitation).
   - Logout.
4. **Field Worker Execution**:
   - Select `5. Field Worker Login` $\rightarrow$ Enter `fw_ramesh` / `fw123`.
   - Select `2. Accept Assigned Ticket` $\rightarrow$ Enter Ticket ID $\rightarrow$ Status becomes `ACCEPTED`.
   - Select `3. Start Physical Work` $\rightarrow$ Enter Ticket ID $\rightarrow$ Status becomes `IN_PROGRESS`.
   - Select `7. Mark Ticket RESOLVED` $\rightarrow$ Enter resolution: `Deploy compact truck and completely cleared waste container site.` $\rightarrow$ Status becomes `RESOLVED`.
   - Logout.
5. **Citizen Rating & Closure**:
   - Select `1. Citizen Login` $\rightarrow$ Enter `citizen_priya` / `citizen123`.
   - Select `5. Give Feedback & Close Resolved Ticket` $\rightarrow$ Enter Ticket ID $\rightarrow$ Give Rating: `5` $\rightarrow$ Comment: `Thank you for the quick cleanup!`.
   - Ticket is now `CLOSED` and satisfaction KPIs are dynamically updated.

---

### Scenario B: Live SLA Breach & Multi-Threaded Daemon Escalation
1. Log in as `admin` / `admin123` via `6. Administrator Login`.
2. Select option `15. SLA Demonstration Testing Tool`.
3. Pick any active ticket (e.g. `CMP-2026-0001`).
4. The tool fast-forwards its SLA deadline into the past and executes an immediate monitor sweep.
5. Observe the background thread update the status to `ESCALATED`, increment `escalationCount`, record an immutable audit entry in `data/audit.log`, and dispatch an urgent alert to the GRO.
6. Log in as `gro_keesara` / `gro123` $\rightarrow$ observe the `[SLA BREACH ALERT]` notification banner.

---

### Scenario C: Citizen Reopen Workflow
1. Log in as `citizen_priya` / `citizen123`.
2. Select `6. Reopen Unresolved Complaint`.
3. Select a ticket that was marked `RESOLVED` but was not adequately addressed in reality.
4. Provide justification: `The trash bin was emptied but construction debris remains on the pavement`.
5. Observe status return to `REOPENED` and re-enter the GRO triage queue for reassignment.

---

## 17. Design Decisions & Trade-Offs

1. **Local Object Serialization vs JSON/XML**:
   - Standard Java Object Serialization (`Serializable`, `ObjectInputStream`, `ObjectOutputStream`) was chosen because it requires zero external dependencies, handles complex object graphs with circular references (e.g., location cross-references), and performs fast binary serialization.
   - To prevent data corruption during power loss, writes are staged to atomic `.tmp` files before renaming.
2. **ScheduledExecutorService vs Thread.sleep**:
   - Multi-threaded periodic execution uses `ScheduledExecutorService` with a daemon thread rather than ad-hoc `Thread.sleep` loops. This ensures scheduled tasks run at fixed rates without accumulating drift and shutdown cleanly alongside the JVM.
3. **Password Security**:
   - Passwords use SHA-256 with application-level salting (`PasswordUtil`), demonstrating defensive security design without requiring external libraries like BCrypt or Spring Security.

---

## 18. Limitations & Future Roadmap

- **Clustered Nodes**: Currently designed for single-node local execution; future iterations could introduce distributed messaging (e.g., Kafka) for multi-city deployment.
- **Geospatial Mapping**: Coordinates are currently hierarchical string tuples; future enhancements can introduce GIS polygon geofencing (GeoJSON).
- **Mobile Field App**: Can be extended with an offline-first mobile sync layer for municipal field workers.

---

*Developed as a complete educational, architectural reference implementation for Municipal Civic Grievance Redressal and Citizen Service Delivery.*
