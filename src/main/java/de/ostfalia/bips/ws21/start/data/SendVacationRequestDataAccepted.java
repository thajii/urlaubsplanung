package de.ostfalia.bips.ws21.start.data;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.HashMap;
import java.util.Map;

public class SendVacationRequestDataAccepted implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
    	execution.setVariable("ANTRAGS_STATUS", "genehmigt");
        final Map<String, Object> data = new HashMap<>();
        data.put("MITARBEITER_ID", execution.getVariable("MITARBEITER_ID"));
        data.put("MITARBEITER_NAME", execution.getVariable("MITARBEITER_NAME"));
        data.put("MITARBEITER_ADDRESS", execution.getVariable("MITARBEITER_ADDRESS"));
        data.put("MITARBEITER_URLAUBSTAGE", execution.getVariable("MITARBEITER_URLAUBSTAGE"));
        data.put("VACATION_ID", execution.getVariable("VACATION_ID"));
        data.put("VACATION_START", execution.getVariable("VACATION_START"));
        data.put("VACATION_END", execution.getVariable("VACATION_END"));
        data.put("VACATION_DAYS", execution.getVariable("VACATION_DAYS"));
        data.put("MITARBEITER_RESTURLAUB", execution.getVariable("MITARBEITER_RESTURLAUB"));
        data.put("ANTRAGS_STATUS", execution.getVariable("ANTRAGS_STATUS"));
        final String key = (String) execution.getVariable("DEMO_BUSINESS_KEY");
        final RuntimeService service = execution.getProcessEngineServices().getRuntimeService();
        service.correlateMessage("Sende Urlaubsantragsdaten", key, data);
    }
}
