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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Country;
import model.Division;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddCustomer implements Initializable {
    public Button onAddCustomerSave;
    public Button onCancel;

    public TextField customerAddressField;
    public TextField customerPhoneField;
    public TextField customerPostalField;
    public TextField customerNameField;
    public TextField customerIdField;

    public ComboBox<Division> divisionComboBox;
    public ComboBox<Country> countryComboBox;

    private static ObservableList<Division> divisionList = FXCollections.observableArrayList();
    private static ObservableList<Country> countryList = FXCollections.observableArrayList();


    public void onSave(ActionEvent actionEvent) throws SQLException, IOException {
        try {
            String customerName = customerNameField.getText();
            String customerAddress = customerAddressField.getText();
            String customerPostalCode = customerPostalField.getText();
            String customerPhone = customerPhoneField.getText();
            int divisionId = divisionComboBox.getSelectionModel().getSelectedItem().getDivisionId();

            Query.insertCustomer(customerName, customerAddress, customerPostalCode, customerPhone, divisionId);

            Parent root = (Parent) FXMLLoader.load(this.getClass().getResource("/view/CustomerRecords.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1200.0, 720.0);
            stage.setTitle("Customer Records");
            stage.setScene(scene);
            stage.show();
        } catch (NullPointerException e) {
            errorAlert("Fields cannot be blank.");
        }
    }

    public void onCancel(ActionEvent actionEvent) throws IOException {
        Parent root = (Parent) FXMLLoader.load(this.getClass().getResource("/view/CustomerRecords.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1200.0, 720.0);
        stage.setTitle("Customer Records");
        stage.setScene(scene);
        stage.show();
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

    public void errorAlert(String contentText) {
        Alert alert;
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(contentText);
        alert.showAndWait();
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


