package io.github.arturik.actionserver.service;

import io.github.rbajek.rasa.sdk.ActionExecutor;
import io.github.rbajek.rasa.sdk.dto.ActionRequest;
import io.github.rbajek.rasa.sdk.dto.ActionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExecutionService {

    @Autowired
    private ActionExecutor executor;

    public ActionResponse executeAction(ActionRequest actionRequest) {
        return executor.run(actionRequest);
    }
}
