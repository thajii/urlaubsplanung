package de.ostfalia.bips.ws21.start.demo;

import de.ostfalia.bips.ws21.start.DatabaseConnection;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;

public class CreateVacationRequest implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final Connection connection = DatabaseConnection.getConnection();
        final CallableStatement max = connection.prepareCall("SELECT MAX(idUA) FROM urlaubsAntrag");
        final ResultSet result = max.executeQuery();
        int idUA = !result.next() ? 0 : result.getInt(1) + 1;
        result.close();
        execution.setVariable("VACATION_ID", idUA);
        execution.setVariable("ANTRAGS_STATUS", "offen");
        final String sql = "INSERT INTO urlaubsantrag (idUA, idM, startDatum, endDatum, idStatus) " +
                "VALUES (?, ?, ?, ?, ?)";
        final CallableStatement statement = connection.prepareCall(sql);
        statement.setInt(1, idUA);
        statement.setInt(2, (int) execution.getVariable("MITARBEITER_ID"));
        statement.setDate(3, java.sql.Date.valueOf(execution.getVariable("VACATION_START").toString()));
        statement.setDate(4, java.sql.Date.valueOf(execution.getVariable("VACATION_END").toString()));
        statement.setInt(5, 1);
        statement.executeUpdate();
        statement.close();
        connection.close();
    }
}
