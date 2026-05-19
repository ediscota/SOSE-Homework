package it.univaq.sose.eaas.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.univaq.sose.eaas.model.Policy;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class PolicyLoader {

    private static final Logger log = LoggerFactory.getLogger(PolicyLoader.class);

    @Value("${eaas.policies.path}")
    private String policiesPath;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Policy> policies = List.of();

    @PostConstruct
    public void load() throws Exception {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(policiesPath + "*.json");
        List<Policy> loaded = new ArrayList<>();
        for (Resource r : resources) {
            try (InputStream in = r.getInputStream()) {
                Policy p = objectMapper.readValue(in, Policy.class);
                loaded.add(p);
                log.info("Loaded policy {} ({}, severity={})", p.id(), p.name(), p.severity());
            }
        }
        loaded.sort((a, b) -> a.id().compareTo(b.id()));
        this.policies = Collections.unmodifiableList(loaded);
    }

    public List<Policy> policies() { return policies; }
}
