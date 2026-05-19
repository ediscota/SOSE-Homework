package it.univaq.sose.eaas.model;

import java.time.Instant;
import java.util.List;

public record EvaluationResponse(
        String auditId,
        Instant evaluatedAt,
        String action,
        String candidateId,
        String jobId,
        String riskLevel,             // LOW / MEDIUM / HIGH / CRITICAL
        String decision,              // PROCEED / REVISE / ESCALATE / REJECT
        String rationale,
        List<PolicyFinding> findings, // every triggered policy with its explanation
        List<String> appliedPolicies, // ids of all policies that were evaluated
        List<String> requiredActions, // remediation steps if not PROCEED
        Provenance provenance         // where the evaluated data came from
) {
    public record Provenance(
            String candidateSource,
            String jobSource,
            String daasEndpoint,
            Instant fetchedAt
    ) { }
}
