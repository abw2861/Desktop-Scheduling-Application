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

public class UpdateCustomer implements Initializable {
    public TextField customerAddressField;
    public TextField customerPhoneField;
    public TextField customerPostalField;
    public TextField customerNameField;

    public ComboBox<Division> divisionComboBox;
    public ComboBox<Country> countryComboBox;

    public TextField customerIDField;

    public Customer customerToUpdate;

    private ObservableList<Country> countryList = FXCollections.observableArrayList();
    private ObservableList<Division> divisionList = FXCollections.observableArrayList();

    public void setCustomerToUpdate(Customer customerToUpdate) throws SQLException {

        this.customerToUpdate = customerToUpdate;

        customerNameField.setText(customerToUpdate.getCustomerName());
        customerAddressField.setText(customerToUpdate.getAddress());
        customerPostalField.setText(customerToUpdate.getPostalCode());
        customerPhoneField.setText(customerToUpdate.getPhone());
        customerIDField.setText((Integer.toString(customerToUpdate.getCustomerId())));

        divisionComboBox.setValue(customerToUpdate.getCustomerDivision());
        countryComboBox.setValue(customerToUpdate.getCustomerDivision().getCountry());

    }

    public void onSelectCountry(ActionEvent actionEvent) throws SQLException {
        int countryId;
        if (countryComboBox.getSelectionModel().getSelectedItem() == null) {
            divisionComboBox.setItems(null);
        }
        else if (countryComboBox.getSelectionModel().getSelectedItem() != null){
            countryId = countryComboBox.getSelectionModel().getSelectedItem().getCountryId();
            divisionComboBox.setItems(Query.getDivisions(countryId));
        }
    }

    public void onSave(ActionEvent actionEvent) throws IOException, SQLException {
        customerToUpdate.setCustomerName(customerNameField.getText());
        customerToUpdate.setAddress(customerAddressField.getText());
        customerToUpdate.setPostalCode(customerPostalField.getText());
        customerToUpdate.setPhone(customerPhoneField.getText());
        customerToUpdate.setCustomerDivision(divisionComboBox.getSelectionModel().getSelectedItem());

        Query.updateCustomer(customerToUpdate);

        Parent root = (Parent) FXMLLoader.load(this.getClass().getResource("/view/CustomerRecords.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1200.0, 720.0);
        stage.setTitle("Customer Records");
        stage.setScene(scene);
        stage.show();
    }

    public void onCancel(ActionEvent actionEvent) throws IOException {
        Parent root = (Parent) FXMLLoader.load(this.getClass().getResource("/view/CustomerRecords.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1200.0, 720.0);
        stage.setTitle("Customer Records");
        stage.setScene(scene);
        stage.show();
    }

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
