package it.univaq.sose.daas.controller;

import it.univaq.sose.daas.model.CompanyDTO;
import it.univaq.sose.daas.model.CompanyRequest;
import it.univaq.sose.daas.service.EmploymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompaniesController {

    private final EmploymentService service;

    public CompaniesController(EmploymentService service) {
        this.service = service;
    }

    @GetMapping
    public List<CompanyDTO> all() {
        return service.listCompanies();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> byId(@PathVariable String id) {
        return service.getCompany(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CompanyDTO> create(@RequestBody CompanyRequest req) {
        CompanyDTO created = service.createCompany(req);
        return ResponseEntity.created(URI.create("/api/companies/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> update(@PathVariable String id,
                                             @RequestBody CompanyRequest req) {
        return service.updateCompany(id, req)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.deleteCompany(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
