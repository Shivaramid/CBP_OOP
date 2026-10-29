package mccgrs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a residential locality or commercial area within a municipal Ward.
 */
public class Area implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String areaId;
    private final String name;
    private final String wardId;
    private final List<Street> streets;

    public Area(String areaId, String name, String wardId) {
        this.areaId = Objects.requireNonNull(areaId, "areaId cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.wardId = Objects.requireNonNull(wardId, "wardId cannot be null");
        this.streets = new ArrayList<>();
    }

    public String getAreaId() {
        return areaId;
    }

    public String getName() {
        return name;
    }

    public String getWardId() {
        return wardId;
    }

    public List<Street> getStreets() {
        return Collections.unmodifiableList(streets);
    }

    public void addStreet(Street street) {
        if (street != null && !streets.contains(street)) {
            streets.add(street);
        }
    }

    public Street findStreetByName(String streetName) {
        for (Street s : streets) {
            if (s.getName().equalsIgnoreCase(streetName)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Area)) return false;
        Area area = (Area) o;
        return areaId.equals(area.areaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(areaId);
    }
}
