package de.ostfalia.bips.ws21.start.data;

import de.ostfalia.bips.ws21.start.DatabaseConnection;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZoneId;


public class CheckForCriticalArea implements JavaDelegate {
    @Override
    
    public void execute(DelegateExecution execution) throws Exception {
    	 final Connection connection = DatabaseConnection.getConnection();
         final PreparedStatement statement = connection.prepareStatement("SELECT * FROM kritischerbereich");
         final ResultSet resultSet = statement.executeQuery();
         
         boolean critical = false;
         LocalDate startDate = LocalDate.parse((CharSequence) execution.getVariable("VACATION_START"));
         LocalDate endDate = LocalDate.parse((CharSequence) execution.getVariable("VACATION_END"));
         
         while(resultSet.next()) {
        	 LocalDate startCritical = LocalDate.parse((CharSequence) resultSet.getString("start"));
        	 LocalDate endCritical = LocalDate.parse((CharSequence) resultSet.getString("ende"));
        	 //Fall, dass Urlaub im kritischen Bereich endet
        	 if (startDate.isBefore(startCritical) 
        			 && endDate.isAfter(startCritical) && endDate.isBefore(endCritical)) {
        		 critical = true;
        	 }
        	 //Fall, dass Urlaub den kritischen Bereich enthält
        	 if (startDate.isBefore(startCritical) 
        			 && endDate.isAfter(endCritical)) {
        		 critical = true;
        	 }
        	 //Fall, dass Urlaub komplett im kritischen Bereich liegt
        	 if (startDate.isAfter(startCritical) && startDate.isBefore(endCritical) 
        			 && endDate.isAfter(startCritical) && endDate.isBefore(endCritical)) {
        		 critical = true;
        	 }
        	 //Fall, dass Urlaub im kritischen Bereich startet
        	 if (startDate.isAfter(startCritical) && startDate.isBefore(endCritical) 
        			 && endDate.isAfter(endCritical)) {
        		 critical = true;
        	 }
         }
        if (critical) {
        	execution.setVariable("ANTRAGS_STATUS", "zur ueberpruefung");
        	
        	final CallableStatement status = connection.prepareCall("SELECT idStatus FROM antragsstatus WHERE bezeichnung = ?");
        	status.setString(1, execution.getVariable("ANTRAGS_STATUS").toString());
        	final ResultSet result = status.executeQuery();
            int idStatus = !result.next() ? 0 : result.getInt(1);
            result.close();
            status.close();
            
            final String sql = "UPDATE urlaubsantrag SET idStatus = ? WHERE idUA = ?";
        	final CallableStatement statusSet = connection.prepareCall(sql);
        	statusSet.setInt(1, idStatus);
        	statusSet.setInt(2, (int) execution.getVariable("VACATION_ID"));
        	statusSet.executeUpdate();
        	statusSet.close();
        }
        resultSet.close();
        statement.close();
        connection.close();
    }
}
