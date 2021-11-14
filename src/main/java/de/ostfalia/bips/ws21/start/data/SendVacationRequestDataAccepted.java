package de.ostfalia.bips.ws21.start.data;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.ostfalia.bips.ws21.start.DatabaseConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class SendVacationRequestDataAccepted implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
    	final Connection connection = DatabaseConnection.getConnection();
    	
    	//Setzen der Prozesvariable zum Antragssatus auf abgelehnt und holen der passenden StatusID aus der DB
    	execution.setVariable("ANTRAGS_STATUS", "genehmigt");
        final CallableStatement statusUrlaub = connection.prepareCall("SELECT anzahlUrlaubstage FROM mitarbeiter WHERE idM = ?");
        statusUrlaub.setString(1, execution.getVariable("MITARBEITER_ID").toString());
        final ResultSet resultUrlaub = statusUrlaub.executeQuery();
        int urlaubsTage = !resultUrlaub.next() ? 0 : resultUrlaub.getInt(1);
        resultUrlaub.close();
        statusUrlaub.close();

        execution.setVariable("MITARBEITER_URLAUBSTAGE", urlaubsTage);
    	final CallableStatement status = connection.prepareCall("SELECT idStatus FROM antragsstatus WHERE bezeichnung = ?");
    	status.setString(1, execution.getVariable("ANTRAGS_STATUS").toString());
    	final ResultSet result = status.executeQuery();
        int idStatus = !result.next() ? 0 : result.getInt(1);
        result.close();
        status.close();
        
        final Map<String, Object> data = new HashMap<>();
        data.put("MITARBEITER_ID", execution.getVariable("MITARBEITER_ID"));
        data.put("MITARBEITER_NAME", execution.getVariable("MITARBEITER_NAME"));
        data.put("MITARBEITER_ADDRESS", execution.getVariable("MITARBEITER_ADDRESS"));
        data.put("MITARBEITER_URLAUBSTAGE", execution.getVariable("MITARBEITER_URLAUBSTAGE"));
        data.put("VACATION_ID", execution.getVariable("VACATION_ID"));
        data.put("VACATION_START", execution.getVariable("VACATION_START"));
        data.put("VACATION_END", execution.getVariable("VACATION_END"));
        data.put("VACATION_DAYS", execution.getVariable("VACATION_DAYS"));
        data.put("MITARBEITER_RESTURLAUB", ((long) execution.getVariable("MITARBEITER_RESTURLAUB")));
        data.put("ANTRAGS_STATUS", execution.getVariable("ANTRAGS_STATUS"));
        final String key = (String) execution.getVariable("DEMO_BUSINESS_KEY");
        
        //Update der StatusID des Urlaubsantrags in DB
        final String sql = "UPDATE urlaubsantrag SET idStatus = ? WHERE idUA = ?";
    	final CallableStatement statement = connection.prepareCall(sql);
        statement.setInt(1, idStatus);
        statement.setInt(2, (int) execution.getVariable("VACATION_ID"));
        statement.executeUpdate();
        statement.close();
        
        //Update der Urlaubstage in der DB
        int idM = (int) execution.getVariable("MITARBEITER_ID");
        final String sqlDays = "UPDATE mitarbeiter SET anzahlUrlaubstage = ? WHERE idM = ?";
        final CallableStatement statementDays = connection.prepareCall(sqlDays);
        statementDays.setInt(1, (int) (long) execution.getVariable("MITARBEITER_RESTURLAUB"));
        statementDays.setInt(2, idM);
        statementDays.executeUpdate();
        statementDays.close();
        connection.close();
        
        final RuntimeService service = execution.getProcessEngineServices().getRuntimeService();
        service.correlateMessage("Sende Urlaubsantragsdaten", key, data);
    }
}
