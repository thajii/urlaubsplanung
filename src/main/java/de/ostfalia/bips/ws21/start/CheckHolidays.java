package de.ostfalia.bips.ws21.start;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.sql.SQLException;

// Klasse, die eine Liste von LocalDates erzeugt in der sich die Feiertage aus der Datenbank befinden
public class CheckHolidays {

    public static List<LocalDate> holidayList() throws SQLException {
        // sql Abfrage um alle Einträge aus feiertag aus der DB zu erhalten
        final Connection connection = DatabaseConnection.getConnection();
        final PreparedStatement statement = connection.prepareStatement("SELECT * FROM feiertag");
        final ResultSet resultSet = statement.executeQuery();
        List<LocalDate> returnlist = new ArrayList<LocalDate>();

        // Feiertagsdaten werden in Liste geschrieben
        while (resultSet.next()) {
            LocalDate day = LocalDate.parse((CharSequence) resultSet.getString("datum"));
            returnlist.add(day);
        }

        resultSet.close();
        statement.close();
        connection.close();

        return returnlist;
        }

}
