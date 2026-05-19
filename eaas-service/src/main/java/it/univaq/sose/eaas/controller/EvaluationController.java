package it.univaq.sose.eaas.controller;

import it.univaq.sose.eaas.engine.PolicyLoader;
import it.univaq.sose.eaas.model.EvaluationRequest;
import it.univaq.sose.eaas.model.EvaluationResponse;
import it.univaq.sose.eaas.model.Policy;
import it.univaq.sose.eaas.service.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ethics")
public class EvaluationController {

    private final EvaluationService service;
    private final PolicyLoader loader;

    public EvaluationController(EvaluationService service, PolicyLoader loader) {
        this.service = service;
        this.loader = loader;
    }

    @PostMapping("/evaluate")
    public EvaluationResponse evaluate(@Valid @RequestBody EvaluationRequest request) {
        return service.evaluate(request);
    }

    @GetMapping("/policies")
    public List<Policy> listPolicies() {
        return loader.policies();
    }
}
