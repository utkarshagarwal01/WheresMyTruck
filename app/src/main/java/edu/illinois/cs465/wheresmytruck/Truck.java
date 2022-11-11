package edu.illinois.cs465.wheresmytruck;

import com.google.android.gms.maps.model.LatLng;
import java.util.Date;

public class Truck {

    private int truckId;
    private String truckName;
    private LatLng coordinates;
    private int positiveVotes;
    private int negativeVotes;
    private Date lastSeen;
    private String headerImage;
    private int reportedUserId;

    public int getTruckId() {
        return truckId;
    }

    public void setTruckId(int truckId) {
        this.truckId = truckId;
    }

    public String getTruckName() {
        return truckName;
    }

    public void setTruckName(String truckName) {
        this.truckName = truckName;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public int getPositiveVotes() {
        return positiveVotes;
    }

    public void setPositiveVotes(int positiveVotes) {
        this.positiveVotes = positiveVotes;
    }

    public int getNegativeVotes() {
        return negativeVotes;
    }

    public void setNegativeVotes(int negativeVotes) {
        this.negativeVotes = negativeVotes;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public int getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(int reportedUserId) {
        this.reportedUserId = reportedUserId;
    }
}
