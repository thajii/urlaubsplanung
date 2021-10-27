package de.ostfalia.bips.ws21.start.demo;

import de.ostfalia.bips.ws21.start.DatabaseConnection;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SetCustomerValue implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final Connection connection = DatabaseConnection.getConnection();
        final String sql = "SELECT * FROM kunde WHERE idKunde = ?";
        final PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, (int) execution.getVariable("CUSTOMER_ID"));
        final ResultSet result = statement.executeQuery();
        while(result.next()) {
            execution.setVariable("CUSTOMER_NAME", result.getString("name"));
            execution.setVariable("CUSTOMER_ADDRESS", result.getString("Adresse"));
            execution.setVariable("CUSTOMER_MAIL", result.getString("E-Mail"));
        }
        result.close();
        statement.close();
        connection.close();
    }
}
