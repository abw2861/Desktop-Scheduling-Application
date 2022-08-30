package controller;

import helper.Query;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.Appointment;
import model.Contact;
import model.User;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

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

    public void setAppTypesList () throws SQLException {

        for (Appointment appointment : appList) {
            appTypesList.addAll(appointment.getAppType());
            Set<String> set = new HashSet<>(appTypesList);
            appTypesList.clear();
            appTypesList.addAll(set);
        }
    }

    public void setTotalAppTextField () {
        int totalApp = 0;
        String selectedType;
        String selectedMonth;
        String appMonth;

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

    public void onPickType(ActionEvent actionEvent) {
        setTotalAppTextField();
    }

    public void onPickMonth(ActionEvent actionEvent) {
        setTotalAppTextField();
        appTypeComboBox.setItems(appTypesList);
    }

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

    public void setTotalAppByUserTextField () {
        int totalAppointments = 0;
        String selectedMonthForUser;
        User selectedUser;
        String appMonthForUser;

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

    public void onChooseUser(ActionEvent actionEvent) {
        userMonthComboBox.setItems(userMonthsList);
        userMonthComboBox.setValue("All");
        setTotalAppByUserTextField();
    }

    public void onChooseUserMonth(ActionEvent actionEvent) {
        setTotalAppByUserTextField();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appIdColumn.setCellValueFactory(cellData -> {return new ReadOnlyObjectWrapper<>(cellData.getValue().getAppointmentId());});
        typeColumn.setCellValueFactory(cellData -> {return new ReadOnlyStringWrapper(cellData.getValue().getAppType());});
        descriptionColumn.setCellValueFactory(cellData -> {return new ReadOnlyStringWrapper(cellData.getValue().getAppDescription());});
        startTimeColumn.setCellValueFactory(cellData -> {return new ReadOnlyObjectWrapper<>(cellData.getValue().getStartTime());});
        endTimeColumn.setCellValueFactory(cellData -> {return new ReadOnlyObjectWrapper<>(cellData.getValue().getEndTime());});
        customerIdColumn.setCellValueFactory(cellData -> {return new ReadOnlyObjectWrapper<>(cellData.getValue().getCustomerId());});
        titleColumn.setCellValueFactory(cellData -> {return new ReadOnlyStringWrapper(cellData.getValue().getAppTitle());});

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
