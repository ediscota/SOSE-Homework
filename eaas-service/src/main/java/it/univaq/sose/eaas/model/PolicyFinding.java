package it.univaq.sose.eaas.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Result of evaluating a single policy against a candidate–job pair")
public record PolicyFinding(
        @Schema(description = "Identifier of the policy that was triggered", example = "AGE_DISCRIMINATION")
        String policyId,
        @Schema(description = "Human-readable name of the policy", example = "Age Discrimination Check")
        String policyName,
        @Schema(description = "Severity level of this finding",
                allowableValues = {"LOW", "MEDIUM", "HIGH", "CRITICAL"}, example = "HIGH")
        String severity,
        @Schema(description = "Human-readable explanation of why this policy was triggered",
                example = "Job offer specifies an age range of 25–35 which may constitute age discrimination.")
        String message,
        @Schema(description = "Specific facts from the evaluated data that triggered the policy")
        List<String> evidence,
        @Schema(description = "Actions required to resolve this specific finding")
        List<String> requiredActions
) { }
