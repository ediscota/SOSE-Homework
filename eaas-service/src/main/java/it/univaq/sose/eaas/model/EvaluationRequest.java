package it.univaq.sose.eaas.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Structured request from a client/backend asking the EaaS to evaluate
 * a proposed action (e.g. showing a job recommendation to a candidate,
 * or screening a candidate for an offer).
 *
 * NOTE: the request MAY include a {@code declaredRisk} but the EaaS MUST
 * ignore it and recompute risk from the actual data fetched via DaaS.
 */
@Schema(description = "Request body for an ethical evaluation. "
        + "The engine will fetch the candidate and job offer from the DaaS independently.")
public record EvaluationRequest(
        @Schema(description = "Action being evaluated", example = "recommend-job",
                allowableValues = {"recommend-job", "screen-candidate"})
        @NotBlank String action,
        @Schema(description = "Identifier of the candidate to evaluate", example = "cand-1")
        @NotBlank String candidateId,
        @Schema(description = "Identifier of the job offer to evaluate", example = "job-3")
        @NotBlank String jobId,
        @Schema(description = "Identity of the requester (UI client, batch job, etc.)", example = "web-ui")
        String requester,
        @Schema(description = "Free-text justification for the request", example = "Checking suitability before presenting to candidate")
        String purpose,
        @Schema(description = "Ignored by the engine — risk is always recomputed from live data",
                accessMode = Schema.AccessMode.WRITE_ONLY)
        String declaredRisk
) {
}
