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
        final PreparedStatement statement = connection.prepareStatement("SELECT m.idM, m.name FROM mitarbeiter m Join projekt_has_mitarbeiter pm On m.idM = pm.idM "
        		+ "Join (SELECT idP from projekt_has_mitarbeiter Where idM = ?) p "
        		+ "On pm.idP = p.idP Where m.idM != ? Group By m.idM Order By m.idM");
        statement.setInt(1, (int) execution.getVariable("MITARBEITER_ID"));
        statement.setInt(2, (int) execution.getVariable("MITARBEITER_ID"));
        final ResultSet resultSet = statement.executeQuery();
        final Map<Integer, String> employees = new HashMap<>();
        while(resultSet.next()) {
        	employees.put(resultSet.getInt("idM"), resultSet.getString("name"));
        }
        statement.close();
        connection.close();
        execution.setVariable("PROJEKTTEAM", Variables.objectValue(employees)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());
        execution.setVariable("PROJEKTTEAM_GROESSE", employees.size());
    	
        final List<String> status = new ArrayList<String>();
    	status.add("genehmigt");
    	status.add("abgelehnt");
        execution.setVariable("AVAILABLE_STATUS", Variables.objectValue(status)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());
       
        
    }
}
