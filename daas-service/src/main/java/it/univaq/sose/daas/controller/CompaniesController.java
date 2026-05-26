package it.univaq.sose.daas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.univaq.sose.daas.model.CompanyDTO;
import it.univaq.sose.daas.model.CompanyRequest;
import it.univaq.sose.daas.service.EmploymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Companies", description = "CRUD operations on employer companies stored in the RDF triplestore")
@RestController
@RequestMapping("/api/companies")
public class CompaniesController {

    private final EmploymentService service;

    public CompaniesController(EmploymentService service) {
        this.service = service;
    }

    @Operation(summary = "List all companies",
               description = "Returns all company individuals currently stored in the dataset.")
    @ApiResponse(responseCode = "200", description = "Array of companies (may be empty)")
    @GetMapping
    public List<CompanyDTO> all() {
        return service.listCompanies();
    }

    @Operation(summary = "Get a company by ID",
               description = "Looks up a single company by its unique identifier (e.g. company-1).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company found"),
            @ApiResponse(responseCode = "404", description = "No company with the given ID", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> byId(
            @Parameter(description = "Company identifier, e.g. company-1") @PathVariable String id) {
        return service.getCompany(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new company",
               description = "Inserts a new company individual into the RDF triplestore.")
    @ApiResponse(responseCode = "201",
                 description = "Company created; the Location response header contains the new resource URI")
    @PostMapping
    public ResponseEntity<CompanyDTO> create(@RequestBody CompanyRequest req) {
        CompanyDTO created = service.createCompany(req);
        return ResponseEntity.created(URI.create("/api/companies/" + created.id())).body(created);
    }

    @Operation(summary = "Update an existing company",
               description = "Replaces the name and/or website of the company identified by {id}.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company updated successfully"),
            @ApiResponse(responseCode = "404", description = "No company with the given ID", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> update(
            @Parameter(description = "Company identifier") @PathVariable String id,
            @RequestBody CompanyRequest req) {
        return service.updateCompany(id, req)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a company",
               description = "Removes the company and all its triples from the dataset.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Company deleted"),
            @ApiResponse(responseCode = "404", description = "No company with the given ID", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Company identifier") @PathVariable String id) {
        if (service.deleteCompany(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
