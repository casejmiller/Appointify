
package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import scheduler.model.AppByCon;
import scheduler.model.AppByMonth;
import scheduler.model.Appointment;
import scheduler.view_controller.LoginController;

/**
 *
 * @author CMiller
 */
public class DBConnect {

    static String driver = "com.mysql.jdbc.Driver";
    static String db = "U04NhH";
    static String url = "jdbc:mysql://52.206.157.109/" + db;
    static String user = "U04NhH";
    static String pass = "53688288856";

    private static Connection conn = null;

    public DBConnect() {

    }

    private static ZoneId zID = ZoneId.systemDefault();

    private static Customer selCustomer = new Customer();

    public static Customer getSelectedCustomer() {
        return selCustomer;
    }

    public static Connection getCurrentConnection() {
        return conn;
    }

    public static void openConn() throws ClassNotFoundException, SQLException {
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex.toString());
            throw ex;
        }

    }

    public static ZonedDateTime convertToLocal(Timestamp datetime) {
        ZonedDateTime zidUTC = datetime.toLocalDateTime().atZone(ZoneId.of("UTC"));
        ZonedDateTime localZDT = zidUTC.withZoneSameInstant(zID);
        return localZDT;
    }

    public static ObservableList getAppointments(Boolean Week, Integer dateValue) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        String where;
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();

        if (Week == true) {
            where = "WHERE WEEK(a.start) = ? ";
        } else {
            where = "WHERE MONTH(a.start) = ? ";
        }

        try {
            openConn();
            String query = "SELECT a.appointmentId, a.description, a.location, a.title, a.start, a.end, c.customerName, a.contact "
                    + " FROM appointment a "
                    + " INNER JOIN customer c ON c.customerId = a.customerId "
                    + where
                    + "ORDER BY a.start ASC";

            st = conn.prepareStatement(query);

            st.setInt(1, dateValue);

            rs = st.executeQuery();

            while (rs.next()) {
                ZonedDateTime timeStart = convertToLocal(rs.getTimestamp("start"));
                ZonedDateTime timeEnd = convertToLocal(rs.getTimestamp("end"));

                appointments.add(
                        new Appointment(
                                rs.getInt("appointmentId"),
                                rs.getString("customerName"),
                                rs.getString("title"),
                                rs.getString("description"),
                                timeStart.toLocalDate().toString(),
                                timeStart.toLocalTime().toString(),
                                timeEnd.toLocalTime().toString(),
                                rs.getString("contact"),
                                rs.getString("location")
                        )
                );
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }

        return appointments;
    }

    public static ObservableList<Customer> getCustomers() throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        ObservableList<Customer> customers = FXCollections.observableArrayList();

        try {
            openConn();
            String query = "SELECT cus.customerId, cus.customerName, a.address, a.phone, a.postalCode, city.city, country.country "
                    + "FROM customer cus "
                    + "JOIN address a ON a.addressId = cus.addressId "
                    + "JOIN city city ON a.cityId = city.cityId "
                    + "JOIN country country ON city.countryId = country.countryId "
                    + "WHERE cus.active = 1 "
                    + "ORDER BY cus.customerId ASC";

            st = conn.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                customers.add(
                        new Customer(
                                rs.getInt("customerId"),
                                rs.getString("customerName"),
                                rs.getString("address"),
                                rs.getString("city"),
                                rs.getString("country"),
                                rs.getString("postalCode"),
                                rs.getString("phone")
                        )
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return customers;
    }

    public static ObservableList<Customer> customerCombo() throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        ObservableList<Customer> customers = FXCollections.observableArrayList();

        try {
            openConn();
            String query = "SELECT customerId, customerName FROM customer WHERE active = 1 ORDER BY customerName ASC;";
            st = conn.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                customers.add(
                        new Customer(
                                rs.getInt("customerId"),
                                rs.getString("customerName")
                        )
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return customers;
    }

    public static void addCustomer(String name, Integer addressId) throws SQLException {
        PreparedStatement st = null;

        try {
            openConn();
            String query = "INSERT INTO customer (customerId, customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES (?, ?, ?, ?, Now(), ?, Now(), ?)";

            st = conn.prepareStatement(query);

            st.setInt(1, getCustomerId());
            st.setString(2, name);
            st.setInt(3, addressId);
            st.setString(4, "1");
            st.setString(5, LoginController.user);
            st.setString(6, "");

            System.out.println(st.toString());

            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }

    public static Integer addAddress(String address, Integer cityID, String postCode, String phone) throws SQLException {
        Integer addressID = 0;
        PreparedStatement st = null;

        try {
            openConn();
            String query = "INSERT INTO address (addressId, address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES (?, ?, ?, ?, ?, ?, Now(), ?, Now(), ?)";

            st = conn.prepareStatement(query);

            addressID = getAddressId();
            st.setInt(1, addressID);
            st.setString(2, address);
            st.setString(3, "");
            st.setInt(4, cityID);
            st.setString(5, postCode);
            st.setString(6, phone);
            st.setString(7, LoginController.user);
            st.setString(8, "");

            System.out.println(st.toString());

            st.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }

        System.out.println(addressID);
        return addressID;
    }

    public static void updateAddress(Integer addressID, String address, Integer cityID, String postCode, String phone) throws SQLException {

        PreparedStatement st = null;

        try {
            openConn();
            String query = "UPDATE address SET address = ?, cityId = ?, "
                    + "postalCode = ?, phone = ?, "
                    + "lastUpdate = Now(), lastUpdateBy = ? "
                    + "WHERE addressId = ?";

            st = conn.prepareStatement(query);

            st.setString(1, address);
            st.setInt(2, cityID);
            st.setString(3, postCode);
            st.setString(4, phone);
            st.setString(5, "");
            st.setInt(6, addressID);

            System.out.println(st.toString());

            st.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }

        System.out.println(addressID);
    }

    public static void updateCustomer(Integer customerID, Integer addressID, String customerName) throws SQLException {

        PreparedStatement st = null;

        try {
            openConn();
            String query = "UPDATE customer SET customerName = ?, addressId = ?, "
                    + "active = ?, "
                    + "lastUpdate = Now(), lastUpdateBy = ? "
                    + "WHERE customerId = ?";

            st = conn.prepareStatement(query);

            st.setString(1, customerName);
            st.setInt(2, addressID);
            st.setInt(3, 1);
            st.setString(4, "");
            st.setInt(5, addressID);

            System.out.println(st.toString());

            st.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }

        System.out.println(customerID);
    }

    static public void deleteCustomer(Integer id) throws SQLException {
        PreparedStatement st = null;

        try {
            openConn();
            String query = "UPDATE customer SET active = 0 WHERE customerId = ?";

            st = conn.prepareStatement(query);

            st.setInt(1, id);

            System.out.println(st.toString());

            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }

    public static void selectCustomer(int customerID) throws SQLException {

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            openConn();
            String query = "SELECT c.customerId, c.customerName, a.address, a.postalCode, a.phone, ci.city, co.country "
                    + "FROM customer c "
                    + "LEFT OUTER JOIN address a ON a.addressId = c.addressId "
                    + "LEFT OUTER JOIN city ci ON ci.cityId = a.cityId "
                    + "LEFT OUTER JOIN country co ON co.countryId = ci.countryId "
                    + "WHERE c.customerId = ?";

            st = conn.prepareStatement(query);

            st.setInt(1, customerID);

            System.out.println(st.toString());

            rs = st.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getInt("customerId"));
                selCustomer.setCustomerID(rs.getInt("customerId"));
                selCustomer.setCustomerName(rs.getString("customerName"));
                selCustomer.setCustomerAddress(rs.getString("address"));
                selCustomer.setCustomerPostCode(rs.getString("postalCode"));
                selCustomer.setCustomerPhone(rs.getString("phone"));
                selCustomer.setCustomerCity(rs.getString("city"));
                selCustomer.setCustomerCountry(rs.getString("country"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }

    public static Integer selectCity(String city, Integer countryID) throws SQLException {
        Integer cityID = 0;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            openConn();
            String query = "SELECT cityId FROM city WHERE LOWER(city) = ? AND countryId = ?";

            st = conn.prepareStatement(query);

            st.setString(1, city.toLowerCase());
            st.setInt(2, countryID);

            System.out.println(st.toString());

            rs = st.executeQuery();

            if (rs.next()) {
                cityID = rs.getInt(1);
            } else {
                query = "INSERT INTO city (cityId, city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy) "
                        + "VALUES (?, ?, ?, Now(), ?, Now(), ?)";

                st = conn.prepareStatement(query);
                cityID = getCityId();

                st.setInt(1, cityID);
                st.setString(2, city);
                st.setInt(3, countryID);
                st.setString(4, LoginController.user);
                st.setString(5, "");

                System.out.println(st.toString());

                st.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }

        System.out.println(cityID);
        return cityID;
    }

    public static Integer selectCountry(String country) throws SQLException {
        Integer countryID = 0;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            openConn();
            String query = "SELECT countryId FROM country WHERE LOWER(country) = ?";

            st = conn.prepareStatement(query);

            st.setString(1, country.toLowerCase());

            System.out.println(st.toString());

            rs = st.executeQuery();

            if (rs.next()) {
                countryID = rs.getInt(1);
            } else {
                query = "INSERT INTO country (countryId, country, createDate, createdBy, lastUpdate, lastUpdateBy) "
                        + "VALUES (?, ?, Now(), ?, Now(), ?)";

                st = conn.prepareStatement(query);
                countryID = getCountryId();

                st.setInt(1, countryID);
                st.setString(2, country);
                st.setString(3, LoginController.user);
                st.setString(4, "");

                System.out.println(st.toString());

                st.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }

        System.out.println(countryID);
        return countryID;
    }

    static public void addAppointment(String title, Customer customer, String location, String contact, Timestamp start, Timestamp end, String description) throws SQLException {
        PreparedStatement st = null;

        try {
            openConn();
            String query = "INSERT INTO appointment (appointmentId, customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, Now(), ?, Now(), ?)";

            st = conn.prepareStatement(query);

            st.setInt(1, getAppointmentId());
            st.setInt(2, customer.getCustomerID());
            st.setString(3, title);
            st.setString(4, description);
            st.setString(5, location);
            st.setString(6, contact);
            st.setString(7, "");
            st.setTimestamp(8, start);
            st.setTimestamp(9, end);
            st.setString(10, "");
            st.setString(11, "");

            System.out.println(st.toString());

            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }

    static public Boolean timeCheck(Integer appointmentID, Timestamp start, Timestamp end) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        String optional = "";
        if(appointmentID != null) {
            optional = "AND appointmentId <> ?";
        }
        try {
            openConn();
            String query = "SELECT appointmentId FROM appointment "
                    + "WHERE (? < end AND ? > start) "
                    + optional;
            st = conn.prepareStatement(query);

            st.setTimestamp(1, start);
            st.setTimestamp(2, end);
            
            if(appointmentID != null) {
                st.setInt(3, appointmentID);
            }
            System.out.println(st.toString());
            rs = st.executeQuery();
            if (rs.next()) {
                return true;
            } 
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return false;
    }

    static public void updateAppointment(Integer id, String title, Customer customer, String location, Timestamp start, Timestamp end, String description) throws SQLException {
        PreparedStatement st = null;

        try {
            openConn();
            String query = "UPDATE appointment SET customerId = ?, title = ?, "
                    + "description = ?, location = ?, "
                    + "start = ?, end = ? WHERE appointmentId = ?;";

            st = conn.prepareStatement(query);

            st.setInt(1, customer.getCustomerID());
            st.setString(2, title);
            st.setString(3, description);
            st.setString(4, location);
            st.setTimestamp(5, start);
            st.setTimestamp(6, end);
            st.setInt(7, id);

            System.out.println(st.toString());

            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }

    static public void updateAppointment(Integer id, String title, String location, Timestamp start, Timestamp end, String description) throws SQLException {
        PreparedStatement st = null;

        try {
            openConn();
            String query = "UPDATE appointment SET title = ?, "
                    + "description = ?, location = ?, "
                    + "start = ?, end = ? WHERE appointmentId = ?;";

            st = conn.prepareStatement(query);

            st.setString(1, title);
            st.setString(2, description);
            st.setString(3, location);
            st.setTimestamp(4, start);
            st.setTimestamp(5, end);
            st.setInt(6, id);

            System.out.println(st.toString());

            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }

    static public void deleteAppointment(Integer id) throws SQLException {
        PreparedStatement st = null;

        try {
            openConn();
            String query = "DELETE FROM appointment WHERE appointmentId = ?";

            st = conn.prepareStatement(query);

            st.setInt(1, id);

            System.out.println(st.toString());

            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }

    public static ObservableList getConApps(String consultant) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;

        ObservableList<Appointment> appointments = FXCollections.observableArrayList();

        try {
            openConn();
            String query = "SELECT a.appointmentId, a.description, a.location, a.title, a.start, a.end, c.customerName, a.contact "
                    + "FROM appointment a "
                    + "INNER JOIN customer c ON c.customerId = a.customerId "
                    + "WHERE a.contact = ? "
                    + "ORDER BY a.start ASC";

            st = conn.prepareStatement(query);

            st.setString(1, consultant);

            rs = st.executeQuery();

            while (rs.next()) {
                ZonedDateTime timeStart = convertToLocal(rs.getTimestamp("start"));
                ZonedDateTime timeEnd = convertToLocal(rs.getTimestamp("end"));

                appointments.add(
                        new Appointment(
                                rs.getInt("appointmentId"),
                                rs.getString("customerName"),
                                rs.getString("title"),
                                rs.getString("description"),
                                timeStart.toLocalDate().toString(),
                                timeStart.toLocalTime().toString(),
                                timeEnd.toLocalTime().toString(),
                                rs.getString("contact"),
                                rs.getString("location")
                        )
                );
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }

        return appointments;
    }

    public static ObservableList getAppByMonth(String month) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;

        ObservableList<AppByMonth> appointments = FXCollections.observableArrayList();

        try {
            openConn();
            String query = "SELECT count(appointmentId) AS count, title "
                    + "FROM appointment "
                    + "WHERE monthname(start) = ? "
                    + "GROUP BY title";

            st = conn.prepareStatement(query);

            st.setString(1, month);

            rs = st.executeQuery();

            while (rs.next()) {

                appointments.add(
                        new AppByMonth(
                                rs.getString("title"),
                                rs.getInt("count")
                        )
                );
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }

        return appointments;
    }

    public static ObservableList getAppByCon() throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;

        ObservableList<AppByCon> results = FXCollections.observableArrayList();

        try {
            openConn();
            String query = "SELECT count(appointmentId) AS count, contact "
                    + "FROM appointment "
                    + "GROUP BY contact";

            st = conn.prepareStatement(query);

            rs = st.executeQuery();

            while (rs.next()) {

                results.add(
                        new AppByCon(
                                rs.getString("contact"),
                                rs.getInt("count")
                        )
                );
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }

        return results;
    }

    public static ObservableList popConsultants() throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;

        ObservableList<String> consultants = FXCollections.observableArrayList();

        try {
            openConn();
            String query = "SELECT userName FROM user ORDER BY userName ASC";

            st = conn.prepareStatement(query);

            rs = st.executeQuery();

            while (rs.next()) {
                consultants.add(rs.getString("userName"));
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }

        return consultants;
    }

    public static int getAddressId() throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        int value = 0;
        try {
            openConn();
            String query = "SELECT MAX( addressId) +1 FROM address";
            st = conn.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                value = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return value;
    }

    public static int selectAddressID(Integer customerID) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        int value = 0;
        try {
            openConn();
            String query = "SELECT addressId FROM customer WHERE customerId = ?";
            st = conn.prepareStatement(query);

            st.setInt(1, customerID);

            rs = st.executeQuery();

            while (rs.next()) {
                value = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return value;
    }

    public static int getCountryId() throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        int value = 0;
        try {
            openConn();
            String query = "SELECT MAX( countryId) +1 FROM country";
            st = conn.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                value = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return value;
    }

    public static int getCityId() throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        int value = 0;
        try {
            openConn();
            String query = "SELECT MAX( cityId) +1 FROM city";
            st = conn.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                value = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return value;
    }

    public static int getAppointmentId() throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        int value = 0;
        try {
            openConn();
            String query = "SELECT MAX( appointmentId) +1 FROM appointment";
            st = conn.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                value = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return value;
    }

    public static int getCustomerId() throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        int value = 0;
        try {
            openConn();
            String query = "SELECT MAX( customerId) +1 FROM customer";
            st = conn.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                value = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return value;
    }

    public static Boolean validateUser(String username, String password) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        Boolean found = false;

        try {
            openConn();
            String query = "SELECT userName, password "
                    + "FROM user "
                    + "WHERE userName = ? AND password = ?";

            st = conn.prepareStatement(query);
            st.setString(1, username);
            st.setString(2, password);
            rs = st.executeQuery();

            if (rs.absolute(1)) {
                found = true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }

        return found;
    }

    public static void upcomingApp(Timestamp checkStart, Timestamp checkTo, String user) throws SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {
            openConn();
            String query = "SELECT a.title, a.start, c.customerName "
                    + "FROM appointment a "
                    + "INNER JOIN customer c ON c.customerId = a.customerId "
                    + "WHERE a.start BETWEEN ? AND ? "
                    + "AND a.contact = ?";

            st = conn.prepareStatement(query);
            st.setTimestamp(1, checkStart);
            st.setTimestamp(2, checkTo);
            st.setString(3, user);
            
            System.out.println(st.toString());
            rs = st.executeQuery();
            
            if (rs.next()) {
                String cusName = rs.getString("customerName");
                String type = rs.getString("title");  
                String time = convertToLocal(rs.getTimestamp("start")).toString();
                Alert alert = new Alert(AlertType.INFORMATION,
                        ("You have a(n) " + type + " appointment with " + cusName + " at " + time));
                alert.showAndWait();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }
    
}
