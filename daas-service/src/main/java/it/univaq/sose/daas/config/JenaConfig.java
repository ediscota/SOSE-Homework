package it.univaq.sose.daas.config;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.OutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class JenaConfig {

    private static final Logger log = LoggerFactory.getLogger(JenaConfig.class);

    @Value("${daas.dataset.file:data/employment.rdf}")
    private Path datasetFile;

    @Value("${daas.dataset.seed:classpath:dataset/employment.rdf}")
    private String datasetSeed;

    @Bean
    public Path employmentDatasetPath() {
        return datasetFile;
    }

    @Bean
    public Dataset employmentDataset(ResourceLoader resourceLoader) throws Exception {
        Model model = ModelFactory.createDefaultModel();
        if (Files.exists(datasetFile)) {
            try (InputStream in = Files.newInputStream(datasetFile)) {
                RDFParser.source(in).lang(Lang.RDFXML).parse(model);
            }
            log.info("Loaded employment dataset from {}: {} triples", datasetFile, model.size());
        } else {
            Resource resource = resourceLoader.getResource(datasetSeed);
            try (InputStream in = resource.getInputStream()) {
                RDFParser.source(in).lang(Lang.RDFXML).parse(model);
            }
            if (datasetFile.getParent() != null) {
                Files.createDirectories(datasetFile.getParent());
            }
            try (OutputStream out = Files.newOutputStream(datasetFile)) {
                RDFDataMgr.write(out, model, RDFFormat.RDFXML_PRETTY);
            }
            log.info("Loaded employment dataset seed from {} and initialized {}: {} triples",
                    datasetSeed, datasetFile, model.size());
        }
        return DatasetFactory.create(model);
    }
}
