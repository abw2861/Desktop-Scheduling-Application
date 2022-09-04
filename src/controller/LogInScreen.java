package controller;

import Utility.Alerts;
import Utility.JDBC;
import Utility.Query;
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
import model.User;

import java.io.IOException;
import java.sql.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/** This is the Log-In form controller class. */
public class LogInScreen implements Initializable {

    public Button logInButton;
    public TextField usernameField;
    public PasswordField passwordField;
    public Label locationLabel;
    public Label currentLocationLabel;
    public Label loginLabel;
    public ObservableList<Appointment> appointmentsListByUser = FXCollections.observableArrayList();
    public ObservableList<User> usersList = FXCollections.observableArrayList();

    PreparedStatement preparedStatement = null;

    /** This method validates that a username and password combo are correct.
     If there is a username and password combination match in the database, the method will return true.
     @param userName The username entered by user
     @param password The password entered by user
      */
    public boolean userValidation (String userName, String password) throws SQLException {
        try {

            userName = usernameField.getText();
            password = passwordField.getText();
            String sql = "SELECT * FROM client_schedule.users WHERE User_Name=? and Password=?";

            preparedStatement = JDBC.connection.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true;
            }

        } catch (SQLException e){
            loginErrorAlert("Error");
        }
        return false;
    }

    /** This method detects if the user that has logged in has an upcoming appointment within 15 minutes of logging in.
     If the current user has an appointment within 15 minutes of logging in, the user will get an alert with the appointment information. If there is no upcoming appointment, the user will get a corresponding alert.
     */
    public void upcomingApt () throws SQLException {

        String userName = usernameField.getText();
        String password = passwordField.getText();
        boolean val = userValidation(userName, password);
        boolean hasUpcomingAppointment = false;
        int upcomingAppId = 0;
        LocalDateTime upcomingAppStart = null;
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime timeIn15Minutes = currentTime.plusMinutes(15);

        usersList.addAll(Query.getUsers());
        //Check if user that logged in has an upcoming appointment within 15 minutes
        if (val) {
            for (User user : usersList) {
                if (user.getUserName().equals(userName)) {
                    User currentUser = new User(user.getUserId(), userName);
                    appointmentsListByUser.addAll(Query.appointmentsFilteredByUser(currentUser.getUserId()));
                }
                for (Appointment appointment: appointmentsListByUser) {
                    if ((appointment.getStartTime().toLocalDateTime().isBefore(timeIn15Minutes) || appointment.getStartTime().toLocalDateTime().isEqual(timeIn15Minutes)) && (appointment.getStartTime().toLocalDateTime().isAfter(currentTime))){
                        hasUpcomingAppointment = true;
                        upcomingAppId = appointment.getAppointmentId();
                        upcomingAppStart = appointment.getStartTime().toLocalDateTime();
                    }
                }
            }

            if (hasUpcomingAppointment){
                Alerts.informationAlert("You have an upcoming appointment: \n\nAppointment ID: " + upcomingAppId + "\n" + "Date: " + upcomingAppStart.getYear() + "-" + upcomingAppStart.getMonth() + "-" + upcomingAppStart.getDayOfMonth() +
                "\nTime: " + upcomingAppStart.getHour() + ":" + displayMinutes(upcomingAppStart));
            }
            else {
                Alerts.informationAlert("You have no upcoming appointments.");
            }
        }
    }

    /** This method formats the display of the minutes in the 'minutes' combo box.
     @param ldt The LocalDateTime of user system.
     */
    public String displayMinutes (LocalDateTime ldt){
        //Show single number minutes with a 0 in front, for readability
        if (ldt.getMinute() <= 9){
            return "0" + ldt.getMinute();
        }
        else {
            return Integer.toString(ldt.getMinute());
        }
    }

    /** This method allows user to log into application.
     If the entered username and password is validated, access will be allowed and the Customer Record window will open. Upcoming appointment method will be called.
     Error alerts for incorrect log-ins will show in French or English depending on user's language settings.
     All log-in activity will be recorded in a .txt file.
     @param actionEvent Log in button is clicked.
     */
    public void onLogIn(ActionEvent actionEvent) throws IOException, SQLException {
            String userName = usernameField.getText();
            String password = passwordField.getText();
            String successfulLogin;
            LocalDateTime localDateTime = LocalDateTime.now();
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            ZonedDateTime utcTime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);
            boolean val = userValidation(userName, password);

            if (usernameField.getText().isEmpty() && passwordField.getText().isEmpty()) {
                loginErrorAlert("EmptyUsernameAndPassword");
                successfulLogin = "Unsuccessful";
            } else if (usernameField.getText().isEmpty()) {
                loginErrorAlert("EmptyUsername");
                successfulLogin = "Unsuccessful";
            } else if (passwordField.getText().isEmpty()) {
                loginErrorAlert("EmptyPassword");
                successfulLogin = "Unsuccessful";
            } else if (!val) {
                loginErrorAlert("IncorrectUsernameOrPassword");
                successfulLogin = "Unsuccessful";
            } else {
                Parent root = FXMLLoader.load(this.getClass().getResource("/view/CustomerRecords.fxml"));
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 1200.0, 720.0);
                stage.setTitle("Customer Records");
                stage.setScene(scene);
                stage.show();

                successfulLogin = "Successful";

                upcomingApt();

            }
            //Logger method called
            logger(userName, utcTime, successfulLogin);
    }

    /** This method creates the logger for log in activity.
     @param userName The username entered
     @param loginSuccess String output for successful or unsuccessful log in
     @param zonedDateTime Time of log in attempt in UTC
     */
    public void logger (String userName, ZonedDateTime zonedDateTime, String loginSuccess) {
        Logger log = Logger.getLogger("login_activity.txt");

        try {
            FileHandler fileHandler = new FileHandler("login_activity.txt", true);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            log.addHandler(fileHandler);
        } catch (IOException | SecurityException e) {
            Logger.getLogger(LogInScreen.class.getName()).log(Level.INFO, null, e);
        }

        log.setLevel(Level.INFO);
        log.info("Log-In Attempt -> Username: " + userName + " || Date: " + zonedDateTime.getYear()+ "-" + zonedDateTime.getMonth() + "-" + zonedDateTime.getDayOfMonth() + " || Time: " + zonedDateTime.getHour() + ":" + zonedDateTime.getMinute() + " UTC || Status: " + loginSuccess);
    }

    /** This method will create an error alert dialog box. The alert will be in French or English depending on user's language settings.
     @param contentText The error alert information text.
     */
    public static void loginErrorAlert(String contentText) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Main.MessagesBundle");
        Alert alert;
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(resourceBundle.getString("Error"));
        alert.setContentText(resourceBundle.getString(contentText));
        alert.showAndWait();
    }

    /** This is the initialize method for the Log-In form.
     The location label text is initialized to show the user's time zone. All button text and prompt text is set to be in French or English depending on user's language settings.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        locationLabel.setText(ZoneId.systemDefault().toString());

        ResourceBundle resourceBundle1 = ResourceBundle.getBundle("Main.MessagesBundle");

        logInButton.setText(resourceBundle1.getString("Submit"));
        usernameField.setPromptText(resourceBundle1.getString("Username"));
        passwordField.setPromptText(resourceBundle1.getString("Password"));
        currentLocationLabel.setText(resourceBundle1.getString("CurrentLocation"));
        loginLabel.setText(resourceBundle1.getString("LOGIN"));
    }
}
