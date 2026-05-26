package it.univaq.sose.daas.service;

import org.apache.jena.query.Dataset;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class DatasetPersistenceService {

    private final Path datasetPath;

    public DatasetPersistenceService(Path employmentDatasetPath) {
        this.datasetPath = employmentDatasetPath;
    }

    public void save(Dataset dataset) {
        try {
            if (datasetPath.getParent() != null) {
                Files.createDirectories(datasetPath.getParent());
            }
            Path temp = datasetPath.resolveSibling(datasetPath.getFileName() + ".tmp");
            try (OutputStream out = Files.newOutputStream(temp)) {
                RDFDataMgr.write(out, dataset.getDefaultModel(), RDFFormat.RDFXML_PRETTY);
            }
            Files.move(temp, datasetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to persist RDF dataset to " + datasetPath, e);
        }
    }
}
