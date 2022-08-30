package controller;

import helper.Query;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import model.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerRecordsScreen implements Initializable {

    public TableColumn<Customer, String> customerName;
    public TableColumn<Customer, String> customerAddress;
    public TableColumn<Customer, String> customerPostal;
    public TableColumn<Customer, String> customerPhone;
    public TableColumn<Customer, String> customerDivision;
    public TableColumn<Customer, String> customerCountry;

    public TableView<Customer> customerTableview;

    ObservableList<Customer> customerList = FXCollections.observableArrayList();
    ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();

    public void onAddCustomer(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/AddCustomer.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1200, 700);
        stage.setTitle("Add Customer");
        stage.setScene(scene);
        stage.show();
    }

    public void onUpdateCustomer(ActionEvent actionEvent) throws IOException, SQLException {
        Customer selectedCustomer = (Customer) customerTableview.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            errorAlert("No customer selected.");
        }
        else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UpdateCustomer.fxml"));
            Parent root = loader.load();

            UpdateCustomer updateCustomer = loader.getController();
            updateCustomer.setCustomerToUpdate(selectedCustomer);


            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1200, 720);
            stage.setTitle("Update Customer");
            stage.setScene(scene);
            stage.show();
        }
    }

    public void onDeleteCustomer(ActionEvent actionEvent) throws SQLException {
        Customer customer = customerTableview.getSelectionModel().getSelectedItem();
        appointmentsList.addAll(Query.getCustomerAppointments());
        boolean hasAppointments = false;

        if(customerTableview.getSelectionModel().getSelectedItem() == null) {
            errorAlert("There is no customer selected.");
        }
        else if (customerTableview.getSelectionModel().getSelectedItem() != null) {
            for (Appointment appointment : appointmentsList) {
                if (appointment.getCustomerId() == customer.getCustomerId()) {
                    hasAppointments = true;
                }
            }
            if (hasAppointments) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This customer has one or more appointments, would you still like to delete? All associated appointments will be cancelled. ", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> results = alert.showAndWait();
                if (results.isPresent() && results.get() == ButtonType.YES) {
                    Query.deleteAppointment(customer.getCustomerId());
                    Query.deleteCustomer(customer.getCustomerId());
                    Alert confirmAlert;
                    confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Confirmation");
                    confirmAlert.setContentText("Customer Record for '" + customer.getCustomerName() + "' has been deleted.");
                    confirmAlert.showAndWait();
                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will permanently delete a customer, do you want to continue?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> results = alert.showAndWait();
                if (results.isPresent() && results.get() == ButtonType.YES) {
                    Query.deleteCustomer(customer.getCustomerId());

                    Alert confirmAlert;
                    confirmAlert = new Alert(Alert.AlertType.INFORMATION);
                    confirmAlert.setTitle("Confirmation");
                    confirmAlert.setContentText("Customer Record for '" + customer.getCustomerName() + "' has been deleted.");
                    confirmAlert.showAndWait();
                }
            }
            customerList.clear();
            customerList.addAll(Query.getCustomerRecords());
        }
    }

    public void toViewAppointments(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/Reports.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1200, 700);
        stage.setTitle("Reports");
        stage.setScene(scene);
        stage.show();

    }

    public void toAppScheduler(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/AppointmentScheduler.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1200, 700);
        stage.setTitle("Appointments Scheduler");
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
        customerName.setCellValueFactory(cellData -> {return new ReadOnlyStringWrapper(cellData.getValue().getCustomerName());});
        customerAddress.setCellValueFactory(cellData -> {return new ReadOnlyStringWrapper(cellData.getValue().getAddress());});
        customerPostal.setCellValueFactory(cellData -> {return new ReadOnlyStringWrapper(cellData.getValue().getPostalCode());});
        customerPhone.setCellValueFactory(cellData -> {return new ReadOnlyStringWrapper(cellData.getValue().getPhone());});
        customerDivision.setCellValueFactory(cellData -> {return new ReadOnlyStringWrapper(cellData.getValue().getCustomerDivision().getDivisionName());});
        customerCountry.setCellValueFactory(cellData -> {return new ReadOnlyStringWrapper(cellData.getValue().getCustomerDivision().getCountry().getCountryName());});

        try {
            customerList.addAll(Query.getCustomerRecords());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        customerTableview.setItems(customerList);


    }


    public void toExit(ActionEvent actionEvent) {
        Platform.exit();
    }
}

