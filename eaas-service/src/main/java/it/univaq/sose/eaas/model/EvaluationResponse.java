package it.univaq.sose.eaas.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Full result of an ethical evaluation, including risk level, decision, per-policy findings, and data provenance")
public record EvaluationResponse(
        @Schema(description = "UUID of the audit record persisted for this evaluation", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        String auditId,
        @Schema(description = "ISO-8601 timestamp at which the evaluation was performed")
        Instant evaluatedAt,
        @Schema(description = "Action that was evaluated", example = "recommend-job")
        String action,
        @Schema(description = "Candidate identifier that was evaluated", example = "cand-1")
        String candidateId,
        @Schema(description = "Job offer identifier that was evaluated", example = "job-3")
        String jobId,
        @Schema(description = "Aggregate risk level computed by the engine",
                allowableValues = {"LOW", "MEDIUM", "HIGH", "CRITICAL"}, example = "MEDIUM")
        String riskLevel,
        @Schema(description = "Decision produced by the engine",
                allowableValues = {"PROCEED", "REVISE", "ESCALATE", "REJECT"}, example = "REVISE")
        String decision,
        @Schema(description = "Human-readable rationale summarising how the decision was reached")
        String rationale,
        @Schema(description = "One entry per policy that was triggered (policies that passed without findings are omitted)")
        List<PolicyFinding> findings,
        @Schema(description = "IDs of every policy that was evaluated (including those that did not trigger)")
        List<String> appliedPolicies,
        @Schema(description = "Ordered list of remediation steps required before the action can proceed (empty when decision is PROCEED)")
        List<String> requiredActions,
        @Schema(description = "Provenance information about the data fetched from the DaaS")
        Provenance provenance
) {
    @Schema(description = "Where the evaluated data came from")
    public record Provenance(
            @Schema(description = "Provenance label of the candidate record", example = "linkedin-import")
            String candidateSource,
            @Schema(description = "Provenance label of the job offer record", example = "indeed-scrape")
            String jobSource,
            @Schema(description = "Base URL of the DaaS instance that was queried", example = "http://localhost:8081")
            String daasEndpoint,
            @Schema(description = "ISO-8601 timestamp at which data was fetched from the DaaS")
            Instant fetchedAt
    ) { }
}
