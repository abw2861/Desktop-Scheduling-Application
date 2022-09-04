package model;

import java.sql.Timestamp;

/** This is the Appointment Class. */
public class Appointment {
    private int appointmentId;
    private String appTitle;
    private String appDescription;
    private String appLocation;
    private String appType;
    private Timestamp startTime;
    private Timestamp endTime;
    private int customerId;
    private User user;
    private Contact contact;

/** This is the Appointment class. */
    public Appointment(int appointmentId, String appTitle, String appDescription, String appLocation, String appType, Timestamp startTime, Timestamp endTime, int customerId, User user, Contact contact) {
        this.appointmentId = appointmentId;
        this.appTitle = appTitle;
        this.appDescription = appDescription;
        this.appLocation = appLocation;
        this.appType = appType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customerId = customerId;
        this.user = user;
        this.contact = contact;
    }

    /** @return The appointment ID */
    public int getAppointmentId() {
        return appointmentId;
    }

    /** @param appointmentId The appointment ID to set */
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    /** @return The appointment title */
    public String getAppTitle() {
        return appTitle;
    }

    /** @param appTitle The appointment title to set */
    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    /** @return The appointment description */
    public String getAppDescription() {
        return appDescription;
    }

    /** @param appDescription The appointment description to set */
    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    /** @return The appointment location */
    public String getAppLocation() {
        return appLocation;
    }

    /** @param appLocation The appointment location to set */
    public void setAppLocation(String appLocation) {
        this.appLocation = appLocation;
    }

    /** @return The appointment type */
    public String getAppType() {
        return appType;
    }

    /** @param appType The appointment type to set */
    public void setAppType(String appType) {
        this.appType = appType;
    }

    /** @return The appointment start time */
    public Timestamp getStartTime() {
        return startTime;
    }

    /** @param startTime The appointment start time to set */
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    /** @return The appointment end time */
    public Timestamp getEndTime() {
        return endTime;
    }

    /** @param endTime The appointment end time to set */
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    /** @return The customer ID */
    public int getCustomerId() {
        return customerId;
    }

    /** @param customerId The customer ID to set */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /** @return The user */
    public User getUser() {
        return user;
    }

    /** @param user The user to set */
    public void setUser(User user) {
        this.user = user;
    }

    /** @return The contact */
    public Contact getContact() {
        return contact;
    }

    /** @param contact The contact to set */
    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
