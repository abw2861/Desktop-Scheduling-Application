package controller;

import Utility.Query;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;
import model.Contact;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/** This is the Reports' controller class. */
public class Reports implements Initializable {


    public ComboBox<String> appMonthComboBox;
    public ComboBox<String> appTypeComboBox;
    public TextField totalAppTextField;

    public ObservableList<String> monthsList = FXCollections.observableArrayList();
    public ObservableList<String> appTypesList = FXCollections.observableArrayList();
    public ObservableList<Appointment> appList = FXCollections.observableArrayList();
    public ObservableList<Appointment> appListByContact = FXCollections.observableArrayList();
    public ObservableList<Contact> contactsList = FXCollections.observableArrayList();
    public ObservableList<String> userMonthsList = FXCollections.observableArrayList();
    public ObservableList<User> usersList = FXCollections.observableArrayList();


    public ComboBox<Contact> contactsComboBox;

    public TableView<Appointment> appByContactTableview;

    public TableColumn<Appointment, Integer> appIdColumn;
    public TableColumn<Appointment, String> titleColumn;
    public TableColumn<Appointment, String> typeColumn;
    public TableColumn<Appointment, String> descriptionColumn;
    public TableColumn<Appointment, Timestamp> startTimeColumn;
    public TableColumn<Appointment, Timestamp> endTimeColumn;
    public TableColumn<Appointment, Integer> customerIdColumn;
    public ComboBox<User> userComboBox;
    public ComboBox<String> userMonthComboBox;
    public TextField totalAppByUserTextField;

    /** This method creates a list of appointment types, without duplicates.
      */
    public void setAppTypesList () throws SQLException {
        //Create appointment types list - hash set used so duplicate types are not shown in combo box
        for (Appointment appointment : appList) {
            appTypesList.addAll(appointment.getAppType());
            Set<String> set = new HashSet<>(appTypesList);
            appTypesList.clear();
            appTypesList.addAll(set);
        }
    }

    /** This method sets a text field text to a total number of appointments, filtered by month and appointment type. */
    public void setTotalAppTextField () {
        int totalApp = 0;
        String selectedType;
        String selectedMonth;
        String appMonth;

        //Appointment total field set - filtered by month & type
        for (Appointment appointment : appList){
            LocalDateTime localDateTime = appointment.getStartTime().toLocalDateTime();
            appMonth = localDateTime.getMonth().toString().toLowerCase();
            selectedMonth = appMonthComboBox.getValue().toLowerCase();
            selectedType = appTypeComboBox.getValue();

            if (Objects.equals(appointment.getAppType(), selectedType) && (Objects.equals(appMonth, selectedMonth))){
                totalApp = totalApp + 1;
            }
        }
        totalAppTextField.setText(Integer.toString(totalApp));
    }

    /** This method updates the total number of appointments text field filtered by chosen appointment type.
     @param actionEvent Type chosen from appTypeComboBox.
     */
    public void onPickType(ActionEvent actionEvent) {
        setTotalAppTextField();
    }

    /** This method updates the total number of appointments text field and populates the appointment types combo box.
     @param actionEvent Month chosen from appMonthComboBox.
     */
    public void onPickMonth(ActionEvent actionEvent) {
        setTotalAppTextField();
        appTypeComboBox.setItems(appTypesList);
    }

    /** This method populates a tableview with a list of appointments filtered by chosen contact.
     @param actionEvent Contact chosen from contactsComboBox.
      */
    public void onChooseContact(ActionEvent actionEvent) throws SQLException {
        boolean hasAppointments = false;
        appListByContact.clear();

        for (Appointment appointment : appList) {
            if (appointment.getContact().getContactId() == contactsComboBox.getValue().getContactId()) {
                hasAppointments = true;
                appListByContact.addAll(appointment);
            }
        }
        if (hasAppointments) {
            appByContactTableview.setItems(appListByContact);
        }
    }

    /** This method sets a text field to the total number of appointments filtered by a selected user and a selected month.
      */
    public void setTotalAppByUserTextField () {
        int totalAppointments = 0;
        String selectedMonthForUser;
        User selectedUser;
        String appMonthForUser;

        //Third report - show total appointments for chosen user, with ability to filter by month
        for (Appointment appointment : appList) {
            LocalDateTime localDateTime = appointment.getStartTime().toLocalDateTime();
            appMonthForUser = localDateTime.getMonth().toString().toLowerCase();
            selectedMonthForUser = userMonthComboBox.getValue().toLowerCase();
            selectedUser = userComboBox.getValue();

            if ((selectedUser.getUserId() == appointment.getUser().getUserId()) && selectedMonthForUser.equals("all")) {
                totalAppointments = totalAppointments + 1;
            }
            else if (selectedUser.getUserId() == appointment.getUser().getUserId() && selectedMonthForUser.equals(appMonthForUser)) {
                totalAppointments = totalAppointments + 1;
            }
        }
        totalAppByUserTextField.setText(Integer.toString(totalAppointments));
    }

    /** This method populates the user month combo box and updates the total appointments filtered by user.
     @param actionEvent User chosen from userComboBox.
      */
    public void onChooseUser(ActionEvent actionEvent) {
        userMonthComboBox.setItems(userMonthsList);
        userMonthComboBox.setValue("All");
        setTotalAppByUserTextField();
    }

    /** This method updates the total appointments filtered by user and month.
      @param actionEvent Month chosen from userMonthComboBox.
      */
    public void onChooseUserMonth(ActionEvent actionEvent) {
        setTotalAppByUserTextField();
    }

    /** This method leaves the Reports form and loads the Customer Records window.
     @param actionEvent Back button is clicked.
     */
    public void onBack(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/CustomerRecords.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1200.0, 720.0);
        stage.setTitle("Customer Records");
        stage.setScene(scene);
        stage.show();
    }

    /** This is the initialize method for the Reports form.
     * <b> Lambda expression used to populate the tableview columns with the appropriate data. </b>
     * The lists of months, contacts, users and appointments are created. The contact and user combo boxes are populated.
      */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appIdColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getAppointmentId()));
        typeColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAppType()));
        descriptionColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAppDescription()));
        startTimeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getStartTime()));
        endTimeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getEndTime()));
        customerIdColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCustomerId()));
        titleColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAppTitle()));

        monthsList.addAll("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        userMonthsList.addAll("All", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        appMonthComboBox.setItems(monthsList);

        try {
            appList.addAll(Query.getCustomerAppointments());
            setAppTypesList();
            contactsList.addAll(Query.getContacts());
            usersList.addAll(Query.getUsers());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        contactsComboBox.setItems(contactsList);
        userComboBox.setItems(usersList);
    }
}
