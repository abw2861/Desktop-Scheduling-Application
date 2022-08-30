package main;

import helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Locale;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/LogInScreen.fxml"));
       // Parent root = FXMLLoader.load(getClass().getResource("/view/CustomerRecords.fxml"));
        stage.setTitle("Log In");
        stage.setScene(new Scene(root, 1200, 700));
        stage.show();

    }


    public static void main(String[] args) throws SQLException {
        //Locale.setDefault(new Locale("fr"));
        JDBC.openConnection();
        launch(args);
        JDBC.closeConnection();
    }
}
