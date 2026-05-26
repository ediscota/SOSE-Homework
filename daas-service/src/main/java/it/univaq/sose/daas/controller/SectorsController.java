package it.univaq.sose.daas.controller;

import it.univaq.sose.daas.model.SectorDTO;
import it.univaq.sose.daas.model.SectorRequest;
import it.univaq.sose.daas.service.EmploymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/sectors")
public class SectorsController {

    private final EmploymentService service;

    public SectorsController(EmploymentService service) {
        this.service = service;
    }

    @GetMapping
    public List<SectorDTO> all() {
        return service.listSectors();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectorDTO> byId(@PathVariable String id) {
        return service.getSector(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SectorDTO> create(@RequestBody SectorRequest req) {
        SectorDTO created = service.createSector(req);
        return ResponseEntity.created(URI.create("/api/sectors/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SectorDTO> update(@PathVariable String id,
                                            @RequestBody SectorRequest req) {
        return service.updateSector(id, req)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.deleteSector(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
