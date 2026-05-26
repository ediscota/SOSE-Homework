package it.univaq.sose.daas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.univaq.sose.daas.model.JobOfferDTO;
import it.univaq.sose.daas.model.JobOfferRequest;
import it.univaq.sose.daas.service.EmploymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Jobs", description = "CRUD operations and search on job offers stored in the RDF triplestore")
@RestController
@RequestMapping("/api/jobs")
public class JobsController {

    private final EmploymentService service;

    public JobsController(EmploymentService service) {
        this.service = service;
    }

    @Operation(summary = "Search job offers",
               description = "Returns all job offers, optionally filtered by sector, location, minimum salary "
                       + "and/or remote flag. All parameters are optional and combined with AND logic in SPARQL.")
    @ApiResponse(responseCode = "200", description = "Filtered list of job offers (may be empty)")
    @GetMapping
    public List<JobOfferDTO> search(
            @Parameter(description = "Filter by industry sector, e.g. Information Technology")
            @RequestParam(required = false) String sector,
            @Parameter(description = "Filter by city or region, e.g. Milan")
            @RequestParam(required = false) String location,
            @Parameter(description = "Minimum annual salary in EUR")
            @RequestParam(required = false) Integer minSalary,
            @Parameter(description = "When true, return only remote positions")
            @RequestParam(required = false) Boolean remote) {
        return service.listJobs(sector, location, minSalary, remote);
    }

    @Operation(summary = "Get a job offer by ID",
               description = "Looks up a single job offer by its unique identifier (e.g. job-1).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Job offer found"),
            @ApiResponse(responseCode = "404", description = "No job offer with the given ID", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<JobOfferDTO> byId(
            @Parameter(description = "Job offer identifier, e.g. job-1") @PathVariable String id) {
        return service.getJob(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "List job offers by sector",
               description = "Convenience shortcut — equivalent to GET /api/jobs?sector={sector}.")
    @ApiResponse(responseCode = "200", description = "Job offers in the given sector (may be empty)")
    @GetMapping("/sector/{sector}")
    public List<JobOfferDTO> bySector(
            @Parameter(description = "Sector label, e.g. Healthcare") @PathVariable String sector) {
        return service.jobsBySector(sector);
    }

    @Operation(summary = "List job offers by location",
               description = "Convenience shortcut — equivalent to GET /api/jobs?location={location}.")
    @ApiResponse(responseCode = "200", description = "Job offers in the given location (may be empty)")
    @GetMapping("/location/{location}")
    public List<JobOfferDTO> byLocation(
            @Parameter(description = "Location label, e.g. Rome") @PathVariable String location) {
        return service.jobsByLocation(location);
    }

    @Operation(summary = "List potentially discriminatory job offers",
               description = "Returns job offers that carry at least one controversial proxy field "
                       + "(explicit age range, gender preference, or nationality requirement) "
                       + "or have unverified provenance. The EaaS uses this signal during ethical evaluation.")
    @ApiResponse(responseCode = "200", description = "Risky job offers (may be empty)")
    @GetMapping("/risky")
    public List<JobOfferDTO> risky() {
        return service.riskyJobs();
    }

    @Operation(summary = "Create a new job offer",
               description = "Inserts a new job-offer individual into the RDF triplestore. "
                       + "The referenced company must already exist (supply its ID). "
                       + "Sector, Location and Skill values are auto-created if absent.")
    @ApiResponse(responseCode = "201",
                 description = "Job offer created; the Location response header contains the new resource URI")
    @PostMapping
    public ResponseEntity<JobOfferDTO> create(@RequestBody JobOfferRequest req) {
        JobOfferDTO created = service.createJob(req);
        return ResponseEntity.created(URI.create("/api/jobs/" + created.id())).body(created);
    }

    @Operation(summary = "Update an existing job offer",
               description = "Replaces all mutable properties of the job offer identified by {id}.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Job offer updated successfully"),
            @ApiResponse(responseCode = "404", description = "No job offer with the given ID", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<JobOfferDTO> update(
            @Parameter(description = "Job offer identifier") @PathVariable String id,
            @RequestBody JobOfferRequest req) {
        return service.updateJob(id, req)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a job offer",
               description = "Removes the job offer and all its triples from the dataset.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Job offer deleted"),
            @ApiResponse(responseCode = "404", description = "No job offer with the given ID", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Job offer identifier") @PathVariable String id) {
        if (service.deleteJob(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
