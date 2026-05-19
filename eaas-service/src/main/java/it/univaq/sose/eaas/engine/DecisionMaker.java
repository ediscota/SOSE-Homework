package it.univaq.sose.eaas.engine;

import it.univaq.sose.eaas.model.PolicyFinding;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Pure governance step: given the findings produced by the policy engine
 * (case analysis), decide the final action (governance decision). Kept
 * separate so that the same findings could feed alternative decision
 * strategies (e.g. stricter for sensitive sectors).
 */
@Component
public class DecisionMaker {

    public record Decision(String riskLevel, String decision, String rationale) { }

    public Decision decide(List<PolicyFinding> findings) {
        if (findings.isEmpty()) {
            return new Decision("LOW", "PROCEED",
                    "No policy was triggered: the recommendation is consistent with the active ethical policies.");
        }
        boolean critical = findings.stream().anyMatch(f -> "CRITICAL".equalsIgnoreCase(f.severity()));
        boolean high     = findings.stream().anyMatch(f -> "HIGH".equalsIgnoreCase(f.severity()));
        boolean medium   = findings.stream().anyMatch(f -> "MEDIUM".equalsIgnoreCase(f.severity()));

        if (critical) {
            return new Decision("CRITICAL", "REJECT",
                    "At least one CRITICAL policy was violated. The action is blocked: " + summary(findings));
        }
        if (high) {
            return new Decision("HIGH", "ESCALATE",
                    "At least one HIGH-severity policy was triggered. Human review is required: " + summary(findings));
        }
        if (medium) {
            return new Decision("MEDIUM", "REVISE",
                    "Medium-severity issues require remediation before the action proceeds: " + summary(findings));
        }
        return new Decision("LOW", "PROCEED",
                "Only LOW-severity advisories were raised. The action may proceed with the listed required actions applied.");
    }

    private String summary(List<PolicyFinding> findings) {
        return findings.stream().map(f -> f.policyId() + "/" + f.severity()).toList().toString();
    }
}
