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
        final String sql = "SELECT * FROM mitarbeiter WHERE idM = ?";
        final PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, (int) execution.getVariable("MITARBEITER_ID"));
        final ResultSet result = statement.executeQuery();
        while(result.next()) {
            execution.setVariable("MITARBEITER_NAME", result.getString("name"));
            execution.setVariable("MITARBEITER_ADDRESS", result.getString("adresse"));
            execution.setVariable("MITARBEITER_URLAUBSTAGE", result.getString("AnzahlUrlaubstage"));
            execution.setVariable("MITARBEITER_PROJEKTE", result.getString("Projekte_idP"));
        }
        result.close();
        statement.close();
        connection.close();
    }
}
