package de.ostfalia.bips.ws21.start.demo;

import de.ostfalia.bips.ws21.start.CheckHolidays;
import de.ostfalia.bips.ws21.start.CountBusinessDays;
import de.ostfalia.bips.ws21.start.DatabaseConnection;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CreatePriorities implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final Connection connection = DatabaseConnection.getConnection();
        
        final CallableStatement max = connection.prepareCall("SELECT MAX(idAP) FROM antragsprioritaeten");
        final ResultSet result = max.executeQuery();
        int idAP = !result.next() ? 0 : result.getInt(1) + 1;
        result.close();
        
        //Optional<List<LocalDate>> holidayList = Optional.of(CheckHolidays.holidayList());
        
        //Speichern von Start- und Enddatum des Antrags in LocalDate
        LocalDate startDate = LocalDate.parse((CharSequence) execution.getVariable("VACATION_START"));
        LocalDate endDate = LocalDate.parse((CharSequence) execution.getVariable("VACATION_END"));
        
        //int dauer = CountBusinessDays.countBusinessDaysBetween(startDate, endDate, holidayList).size();
        //startDate = startDate.plusDays(1);
        //endDate = endDate.plusDays(1);
        
        //execution.setVariable("PRIO_ID", idAP);
        //execution.setVariable("ANTRAGS_STATUS", "offen");
        
        //Schreiben des Urlaubsantrags in die DB
        final String sql = "INSERT INTO antragsprioritaeten (idAP, idUA, numDaysMin, numDaysMax, earliestDay, latestDay, prioritaeten) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        final CallableStatement statement = connection.prepareCall(sql);
        statement.setInt(1, idAP);
        statement.setInt(2, (int) execution.getVariable("MITARBEITER_ID"));
        statement.setInt(3, (int) execution.getVariable("NUM_DAYS_MIN"));
        statement.setInt(4, (int) execution.getVariable("VACATION_DAYS"));
        statement.setDate(5, java.sql.Date.valueOf(startDate));
        statement.setDate(6, java.sql.Date.valueOf(endDate));
        statement.setString(7, (String) execution.getVariable("PRIORITIES"));
        statement.executeUpdate();
        statement.close();
        connection.close();
    }
}
