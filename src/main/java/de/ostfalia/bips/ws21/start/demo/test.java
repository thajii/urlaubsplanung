package de.ostfalia.bips.ws21.start.demo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import de.ostfalia.bips.ws21.start.CheckHolidays;
import de.ostfalia.bips.ws21.start.CountBusinessDays;

public class test {
	public static void main(String[] args) throws SQLException{ 
		LocalDate minDate = LocalDate.of(2022, 6, 1);
	    LocalDate maxDate = LocalDate.of(2022, 8, 1);
	    
		Optional<List<LocalDate>> holidayList = Optional.of(CheckHolidays.holidayList()); 
	    List<LocalDate> days = CountBusinessDays.countBusinessDaysBetween(minDate, maxDate, holidayList);
	    
		String createquery = "CREATE TABLE gams ( " + "employee INTEGER NOT NULL";
		
		for (int k = 1; k < days.size() ; k++) {
			String day = "day"+k;
		        createquery += " , " + day + " INTEGER ";
		}
		createquery += ", PRIMARY KEY (employee));";
		System.out.println("Your create query : " + createquery);
		System.out.println(days.size()-1);
	}
}
