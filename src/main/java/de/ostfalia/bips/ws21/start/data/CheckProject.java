package de.ostfalia.bips.ws21.start.data;

import de.ostfalia.bips.ws21.start.DatabaseConnection;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckProject implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final Connection connection = DatabaseConnection.getConnection();
        final PreparedStatement statement = connection.prepareStatement("SELECT * FROM projekt");
        final ResultSet resultSet = statement.executeQuery();
        final Map<Integer, String> projects = new HashMap<>();
        while (resultSet.next()) {
            projects.put(resultSet.getInt("idP"), resultSet.getString("name"));
        }
        execution.setVariable("AVAILABLE_PROJEKT", Variables.objectValue(projects)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());
        resultSet.close();
        statement.close();
        connection.close();

    }
}
