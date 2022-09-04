package Utility;


import javafx.scene.control.Alert;

/** This is the Alerts abstract class. */
public abstract class Alerts {

    /** This method creates an error dialog box alert.
     @param contentText The error alert text
     */
    public static void errorAlert(String contentText) {
        Alert alert;
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /** This method creates an information dialog box alert.
      @param contentText The information alert text
      */
    public static void informationAlert(String contentText) {
        Alert alert;
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /** This method creates a confirmation dialog box alert.
     @param contentText The confirmation alert text
     */
    public static void confirmationAlert(String contentText) {
        Alert confirmAlert;
        confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setContentText(contentText);
        confirmAlert.showAndWait();
    }

}
