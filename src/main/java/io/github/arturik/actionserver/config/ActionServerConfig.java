package io.github.arturik.actionserver.config;

import io.github.rbajek.rasa.sdk.ActionExecutor;
import io.github.rbajek.rasa.sdk.action.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@ComponentScan(value = "io.github.arturik.actionserver")
public class ActionServerConfig {

    @Autowired
    List<Action> actions;

    @Bean
    public ActionExecutor rasaActionExecutor() {
        ActionExecutor executor = new ActionExecutor();
        actions.forEach(executor::registerAction);
        return executor;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
