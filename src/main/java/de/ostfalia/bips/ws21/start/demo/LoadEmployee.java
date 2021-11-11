package de.ostfalia.bips.ws21.start.demo;

import de.ostfalia.bips.ws21.start.BusinessKeyGenerator;
import de.ostfalia.bips.ws21.start.DatabaseConnection;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class LoadEmployee implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final Connection connection = DatabaseConnection.getConnection();
        final PreparedStatement statement = connection.prepareStatement("SELECT * FROM mitarbeiter");
        final ResultSet resultSet = statement.executeQuery();
        final Map<Integer, String> customers = new HashMap<>();
        while(resultSet.next()) {
            customers.put(resultSet.getInt("idM"), resultSet.getString("name"));
        }
        customers.put(-1, "Mitarbeiter anlegen");
        execution.setVariable("AVAILABLE_MITARBEITER", Variables.objectValue(customers)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());
        resultSet.close();
        statement.close();



        final PreparedStatement state = connection.prepareStatement("SELECT * FROM projekt");
        final ResultSet resultSett = state.executeQuery();
        final Map<Integer, String> projects = new HashMap<>();
        while (resultSett.next()) {
            projects.put(resultSett.getInt("idP"), resultSett.getString("name"));
        }
        execution.setVariable("AVAILABLE_PROJEKT", Variables.objectValue(projects)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());
        resultSett.close();
        state.close();
        connection.close();

        // Check business key
        if (execution.getProcessBusinessKey() == null) {
            final String key = BusinessKeyGenerator.getKey(21);
            execution.setProcessBusinessKey(key);
            execution.setVariable("DEMO_BUSINESS_KEY", key);
        } else {
            execution.setVariable("DEMO_BUSINESS_KEY", execution.getProcessBusinessKey());
        }
    }
}
