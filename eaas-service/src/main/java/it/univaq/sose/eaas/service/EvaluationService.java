package it.univaq.sose.eaas.service;

import it.univaq.sose.eaas.engine.AuditService;
import it.univaq.sose.eaas.engine.DaasClient;
import it.univaq.sose.eaas.engine.DecisionMaker;
import it.univaq.sose.eaas.engine.PolicyEngine;
import it.univaq.sose.eaas.model.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class EvaluationService {

    private final DaasClient daasClient;
    private final PolicyEngine policyEngine;
    private final DecisionMaker decisionMaker;
    private final AuditService auditService;

    public EvaluationService(DaasClient daasClient,
                             PolicyEngine policyEngine,
                             DecisionMaker decisionMaker,
                             AuditService auditService) {
        this.daasClient = daasClient;
        this.policyEngine = policyEngine;
        this.decisionMaker = decisionMaker;
        this.auditService = auditService;
    }

    public EvaluationResponse evaluate(EvaluationRequest request) {
        // 1. Fetch canonical data from the DaaS — never trust the caller.
        Map<String, Object> candidate = safeFetch(() -> daasClient.getCandidate(request.candidateId()));
        Map<String, Object> job       = safeFetch(() -> daasClient.getJob(request.jobId()));

        // 2. Case analysis: apply each loaded policy to the data + request.
        List<PolicyFinding> findings = policyEngine.evaluate(request, candidate, job);

        // 3. Governance decision: aggregate findings into risk + decision.
        DecisionMaker.Decision dec = decisionMaker.decide(findings);

        // 4. Collect remediation actions across all findings.
        List<String> requiredActions = new ArrayList<>();
        findings.forEach(f -> { if (f.requiredActions() != null) requiredActions.addAll(f.requiredActions()); });

        // 5. Persist audit record and build the response.
        String auditId = "audit-" + UUID.randomUUID();
        Instant now = Instant.now();

        AuditRecord audit = new AuditRecord(
                auditId, now, request, candidate, job,
                dec.riskLevel(), dec.decision(), findings, policyEngine.appliedPolicyIds()
        );
        auditService.save(audit);

        EvaluationResponse.Provenance prov = new EvaluationResponse.Provenance(
                candidate != null ? String.valueOf(candidate.get("source")) : null,
                job != null ? String.valueOf(job.get("source")) : null,
                "http://localhost:8081/api",
                now
        );

        return new EvaluationResponse(
                auditId, now, request.action(),
                request.candidateId(), request.jobId(),
                dec.riskLevel(), dec.decision(), dec.rationale(),
                findings,
                policyEngine.appliedPolicyIds(),
                requiredActions,
                prov
        );
    }

    private Map<String, Object> safeFetch(java.util.function.Supplier<Map<String, Object>> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return null;
        }
    }
}
