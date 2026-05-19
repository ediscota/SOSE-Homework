package it.univaq.sose.eaas.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record AuditRecord(
        String auditId,
        Instant timestamp,
        EvaluationRequest request,
        Map<String, Object> evaluatedCandidate,
        Map<String, Object> evaluatedJob,
        String riskLevel,
        String decision,
        List<PolicyFinding> findings,
        List<String> appliedPolicies
) { }
