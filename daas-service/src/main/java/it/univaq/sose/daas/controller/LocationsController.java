package it.univaq.sose.daas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.univaq.sose.daas.model.LocationDTO;
import it.univaq.sose.daas.model.LocationRequest;
import it.univaq.sose.daas.service.EmploymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Locations", description = "CRUD operations on geographic locations used to tag candidates and job offers")
@RestController
@RequestMapping("/api/locations")
public class LocationsController {

    private final EmploymentService service;

    public LocationsController(EmploymentService service) {
        this.service = service;
    }

    @Operation(summary = "List all locations",
               description = "Returns all location individuals currently stored in the dataset.")
    @ApiResponse(responseCode = "200", description = "Array of locations (may be empty)")
    @GetMapping
    public List<LocationDTO> all() {
        return service.listLocations();
    }

    @Operation(summary = "Get a location by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Location found"),
            @ApiResponse(responseCode = "404", description = "No location with the given ID", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> byId(
            @Parameter(description = "Location identifier, e.g. location-1") @PathVariable String id) {
        return service.getLocation(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new location",
               description = "Inserts a new location individual into the RDF triplestore.")
    @ApiResponse(responseCode = "201",
                 description = "Location created; the Location response header contains the new resource URI")
    @PostMapping
    public ResponseEntity<LocationDTO> create(@RequestBody LocationRequest req) {
        LocationDTO created = service.createLocation(req);
        return ResponseEntity.created(URI.create("/api/locations/" + created.id())).body(created);
    }

    @Operation(summary = "Update an existing location",
               description = "Replaces the label of the location identified by {id}.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Location updated successfully"),
            @ApiResponse(responseCode = "404", description = "No location with the given ID", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> update(
            @Parameter(description = "Location identifier") @PathVariable String id,
            @RequestBody LocationRequest req) {
        return service.updateLocation(id, req)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a location",
               description = "Removes the location and all its triples from the dataset.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Location deleted"),
            @ApiResponse(responseCode = "404", description = "No location with the given ID", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Location identifier") @PathVariable String id) {
        if (service.deleteLocation(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
