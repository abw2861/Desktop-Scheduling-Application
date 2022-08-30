package model;

import javafx.util.converter.DateTimeStringConverter;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    public String getAppLocation() {
        return appLocation;
    }

    public void setAppLocation(String appLocation) {
        this.appLocation = appLocation;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(int userId) {
        this.user = user;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
