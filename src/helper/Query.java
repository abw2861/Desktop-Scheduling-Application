package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Query {

    public static void select() throws SQLException {
        String sql = "SELECT * FROM client_schedule.customers";
        PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            int customerID = resultSet.getInt("Customer_ID");
            String customerName = resultSet.getString("Customer_Name");
            String customerAddress = resultSet.getString("Address");
            String customerPostalCode = resultSet.getString("Postal_Code");
            String customerPhone = resultSet.getString("Phone");
        }
    }

    public static void select(int customerID) throws SQLException {
        String sql = "SELECT * FROM client_schedule.customers WHERE Customer_ID = ?";
        PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
        preparedStatement.setInt(1, customerID);
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            customerID = resultSet.getInt("Customer_ID");
            String customerName = resultSet.getString("Customer_Name");
            String customerAddress = resultSet.getString("Address");
            String customerPostalCode = resultSet.getString("Postal_Code");
            String customerPhone = resultSet.getString("Phone");
        }
    }

  /*  public static selectDivisionId (String division) throws SQLException {
        String sql = "SELECT Division_ID FROM first_level_division WHERE Division =?";
        PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
        preparedStatement.setString(1, division);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            division = resultSet.getString("Division");
            int divisionId = resultSet.getInt("Division_ID");
        }
        return di
    }

   */

    private static Customer getCustomer(ResultSet resultSet) throws SQLException {
        Customer customer = null;
        while (resultSet.next()) {
           // customers = new Customers();
            customer.setCustomerId(resultSet.getInt("Customer_ID"));
            customer.setCustomerName(resultSet.getString("Customer_Name"));
            customer.setAddress(resultSet.getString("Address"));
            customer.setPhone(resultSet.getString("Phone"));
            customer.setPostalCode(resultSet.getString("Postal_Code"));
        }
        return customer;
    }

    /*public static ObservableList<Customer> selectAllCustomers () throws SQLException {
        ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
        JDBC.openConnection();
        String sql = "SELECT * FROM client_schedule.customers";
        PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
        ResultSet resultSetCustomers = preparedStatement.executeQuery();
        while (resultSetCustomers.next()){
            String customerName = resultSetCustomers.getString("Customer_Name");
            String customerAddress = resultSetCustomers.getString("Address");
            String customerPostalCode = resultSetCustomers.getString("Postal_Code");
            String customerPhone = resultSetCustomers.getString("Phone");
            int customerID = resultSetCustomers.getInt("Customer_ID");
            Customer customerResult = new Customer(customerName, customerAddress, customerPostalCode, customerPhone, customerID);
            allCustomers.add(customerResult);
        }
        return allCustomers;
    }*/

    //gets the customers and their associated divisions
    public static ObservableList<Customer> getCustomerRecords () throws SQLException {
        ObservableList<Customer> customerDivisions = FXCollections.observableArrayList();
        String sql = "SELECT c.Customer_ID, c.Customer_Name, c.Address, c.Postal_Code, c.Phone, fld.Division_ID, fld.Division, co.Country_ID, co.Country " +
                "FROM customers c " +
                "JOIN first_level_divisions fld ON fld.Division_ID = c.Division_ID " +
                "JOIN countries co ON co.Country_ID = fld.Country_ID " +
                "ORDER BY c.Customer_ID";
        PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int customerId = resultSet.getInt("Customer_ID");
            String customerName = resultSet.getString("Customer_Name");
            String customerAddress = resultSet.getString("Address");
            String customerPostalCode = resultSet.getString("Postal_Code");
            String customerPhone = resultSet.getString("Phone");
            int divisionId = resultSet.getInt("Division_ID");
            String divisionName = resultSet.getString("Division");
            int countryId = resultSet.getInt("Country_ID");
            String countryName = resultSet.getString("Country");
            Customer customerRecord = new Customer(customerId, customerName, customerAddress, customerPostalCode, customerPhone, new Division(divisionId, divisionName, new Country(countryId, countryName)));
            customerDivisions.add(customerRecord);
        }
        return customerDivisions;
    }

    public static ObservableList<Appointment> getCustomerAppointments () throws SQLException {
        ObservableList<Appointment> customerAppointments = FXCollections.observableArrayList();
        String sql = "SELECT a.Appointment_ID, a.Title, a.Description, a.Location, a.Type, a.Start, a.End, a.Customer_ID, a.User_ID, c.Contact_Name, c.Contact_ID, u.User_Name " +
                "FROM appointments a " +
                "JOIN contacts c ON a.Contact_ID = c.Contact_ID " +
                "JOIN users u ON a.User_ID = u.User_ID " +
                "ORDER BY a.Appointment_ID";
        PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int appointmentId = resultSet.getInt("Appointment_ID");
            String appTitle = resultSet.getString("Title");
            String appDescription = resultSet.getString("Description");
            String appLocation = resultSet.getString("Location");
            String appType = resultSet.getString("Type");

            Timestamp startTime = resultSet.getTimestamp("Start");
            LocalDateTime localStartTime = startTime.toLocalDateTime();
            ZonedDateTime zonedStartTime = localStartTime.atZone(ZoneId.of("UTC"));
            ZonedDateTime zonedStartToLocalStart = zonedStartTime.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
            LocalDateTime localStartTimeFinal = zonedStartToLocalStart.toLocalDateTime();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            Timestamp startTimestamp = Timestamp.valueOf(dateTimeFormatter.format(localStartTimeFinal));


            Timestamp endTime = resultSet.getTimestamp("End");
            LocalDateTime localEndTime = endTime.toLocalDateTime();
            ZonedDateTime zonedEndTime = localEndTime.atZone(ZoneId.of("UTC"));
            ZonedDateTime zonedEndToLocalEnd = zonedEndTime.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
            LocalDateTime localEndTimeFinal = zonedEndToLocalEnd.toLocalDateTime();
            Timestamp endTimestamp = Timestamp.valueOf(dateTimeFormatter.format(localEndTimeFinal));

            int customerId = resultSet.getInt("Customer_ID");
            int userId = resultSet.getInt("User_ID");
            String userName = resultSet.getString("User_Name");
            int contactId = resultSet.getInt("Contact_ID");
            String contactName = resultSet.getString("contact_Name");
            Appointment customerAppointment = new Appointment(appointmentId, appTitle, appDescription, appLocation, appType, startTimestamp, endTimestamp, customerId, new User(userId, userName), new Contact(contactId, contactName));
            customerAppointments.add(customerAppointment);
        }
        return customerAppointments;
    }

    //inserts a new customer into customer table
    public static int insertCustomer(String customerName, String customerAddress, String customerPostalCode, String customerPhone, int divisionId) throws SQLException {
        String sql = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Division_ID, Create_Date, Created_By, Last_Update, Last_Updated_By) VALUES(?, ?, ?, ?, ?, NOW(), 'user', NOW(), 'user')";
        PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
        preparedStatement.setString(1, customerName);
        preparedStatement.setString(2, customerAddress);
        preparedStatement.setString(3, customerPostalCode);
        preparedStatement.setString(4, customerPhone);
        preparedStatement.setInt(5, divisionId);
        int rowsAffected = preparedStatement.executeUpdate();
        return rowsAffected;
    }

    public static int insertAppointment(String title, String description, String location, String type, Timestamp startDateTime, Timestamp endDateTime, int customerId, int userId, int contactId) throws SQLException {
        String sql = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID, Create_Date, Created_By, Last_Update, Last_Updated_By ) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), 'user', NOW(), 'user' )";
        PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
        preparedStatement.setString(1, title);
        preparedStatement.setString(2, description);
        preparedStatement.setString(3, location);
        preparedStatement.setString(4, type);
        preparedStatement.setTimestamp(5, startDateTime);
        preparedStatement.setTimestamp(6, endDateTime);
        preparedStatement.setInt(7, customerId);
        preparedStatement.setInt(8, userId);
        preparedStatement.setInt(9, contactId);
        int rowsAffected = preparedStatement.executeUpdate();
        return rowsAffected;
    }


    //gets the divisions and their division id's
    public static ObservableList<Division> getDivisions (int selectedCountryId) throws SQLException {
        ObservableList<Division> divisionsList = FXCollections.observableArrayList();
        String sql = "SELECT fld.Division, fld.Division_ID, co.Country_ID, co.Country " +
                "FROM first_level_divisions fld " +
                "JOIN countries co ON co.Country_ID = fld.Country_ID " +
                "WHERE co.Country_ID = ?";
        PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
        preparedStatement.setInt(1, selectedCountryId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            String divisionName = resultSet.getString("Division");
            int divisionId = resultSet.getInt("Division_ID");
            String countryName = resultSet.getString("Country");
            int countryId = resultSet.getInt("Country_ID");
            Division divisions = new Division(divisionId, divisionName, new Country(countryId, countryName));
            divisionsList.add(divisions);
        }
        return divisionsList;
    }

    public static ObservableList<Contact> getContacts () throws SQLException {
        ObservableList<Contact> contactsList = FXCollections.observableArrayList();
        String sql = "SELECT Contact_ID, Contact_Name " +
                "FROM contacts";
        PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            String contactName = resultSet.getString("Contact_Name");
            int contactId = resultSet.getInt("Contact_ID");
            Contact contact = new Contact(contactId, contactName);
            contactsList.add(contact);
        }
        return contactsList;
    }

    //deletes a customer from database
    public static int deleteCustomer (int customerId) throws SQLException {;
        String sql = "DELETE FROM customers WHERE Customer_id = ?";
        PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
        preparedStatement.setInt(1, customerId);
        int rowsAffected = preparedStatement.executeUpdate();
        return rowsAffected;
    }

    public static int deleteAppointment (int customerId) throws SQLException {
        String sql = "DELETE FROM appointments WHERE Customer_ID = ?";
        PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
        preparedStatement.setInt(1, customerId);
        int rowsAffected = preparedStatement.executeUpdate();
        return rowsAffected;
    }

    //updates an existing customer
    public static void updateCustomer (Customer customer) throws SQLException {
        String sql = "UPDATE customers " +
                "SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Last_Update = NOW(), Last_Updated_By = 'user', Division_ID = ? " +
                "WHERE Customer_ID = ?";
        PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
        preparedStatement.setString(1, customer.getCustomerName());
        preparedStatement.setString(2, customer.getAddress());
        preparedStatement.setString(3, customer.getPostalCode());
        preparedStatement.setString(4, customer.getPhone());
        preparedStatement.setInt(5, customer.getCustomerDivision().getDivisionId());
        preparedStatement.setInt(6, customer.getCustomerId());
        preparedStatement.execute();
    }

    public static void updateAppointment (Appointment appointment) throws SQLException {
        String sql = "UPDATE appointments " +
                "SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = NOW(), Last_Updated_By = 'user', Customer_ID = ?, User_ID = ?, Contact_ID = ? " +
                "WHERE Appointment_ID = ?";
        PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
        preparedStatement.setString(1, appointment.getAppTitle());
        preparedStatement.setString(2, appointment.getAppDescription());
        preparedStatement.setString(3, appointment.getAppLocation());
        preparedStatement.setString(4, appointment.getAppType());
        preparedStatement.setTimestamp(5, appointment.getStartTime());
        preparedStatement.setTimestamp(6, appointment.getEndTime());
        preparedStatement.setInt(7, appointment.getCustomerId());
        preparedStatement.setInt(8, appointment.getUser().getUserId());
        preparedStatement.setInt(9, appointment.getContact().getContactId());
        preparedStatement.setInt(10, appointment.getAppointmentId());
        preparedStatement.execute();
    }

    //gets a list of all the countries and their id's
    public static ObservableList<Country> getCountries () throws SQLException {
        ObservableList<Country> countriesList = FXCollections.observableArrayList();
        String sql = "SELECT Country, Country_ID FROM countries";
        PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            String country = resultSet.getString("Country");
            int countryId = resultSet.getInt("Country_ID");
            Country countries = new Country(countryId, country);
            countriesList.add(countries);
        }
        return countriesList;
    }

    public static ObservableList<User> getUsers () throws SQLException {
        ObservableList<User> usersList = FXCollections.observableArrayList();
        String sql = "SELECT User_ID, User_Name FROM users";
        PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            String userName = resultSet.getString("User_Name");
            int userId = resultSet.getInt("User_ID");
            User users = new User(userId, userName);
            usersList.add(users);
        }
        return usersList;
    }

    public static ObservableList<Appointment> appointmentsFilteredByUser (int selectedUserId) throws SQLException {
        ObservableList<Appointment> customerAppointments = FXCollections.observableArrayList();
        String sql = "SELECT a.Appointment_ID, a.Title, a.Description, a.Location, a.Type, a.Start, a.End, a.Customer_ID, a.User_ID, c.Contact_Name, c.Contact_ID, u.User_Name " +
                "FROM appointments a " +
                "JOIN contacts c ON a.Contact_ID = c.Contact_ID " +
                "JOIN users u ON a.User_ID = u.User_ID " +
                "WHERE a.User_ID = ?";
        PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
        preparedStatement.setInt(1, selectedUserId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int appointmentId = resultSet.getInt("Appointment_ID");
            String appTitle = resultSet.getString("Title");
            String appDescription = resultSet.getString("Description");
            String appLocation = resultSet.getString("Location");
            String appType = resultSet.getString("Type");

            Timestamp startTime = resultSet.getTimestamp("Start");
            LocalDateTime localStartTime = startTime.toLocalDateTime();
            ZonedDateTime zonedStartTime = localStartTime.atZone(ZoneId.of("UTC"));
            ZonedDateTime zonedStartToLocalStart = zonedStartTime.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
            LocalDateTime localStartTimeFinal = zonedStartToLocalStart.toLocalDateTime();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            Timestamp startTimestamp = Timestamp.valueOf(dateTimeFormatter.format(localStartTimeFinal));


            Timestamp endTime = resultSet.getTimestamp("End");
            LocalDateTime localEndTime = endTime.toLocalDateTime();
            ZonedDateTime zonedEndTime = localEndTime.atZone(ZoneId.of("UTC"));
            ZonedDateTime zonedEndToLocalEnd = zonedEndTime.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
            LocalDateTime localEndTimeFinal = zonedEndToLocalEnd.toLocalDateTime();
            Timestamp endTimestamp = Timestamp.valueOf(dateTimeFormatter.format(localEndTimeFinal));

            int customerId = resultSet.getInt("Customer_ID");
            int userId = resultSet.getInt("User_ID");
            String userName = resultSet.getString("User_Name");
            int contactId = resultSet.getInt("Contact_ID");
            String contactName = resultSet.getString("contact_Name");
            Appointment customerAppointment = new Appointment(appointmentId, appTitle, appDescription, appLocation, appType, startTimestamp, endTimestamp, customerId, new User(userId, userName), new Contact(contactId, contactName));
            customerAppointments.add(customerAppointment);
        }
        return customerAppointments;
    }

}
