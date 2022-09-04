package controller;

import Utility.Alerts;
import Utility.Query;
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
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

/** This is the Customer Records controller class. */
public class CustomerRecordsScreen implements Initializable {

    public TableColumn<Customer, String> customerName;
    public TableColumn<Customer, String> customerAddress;
    public TableColumn<Customer, String> customerPostal;
    public TableColumn<Customer, String> customerPhone;
    public TableColumn<Customer, String> customerDivision;
    public TableColumn<Customer, String> customerCountry;

    public TableView<Customer> customerTableview;
    public Button exitButton;
    public Button addCustomer;
    public Button viewReportsButton;
    public Button appSchedulerButton;

    public ObservableList<Customer> customerList = FXCollections.observableArrayList();
    public ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();

    /** This method selects a customer to modify and opens the Update Customer form.
     If a customer is selected, the customer information will be sent to Update Customer form and the window will be loaded. If no customer is selected, an error dialog box will pop up.
     @param actionEvent The update customer button is clicked.
     */
    public void onUpdateCustomer(ActionEvent actionEvent) throws IOException, SQLException {
        Customer selectedCustomer = customerTableview.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            Alerts.errorAlert("No customer selected.");
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

    /** This method deletes a customer from the database.
     A selected customer will be deleted from the database. If the customer has any appointments, all appointments will be deleted first. The user will be prompted with a confirmation box prior to deletion and an error box if no customer is selected.
     @param actionEvent The delete button is clicked.
     */
    public void onDeleteCustomer(ActionEvent actionEvent) throws SQLException {
        Customer customer = customerTableview.getSelectionModel().getSelectedItem();
        appointmentsList.addAll(Query.getCustomerAppointments());
        boolean hasAppointments = false;

        if(customerTableview.getSelectionModel().getSelectedItem() == null) {
            Alerts.errorAlert("There is no customer selected.");
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
                    Alerts.confirmationAlert("Customer Record for '" + customer.getCustomerName() + "' has been deleted.");
                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will permanently delete a customer, do you want to continue?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> results = alert.showAndWait();
                if (results.isPresent() && results.get() == ButtonType.YES) {
                    Query.deleteCustomer(customer.getCustomerId());
                    Alerts.confirmationAlert("Customer Record for '" + customer.getCustomerName() + "' has been deleted.");
                }
            }
            customerList.clear();
            customerList.addAll(Query.getCustomerRecords());
        }
    }

    /** This method will load a new stage.
     @param actionEvent A button is clicked.
     @param controllerView The name of the fxml view to be loaded.
     @param title The desired title of the window.
     */
    public void loadPage (ActionEvent actionEvent, String controllerView, String title) {
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

    /** This is the initialize method for the Customer Records form.
      The tableview is populated with list of customers. Simple button functionality is added.
      <b> Lambda expression used to populate the data in the appropriate columns.
      Lambda expression used to add button functionality to load new pages and exit the application. This helped to reduce the amount of redundant code and the need for an additional method creation.
      </b>
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        customerName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCustomerName()));
        customerAddress.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAddress()));
        customerPostal.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPostalCode()));
        customerPhone.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPhone()));
        customerDivision.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCustomerDivision().getDivisionName()));
        customerCountry.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getCustomerDivision().getCountry().getCountryName()));

        exitButton.setOnAction(e -> Platform.exit());

        addCustomer.setOnAction(event -> {
            loadPage(event, "AddCustomer", "Add Customer");
        });

        viewReportsButton.setOnAction(event -> {
            loadPage(event, "Reports", "Reports");
        });

        appSchedulerButton.setOnAction(event -> {
            loadPage(event, "AppointmentScheduler", "AppointmentScheduler");
        });

        try {
            customerList.addAll(Query.getCustomerRecords());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        customerTableview.setItems(customerList);
    }
}

