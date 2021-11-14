package de.ostfalia.bips.ws21.start.data;

import de.ostfalia.bips.ws21.start.DatabaseConnection;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CheckProjektTeam implements JavaDelegate {
    @Override
    
    public void execute(DelegateExecution execution) throws Exception {
    	final Connection connection = DatabaseConnection.getConnection();
        final PreparedStatement statement = connection.prepareStatement("SELECT se.idM, se.name, pr.name AS projekt FROM projekt pr JOIN "
        		+ "(SELECT m.idM, m.name, pm.idP FROM mitarbeiter m Join projekt_has_mitarbeiter pm On m.idM = pm.idM "
        		+ "Join (SELECT idP from projekt_has_mitarbeiter Where idM = ?) p "
        		+ "On pm.idP = p.idP Where m.idM != ? ORDER BY m.idM) se ON se.idP = pr.idP ORDER BY se.idM");
        statement.setInt(1, (int) execution.getVariable("MITARBEITER_ID"));
        statement.setInt(2, (int) execution.getVariable("MITARBEITER_ID"));
        final ResultSet resultSet = statement.executeQuery();
        resultSet.last();
        final String[][] teamArray = new String[resultSet.getRow()+1][5];
        resultSet.beforeFirst();
        //final Map<Integer, String> employees = new HashMap<>();
        int i = 0;
        
        LocalDate startDate = LocalDate.parse((CharSequence) execution.getVariable("VACATION_START"));
        LocalDate endDate = LocalDate.parse((CharSequence) execution.getVariable("VACATION_END"));
        
        while(resultSet.next()) {
        	//employees.put(resultSet.getInt("idM"), resultSet.getString("name"));
        	if (i > 0 && teamArray[i-1][0].equals(String.valueOf(resultSet.getInt("idM")))) {
        		String projekt = resultSet.getString("projekt");
        		teamArray[i-1][2] += " , " + projekt;
        	} else {
        		teamArray[i][0] = String.valueOf(resultSet.getInt("idM"));
	        	teamArray[i][1] = resultSet.getString("name");
	        	teamArray[i][2] = resultSet.getString("projekt");
	        	
	        	final PreparedStatement statementVacation = connection.prepareStatement("SELECT startDatum, endDatum FROM urlaubsantrag WHERE idM = ? AND idStatus = 3");
	        	statementVacation.setInt(1, resultSet.getInt("idM"));
	        	final ResultSet resultSetVacation = statementVacation.executeQuery();
	        	while(resultSetVacation.next()) {
	        		LocalDate startGenehmigterUrlaub = LocalDate.parse((CharSequence) resultSetVacation.getString("startDatum"));
	           	 	LocalDate endGenehmigterUrlaub = LocalDate.parse((CharSequence) resultSetVacation.getString("endDatum"));
	        		//Fall, dass Urlaube sich zu Beginn überschneiden
		           	 if (startDate.isBefore(startGenehmigterUrlaub) 
		           			 && endDate.isAfter(startGenehmigterUrlaub) && endDate.isBefore(endGenehmigterUrlaub)) {
		           		teamArray[i][3] = startGenehmigterUrlaub.toString();
		           		teamArray[i][4] = endGenehmigterUrlaub.toString();
		           	 }
		           	 //Fall, dass Urlaub den bereits genehmigten enthält
		           	 if (startDate.isBefore(startGenehmigterUrlaub) 
		           			 && endDate.isAfter(endGenehmigterUrlaub)) {
		           		teamArray[i][3] = startGenehmigterUrlaub.toString();
		           		teamArray[i][4] = endGenehmigterUrlaub.toString();
		           	 }
		           	 //Fall, dass Urlaub komplett innerhalb eines bereits genehmigten Urlaub liegt
		           	 if (startDate.isAfter(startGenehmigterUrlaub) && startDate.isBefore(endGenehmigterUrlaub) 
		           			 && endDate.isAfter(startGenehmigterUrlaub) && endDate.isBefore(endGenehmigterUrlaub)) {
		           		teamArray[i][3] = startGenehmigterUrlaub.toString();
		           		teamArray[i][4] = endGenehmigterUrlaub.toString();
		           	 }
		           	 //Fall, dass Urlaube beginnt, bevor bereits genehmigter endet
		           	 if (startDate.isAfter(startGenehmigterUrlaub) && startDate.isBefore(endGenehmigterUrlaub) 
		           			 && endDate.isAfter(endGenehmigterUrlaub)) {
		           		teamArray[i][3] = startGenehmigterUrlaub.toString();
		           		teamArray[i][4] = endGenehmigterUrlaub.toString();
		           	 }
		           //Fall, dass Start oder Enddatum gleich sind
		           	 if (startDate.equals(startGenehmigterUrlaub) || endDate.equals(endGenehmigterUrlaub)) {
		           		teamArray[i][3] = startGenehmigterUrlaub.toString();
		           		teamArray[i][4] = endGenehmigterUrlaub.toString();
		           	 }
	        	}
	        	i++;
        	}	       	
        }
        statement.close();
        connection.close();
        if (teamArray.length>0 && teamArray[0][3] != null) {
        	execution.setVariable("MITARBEITER_1", "Mitarbeiter: " + teamArray[0][1] + " ist auch im Projekt: " + teamArray[0][2] 
        			+ " und hat bereits Urlaub vom: " + teamArray[0][3] + " bis zum: " + teamArray[0][4]);
        	if (teamArray.length>1 && teamArray[1][3] != null) {
        		execution.setVariable("MITARBEITER_2", "Mitarbeiter: " + teamArray[1][1] + " ist auch im Projekt: " + teamArray[1][2] 
        				+ " und hat bereits Urlaub vom: " + teamArray[1][3] + " bis zum: " + teamArray[1][4]);
        		if (teamArray.length>2 && teamArray[2][3] != null) {
        			execution.setVariable("MITARBEITER_3", "Mitarbeiter: " + teamArray[2][1] + " ist auch im Projekt: " + teamArray[2][2]
        					+ " und hat bereits Urlaub vom: " + teamArray[2][3] + " bis zum: " + teamArray[2][4]);
        			if (teamArray.length>3 && teamArray[3][3] != null) {
        				execution.setVariable("MITARBEITER_4", "Mitarbeiter: " + teamArray[3][1] + " ist auch im Projekt: " + teamArray[3][2]
        						+ " und hat bereits Urlaub vom: " + teamArray[3][3] + " bis zum: " + teamArray[3][4]);
        				if (teamArray.length>4 && teamArray[4][3] != null) {
        					execution.setVariable("MITARBEITER_5", "Mitarbeiter: " + teamArray[4][1] + " ist auch im Projekt: " + teamArray[4][2] 
        							+ " und hat bereits Urlaub vom: " + teamArray[4][3] + " bis zum: " + teamArray[4][4]);
        				}
        			}
        		}
        	}
        }
        
        execution.setVariable("PROJEKTTEAM", Variables.objectValue(teamArray)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());
        execution.setVariable("PROJEKTTEAM_GROESSE", i);
    	
        final List<String> status = new ArrayList<String>();
    	status.add("genehmigt");
    	status.add("abgelehnt");
        execution.setVariable("AVAILABLE_STATUS", Variables.objectValue(status)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());
       
        
    }
}
