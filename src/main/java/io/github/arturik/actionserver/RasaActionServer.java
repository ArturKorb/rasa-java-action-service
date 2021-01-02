package io.github.arturik.actionserver;

import io.github.arturik.actionserver.config.ActionServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@Import({ActionServerConfig.class})
public class RasaActionServer {

    public static void main(String[] args) {
        SpringApplication.run(RasaActionServer.class, args);
    }
}
