package it.univaq.sose.daas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.univaq.sose.daas.model.SkillDTO;
import it.univaq.sose.daas.model.SkillRequest;
import it.univaq.sose.daas.service.EmploymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Skills", description = "CRUD operations on skills used to tag candidates and job offers")
@RestController
@RequestMapping("/api/skills")
public class SkillsController {

    private final EmploymentService service;

    public SkillsController(EmploymentService service) {
        this.service = service;
    }

    @Operation(summary = "List all skills",
               description = "Returns all skill individuals currently stored in the dataset.")
    @ApiResponse(responseCode = "200", description = "Array of skills (may be empty)")
    @GetMapping
    public List<SkillDTO> all() {
        return service.listSkills();
    }

    @Operation(summary = "Get a skill by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Skill found"),
            @ApiResponse(responseCode = "404", description = "No skill with the given ID", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<SkillDTO> byId(
            @Parameter(description = "Skill identifier, e.g. skill-1") @PathVariable String id) {
        return service.getSkill(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new skill",
               description = "Inserts a new skill individual into the RDF triplestore.")
    @ApiResponse(responseCode = "201",
                 description = "Skill created; the Location response header contains the new resource URI")
    @PostMapping
    public ResponseEntity<SkillDTO> create(@RequestBody SkillRequest req) {
        SkillDTO created = service.createSkill(req);
        return ResponseEntity.created(URI.create("/api/skills/" + created.id())).body(created);
    }

    @Operation(summary = "Update an existing skill",
               description = "Replaces the label of the skill identified by {id}.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Skill updated successfully"),
            @ApiResponse(responseCode = "404", description = "No skill with the given ID", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<SkillDTO> update(
            @Parameter(description = "Skill identifier") @PathVariable String id,
            @RequestBody SkillRequest req) {
        return service.updateSkill(id, req)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a skill",
               description = "Removes the skill and all its triples from the dataset.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Skill deleted"),
            @ApiResponse(responseCode = "404", description = "No skill with the given ID", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Skill identifier") @PathVariable String id) {
        if (service.deleteSkill(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
