package main;

import Utility.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Locale;

/** This is the Main class. */
public class Main extends Application {

    /** This method loads the log in screen. */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/LogInScreen.fxml"));
        stage.setTitle("Log In");
        stage.setScene(new Scene(root, 1200, 700));
        stage.show();

    }

    /** This method launches the application and open/closes the database connection. */
    public static void main(String[] args) throws SQLException {
        JDBC.openConnection();
        launch(args);
        JDBC.closeConnection();
    }
}
