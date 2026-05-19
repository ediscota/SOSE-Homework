package it.univaq.sose.daas.config;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;

@Configuration
public class JenaConfig {

    private static final Logger log = LoggerFactory.getLogger(JenaConfig.class);

    @Value("${daas.dataset.path}")
    private String datasetPath;

    @Bean
    public Dataset employmentDataset(ResourceLoader resourceLoader) throws Exception {
        Resource resource = resourceLoader.getResource(datasetPath);
        Model model = ModelFactory.createDefaultModel();
        try (InputStream in = resource.getInputStream()) {
            RDFParser.source(in).lang(Lang.TURTLE).parse(model);
        }
        log.info("Loaded employment dataset from {}: {} triples", datasetPath, model.size());
        return DatasetFactory.create(model);
    }
}
