package sber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("sber")
@EnableAutoConfiguration
public class BankServer {
    public static void main(String[] args) {
        SpringApplication.run(BankServer.class, args);
    }
}
