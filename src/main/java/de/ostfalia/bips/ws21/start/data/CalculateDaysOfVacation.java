package de.ostfalia.bips.ws21.start.data;

import de.ostfalia.bips.ws21.start.DatabaseConnection;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.joda.time.Days;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CalculateDaysOfVacation implements JavaDelegate {
    @Override
    
    public void execute(DelegateExecution execution) throws Exception {
    	LocalDate startDate = LocalDate.parse((CharSequence) execution.getVariable("VACATION_START"));
    	LocalDate endDate = LocalDate.parse((CharSequence) execution.getVariable("VACATION_END"));
    	//int anzahl = (int)execution.getVariable("VACATION_END") - (int)execution.getVariable("VACATION_START");
    	long anzahl = ChronoUnit.DAYS.between(startDate, endDate) + 1;
    	long rest = Long.parseLong((String) execution.getVariable("MITARBEITER_URLAUBSTAGE")) - anzahl;
    	execution.setVariable("VACATION_DAYS", anzahl);
    	execution.setVariable("MITARBEITER_RESTURLAUB", rest);
    }
}
