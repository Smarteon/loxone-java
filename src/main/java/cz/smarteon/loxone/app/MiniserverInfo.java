package cz.smarteon.loxone.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the miniserver generic info - contains static information on the Miniserver and it’s configuration.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MiniserverInfo {

    private final String serialNumber;
    private final String name;
    private final String projectName;
    private final String localUrl;
    private final String remoteUrl;
    private final TemperatureUnit temperatureUnit;
    private final String currency;
    private final String squareMeasure;
    private final String location;
    private final String categoryTitle;
    private final String roomTitle;
    private final MiniserverType type;
    private final boolean sortByRating;
    private final MiniserverUser currentUser;

    @JsonCreator
    @SuppressWarnings("checkstyle:parameternumber")
    public MiniserverInfo(@JsonProperty("serialNr") String serialNumber,
                          @JsonProperty("msName") String name,
                          @JsonProperty("projectName") String projectName,
                          @JsonProperty("localUrl") String localUrl,
                          @JsonProperty("remoteUrl") String remoteUrl,
                          @JsonProperty("tempUnit") TemperatureUnit temperatureUnit,
                          @JsonProperty("currency") String currency,
                          @JsonProperty("squareMeasure") String squareMeasure,
                          @JsonProperty("location") String location,
                          @JsonProperty("catTitle") String categoryTitle,
                          @JsonProperty("roomTitle") String roomTitle,
                          @JsonProperty("miniserverType") MiniserverType type,
                          @JsonProperty("sortByRating") boolean sortByRating,
                          @JsonProperty("currentUser") MiniserverUser currentUser) {
        this.serialNumber = serialNumber;
        this.name = name;
        this.projectName = projectName;
        this.localUrl = localUrl;
        this.remoteUrl = remoteUrl;
        this.temperatureUnit = temperatureUnit;
        this.currency = currency;
        this.squareMeasure = squareMeasure;
        this.location = location;
        this.categoryTitle = categoryTitle;
        this.roomTitle = roomTitle;
        this.type = type;
        this.sortByRating = sortByRating;
        this.currentUser = currentUser;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getName() {
        return name;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public TemperatureUnit getTemperatureUnit() {
        return temperatureUnit;
    }

    public String getCurrency() {
        return currency;
    }

    public String getSquareMeasure() {
        return squareMeasure;
    }

    public String getLocation() {
        return location;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public MiniserverType getType() {
        return type;
    }

    public boolean shouldSortByRating() {
        return sortByRating;
    }

    public MiniserverUser getCurrentUser() {
        return currentUser;
    }
}
