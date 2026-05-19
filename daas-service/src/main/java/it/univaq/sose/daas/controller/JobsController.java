package it.univaq.sose.daas.controller;

import it.univaq.sose.daas.model.JobOfferDTO;
import it.univaq.sose.daas.service.EmploymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobsController {

    private final EmploymentService service;

    public JobsController(EmploymentService service) {
        this.service = service;
    }

    /**
     * Search endpoint with optional filters. Maps to a multi-condition SPARQL.
     * Examples:
     *   /api/jobs
     *   /api/jobs?sector=Information%20Technology&remote=true
     *   /api/jobs?location=Milan&minSalary=40000
     */
    @GetMapping
    public List<JobOfferDTO> search(
            @RequestParam(required = false) String sector,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(required = false) Boolean remote
    ) {
        return service.listJobs(sector, location, minSalary, remote);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobOfferDTO> byId(@PathVariable String id) {
        return service.getJob(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/sector/{sector}")
    public List<JobOfferDTO> bySector(@PathVariable String sector) {
        return service.jobsBySector(sector);
    }

    @GetMapping("/location/{location}")
    public List<JobOfferDTO> byLocation(@PathVariable String location) {
        return service.jobsByLocation(location);
    }

    /**
     * Multi-condition query: jobs flagged as "risky" — they carry a discriminatory
     * proxy field (age range / gender preference / nationality requirement) OR have
     * an unverified provenance. EaaS uses this signal during evaluation.
     */
    @GetMapping("/risky")
    public List<JobOfferDTO> risky() {
        return service.riskyJobs();
    }
}
