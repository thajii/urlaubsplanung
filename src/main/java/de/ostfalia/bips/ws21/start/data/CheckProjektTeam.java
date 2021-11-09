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
    	final List<String> status = new ArrayList<String>();
    	status.add("genehmigt");
    	status.add("abgelehnt");
        execution.setVariable("AVAILABLE_STATUS", Variables.objectValue(status)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());
    }
}
