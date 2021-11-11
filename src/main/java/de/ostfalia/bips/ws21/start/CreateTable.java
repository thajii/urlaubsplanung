package de.ostfalia.bips.ws21.start;

import de.ostfalia.bips.ws21.start.DatabaseConnection;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class CreateTable implements JavaDelegate {
    @Override

    public void execute(DelegateExecution execution) throws Exception {
        final Connection connection = DatabaseConnection.getConnection();
        final String sql = "SELECT idM, name  FROM mitarbeiter WHERE idM IN SELECT * FROM mitarbeiter_has_projekt WHERE idP = ?";
        final PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, (int) execution.getVariable("PROJEKT_ID"));
        final ResultSet result = statement.executeQuery();
        final Map<Integer, String> employees = new HashMap<>();
        while(result.next()) {
            employees.put(result.getInt("idM"), result.getString("name"));
        }
        result.close();
        statement.close();
        connection.close();

        for (Map.Entry<Integer, String> entry : employees.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }
}
