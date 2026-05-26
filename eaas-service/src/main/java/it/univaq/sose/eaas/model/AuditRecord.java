package it.univaq.sose.eaas.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Schema(description = "Immutable audit record created for every call to POST /api/ethics/evaluate")
public record AuditRecord(
        @Schema(description = "UUID of this audit record", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        String auditId,
        @Schema(description = "ISO-8601 timestamp at which the evaluation was recorded")
        Instant timestamp,
        @Schema(description = "The original evaluation request")
        EvaluationRequest request,
        @Schema(description = "Snapshot of the candidate data fetched from the DaaS at evaluation time")
        Map<String, Object> evaluatedCandidate,
        @Schema(description = "Snapshot of the job offer data fetched from the DaaS at evaluation time")
        Map<String, Object> evaluatedJob,
        @Schema(description = "Aggregate risk level assigned by the engine",
                allowableValues = {"LOW", "MEDIUM", "HIGH", "CRITICAL"}, example = "MEDIUM")
        String riskLevel,
        @Schema(description = "Decision produced by the engine",
                allowableValues = {"PROCEED", "REVISE", "ESCALATE", "REJECT"}, example = "REVISE")
        String decision,
        @Schema(description = "Findings for every policy that was triggered")
        List<PolicyFinding> findings,
        @Schema(description = "IDs of every policy that was evaluated")
        List<String> appliedPolicies
) { }
