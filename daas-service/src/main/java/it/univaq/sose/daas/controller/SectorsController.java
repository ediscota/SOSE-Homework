package it.univaq.sose.daas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.univaq.sose.daas.model.SectorDTO;
import it.univaq.sose.daas.model.SectorRequest;
import it.univaq.sose.daas.service.EmploymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Sectors", description = "CRUD operations on industry sectors used to classify job offers")
@RestController
@RequestMapping("/api/sectors")
public class SectorsController {

    private final EmploymentService service;

    public SectorsController(EmploymentService service) {
        this.service = service;
    }

    @Operation(summary = "List all sectors",
               description = "Returns all sector individuals currently stored in the dataset.")
    @ApiResponse(responseCode = "200", description = "Array of sectors (may be empty)")
    @GetMapping
    public List<SectorDTO> all() {
        return service.listSectors();
    }

    @Operation(summary = "Get a sector by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sector found"),
            @ApiResponse(responseCode = "404", description = "No sector with the given ID", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<SectorDTO> byId(
            @Parameter(description = "Sector identifier, e.g. sector-1") @PathVariable String id) {
        return service.getSector(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new sector",
               description = "Inserts a new sector individual into the RDF triplestore.")
    @ApiResponse(responseCode = "201",
                 description = "Sector created; the Location response header contains the new resource URI")
    @PostMapping
    public ResponseEntity<SectorDTO> create(@RequestBody SectorRequest req) {
        SectorDTO created = service.createSector(req);
        return ResponseEntity.created(URI.create("/api/sectors/" + created.id())).body(created);
    }

    @Operation(summary = "Update an existing sector",
               description = "Replaces the label of the sector identified by {id}.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sector updated successfully"),
            @ApiResponse(responseCode = "404", description = "No sector with the given ID", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<SectorDTO> update(
            @Parameter(description = "Sector identifier") @PathVariable String id,
            @RequestBody SectorRequest req) {
        return service.updateSector(id, req)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a sector",
               description = "Removes the sector and all its triples from the dataset.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sector deleted"),
            @ApiResponse(responseCode = "404", description = "No sector with the given ID", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Sector identifier") @PathVariable String id) {
        if (service.deleteSector(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
