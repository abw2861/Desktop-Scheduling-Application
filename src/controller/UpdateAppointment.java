package controller;

import Utility.Alerts;
import Utility.Query;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;
import model.Contact;
import model.Customer;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/** This is the Update Appointment controller class. */
public class UpdateAppointment implements Initializable {
    public TextField titleField;
    public TextField descriptionField;
    public TextField locationField;
    public TextField typeField;
    public TextField appIdField;

    public ComboBox<Contact> contactComboBox;
    public ComboBox<Customer> customerComboBox;
    public ComboBox<String> startHourComboBox;
    public ComboBox<String> startMinComboBox;
    public ComboBox<String> endHourComboBox;
    public ComboBox<String> endMinComboBox;
    public ComboBox<User> userComboBox;

    public DatePicker datePicker;

    public Appointment appointmentToUpdate;
    public ObservableList<Customer> customersList = FXCollections.observableArrayList();
    public ObservableList<Contact> contactsList = FXCollections.observableArrayList();
    public ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();
    public ObservableList<User> usersList = FXCollections.observableArrayList();
    public ObservableList<String> hours = FXCollections.observableArrayList();
    public ObservableList<String> minutes = FXCollections.observableArrayList();

    /** This method gets the selected appointment from the Appointment Scheduler form and populates the fields with the appointment data.
     @param appointmentToUpdate The selected appointment
      */
    public void setAppointmentToUpdate(Appointment appointmentToUpdate) throws SQLException {
        customersList.addAll(Query.getCustomerRecords());
        //Get appointment start time
        LocalDateTime startTime = appointmentToUpdate.getStartTime().toLocalDateTime();
        String startHour;
        String startMin;
        //Adds a '0' to beginning of single digit numbers for readability
        if (startTime.getHour() <= 9) {
            startHour = "0" + startTime.getHour();
        } else {
            startHour = Integer.toString(startTime.getHour());
        }
        if (startTime.getMinute() <= 9) {
            startMin = "0" + startTime.getMinute();
        } else {
            startMin = Integer.toString(startTime.getMinute());
        }
        //Get appointment end time
        LocalDateTime endTime = appointmentToUpdate.getEndTime().toLocalDateTime();
        String endHour;
        String endMin;
        //Added readability for end times
        if (endTime.getHour() <= 9){
            endHour = "0" + endTime.getHour();
        } else {
            endHour = Integer.toString(endTime.getHour());
        }
        if (endTime.getMinute() <= 9) {
            endMin = "0" + endTime.getMinute();
        } else {
            endMin = Integer.toString(endTime.getMinute());
        }

        LocalDate date = startTime.toLocalDate();

        this.appointmentToUpdate = appointmentToUpdate;
        //Populate fields with data
        datePicker.setValue(date);

        appIdField.setText(Integer.toString(appointmentToUpdate.getAppointmentId()));
        titleField.setText(appointmentToUpdate.getAppTitle());
        descriptionField.setText(appointmentToUpdate.getAppDescription());
        locationField.setText(appointmentToUpdate.getAppLocation());
        typeField.setText(appointmentToUpdate.getAppType());

        contactComboBox.setValue(appointmentToUpdate.getContact());
        startHourComboBox.setValue(startHour);
        startMinComboBox.setValue(startMin);
        endHourComboBox.setValue(endHour);
        endMinComboBox.setValue(endMin);
        userComboBox.setValue(appointmentToUpdate.getUser());
        for (Customer customer : customersList) {
            if (customer.getCustomerId() == appointmentToUpdate.getCustomerId()) {
                customerComboBox.setValue(customer);
            }
        }
    }

    /** This method will update an appointment in the database.
     An existing appointment will be updated in the database. The appointment must be scheduled between 8am - 10pm EST. The appointment cannot overlap another appointment for the same customer. No field may be left blank.++
     The appointment times are saved in the database in the UTC time zone.
     @param actionEvent Save button is clicked.
      */
    public void onSave(ActionEvent actionEvent) throws SQLException, IOException {
        try {
            boolean hasSchedConflicts = false;
            LocalDate localAppDate = datePicker.getValue();

            //Create local date time from date picker and combo boxes
            LocalDateTime startDateTime = LocalDateTime.of(localAppDate.getYear(), localAppDate.getMonth(), localAppDate.getDayOfMonth(), Integer.parseInt(startHourComboBox.getValue()), Integer.parseInt(startMinComboBox.getValue()));
            LocalDateTime endDateTime = LocalDateTime.of(localAppDate.getYear(), localAppDate.getMonth(), localAppDate.getDayOfMonth(), Integer.parseInt(endHourComboBox.getValue()), Integer.parseInt(endMinComboBox.getValue()));

            //Local date time to zoned system default
            ZonedDateTime zonedStartDateTime = startDateTime.atZone(ZoneId.systemDefault());
            ZonedDateTime zonedEndDateTime = endDateTime.atZone(ZoneId.systemDefault());

            //System default to UTC time
            ZonedDateTime utcStartDateTime = zonedStartDateTime.withZoneSameInstant(ZoneOffset.UTC);
            ZonedDateTime utcEndDateTime = zonedEndDateTime.withZoneSameInstant(ZoneOffset.UTC);

            //System default to EST time
            ZonedDateTime estStartDateTime = zonedStartDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime estEndDateTime = zonedEndDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            //Get timestamps in UTC
            Timestamp startTimestamp = Timestamp.valueOf(dateTimeFormatter.format(utcStartDateTime));
            Timestamp endTimestamp = Timestamp.valueOf(dateTimeFormatter.format(utcEndDateTime));

            appointmentToUpdate.setAppDescription(descriptionField.getText());
            appointmentToUpdate.setAppLocation(locationField.getText());
            appointmentToUpdate.setAppTitle(titleField.getText());
            appointmentToUpdate.setAppType(typeField.getText());
            appointmentToUpdate.setCustomerId(customerComboBox.getSelectionModel().getSelectedItem().getCustomerId());
            appointmentToUpdate.setUser(userComboBox.getSelectionModel().getSelectedItem());
            appointmentToUpdate.setContact(contactComboBox.getSelectionModel().getSelectedItem());
            appointmentToUpdate.setStartTime(startTimestamp);
            appointmentToUpdate.setEndTime(endTimestamp);

            //Check for scheduling conflicts - ignores appointment times for selected appointment
            for (Appointment appointment : appointmentsList) {
                if (appointment.getCustomerId() == customerComboBox.getSelectionModel().getSelectedItem().getCustomerId()) {
                    if (appointment.getAppointmentId() != appointmentToUpdate.getAppointmentId()) {

                        LocalDateTime localBeginA = appointment.getStartTime().toLocalDateTime();
                        ZonedDateTime beginA = localBeginA.atZone(ZoneId.systemDefault());
                        LocalDateTime localEndA = appointment.getEndTime().toLocalDateTime();
                        ZonedDateTime endA = localEndA.atZone(ZoneId.systemDefault());

                        if ((zonedStartDateTime.isAfter(beginA) || zonedStartDateTime.isEqual(beginA)) && (zonedStartDateTime.isBefore(endA))) {
                            hasSchedConflicts = true;
                        } else if ((zonedEndDateTime.isAfter(beginA)) && (zonedEndDateTime.isBefore(endA) || zonedEndDateTime.isEqual(endA))) {
                            hasSchedConflicts = true;
                        } else if ((zonedStartDateTime.isBefore(beginA) || zonedStartDateTime.isEqual(beginA)) && (zonedEndDateTime.isAfter(endA) || zonedEndDateTime.isEqual(endA))) {
                            hasSchedConflicts = true;
                        }
                    }
                }
            }
            //Set business hours in EST 8am & 10pm
            LocalDateTime estBusStart = LocalDateTime.of(localAppDate.getYear(), localAppDate.getMonth(), localAppDate.getDayOfMonth(), 8, 0);
            ZonedDateTime zoneEBS = estBusStart.atZone(ZoneId.of("America/New_York"));
            LocalDateTime estBusEnd = LocalDateTime.of(localAppDate.getYear(), localAppDate.getMonth(), localAppDate.getDayOfMonth(), 22, 0);
            ZonedDateTime zoneEBE = estBusEnd.atZone(ZoneId.of("America/New_York"));

            //Logical checks for appointment overlap, business hours, start time before end time; check blank fields
            if (typeField.getText().isEmpty() || descriptionField.getText().isEmpty()|| locationField.getText().isEmpty() || titleField.getText().isEmpty()) {
                Alerts.errorAlert("Fields may not be left blank.");
            } else if (estStartDateTime.isBefore(zoneEBS)) {
                Alerts.errorAlert("Appointment cannot be scheduled before 8:00 AM EST.");
            } else if (estEndDateTime.isAfter(zoneEBE)) {
                Alerts.errorAlert("Appointment cannot end after 10:00 PM EST.");
            } else if (zonedStartDateTime.isAfter(zonedEndDateTime) || zonedEndDateTime.isBefore(zonedStartDateTime) || zonedStartDateTime.isEqual(zonedEndDateTime)) {
                Alerts.errorAlert("Start time must be before end time.");
            } else if (hasSchedConflicts) {
                Alerts.errorAlert("Unable to schedule. The desired time conflicts with an existing appointment.");
            } else {
                Query.updateAppointment(appointmentToUpdate);

                Parent root = FXMLLoader.load(this.getClass().getResource("/view/AppointmentScheduler.fxml"));
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 1200, 700);
                stage.setTitle("Appointments Scheduler");
                stage.setScene(scene);
                stage.show();
            }
        } catch (NullPointerException | NumberFormatException e) {
            Alerts.errorAlert("Fields cannot be left blank.");
        }
    }

    /** This method cancels the update appointment form.
      The user will be redirected back to the appointment scheduler form.
     @param actionEvent Cancel button is clicked.
      */
    public void onCancel(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/AppointmentScheduler.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1200, 700);
        stage.setTitle("Appointments Scheduler");
        stage.setScene(scene);
        stage.show();
    }

    /** This is the initialize method for the Update Appointment form.
      The lists of users, contacts, appointments, time choices and their corresponding combo boxes will be populated.
      */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hours.addAll("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
        minutes.addAll("00", "15", "30", "45");
        startHourComboBox.setItems(hours);
        startMinComboBox.setItems(minutes);
        endHourComboBox.setItems(hours);
        endMinComboBox.setItems(minutes);

        try {
            usersList.addAll(Query.getUsers());
            contactsList.addAll(Query.getContacts());
            appointmentsList.addAll(Query.getCustomerAppointments());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        customerComboBox.setItems(customersList);
        contactComboBox.setItems(contactsList);
        userComboBox.setItems(usersList);
    }
}
