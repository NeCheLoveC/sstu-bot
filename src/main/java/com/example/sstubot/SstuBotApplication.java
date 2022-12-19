package com.example.sstubot;

import com.example.sstubot.initial.InstitutesLoad;
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
    CommandLineRunner run(InstitutesLoad institutesLoad)
    {
        return args -> {
            institutesLoad.load();
        };
    }

}
