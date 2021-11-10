package de.ostfalia.bips.ws21.start.data;

import de.ostfalia.bips.ws21.start.CountBusinessDays;
import de.ostfalia.bips.ws21.start.DatabaseConnection;
import de.ostfalia.bips.ws21.start.CheckHolidays;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.joda.time.Days;

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
    	execution.setVariable("ANTRAGS_STATUS", "in bearbeitung");
    	LocalDate startDate = LocalDate.parse((CharSequence) execution.getVariable("VACATION_START"));
    	LocalDate endDate = LocalDate.parse((CharSequence) execution.getVariable("VACATION_END"));
		Optional<List<LocalDate>> holidayList = Optional.of(CheckHolidays.holidayList());
    	long anzahl = CountBusinessDays.countBusinessDaysBetween(startDate, endDate, holidayList).size();
    	long rest = Long.valueOf((String) execution.getVariable("MITARBEITER_URLAUBSTAGE")) - anzahl;
    	execution.setVariable("VACATION_DAYS", anzahl);
    	execution.setVariable("MITARBEITER_RESTURLAUB", rest);
    }
}
