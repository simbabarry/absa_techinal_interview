package com.absabanking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication(scanBasePackages={"com.absabanking.batch","com.absabanking.listener","com.absabanking.dto","com.absabanking.rest","com.absabanking.repository","com.absabanking.model","com.absabanking.enums","com.absabanking.config","com.absabanking.service"})
public class AbsaBankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AbsaBankingApplication.class, args);
    }

}
