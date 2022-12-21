package com.example.sstubot;

import com.example.sstubot.initial.InstitutesLoad;
import com.example.sstubot.initial.LoadEntities;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SstuBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(SstuBotApplication.class, args);
    }

    @Bean
    CommandLineRunner run(LoadEntities loadEntities)
    {
        return args -> {
            loadEntities.load();
        };
    }

}
