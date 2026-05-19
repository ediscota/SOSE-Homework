package it.univaq.sose.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WebController {

    private static final ParameterizedTypeReference<List<Map<String, Object>>> LIST_MAP =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<Map<String, Object>> MAP =
            new ParameterizedTypeReference<>() {};

    private final RestClient daas;
    private final RestClient eaas;

    public WebController(@Qualifier("daasClient") RestClient daas,
                         @Qualifier("eaasClient") RestClient eaas) {
        this.daas = daas;
        this.eaas = eaas;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Map<String, Object>> candidates = daas.get().uri("/api/candidates").retrieve().body(LIST_MAP);
        model.addAttribute("candidates", candidates);
        return "home";
    }

    @GetMapping("/match")
    public String match(@RequestParam String candidateId, Model model) {
        Map<String, Object> candidate = daas.get()
                .uri("/api/candidates/{id}", candidateId)
                .retrieve().body(MAP);
        List<Map<String, Object>> matches = daas.get()
                .uri("/api/match/candidate/{id}", candidateId)
                .retrieve().body(LIST_MAP);
        model.addAttribute("candidate", candidate);
        model.addAttribute("matches", matches);
        return "match";
    }

    @PostMapping("/evaluate")
    public String evaluate(@RequestParam String candidateId,
                           @RequestParam String jobId,
                           @RequestParam(required = false, defaultValue = "ui-user") String requester,
                           @RequestParam(required = false, defaultValue = "show-job-recommendation") String purpose,
                           Model model) {
        Map<String, Object> req = new HashMap<>();
        req.put("action", "recommend-job");
        req.put("candidateId", candidateId);
        req.put("jobId", jobId);
        req.put("requester", requester);
        req.put("purpose", purpose);
        // Intentionally send a declared risk to demonstrate it is ignored.
        req.put("declaredRisk", "LOW");

        Map<String, Object> response = eaas.post()
                .uri("/api/ethics/evaluate")
                .body(req)
                .retrieve()
                .body(MAP);

        Map<String, Object> candidate = daas.get()
                .uri("/api/candidates/{id}", candidateId)
                .retrieve().body(MAP);
        Map<String, Object> job = daas.get()
                .uri("/api/jobs/{id}", jobId)
                .retrieve().body(MAP);

        model.addAttribute("candidate", candidate);
        model.addAttribute("job", job);
        model.addAttribute("evaluation", response);
        model.addAttribute("request", req);
        return "evaluation";
    }

    @GetMapping("/audit")
    public String audit(Model model) {
        List<Map<String, Object>> records = eaas.get().uri("/api/audit").retrieve().body(LIST_MAP);
        model.addAttribute("records", records);
        return "audit";
    }

    @GetMapping("/audit/{id}")
    public String auditById(@org.springframework.web.bind.annotation.PathVariable String id, Model model) {
        Map<String, Object> record = eaas.get().uri("/api/audit/{id}", id).retrieve().body(MAP);
        model.addAttribute("record", record);
        return "audit-detail";
    }

    @GetMapping("/policies")
    public String policies(Model model) {
        List<Map<String, Object>> policies = eaas.get().uri("/api/ethics/policies").retrieve().body(LIST_MAP);
        model.addAttribute("policies", policies);
        return "policies";
    }
}
