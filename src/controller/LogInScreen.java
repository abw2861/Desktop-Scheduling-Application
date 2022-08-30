package controller;

import helper.JDBC;
import helper.Query;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


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


    public boolean userValidation (String userName, String password) throws SQLException {
        try {

            userName = usernameField.getText().toString();
            password = passwordField.getText().toString();
            String sql = "SELECT * FROM client_schedule.users WHERE User_Name=? and Password=?";

            preparedStatement = JDBC.connection.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e){
            errorAlert("Error");
        }
        return false;
    }

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
                appointmentAlert("You have an upcoming appointment: \n\nAppointment ID: " + upcomingAppId + "\n" + "Date: " + upcomingAppStart.getYear() + "-" + upcomingAppStart.getMonth() + "-" + upcomingAppStart.getDayOfMonth() +
                "\nTime: " + upcomingAppStart.getHour() + ":" + displayMinutes(upcomingAppStart));
            }
            else {
                appointmentAlert("You have no upcoming appointments.");
            }
        }
    }

    public String displayMinutes (LocalDateTime ldt){
        if (ldt.getMinute() <= 9){
            return "0" + ldt.getMinute();
        }
        else {
            return Integer.toString(ldt.getMinute());
        }
    }

    public void onLogIn(ActionEvent actionEvent) throws IOException, SQLException {
            String userName = usernameField.getText();
            String password = passwordField.getText();
            String successfulLogin = null;
            LocalDateTime localDateTime = LocalDateTime.now();
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            ZonedDateTime utcTime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);
            boolean val = userValidation(userName, password);

            if (usernameField.getText().isEmpty() && passwordField.getText().isEmpty()) {
                errorAlert("EmptyUsernameAndPassword");
                successfulLogin = "Unsuccessful";
            } else if (usernameField.getText().isEmpty()) {
                errorAlert("EmptyUsername");
                successfulLogin = "Unsuccessful";
            } else if (passwordField.getText().isEmpty()) {
                errorAlert("EmptyPassword");
                successfulLogin = "Unsuccessful";
            } else if (!val) {
                errorAlert("IncorrectUsernameOrPassword");
                successfulLogin = "Unsuccessful";
            } else {
                Parent root = (Parent) FXMLLoader.load(this.getClass().getResource("/view/CustomerRecords.fxml"));
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 1200.0, 720.0);
                stage.setTitle("Customer Records");
                stage.setScene(scene);
                stage.show();

                successfulLogin = "Successful";

                upcomingApt();

            }
            logger(userName, utcTime, successfulLogin);
            //System.out.println("Log-In Attempt -> Username: " + userName + " || Date: " + localDateTime.getYear()+ "-" + localDateTime.getMonth() + "-" + localDateTime.getDayOfMonth() + " || Time: " + localDateTime.getHour() + ":" + localDateTime.getMinute() + " || Status: " + successfulLogin);
    }

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

    public static void errorAlert(String contentText) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Main.MessagesBundle");
        Alert alert;
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(resourceBundle.getString("Error"));
        alert.setContentText(resourceBundle.getString(contentText));
        alert.showAndWait();
    }

    public static void appointmentAlert(String contentText) {
        Alert alert;
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alert");
        alert.setContentText(contentText);
        alert.showAndWait();
    }



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
