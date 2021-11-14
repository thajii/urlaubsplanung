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
        		+ "On pm.idP = p.idP Where m.idM != ? Order By m.idM) se ON se.idP = pr.idP");
        statement.setInt(1, (int) execution.getVariable("MITARBEITER_ID"));
        statement.setInt(2, (int) execution.getVariable("MITARBEITER_ID"));
        final ResultSet resultSet = statement.executeQuery();
        resultSet.last();
        final String[][] teamArray = new String[resultSet.getRow()+1][3];
        resultSet.beforeFirst();
        //final Map<Integer, String> employees = new HashMap<>();
        int i = 0;
        while(resultSet.next()) {
        	//employees.put(resultSet.getInt("idM"), resultSet.getString("name"));
        	teamArray[i][0] = String.valueOf(resultSet.getInt("idM"));
        	teamArray[i][1] = resultSet.getString("name");
        	teamArray[i][2] = resultSet.getString("projekt");
        	i++;
        }
        statement.close();
        connection.close();
        if (teamArray.length>0) {
        	execution.setVariable("MITARBEITER_1", "Mitarbeiter: " + teamArray[0][1] + " ist auch im Projekt: " + teamArray[0][2]);
        	if (teamArray.length>1) {
        		execution.setVariable("MITARBEITER_2", "Mitarbeiter: " + teamArray[1][1] + " ist auch im Projekt: " + teamArray[1][2]);
        		if (teamArray.length>2) {
        			execution.setVariable("MITARBEITER_3", "Mitarbeiter: " + teamArray[2][1] + " ist auch im Projekt: " + teamArray[1][2]);
        			if (teamArray.length>3) {
        				execution.setVariable("MITARBEITER_4", "Mitarbeiter: " + teamArray[3][1] + " ist auch im Projekt: " + teamArray[1][2]);
        				if (teamArray.length>4) {
        					execution.setVariable("MITARBEITER_5", "Mitarbeiter: " + teamArray[4][1] + " ist auch im Projekt: " + teamArray[1][2]);
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
