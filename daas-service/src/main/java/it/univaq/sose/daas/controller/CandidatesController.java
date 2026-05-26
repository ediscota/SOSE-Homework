package it.univaq.sose.daas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.univaq.sose.daas.model.CandidateDTO;
import it.univaq.sose.daas.model.CandidateRequest;
import it.univaq.sose.daas.service.EmploymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Candidates", description = "CRUD operations on job-seeker candidates stored in the RDF triplestore")
@RestController
@RequestMapping("/api/candidates")
public class CandidatesController {

    private final EmploymentService service;

    public CandidatesController(EmploymentService service) {
        this.service = service;
    }

    @Operation(summary = "List all candidates",
               description = "Returns the full list of candidate individuals currently stored in the dataset.")
    @ApiResponse(responseCode = "200", description = "Array of candidates (may be empty)")
    @GetMapping
    public List<CandidateDTO> all() {
        return service.listCandidates();
    }

    @Operation(summary = "Get a candidate by ID",
               description = "Looks up a single candidate by their unique identifier (e.g. cand-1).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Candidate found"),
            @ApiResponse(responseCode = "404", description = "No candidate with the given ID", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CandidateDTO> byId(
            @Parameter(description = "Candidate identifier, e.g. cand-1") @PathVariable String id) {
        return service.getCandidate(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new candidate",
               description = "Inserts a new candidate individual into the RDF triplestore. "
                       + "Referenced Location and Skill values are auto-created if they do not yet exist.")
    @ApiResponse(responseCode = "201",
                 description = "Candidate created; the Location response header contains the new resource URI")
    @PostMapping
    public ResponseEntity<CandidateDTO> create(@RequestBody CandidateRequest req) {
        CandidateDTO created = service.createCandidate(req);
        return ResponseEntity.created(URI.create("/api/candidates/" + created.id())).body(created);
    }

    @Operation(summary = "Update an existing candidate",
               description = "Replaces all mutable properties of the candidate identified by {id}.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Candidate updated successfully"),
            @ApiResponse(responseCode = "404", description = "No candidate with the given ID", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CandidateDTO> update(
            @Parameter(description = "Candidate identifier") @PathVariable String id,
            @RequestBody CandidateRequest req) {
        return service.updateCandidate(id, req)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a candidate",
               description = "Removes the candidate and all its triples from the dataset.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Candidate deleted"),
            @ApiResponse(responseCode = "404", description = "No candidate with the given ID", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Candidate identifier") @PathVariable String id) {
        if (service.deleteCandidate(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
