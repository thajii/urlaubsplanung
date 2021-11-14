package de.ostfalia.bips.ws21.start;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // The part '?serverTimezone=UTC&useLegacyDatetimeCode=false' fix the error: InvalidConnectionAttributeException:
    // The server time zone value 'CEST' is unrecognized or represents more than one time zone. You must configure
    // either the server or JDBC driver (via the 'serverTimezone' configuration property) to use a more specifc time
    // zone value if you want to utilize time zone support.
    public static final String URL = "jdbc:mysql://localhost:3306/vacationplanner?serverTimezone=UTC&useLegacyDatetimeCode=false";
    public static final String USER = "root";
    public static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
