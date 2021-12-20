package de.ostfalia.bips.ws21.start.data;

import de.ostfalia.bips.ws21.start.CheckHolidays;
import de.ostfalia.bips.ws21.start.CountBusinessDays;
import de.ostfalia.bips.ws21.start.DatabaseConnection;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CreateGamsTable implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {        
        final Connection connection = DatabaseConnection.getConnection();
        final PreparedStatement statement = connection.prepareStatement("SELECT * FROM antragsprioritaeten ORDER BY idUA ASC");
        final ResultSet resultSet = statement.executeQuery();
        
        LocalDate minDate = null;
        LocalDate maxDate = null;
        int[] employee = {};
        int[] numDaysMin = {};
        int[] numDaysMax = {};
        LocalDate[] earliestDay = {};
        LocalDate[] latestDay = {};
        String[] prioritaeten = {};
        int i = 0;
        int j = 0;
              
        //Iterieren über die Prioritätsanträge
        while(resultSet.next()) {
        	//Speichern von Start- und Enddatum des kritischen Bereichs
        	LocalDate startDate = LocalDate.parse((CharSequence) resultSet.getString("earliestDay")).minusDays(1);
        	LocalDate endDate = LocalDate.parse((CharSequence) resultSet.getString("latestDay")).minusDays(1);
        	
        	if (minDate != null) {
        		if (startDate.isBefore(minDate)) {
        			minDate = startDate;
        		}
        	} else {
        		minDate = startDate;
        	}
        	if (maxDate != null) {
        		if (endDate.isAfter(maxDate)) {
        			maxDate = endDate;
        		}
        	} else {
        		maxDate = endDate;
        	}
        	
        	employee[i] = resultSet.getInt("idUA");
        	numDaysMin[i] = resultSet.getInt("numDaysMin");
        	numDaysMax[i] = resultSet.getInt("numDaysMax");
        	earliestDay[i] = startDate;
        	latestDay[i] = endDate;
        	prioritaeten[i] = resultSet.getString("prioritaeten");
        	
        	i++;
         }
        
        Optional<List<LocalDate>> holidayList = Optional.of(CheckHolidays.holidayList()); 
        List<LocalDate> days = CountBusinessDays.countBusinessDaysBetween(minDate, maxDate, holidayList);
        
        
        String createquery = "CREATE TABLE gams ( " + "employee INTEGER NOT NULL";
        	for (int k = 1; k < days.size() ; k++) {
        		String day = "day"+k;
        	    createquery += " , " + day + " INTEGER ";
        	}
        	createquery += ", PRIMARY KEY (employee));";
        
        
        final CallableStatement statementCreateGams = connection.prepareCall(createquery); 
        statementCreateGams.executeUpdate();
        statementCreateGams.close();
        
        while (j<=i) {
        	
        	
        	String sql = "INSERT INTO gams (employee";
        	 for (int k = 1; k < days.size() ; k++) {
         		String day = "day"+k;
         		sql += " , " + day;
         	 }
        	 sql += ") VALUES(?";
        	 for (int k = 1; k < days.size() ; k++) {
          		sql += " ,?";
          	 }
        	 sql += ");";
        	 
             final CallableStatement statementInsert = connection.prepareCall(sql);
             
             statementInsert.setInt(1, employee[j]);
             int firstday = days.indexOf(earliestDay[i]);
             int lastday = days.indexOf(latestDay[i]);
             
             
             String[] prioArrayEmployee = prioritaeten[j].split(",");
             int counter=0;
             
             
             for (int k = 2; k < days.size() ; k++) {
            	 if (k < firstday || k > lastday) {
            		 statementInsert.setInt(k, 0);
            	 } else {
            		 statementInsert.setInt(k, Integer.parseInt(prioArrayEmployee[counter]));
            	 }
           	 }
             statementInsert.executeUpdate();
             statementInsert.close();
        }
        connection.close();
    }
}
