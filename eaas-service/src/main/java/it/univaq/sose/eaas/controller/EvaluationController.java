package it.univaq.sose.eaas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.univaq.sose.eaas.engine.PolicyLoader;
import it.univaq.sose.eaas.model.EvaluationRequest;
import it.univaq.sose.eaas.model.EvaluationResponse;
import it.univaq.sose.eaas.model.Policy;
import it.univaq.sose.eaas.service.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Ethics Evaluation",
     description = "Policy-based ethical evaluation of job-recommendation actions. "
             + "Fetches live candidate and job data from the DaaS, runs the full policy engine, "
             + "and persists an immutable audit record for every call.")
@RestController
@RequestMapping("/api/ethics")
public class EvaluationController {

    private final EvaluationService service;
    private final PolicyLoader loader;

    public EvaluationController(EvaluationService service, PolicyLoader loader) {
        this.service = service;
        this.loader = loader;
    }

    @Operation(summary = "Evaluate an action for ethical compliance",
               description = "Submits a candidate–job pair for ethical evaluation. "
                       + "The engine fetches both resources from the DaaS, applies every active policy, "
                       + "assigns a risk level (LOW / MEDIUM / HIGH / CRITICAL) and returns a decision "
                       + "(PROCEED / REVISE / ESCALATE / REJECT) together with per-policy findings "
                       + "and any required remediation actions. "
                       + "Note: any declaredRisk field in the request body is ignored — risk is always recomputed.")
    @ApiResponse(responseCode = "200",
                 description = "Evaluation completed; see decision and riskLevel fields for the outcome")
    @PostMapping("/evaluate")
    public EvaluationResponse evaluate(@Valid @RequestBody EvaluationRequest request) {
        return service.evaluate(request);
    }

    @Operation(summary = "List active policies",
               description = "Returns all anti-discrimination policies currently loaded by the engine "
                       + "(loaded from the policies.json configuration file at startup).")
    @ApiResponse(responseCode = "200", description = "Array of active policies (may be empty if no file is found)")
    @GetMapping("/policies")
    public List<Policy> listPolicies() {
        return loader.policies();
    }
}
