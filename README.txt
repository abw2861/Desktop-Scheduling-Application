C-195 Java Application

Purpose: This is a scheduling desktop application. It allows authorized users to create and edit customer files, as well as scheduling appointments for the individual customers. 

Author: Adina Williams

Contact Information: awi2861@wgu.edu

Student Application Version 1.2

Date: 09-07-2022

IDE: IntelliJ IDEA Community Edition 2021.1.3

JDK: 17.0.1

SDK: JavaFX-SDK-17.0.1

MySQL Connector: mysql-connector-java-8.0.25

Additional Report: The additional report shows a total number of appointments for a chosen user, which can be filtered by month. This could be used as a KPI by tracking user scheduling.

Instructions to run program:
    1. Open the project in IntelliJ IDEA
    2. Go to File -> 'Project Structure' and update the library paths to the MySQL driver and the JavaFX SDK.
    3. Go to Run -> 'Add Configuration' -> 'Application' and copy: --module-path ${PATH_TO_FX} --add-modules javafx.fxml,javafx.controls,javafx.graphics to the VM options.
    4. While still in configurations, add 'main.Main' to the main class. Apply and select OK.
    5. Press Run JavaProject. Log in using username 'test' and password 'test'.