package it.univaq.sose.eaas.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.univaq.sose.eaas.model.AuditRecord;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Append-only audit trail. Records are stored both in memory (for fast lookup
 * during a session) and on disk as JSONL so that an external auditor can
 * inspect them after the fact.
 */
@Component
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    @Value("${eaas.audit.file}")
    private String auditFile;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final ConcurrentMap<String, AuditRecord> records = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws IOException {
        Path p = Paths.get(auditFile);
        if (p.getParent() != null) Files.createDirectories(p.getParent());
        if (!Files.exists(p)) Files.createFile(p);
        log.info("Audit trail file: {}", p.toAbsolutePath());
    }

    public void save(AuditRecord record) {
        records.put(record.auditId(), record);
        try {
            String line = objectMapper.writeValueAsString(record) + System.lineSeparator();
            Files.writeString(Paths.get(auditFile), line,
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            log.error("Failed to persist audit record {}: {}", record.auditId(), e.getMessage());
        }
    }

    public AuditRecord get(String id) {
        return records.get(id);
    }

    public List<AuditRecord> all() {
        return new ArrayList<>(records.values());
    }
}
