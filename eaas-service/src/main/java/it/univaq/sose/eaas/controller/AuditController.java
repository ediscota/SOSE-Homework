package it.univaq.sose.eaas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.univaq.sose.eaas.engine.AuditService;
import it.univaq.sose.eaas.model.AuditRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Audit Trail",
     description = "Read-only access to the immutable audit trail. "
             + "Every call to POST /api/ethics/evaluate produces one record here.")
@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @Operation(summary = "List all audit records",
               description = "Returns the complete, time-ordered list of evaluation audit records "
                       + "held in the in-memory store since the service started.")
    @ApiResponse(responseCode = "200", description = "Array of audit records (may be empty)")
    @GetMapping
    public List<AuditRecord> all() {
        return auditService.all();
    }

    @Operation(summary = "Get a single audit record by ID",
               description = "Looks up an audit record by its UUID (the auditId field returned by /evaluate).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audit record found"),
            @ApiResponse(responseCode = "404", description = "No audit record with the given ID", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<AuditRecord> byId(
            @Parameter(description = "Audit record UUID, e.g. 3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable String id) {
        AuditRecord r = auditService.get(id);
        return r == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(r);
    }
}
