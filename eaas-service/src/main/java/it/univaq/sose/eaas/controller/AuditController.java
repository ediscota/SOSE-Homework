package it.univaq.sose.eaas.controller;

import it.univaq.sose.eaas.engine.AuditService;
import it.univaq.sose.eaas.model.AuditRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public List<AuditRecord> all() {
        return auditService.all();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditRecord> byId(@PathVariable String id) {
        AuditRecord r = auditService.get(id);
        return r == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(r);
    }
}
