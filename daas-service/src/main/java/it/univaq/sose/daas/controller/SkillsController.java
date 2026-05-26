package it.univaq.sose.daas.controller;

import it.univaq.sose.daas.model.SkillDTO;
import it.univaq.sose.daas.model.SkillRequest;
import it.univaq.sose.daas.service.EmploymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillsController {

    private final EmploymentService service;

    public SkillsController(EmploymentService service) {
        this.service = service;
    }

    @GetMapping
    public List<SkillDTO> all() {
        return service.listSkills();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillDTO> byId(@PathVariable String id) {
        return service.getSkill(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SkillDTO> create(@RequestBody SkillRequest req) {
        SkillDTO created = service.createSkill(req);
        return ResponseEntity.created(URI.create("/api/skills/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillDTO> update(@PathVariable String id,
                                           @RequestBody SkillRequest req) {
        return service.updateSkill(id, req)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.deleteSkill(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
