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

public class CreateVacationRequest implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final Connection connection = DatabaseConnection.getConnection();
        final CallableStatement max = connection.prepareCall("SELECT MAX(idUA) FROM urlaubsAntrag");
        final ResultSet result = max.executeQuery();
        int idUA = !result.next() ? 0 : result.getInt(1) + 1;
        result.close();
        Optional<List<LocalDate>> holidayList = Optional.of(CheckHolidays.holidayList());
        LocalDate startDate = LocalDate.parse((CharSequence) execution.getVariable("VACATION_START"));
        LocalDate endDate = LocalDate.parse((CharSequence) execution.getVariable("VACATION_END"));
        int dauer = CountBusinessDays.countBusinessDaysBetween(startDate, endDate, holidayList).size();
        startDate = startDate.plusDays(1);
        endDate = endDate.plusDays(1);
        execution.setVariable("VACATION_ID", idUA);
        execution.setVariable("ANTRAGS_STATUS", "offen");
        final String sql = "INSERT INTO urlaubsantrag (idUA, idM, startDatum, endDatum, idStatus, dauer) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        final CallableStatement statement = connection.prepareCall(sql);
        statement.setInt(1, idUA);
        statement.setInt(2, (int) execution.getVariable("MITARBEITER_ID"));
        statement.setDate(3, java.sql.Date.valueOf(startDate));
        statement.setDate(4, java.sql.Date.valueOf(endDate));
        statement.setInt(5, 1);
        statement.setInt(6, dauer);
        statement.executeUpdate();
        statement.close();
        connection.close();
    }
}
