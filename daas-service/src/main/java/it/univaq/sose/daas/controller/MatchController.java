package it.univaq.sose.daas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.univaq.sose.daas.model.MatchDTO;
import it.univaq.sose.daas.service.EmploymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Matching", description = "SPARQL-backed job recommendation engine")
@RestController
@RequestMapping("/api/match")
public class MatchController {

    private final EmploymentService service;

    public MatchController(EmploymentService service) {
        this.service = service;
    }

    @Operation(summary = "Recommended jobs for a candidate",
               description = "Runs a multi-condition SPARQL query that combines three signals: "
                       + "(1) skill overlap between candidate and job, "
                       + "(2) location compatibility, and "
                       + "(3) years-of-experience threshold. "
                       + "Results are sorted by a composite score in descending order.")
    @ApiResponse(responseCode = "200",
                 description = "Ranked list of job matches for the candidate (may be empty if no jobs qualify)")
    @GetMapping("/candidate/{id}")
    public List<MatchDTO> forCandidate(
            @Parameter(description = "Candidate identifier, e.g. cand-1") @PathVariable String id) {
        return service.recommendedJobsForCandidate(id);
    }
}
