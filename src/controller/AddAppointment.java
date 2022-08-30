package controller;

import helper.Query;
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
import java.text.ParseException;
import java.time.*;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.ResourceBundle;

public class AddAppointment implements Initializable {

    public TextField titleField;
    public TextField descriptionField;
    public TextField locationField;
    public TextField typeField;

    public DatePicker datePicker;

    public ComboBox<Contact> contactComboBox;
    public ComboBox<Customer> customerComboBox;
    public ComboBox<User> userComboBox;
    public ComboBox<String> startHourComboBox;
    public ComboBox<String> startMinComboBox;
    public ComboBox<String> endHourComboBox;
    public ComboBox<String> endMinComboBox;

    public ObservableList<Contact> contactsList = FXCollections.observableArrayList();
    public ObservableList<Customer> customersList = FXCollections.observableArrayList();
    public ObservableList<User> usersList =  FXCollections.observableArrayList();
    public ObservableList<String> hours = FXCollections.observableArrayList();
    public ObservableList<String> minutes = FXCollections.observableArrayList();
    public ObservableList<Appointment> appointmentsList =  FXCollections.observableArrayList();


    public void onSave(ActionEvent actionEvent) throws SQLException, IOException {
        try {
            boolean hasSchedConflicts = false;
            LocalDate appDateLocal = datePicker.getValue();
            String startHour = startHourComboBox.getValue();
            String startMin = startMinComboBox.getValue();
            String endHour = endHourComboBox.getValue();
            String endMin = endMinComboBox.getValue();

            String appTitle = titleField.getText();
            String appDescription = descriptionField.getText();
            String appLocation = locationField.getText();
            String appType = typeField.getText();
            int customerId = customerComboBox.getSelectionModel().getSelectedItem().getCustomerId();
            int userId = userComboBox.getSelectionModel().getSelectedItem().getUserId();
            int contactId = contactComboBox.getSelectionModel().getSelectedItem().getContactId();

            LocalDateTime startDateTime = LocalDateTime.of(appDateLocal.getYear(), appDateLocal.getMonth(), appDateLocal.getDayOfMonth(), Integer.parseInt(startHour), Integer.parseInt(startMin));
            LocalDateTime endDateTime = LocalDateTime.of(appDateLocal.getYear(), appDateLocal.getMonth(), appDateLocal.getDayOfMonth(), Integer.parseInt(endHour), Integer.parseInt(endMin));

            ZonedDateTime zonedStartDateTime = startDateTime.atZone(ZoneId.systemDefault());
            ZonedDateTime zonedEndDateTime = endDateTime.atZone(ZoneId.systemDefault());

            ZonedDateTime utcStartDateTime = zonedStartDateTime.withZoneSameInstant(ZoneOffset.UTC);
            ZonedDateTime utcEndDateTime = zonedEndDateTime.withZoneSameInstant(ZoneOffset.UTC);

            ZonedDateTime estStartDateTime = zonedStartDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime estEndDateTime = zonedEndDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            Timestamp startTimestamp = Timestamp.valueOf(dateTimeFormatter.format(utcStartDateTime));
            Timestamp endTimestamp = Timestamp.valueOf(dateTimeFormatter.format(utcEndDateTime));

            LocalDateTime estBusStart = LocalDateTime.of(appDateLocal.getYear(), appDateLocal.getMonth(), appDateLocal.getDayOfMonth(), 8, 0);
            ZonedDateTime zoneEBS = estBusStart.atZone(ZoneId.of("America/New_York"));
            LocalDateTime estBusEnd = LocalDateTime.of(appDateLocal.getYear(), appDateLocal.getMonth(), appDateLocal.getDayOfMonth(), 22, 0);
            ZonedDateTime zoneEBE = estBusEnd.atZone(ZoneId.of("America/New_York"));


            for (Appointment appointment : appointmentsList) {
                if (appointment.getCustomerId() == customerComboBox.getSelectionModel().getSelectedItem().getCustomerId()) {

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

            if (estStartDateTime.isBefore(zoneEBS)) {
                errorAlert("Appointment cannot be scheduled before 8:00 AM EST.");
            } else if (estEndDateTime.isAfter(zoneEBE)) {
                errorAlert("Appointment cannot end after 10:00 PM EST.");
            } else if (zonedStartDateTime.isAfter(zonedEndDateTime) || zonedEndDateTime.isBefore(zonedStartDateTime) || zonedStartDateTime.isEqual(zonedEndDateTime)) {
                errorAlert("Start time must be before end time.");
            } else if (hasSchedConflicts) {
                errorAlert("Unable to schedule. The desired time conflicts with an existing appointment.");
            } else {
                Query.insertAppointment(appTitle, appDescription, appLocation, appType, startTimestamp, endTimestamp, customerId, userId, contactId);

                Parent root = (Parent) FXMLLoader.load(this.getClass().getResource("/view/AppointmentScheduler.fxml"));
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 1200.0, 720.0);
                stage.setTitle("Appointment Scheduler");
                stage.setScene(scene);
                stage.show();
            }
        } catch (NullPointerException e) {
            errorAlert("Fields cannot be left blank.");
        }
    }

    public void onCancel(ActionEvent actionEvent) throws IOException {
        Parent root = (Parent) FXMLLoader.load(this.getClass().getResource("/view/AppointmentScheduler.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1200.0, 720.0);
        stage.setTitle("Appointment Scheduler");
        stage.setScene(scene);
        stage.show();

    }

    public static void errorAlert(String contentText) {
        Alert alert;
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hours.addAll("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
        minutes.addAll("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55");
        startHourComboBox.setItems(hours);
        startMinComboBox.setItems(minutes);
        endHourComboBox.setItems(hours);
        endMinComboBox.setItems(minutes);
        try {
            appointmentsList.addAll(Query.getCustomerAppointments());
            contactsList.addAll(Query.getContacts());
            customersList.addAll(Query.getCustomerRecords());
            usersList.addAll(Query.getUsers());
            contactComboBox.setItems(contactsList);
            customerComboBox.setItems(customersList);
            userComboBox.setItems(usersList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

}
