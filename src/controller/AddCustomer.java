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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Country;
import model.Division;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/** This is the Add Customer controller class. */
public class AddCustomer implements Initializable {

    public TextField customerAddressField;
    public TextField customerPhoneField;
    public TextField customerPostalField;
    public TextField customerNameField;

    public ComboBox<Division> divisionComboBox;
    public ComboBox<Country> countryComboBox;

    public static ObservableList<Country> countryList = FXCollections.observableArrayList();

    /** This method will save a new customer to the database.
     A new customer will be created in the database. No field may be left blank.
     @param actionEvent The save button is clicked.
     */
    public void onSave(ActionEvent actionEvent) throws SQLException, IOException {
        try {
            String customerName = customerNameField.getText();
            String customerAddress = customerAddressField.getText();
            String customerPostalCode = customerPostalField.getText();
            String customerPhone = customerPhoneField.getText();
            int divisionId = divisionComboBox.getSelectionModel().getSelectedItem().getDivisionId();

            if (customerNameField.getText().isEmpty() || customerAddressField.getText().isEmpty() || customerPostalField.getText().isEmpty() || customerPhoneField.getText().isEmpty()) {
                Alerts.errorAlert("No fields may be left blank. ");
            } else {
                Query.insertCustomer(customerName, customerAddress, customerPostalCode, customerPhone, divisionId);

                Parent root = FXMLLoader.load(this.getClass().getResource("/view/CustomerRecords.fxml"));
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 1200.0, 720.0);
                stage.setTitle("Customer Records");
                stage.setScene(scene);
                stage.show();
            }
        } catch (NullPointerException e) {
            Alerts.errorAlert("Fields cannot be blank.");
        }
    }

    /** This method will cancel adding a new customer.
      The user will be redirected to the Customer Records form.
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

    /** This method will populate the division combo box.
     The division combo box will be populated with a list of divisions that are available for the country selected in the country combo box.
     @param actionEvent A country is selected from countryComboBox.
     */
    public void onSelectCountry(ActionEvent actionEvent) throws SQLException {
        int countryId;
        //Division combo box is empty until a country is chosen
        if (countryComboBox.getSelectionModel().getSelectedItem() == null) {
            divisionComboBox.setItems(null);
        }
        else if (countryComboBox.getSelectionModel().getSelectedItem() != null){
            countryId = countryComboBox.getSelectionModel().getSelectedItem().getCountryId();
            divisionComboBox.setItems(Query.getDivisions(countryId));
        }
    }

    /** This is the initialize method for the Add Customer form.
      The country combo box will be initialized with an observable list of countries.
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


