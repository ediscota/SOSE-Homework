package it.univaq.sose.eaas.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

/**
 * External policy loaded from a JSON file. The {@code rule} identifier
 * binds the policy to a Java implementation in the engine, while
 * severity, description, and parameters stay declarative.
 */
@Schema(description = "Anti-discrimination policy loaded from the policies.json configuration file")
public record Policy(
        @Schema(description = "Unique policy identifier", example = "AGE_DISCRIMINATION")
        String id,
        @Schema(description = "Human-readable policy name", example = "Age Discrimination Check")
        String name,
        @Schema(description = "Detailed description of what this policy checks")
        String description,
        @Schema(description = "Internal rule key that binds this policy to its Java engine implementation",
                example = "age-range-check")
        String rule,
        @Schema(description = "Default severity level if the policy is triggered",
                allowableValues = {"LOW", "MEDIUM", "HIGH", "CRITICAL"}, example = "HIGH")
        String severity,
        @Schema(description = "Declarative parameters passed to the rule engine (e.g. thresholds, allowed values)")
        Map<String, Object> parameters,
        @Schema(description = "Default required actions suggested when this policy triggers")
        List<String> requiredActions
) { }
