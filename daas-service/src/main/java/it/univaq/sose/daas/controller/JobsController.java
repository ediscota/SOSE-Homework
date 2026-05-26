package it.univaq.sose.daas.controller;

import it.univaq.sose.daas.model.JobOfferDTO;
import it.univaq.sose.daas.model.JobOfferRequest;
import it.univaq.sose.daas.service.EmploymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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

    @PostMapping
    public ResponseEntity<JobOfferDTO> create(@RequestBody JobOfferRequest req) {
        JobOfferDTO created = service.createJob(req);
        return ResponseEntity.created(URI.create("/api/jobs/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobOfferDTO> update(@PathVariable String id,
                                              @RequestBody JobOfferRequest req) {
        return service.updateJob(id, req)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.deleteJob(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
