package it.univaq.sose.daas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Employment DaaS API")
                        .description("""
                                Data-as-a-Service exposing the employment RDF dataset via REST.
                                Provides full CRUD on Candidates, Job Offers, Companies, Skills, Sectors and Locations,
                                backed by an Apache Jena in-memory triplestore (SPARQL 1.1).
                                The matching endpoint runs a multi-condition SPARQL query combining skill overlap,
                                location compatibility, and years-of-experience thresholds.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SoSE Midterm Group — Università dell'Aquila")
                                .url("https://github.com/SOSE-Homework"))
                        .license(new License().name("Academic use only")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Local DaaS server")));
    }
}
