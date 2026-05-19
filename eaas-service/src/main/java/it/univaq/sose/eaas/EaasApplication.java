package it.univaq.sose.eaas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class EaasApplication {

    public static void main(String[] args) {
        SpringApplication.run(EaasApplication.class, args);
    }

    @Bean
    public RestClient daasRestClient(
            @org.springframework.beans.factory.annotation.Value("${eaas.daas.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
