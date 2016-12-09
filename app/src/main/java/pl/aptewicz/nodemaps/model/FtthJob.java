package pl.aptewicz.nodemaps.model;


public class FtthJob {

    private final Long id;
    private final String description;
    private final double latitude;
    private final double longitude;

    public FtthJob(Long id, String description, double latitude, double longitude) {
        this.id = id;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
