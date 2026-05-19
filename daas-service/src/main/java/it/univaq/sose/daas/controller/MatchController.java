package it.univaq.sose.daas.controller;

import it.univaq.sose.daas.model.MatchDTO;
import it.univaq.sose.daas.service.EmploymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    private final EmploymentService service;

    public MatchController(EmploymentService service) {
        this.service = service;
    }

    /**
     * Recommended jobs for a candidate. Combines three relations in a single SPARQL:
     * skill overlap, location compatibility, experience match.
     */
    @GetMapping("/candidate/{id}")
    public List<MatchDTO> forCandidate(@PathVariable String id) {
        return service.recommendedJobsForCandidate(id);
    }
}
