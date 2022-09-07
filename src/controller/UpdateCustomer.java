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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Country;
import model.Customer;
import model.Division;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/** This is the Update Customer controller class. */
public class UpdateCustomer implements Initializable {
    public TextField customerAddressField;
    public TextField customerPhoneField;
    public TextField customerPostalField;
    public TextField customerNameField;

    public ComboBox<Division> divisionComboBox;
    public ComboBox<Country> countryComboBox;

    public TextField customerIDField;

    public Customer customerToUpdate;

    public ObservableList<Country> countryList = FXCollections.observableArrayList();
    public ObservableList<Division> divisionsList = FXCollections.observableArrayList();

    /** This method gets the selected customer from the Customer Records form and populates the fields with the data.
      @param customerToUpdate The selected customer.
      */
    public void setCustomerToUpdate(Customer customerToUpdate) throws SQLException {

        this.customerToUpdate = customerToUpdate;

        customerNameField.setText(customerToUpdate.getCustomerName());
        customerAddressField.setText(customerToUpdate.getAddress());
        customerPostalField.setText(customerToUpdate.getPostalCode());
        customerPhoneField.setText(customerToUpdate.getPhone());
        customerIDField.setText((Integer.toString(customerToUpdate.getCustomerId())));

        divisionComboBox.setValue(customerToUpdate.getCustomerDivision());
        countryComboBox.setValue(customerToUpdate.getCustomerDivision().getCountry());

        int countryId = customerToUpdate.getCustomerDivision().getCountry().getCountryId();
        divisionsList = Query.getDivisions(countryId);
        divisionComboBox.setItems(divisionsList);
    }

    /** This method populates the divisions' combo box when a country is selected.
     The user is only able to select divisions that are available for the selected country.
     @param actionEvent A country is selected.
      */
    public void onSelectCountry(ActionEvent actionEvent) throws SQLException {
            int countryId = countryComboBox.getSelectionModel().getSelectedItem().getCountryId();
            divisionComboBox.setValue(null);
            divisionComboBox.setItems(Query.getDivisions(countryId));
    }

    /** This method will update a customer record in the database and then redirect user back to Customer Records form.
      @param actionEvent The save button is clicked.
      */
    public void onSave(ActionEvent actionEvent) throws IOException, SQLException {
        try {
            customerToUpdate.setCustomerName(customerNameField.getText());
            customerToUpdate.setAddress(customerAddressField.getText());
            customerToUpdate.setPostalCode(customerPostalField.getText());
            customerToUpdate.setPhone(customerPhoneField.getText());
            customerToUpdate.setCustomerDivision(divisionComboBox.getSelectionModel().getSelectedItem());

            if (customerNameField.getText().isEmpty() || customerAddressField.getText().isEmpty() || customerPostalField.getText().isEmpty() || customerPhoneField.getText().isEmpty()) {
                Alerts.errorAlert("No fields may be left blank. ");
            } else {
                Query.updateCustomer(customerToUpdate);

                Parent root = FXMLLoader.load(this.getClass().getResource("/view/CustomerRecords.fxml"));
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 1200.0, 720.0);
                stage.setTitle("Customer Records");
                stage.setScene(scene);
                stage.show();
            }
        } catch (NullPointerException e) {
            Alerts.errorAlert("Fields cannot be left blank. ");
        }
    }

    /** This method will cancel the update customer form and redirect user back to Customer Records form.
     @param actionEvent The cancel button is clicked.
     */
    public void onCancel(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/CustomerRecords.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1200.0, 720.0);
        stage.setTitle("Customer Records");
        stage.setScene(scene);
        stage.show();
    }

    /** This is the initialize method for the Update Customer form.
     The list of countries and country combo box is populated.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            countryList = Query.getCountries();
            countryComboBox.setItems(countryList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
