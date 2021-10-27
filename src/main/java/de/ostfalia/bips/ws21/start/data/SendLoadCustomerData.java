package de.ostfalia.bips.ws21.start.data;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.HashMap;
import java.util.Map;

public class SendLoadCustomerData implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final Map<String, Object> data = new HashMap<>();
        data.put("CUSTOMER_ID", execution.getVariable("CUSTOMER_ID"));
        data.put("CUSTOMER_NAME", execution.getVariable("CUSTOMER_NAME"));
        data.put("CUSTOMER_ADDRESS", execution.getVariable("CUSTOMER_ADDRESS"));
        data.put("CUSTOMER_MAIL", execution.getVariable("CUSTOMER_MAIL"));
        data.put("DEMO_BUSINESS_KEY", execution.getVariable("DEMO_BUSINESS_KEY"));
        final RuntimeService service = execution.getProcessEngineServices().getRuntimeService();
        service.startProcessInstanceByMessage("Lade Kundendaten", data);
    }
}
