package it.univaq.sose.eaas.model;

import java.util.List;

public record PolicyFinding(
        String policyId,
        String policyName,
        String severity,           // LOW / MEDIUM / HIGH / CRITICAL
        String message,            // human-readable explanation
        List<String> evidence,     // facts from the data that triggered the policy
        List<String> requiredActions
) { }
