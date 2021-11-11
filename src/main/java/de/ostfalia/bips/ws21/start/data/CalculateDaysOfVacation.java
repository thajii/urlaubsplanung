package de.ostfalia.bips.ws21.start.data;

import de.ostfalia.bips.ws21.start.CountBusinessDays;
import de.ostfalia.bips.ws21.start.DatabaseConnection;
import de.ostfalia.bips.ws21.start.CheckHolidays;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.joda.time.Days;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class CalculateDaysOfVacation implements JavaDelegate {
    @Override
    
    public void execute(DelegateExecution execution) throws Exception {
    	final Connection connection = DatabaseConnection.getConnection();
    	execution.setVariable("ANTRAGS_STATUS", "in bearbeitung");
    	final CallableStatement status = connection.prepareCall("SELECT idStatus FROM antragsstatus WHERE bezeichnung = ?");
    	status.setString(1, execution.getVariable("ANTRAGS_STATUS").toString());
    	final ResultSet result = status.executeQuery();
        int idStatus = !result.next() ? 0 : result.getInt(1);
        result.close();
        status.close();
       
    	LocalDate startDate = LocalDate.parse((CharSequence) execution.getVariable("VACATION_START"));
    	LocalDate endDate = LocalDate.parse((CharSequence) execution.getVariable("VACATION_END"));
		Optional<List<LocalDate>> holidayList = Optional.of(CheckHolidays.holidayList());
    	long anzahl = CountBusinessDays.countBusinessDaysBetween(startDate, endDate, holidayList).size();
    	long rest = Long.valueOf((String) execution.getVariable("MITARBEITER_URLAUBSTAGE")) - anzahl;
    	execution.setVariable("VACATION_DAYS", anzahl);
    	execution.setVariable("MITARBEITER_RESTURLAUB", rest);
    	
    	final String sql = "UPDATE urlaubsantrag SET idStatus = ? WHERE idUA = ?";
    	final CallableStatement statement = connection.prepareCall(sql);
        statement.setInt(1, idStatus);
        statement.setInt(2, (int) execution.getVariable("VACATION_ID"));
        statement.executeUpdate();
        statement.close();
        connection.close();
    }
}
