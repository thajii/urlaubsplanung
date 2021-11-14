package de.ostfalia.bips.ws21.start.data;

import de.ostfalia.bips.ws21.start.DatabaseConnection;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class CheckForCriticalArea implements JavaDelegate {
    @Override
    
    public void execute(DelegateExecution execution) throws Exception {
    	 final Connection connection = DatabaseConnection.getConnection();
         final PreparedStatement statement = connection.prepareStatement("SELECT * FROM kritischerbereich");
         final ResultSet resultSet = statement.executeQuery();
         
         boolean critical = false;
         //Speichern von Start- und Enddatum des aktuellen Urlaubantrags in LocalDate
         LocalDate startDate = LocalDate.parse((CharSequence) execution.getVariable("VACATION_START"));
         LocalDate endDate = LocalDate.parse((CharSequence) execution.getVariable("VACATION_END"));
         
         //Iterieren über die kritischen Bereiche
         while(resultSet.next()) {
        	//Speichern von Start- und Enddatum des kritischen Bereichs
        	LocalDate startCritical = LocalDate.parse((CharSequence) resultSet.getString("start")).minusDays(1);
        	LocalDate endCritical = LocalDate.parse((CharSequence) resultSet.getString("ende")).minusDays(1);
        	
        	//Prüfung ob Urlaubszeitraum und kritischer Bereich sich schneiden
        	
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
         
         //Wenn sich Urlaubszeitraum und kritischer Bereich schneiden	
         if (critical) {
        	 //Prozessvariable für den Status des Antrags auf zur ueberprüfung setzen
        	 execution.setVariable("ANTRAGS_STATUS", "zur ueberpruefung");
        	 
        	 //Holen der StatusID aus der Datenbank 
        	 final CallableStatement status = connection.prepareCall("SELECT idStatus FROM antragsstatus WHERE bezeichnung = ?");
        	 status.setString(1, execution.getVariable("ANTRAGS_STATUS").toString());
        	 final ResultSet result = status.executeQuery();
             int idStatus = !result.next() ? 0 : result.getInt(1);
             result.close();
             status.close();
            
             //Update der StatusID des Urlaubsantrags in DB
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
