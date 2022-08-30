package controller;

import helper.Query;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.Appointment;
import model.Appointment;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppointmentScheduler implements Initializable {
    public TableView<Appointment> appointmentsTableView;

    public TableColumn<Appointment, Integer> appIdColumn;
    public TableColumn<Appointment, String> titleColumn;
    public TableColumn<Appointment, String>  descriptionColumn;
    public TableColumn<Appointment, String>  locationColumn;
    public TableColumn<Appointment, String>  contactColumn;
    public TableColumn<Appointment, String>  typeColumn;
    public TableColumn<Appointment, Timestamp> startColumn;
    public TableColumn<Appointment, Timestamp> endColumn;
    public TableColumn<Appointment, Integer> cusIdColumn;
    public TableColumn<Appointment, Integer> userIdColumn;

    private ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();
    private ObservableList<Appointment> appointmentsListByMonth = FXCollections.observableArrayList();
    private ObservableList<Appointment> appointmentsListByWeek = FXCollections.observableArrayList();

    public void onShowAll(Event event) {
        appointmentsTableView.setItems(appointmentsList);
    }

    public void onShowMonth(Event event) {
        Month currentMonth = LocalDateTime.now().getMonth();
        appointmentsListByMonth.clear();

        for (Appointment appointment: appointmentsList){
            if (appointment.getStartTime().toLocalDateTime().getMonth() == currentMonth){
                appointmentsListByMonth.add(appointment);
            }
            appointmentsTableView.setItems(appointmentsListByMonth);
        }
        if (appointmentsListByMonth.isEmpty()){
            System.out.println("No appointments this Month.");
        }
}

    public void onShowWeek(Event event) {
        Timestamp weekStart = Timestamp.valueOf(LocalDateTime.now().with(DayOfWeek.MONDAY));
        Timestamp weekEnd = Timestamp.valueOf(LocalDateTime.now().with(DayOfWeek.SUNDAY));
        appointmentsListByWeek.clear();

        for (Appointment appointment: appointmentsList){
            if(appointment.getStartTime().toLocalDateTime().isAfter(weekStart.toLocalDateTime()) && appointment.getStartTime().toLocalDateTime().isBefore(weekEnd.toLocalDateTime())){
                appointmentsListByWeek.add(appointment);
            }
            appointmentsTableView.setItems(appointmentsListByWeek);
        }
        if (appointmentsListByWeek.isEmpty()) {
            System.out.println("No appointments this week.");
        }
    }

    public void toAddAppointment(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/AddAppointment.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1200, 700);
        stage.setTitle("Create New Appointment");
        stage.setScene(scene);
        stage.show();
    }

    public void toEditAppointment(ActionEvent actionEvent) throws IOException, SQLException {
        Appointment selectedAppointment = appointmentsTableView.getSelectionModel().getSelectedItem();

        if (selectedAppointment == null) {
            errorAlert("No appointment selected.");
        }
        else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UpdateAppointment.fxml"));
            Parent root = loader.load();

            UpdateAppointment updateAppointment = loader.getController();
            updateAppointment.setAppointmentToUpdate(selectedAppointment);


            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1200, 720);
            stage.setTitle("Update Appointment");
            stage.setScene(scene);
            stage.show();
        }

    }

    public void toDeleteAppointment(ActionEvent actionEvent) throws SQLException {
        Appointment appointment = appointmentsTableView.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            errorAlert("No appointment selected");
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will permanently delete an appointment, do you want to continue?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> results = alert.showAndWait();
            if (results.isPresent() && results.get() == ButtonType.YES) {
                Query.deleteAppointment(appointment.getCustomerId());

                Alert confirmAlert;
                confirmAlert = new Alert(Alert.AlertType.INFORMATION);
                confirmAlert.setTitle("Confirmation");
                confirmAlert.setContentText("Appointment #" + appointment.getAppointmentId() + ": " + appointment.getAppType() + " has been cancelled.");
                confirmAlert.showAndWait();
            }
            appointmentsList.clear();
            appointmentsList.addAll(Query.getCustomerAppointments());
        }
    }

    public void backToRecords(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/CustomerRecords.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1200.0, 720.0);
        stage.setTitle("Customer Records");
        stage.setScene(scene);
        stage.show();
    }

    public void errorAlert(String contentText) {
        Alert alert;
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        appIdColumn.setCellValueFactory(cellData -> {return new ReadOnlyObjectWrapper<>(cellData.getValue().getAppointmentId());});
        titleColumn.setCellValueFactory(cellData -> {return new ReadOnlyStringWrapper(cellData.getValue().getAppTitle());});
        descriptionColumn.setCellValueFactory(cellData -> {return new ReadOnlyStringWrapper(cellData.getValue().getAppDescription());});
        locationColumn.setCellValueFactory(cellData -> {return new ReadOnlyStringWrapper(cellData.getValue().getAppLocation());});
        contactColumn.setCellValueFactory(cellData -> {return new ReadOnlyStringWrapper(cellData.getValue().getContact().getContactName());});
        typeColumn.setCellValueFactory(cellData -> {return new ReadOnlyStringWrapper(cellData.getValue().getAppType());});
        startColumn.setCellValueFactory(cellData -> {return new ReadOnlyObjectWrapper<>(cellData.getValue().getStartTime());});
        endColumn.setCellValueFactory(cellData -> {return new ReadOnlyObjectWrapper<>(cellData.getValue().getEndTime());});
        cusIdColumn.setCellValueFactory(cellData -> {return new ReadOnlyObjectWrapper<>(cellData.getValue().getCustomerId());});
        userIdColumn.setCellValueFactory(cellData -> {return new ReadOnlyObjectWrapper<>(cellData.getValue().getUser().getUserId());});



        try {
            appointmentsList.addAll(Query.getCustomerAppointments());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        appointmentsTableView.setItems(appointmentsList);
    }

}
