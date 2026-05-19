package it.univaq.sose.eaas.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Structured request from a client/backend asking the EaaS to evaluate
 * a proposed action (e.g. showing a job recommendation to a candidate,
 * or screening a candidate for an offer).
 *
 * NOTE: the request MAY include a {@code declaredRisk} but the EaaS MUST
 * ignore it and recompute risk from the actual data fetched via DaaS.
 */
public record EvaluationRequest(
        @NotBlank String action,        // e.g. "recommend-job", "screen-candidate"
        @NotBlank String candidateId,
        @NotBlank String jobId,
        String requester,               // who is asking (UI client, batch job, ...)
        String purpose,                 // free-text justification
        String declaredRisk             // IGNORED by the engine (never trusted)
) {
}
