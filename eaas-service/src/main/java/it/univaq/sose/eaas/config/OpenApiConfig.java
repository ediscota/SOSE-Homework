package it.univaq.sose.eaas.config;

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
                        .title("Employment EaaS API")
                        .description("""
                                Ethics-as-a-Service: policy-based ethical evaluation of job recommendations in the employment domain.
                                The engine fetches candidate and job-offer data from the DaaS, evaluates them against
                                a set of configurable anti-discrimination policies (inspired by D.Lgs. 198/2006 and L. 68/1999),
                                assigns a risk level (LOW / MEDIUM / HIGH / CRITICAL) and produces a decision
                                (PROCEED / REVISE / ESCALATE / REJECT).
                                Every evaluation is persisted in an immutable in-memory audit trail.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SoSE Midterm Group — Università dell'Aquila")
                                .url("https://github.com/SOSE-Homework"))
                        .license(new License().name("Academic use only")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Local EaaS server")));
    }
}
