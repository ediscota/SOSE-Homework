package it.univaq.sose.eaas.model;

import java.util.List;
import java.util.Map;

/**
 * External policy loaded from a JSON file. The {@code rule} identifier
 * binds the policy to a Java implementation in the engine, while
 * severity, description, and parameters stay declarative.
 */
public record Policy(
        String id,
        String name,
        String description,
        String rule,
        String severity,                 // LOW / MEDIUM / HIGH / CRITICAL
        Map<String, Object> parameters,
        List<String> requiredActions
) { }
