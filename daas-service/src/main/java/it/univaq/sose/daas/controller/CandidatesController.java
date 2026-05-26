package it.univaq.sose.daas.controller;

import it.univaq.sose.daas.model.CandidateDTO;
import it.univaq.sose.daas.model.CandidateRequest;
import it.univaq.sose.daas.service.EmploymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/candidates")
public class CandidatesController {

    private final EmploymentService service;

    public CandidatesController(EmploymentService service) {
        this.service = service;
    }

    @GetMapping
    public List<CandidateDTO> all() {
        return service.listCandidates();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateDTO> byId(@PathVariable String id) {
        return service.getCandidate(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CandidateDTO> create(@RequestBody CandidateRequest req) {
        CandidateDTO created = service.createCandidate(req);
        return ResponseEntity.created(URI.create("/api/candidates/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandidateDTO> update(@PathVariable String id,
                                               @RequestBody CandidateRequest req) {
        return service.updateCandidate(id, req)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.deleteCandidate(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
