package io.github.arturik.actionserver.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.rbajek.rasa.sdk.CollectingDispatcher;
import io.github.rbajek.rasa.sdk.action.Action;
import io.github.rbajek.rasa.sdk.dto.Domain;
import io.github.rbajek.rasa.sdk.dto.Tracker;
import io.github.rbajek.rasa.sdk.dto.event.AbstractEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
public class ActionSample implements Action {

    private static final String JOKES_URI = "http://api.icndb.com/jokes/random";

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public String name() {
        return "action_joke";
    }

    @Override
    public List<AbstractEvent> run(CollectingDispatcher dispatcher, Tracker tracker, Domain domain) {

        String jokeJson = restTemplate.getForObject(JOKES_URI, String.class);
        String joke = null;
        try {
            joke = new ObjectMapper().readValue(jokeJson, JsonNode.class).at("/value/joke").asText();
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            joke = "An Irish Man walks out of a bar.";
        }
        dispatcher.utterMessage(joke);
        return Collections.emptyList();
    }

}
