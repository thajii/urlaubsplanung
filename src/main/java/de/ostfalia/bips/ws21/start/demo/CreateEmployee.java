package de.ostfalia.bips.ws21.start.demo;

import de.ostfalia.bips.ws21.start.DatabaseConnection;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

public class CreateEmployee implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final Connection connection = DatabaseConnection.getConnection();
        final CallableStatement max = connection.prepareCall("SELECT MAX(idM) FROM mitarbeiter");
        final ResultSet result = max.executeQuery();
        int id = !result.next() ? 0 : result.getInt(1) + 1;
        result.close();
        execution.setVariable("MITARBEITER_ID", id);
        final String sql = "INSERT INTO mitarbeiter (idM, name, adresse, anzahlUrlaubstage) " +
                "VALUES (?, ?, ?, ?)";
        final CallableStatement statement = connection.prepareCall(sql);
        statement.setInt(1, id);
        statement.setString(2, execution.getVariable("MITARBEITER_NAME").toString());
        statement.setString(3, execution.getVariable("MITARBEITER_ADDRESS").toString());
        statement.setString(4, execution.getVariable("MITARBEITER_URLAUBSTAGE").toString());
        statement.executeUpdate();
        statement.close();

        final CallableStatement checksAus = connection.prepareCall("SET FOREIGN_KEY_CHECKS=0");
        checksAus.execute();
        final CallableStatement maxi = connection.prepareCall("SELECT MAX(idMP) FROM projekt_has_mitarbeiter");
        final ResultSet resultt = maxi.executeQuery();
        int idMP = !resultt.next() ? 0 : resultt.getInt(1) + 1;
        resultt.close();
        
        execution.setVariable("MITARBEITER_PROJEKT_ID", idMP);
        
        final String sqlstatement = "INSERT INTO projekt_has_mitarbeiter (idMP, idP, idM) " +
                "VALUES (?, ?, ?)";
        final CallableStatement statements = connection.prepareCall(sqlstatement);
        statements.setInt(1, idMP);
        statements.setInt(2, (int) execution.getVariable("MITARBEITER_ID"));
        statements.setInt(3, (int) execution.getVariable("PROJEKT_ID"));
        statements.executeUpdate();
        statements.close();
        
        final CallableStatement checksAn = connection.prepareCall("SET FOREIGN_KEY_CHECKS=1");
        checksAn.execute();
        connection.close();
    }
}
