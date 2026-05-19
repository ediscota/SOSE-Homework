package it.univaq.sose.eaas.engine;

import it.univaq.sose.eaas.model.EvaluationRequest;
import it.univaq.sose.eaas.model.Policy;
import it.univaq.sose.eaas.model.PolicyFinding;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Applies declarative policies (loaded from JSON) against a candidate/job pair.
 * Each policy carries a {@code rule} identifier that is dispatched to a
 * dedicated Java implementation here. Severity, parameters, and required
 * actions remain external so non-developers can edit them.
 */
@Component
public class PolicyEngine {

    private final PolicyLoader loader;

    public PolicyEngine(PolicyLoader loader) {
        this.loader = loader;
    }

    public List<PolicyFinding> evaluate(EvaluationRequest req,
                                        Map<String, Object> candidate,
                                        Map<String, Object> job) {
        List<PolicyFinding> findings = new ArrayList<>();
        for (Policy p : loader.policies()) {
            PolicyFinding finding = switch (p.rule()) {
                case "discriminatory_proxy_in_offer" -> ruleDiscriminatoryProxy(p, job);
                case "weak_provenance_or_quality"   -> ruleProvenance(p, job);
                case "disability_friendly_required" -> ruleDisability(p, candidate, job);
                case "purpose_and_requester_declared" -> ruleTransparency(p, req);
                default -> null;
            };
            if (finding != null) findings.add(finding);
        }
        return findings;
    }

    public List<String> appliedPolicyIds() {
        return loader.policies().stream().map(Policy::id).toList();
    }

    // ------------- rule implementations -------------

    private PolicyFinding ruleDiscriminatoryProxy(Policy p, Map<String, Object> job) {
        if (job == null) return null;
        @SuppressWarnings("unchecked")
        List<String> forbidden = (List<String>) p.parameters().getOrDefault("forbiddenFields", List.of());
        List<String> evidence = new ArrayList<>();
        for (String f : forbidden) {
            Object v = job.get(f);
            if (v != null) {
                evidence.add("Offer carries '" + f + "' = " + v);
            }
        }
        if (evidence.isEmpty()) return null;
        return new PolicyFinding(
                p.id(), p.name(), p.severity(),
                "The offer uses one or more discriminatory proxy fields in candidate selection.",
                evidence,
                p.requiredActions()
        );
    }

    private PolicyFinding ruleProvenance(Policy p, Map<String, Object> job) {
        if (job == null) return null;
        @SuppressWarnings("unchecked")
        List<String> trusted = (List<String>) p.parameters().getOrDefault("trustedSources", List.of());
        @SuppressWarnings("unchecked")
        List<String> required = (List<String>) p.parameters().getOrDefault("requiredJobFields", List.of());

        List<String> evidence = new ArrayList<>();
        Object src = job.get("source");
        if (src == null || !trusted.contains(String.valueOf(src))) {
            evidence.add("Offer source is '" + src + "', not in trusted sources " + trusted);
        }
        for (String f : required) {
            if (job.get(f) == null) {
                evidence.add("Required field '" + f + "' is missing");
            }
        }
        if (evidence.isEmpty()) return null;
        return new PolicyFinding(
                p.id(), p.name(), p.severity(),
                "The offer data does not meet the provenance/quality threshold.",
                evidence,
                p.requiredActions()
        );
    }

    private PolicyFinding ruleDisability(Policy p, Map<String, Object> candidate, Map<String, Object> job) {
        if (candidate == null || job == null) return null;
        Object hasDisability = candidate.get("hasDisability");
        if (!Boolean.TRUE.equals(hasDisability)) return null;

        Object friendly = job.get("disabilityFriendly");
        if (Boolean.TRUE.equals(friendly)) return null;

        List<String> evidence = new ArrayList<>();
        evidence.add("Candidate has declared a disability.");
        evidence.add("Offer is not flagged as disabilityFriendly (field=" + friendly + ").");
        return new PolicyFinding(
                p.id(), p.name(), p.severity(),
                "Disability-aware filtering is needed before showing this match.",
                evidence,
                p.requiredActions()
        );
    }

    private PolicyFinding ruleTransparency(Policy p, EvaluationRequest req) {
        List<String> evidence = new ArrayList<>();
        if (req.requester() == null || req.requester().isBlank())
            evidence.add("requester is missing.");
        if (req.purpose() == null || req.purpose().isBlank())
            evidence.add("purpose is missing.");
        if (evidence.isEmpty()) return null;
        return new PolicyFinding(
                p.id(), p.name(), p.severity(),
                "Request lacks transparency metadata.",
                evidence,
                p.requiredActions()
        );
    }
}
