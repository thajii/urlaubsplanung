package de.ostfalia.bips.ws21.start.demo;

import de.ostfalia.bips.ws21.start.DatabaseConnection;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

public class CreateVacationRequest implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final Connection connection = DatabaseConnection.getConnection();
        final CallableStatement max = connection.prepareCall("SELECT MAX(idUA) FROM mitarbeiter");
        final ResultSet result = max.executeQuery();
        int id = !result.next() ? 0 : result.getInt(1) + 1;
        result.close();
        execution.setVariable("MITARBEITER_ID", id);
        final String sql = "INSERT INTO urlaubsantrag (idUA, idM, adresse, AnzahlUrlaubstage, Projekte_idP) " +
                "VALUES (?, ?, ?, ?, ?)";
        final CallableStatement statement = connection.prepareCall(sql);
        statement.setInt(1, id);
        statement.setString(2, execution.getVariable("MITARBEITER_NAME").toString());
        statement.setString(3, execution.getVariable("MITARBEITER_ADDRESS").toString());
        statement.setString(4, execution.getVariable("MITARBEITER_URLAUBSTAGE").toString());
        statement.setString(5, execution.getVariable("MITARBEITER_PROJEKTE").toString());
        statement.executeUpdate();
        statement.close();
        connection.close();
    }
}
