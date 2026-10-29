package mccgrs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents an administrative Circle under a Zone.
 * Each Circle is headed by a Grievance Redressal Officer (GRO) and contains multiple Wards.
 */
public class Circle implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String circleId;
    private final String name;
    private final String zoneId;
    private String assignedGroId;
    private final List<String> wardIds;

    public Circle(String circleId, String name, String zoneId) {
        this.circleId = Objects.requireNonNull(circleId, "circleId cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.zoneId = Objects.requireNonNull(zoneId, "zoneId cannot be null");
        this.wardIds = new ArrayList<>();
    }

    public String getCircleId() {
        return circleId;
    }

    public String getName() {
        return name;
    }

    public String getZoneId() {
        return zoneId;
    }

    public String getAssignedGroId() {
        return assignedGroId;
    }

    public void setAssignedGroId(String assignedGroId) {
        this.assignedGroId = assignedGroId;
    }

    public List<String> getWardIds() {
        return Collections.unmodifiableList(wardIds);
    }

    public void addWardId(String wardId) {
        if (wardId != null && !wardIds.contains(wardId)) {
            wardIds.add(wardId);
        }
    }

    public void removeWardId(String wardId) {
        wardIds.remove(wardId);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Circle)) return false;
        Circle circle = (Circle) o;
        return circleId.equals(circle.circleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(circleId);
    }
}
