package de.ostfalia.bips.ws21.start.data;

import de.ostfalia.bips.ws21.start.DatabaseConnection;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class CheckForVacationOverlap implements JavaDelegate {
    @Override

    public void execute(DelegateExecution execution) throws Exception {
        final Connection connection = DatabaseConnection.getConnection();

        final PreparedStatement statement = connection.prepareStatement("SELECT startDatum, endDatum FROM urlaubsantrag WHERE idM = ? AND idStatus = 3");
        statement.setInt(1, (int) execution.getVariable("MITARBEITER_ID"));
        final ResultSet resultSetVacation = statement.executeQuery();

        //Speichern von Start- und Enddatum des aktuellen Urlaubantrags in LocalDate
        LocalDate startDate = LocalDate.parse((CharSequence) execution.getVariable("VACATION_START"));
        LocalDate endDate = LocalDate.parse((CharSequence) execution.getVariable("VACATION_END"));

        boolean check = false;
        while(resultSetVacation.next()) {
            LocalDate startGenehmigterUrlaub = LocalDate.parse((CharSequence) resultSetVacation.getString("startDatum"));
            LocalDate endGenehmigterUrlaub = LocalDate.parse((CharSequence) resultSetVacation.getString("endDatum"));

            //Fall, dass Urlaube sich überschneiden (Ende im bereits genehmigten)
            if (startDate.isBefore(startGenehmigterUrlaub)
                    && endDate.isAfter(startGenehmigterUrlaub) && endDate.isBefore(endGenehmigterUrlaub)) {
                check = true;
            }
            //Fall, dass Urlaub den bereits genehmigten enthält
            if (startDate.isBefore(startGenehmigterUrlaub)
                    && endDate.isAfter(endGenehmigterUrlaub)) {
                check = true;
            }
            //Fall, dass Urlaub komplett innerhalb eines bereits genehmigten Urlaub liegt
            if (startDate.isAfter(startGenehmigterUrlaub) && startDate.isBefore(endGenehmigterUrlaub)
                    && endDate.isAfter(startGenehmigterUrlaub) && endDate.isBefore(endGenehmigterUrlaub)) {
                check = true;
            }
            //Fall, dass Urlaube beginnt, bevor bereits genehmigter endet
            if (startDate.isAfter(startGenehmigterUrlaub) && startDate.isBefore(endGenehmigterUrlaub)
                    && endDate.isAfter(endGenehmigterUrlaub)) {
                check = true;
            }
            //Fall, dass Start oder Enddatum gleich sind
            if (startDate.equals(startGenehmigterUrlaub) || endDate.equals(endGenehmigterUrlaub)) {
                check = true;
            }
        }

        if(check) {
            execution.setVariable("ANTRAGS_STATUS", "abgelehnt");
        }
        resultSetVacation.close();
        statement.close();
        connection.close();
    }
}
