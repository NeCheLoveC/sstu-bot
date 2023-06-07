package com.example.sstubot;

import com.example.sstubot.database.model.urils.ParserManager;
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
    CommandLineRunner run(LoadEntities loadEntities, ParserManager parserManager)
    {
        return args -> {
            while(parserManager.isParserWork())
            {
                loadEntities.load();
                System.out.println("Ожидание потока");
                System.out.println("Ожидание потока");
                System.out.println("Ожидание потока");
                System.out.println("Ожидание потока");
                System.out.println("Ожидание потока");
                System.out.println("Ожидание потока");
                System.out.println("Ожидание потока");
                System.out.println("Ожидание потока");
                System.out.println("Ожидание потока");
                System.out.println("Ожидание потока");
                System.out.println("Ожидание потока");
            }
        };
    }

}
