package it.univaq.sose.daas.controller;

import it.univaq.sose.daas.model.LocationDTO;
import it.univaq.sose.daas.model.LocationRequest;
import it.univaq.sose.daas.service.EmploymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationsController {

    private final EmploymentService service;

    public LocationsController(EmploymentService service) {
        this.service = service;
    }

    @GetMapping
    public List<LocationDTO> all() {
        return service.listLocations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> byId(@PathVariable String id) {
        return service.getLocation(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LocationDTO> create(@RequestBody LocationRequest req) {
        LocationDTO created = service.createLocation(req);
        return ResponseEntity.created(URI.create("/api/locations/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> update(@PathVariable String id,
                                              @RequestBody LocationRequest req) {
        return service.updateLocation(id, req)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.deleteLocation(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
