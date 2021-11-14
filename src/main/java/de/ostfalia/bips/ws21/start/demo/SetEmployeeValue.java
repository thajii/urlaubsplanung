package de.ostfalia.bips.ws21.start.demo;

import de.ostfalia.bips.ws21.start.DatabaseConnection;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SetEmployeeValue implements JavaDelegate {
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
            execution.setVariable("MITARBEITER_URLAUBSTAGE", result.getString("anzahlUrlaubstage"));
        }
        result.close();
        statement.close();
        
        final PreparedStatement statementSum = connection.prepareStatement("SELECT SUM(dauer) FROM urlaubsantrag WHERE idM = ? AND (idStatus < 3 OR idStatus > 4)");
        statementSum.setInt(1, (int) execution.getVariable("MITARBEITER_ID"));
        final ResultSet resultSum = statementSum.executeQuery();
        int sumOffeneTage = !resultSum.next() ? 0 : resultSum.getInt(1);
        execution.setVariable("MITARBEITER_URLAUBSTAGE_OFFEN", sumOffeneTage);
        resultSum.close();
        statementSum.close();
        connection.close();
    }
}
