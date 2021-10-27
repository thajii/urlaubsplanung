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

public class LoadCustomer implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final Connection connection = DatabaseConnection.getConnection();
        final PreparedStatement statement = connection.prepareStatement("SELECT * FROM kunde");
        final ResultSet resultSet = statement.executeQuery();
        final Map<Integer, String> customers = new HashMap<>();
        while(resultSet.next()) {
            customers.put(resultSet.getInt("idKunde"), resultSet.getString("name"));
        }
        customers.put(-1, "Kunden anlegen");
        execution.setVariable("AVAILABLE_CUSTOMER", Variables.objectValue(customers)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());
        resultSet.close();
        statement.close();
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
