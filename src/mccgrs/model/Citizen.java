package mccgrs.model;

import mccgrs.enums.UserRole;

/**
 * Concrete subclass representing a civic resident / complainant.
 */
public class Citizen extends User {
    private static final long serialVersionUID = 1L;

    private String email;
    private String phone;
    private String address;

    public Citizen(String userId, String name, String username, String passwordHash,
                   String email, String phone, String address) {
        super(userId, name, username, passwordHash, UserRole.CITIZEN);
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getDashboardTitle() {
        return "CITIZEN CIVIC GRIEVANCE & SELF-SERVICE PORTAL";
    }

    @Override
    public String getAssignmentSummary() {
        return String.format("Resident [Phone: %s, Address: %s]", phone, address);
    }

    @Override
    public boolean canManageTicket(Ticket ticket) {
        return ticket != null && getUserId().equals(ticket.getCitizenId());
    }
}
