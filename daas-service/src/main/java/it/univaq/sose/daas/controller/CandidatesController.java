package it.univaq.sose.daas.controller;

import it.univaq.sose.daas.model.CandidateDTO;
import it.univaq.sose.daas.service.EmploymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
