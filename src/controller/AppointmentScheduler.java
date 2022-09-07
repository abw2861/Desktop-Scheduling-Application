package controller;

import Utility.Alerts;
import Utility.Query;
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
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointment;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.Optional;
import java.util.ResourceBundle;

/** This is the Appointment Scheduler controller class. */
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

    public ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();
    public ObservableList<Appointment> appointmentsListByMonth = FXCollections.observableArrayList();
    public ObservableList<Appointment> appointmentsListByWeek = FXCollections.observableArrayList();

    public Button addAppointmentButton;
    public Button customerRecordsButton;

    /** This method will show all appointments in the tableview.
     @param event The 'All' tab is selected.
     */
    public void onShowAll(Event event) {
        appointmentsTableView.setItems(appointmentsList);
        appointmentsTableView.setPlaceholder(new Label("No appointments"));
    }

    /** This method will show all appointments for the current month in the tableview.
     @param event The 'Month' tab is selected.
     */
    public void onShowMonth(Event event) {
        Month currentMonth = LocalDateTime.now().getMonth();
        appointmentsListByMonth.clear();

        for (Appointment appointment: appointmentsList){
            if (appointment.getStartTime().toLocalDateTime().getMonth() == currentMonth){
                appointmentsListByMonth.add(appointment);
            }
            appointmentsTableView.setItems(appointmentsListByMonth);
        }
        if (appointmentsListByMonth.isEmpty()) {
            appointmentsTableView.setPlaceholder(new Label("No appointments this month"));
        }
    }

    /** This method will show all appointments for the current week in the tableview.
     @param event The 'Week' tab is selected.
     */
    public void onShowWeek(Event event) {
        Timestamp weekStart = Timestamp.valueOf(LocalDateTime.now().with(DayOfWeek.MONDAY));
        Timestamp weekEnd = Timestamp.valueOf(LocalDateTime.now().with(DayOfWeek.SUNDAY));
        appointmentsListByWeek.clear();
        //Show appointments from Monday-Sunday of current week
        for (Appointment appointment: appointmentsList){
            if((appointment.getStartTime().toLocalDateTime().isAfter(weekStart.toLocalDateTime()) || appointment.getStartTime().toLocalDateTime().isEqual(weekStart.toLocalDateTime()))
                    && (appointment.getStartTime().toLocalDateTime().isBefore(weekEnd.toLocalDateTime())) || appointment.getStartTime().toLocalDateTime().isEqual(weekEnd.toLocalDateTime())){
                appointmentsListByWeek.add(appointment);
            }
            appointmentsTableView.setItems(appointmentsListByWeek);
        }
        if (appointmentsListByWeek.isEmpty()) {
            appointmentsTableView.setPlaceholder(new Label("No appointments this week"));
        }
    }

    /** This method selects an appointment to modify and opens the Update Appointment form.
     If an appointment is selected, the appointment information will be sent to the Update Appointment form, if no appointment is selected, an error dialog box will pop up.
     @param actionEvent The edit appointment button is clicked.
     */
    public void toEditAppointment(ActionEvent actionEvent) throws IOException, SQLException {
        Appointment selectedAppointment = appointmentsTableView.getSelectionModel().getSelectedItem();

        if (selectedAppointment == null) {
            Alerts.errorAlert("No appointment selected.");
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

    /** This method will delete an appointment from the database.
     The user will be prompted with a confirmation box prior to deletion and the appointment information will show on the screen after it is deleted.
     @param actionEvent The delete button is clicked.
     */
    public void toDeleteAppointment(ActionEvent actionEvent) throws SQLException {
        Appointment appointment = appointmentsTableView.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            Alerts.errorAlert("No appointment selected");
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will permanently delete an appointment, do you want to continue?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> results = alert.showAndWait();
            if (results.isPresent() && results.get() == ButtonType.YES) {
                Query.deleteAppointmentById(appointment.getAppointmentId());
                Alerts.confirmationAlert("'Appointment #" + appointment.getAppointmentId() + ": " + appointment.getAppType() + "' has been cancelled.");
            }
            appointmentsList.clear();
            appointmentsList.addAll(Query.getCustomerAppointments());
        }
    }

    /** This method will load a new stage.
     @param actionEvent A button is clicked.
     @param controllerView The name of the fxml view to be loaded.
     @param title The desired title of the window.
     */
    public void loadPage(ActionEvent actionEvent, String controllerView, String title) {
        try {
            Parent root = FXMLLoader.load(this.getClass().getResource("/view/" + controllerView + ".fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1200, 700);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** This is the initialize method for the Appointment Scheduler form.
     The tableview is populated with list of appointments. Simple button functionality is added.
     <b> Lambda expression used to populate the data in the appropriate columns.
     Lambda expression used to add button functionality to load new pages. This helped to reduce the amount of redundant code by eliminating the need to create multiple methods.
     </b>
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        appIdColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getAppointmentId()));
        titleColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAppTitle()));
        descriptionColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAppDescription()));
        locationColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAppLocation()));
        contactColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getContact().getContactName()));
        typeColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAppType()));
        startColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getStartTime()));
        endColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getEndTime()));
        cusIdColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCustomerId()));
        userIdColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getUser().getUserId()));

        addAppointmentButton.setOnAction(event -> {
            loadPage(event, "AddAppointment", "Create New Appointment");
        });
        customerRecordsButton.setOnAction(event -> {
            loadPage(event, "CustomerRecords", "Customer Records");
        });

        try {
            appointmentsList.addAll(Query.getCustomerAppointments());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        appointmentsTableView.setItems(appointmentsList);
    }
}
