package io.github.arturik.actionserver.controller;

import io.github.arturik.actionserver.service.ExecutionService;
import io.github.rbajek.rasa.sdk.dto.ActionRequest;
import io.github.rbajek.rasa.sdk.dto.ActionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ActionController {

    @Autowired
    private ExecutionService executionService;

    @PostMapping("/webhook")
    public ActionResponse executeAction(@RequestBody ActionRequest actionRequest) {
        return executionService.executeAction(actionRequest);
    }
}
