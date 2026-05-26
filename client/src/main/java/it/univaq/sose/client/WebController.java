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

    // =====================================================================
    // Home / Candidates list
    // =====================================================================

    @GetMapping("/")
    public String home(Model model) {
        List<Map<String, Object>> candidates = daas.get().uri("/api/candidates").retrieve().body(LIST_MAP);
        model.addAttribute("candidates", candidates);
        return "home";
    }

    @GetMapping("/candidates")
    public String candidates(Model model) {
        List<Map<String, Object>> candidates = daas.get().uri("/api/candidates").retrieve().body(LIST_MAP);
        model.addAttribute("candidates", candidates);
        return "home";
    }

    // =====================================================================
    // Candidate CRUD
    // =====================================================================

    @GetMapping("/candidates/new")
    public String newCandidateForm(Model model) {
        List<Map<String, Object>> locations = daas.get().uri("/api/locations").retrieve().body(LIST_MAP);
        List<Map<String, Object>> skills = daas.get().uri("/api/skills").retrieve().body(LIST_MAP);
        model.addAttribute("locations", locations);
        model.addAttribute("skills", skills);
        model.addAttribute("candidate", null);
        model.addAttribute("isEdit", false);
        return "candidate-form";
    }

    @PostMapping("/candidates/new")
    public String createCandidate(
            @RequestParam String name,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String nationality,
            @RequestParam(required = false) Boolean hasDisability,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer yearsOfExperience,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(required = false) List<String> skills,
            @RequestParam(required = false) String source) {

        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("gender", gender);
        body.put("age", age);
        body.put("nationality", nationality);
        body.put("hasDisability", hasDisability != null && hasDisability);
        body.put("location", location);
        body.put("yearsOfExperience", yearsOfExperience);
        body.put("minSalary", minSalary);
        body.put("skills", skills != null ? skills : List.of());
        body.put("source", source);

        daas.post().uri("/api/candidates").body(body).retrieve().toBodilessEntity();
        return "redirect:/";
    }

    @GetMapping("/candidates/{id}/edit")
    public String editCandidateForm(@PathVariable String id, Model model) {
        Map<String, Object> candidate = daas.get().uri("/api/candidates/{id}", id).retrieve().body(MAP);
        List<Map<String, Object>> locations = daas.get().uri("/api/locations").retrieve().body(LIST_MAP);
        List<Map<String, Object>> skills = daas.get().uri("/api/skills").retrieve().body(LIST_MAP);
        model.addAttribute("candidate", candidate);
        model.addAttribute("locations", locations);
        model.addAttribute("skills", skills);
        model.addAttribute("isEdit", true);
        return "candidate-form";
    }

    @PostMapping("/candidates/{id}/edit")
    public String updateCandidate(
            @PathVariable String id,
            @RequestParam String name,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String nationality,
            @RequestParam(required = false) Boolean hasDisability,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer yearsOfExperience,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(required = false) List<String> skills,
            @RequestParam(required = false) String source) {

        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("gender", gender);
        body.put("age", age);
        body.put("nationality", nationality);
        body.put("hasDisability", hasDisability != null && hasDisability);
        body.put("location", location);
        body.put("yearsOfExperience", yearsOfExperience);
        body.put("minSalary", minSalary);
        body.put("skills", skills != null ? skills : List.of());
        body.put("source", source);

        daas.put().uri("/api/candidates/{id}", id).body(body).retrieve().toBodilessEntity();
        return "redirect:/";
    }

    @PostMapping("/candidates/{id}/delete")
    public String deleteCandidate(@PathVariable String id) {
        daas.delete().uri("/api/candidates/{id}", id).retrieve().toBodilessEntity();
        return "redirect:/";
    }

    // =====================================================================
    // Jobs
    // =====================================================================

    @GetMapping("/jobs")
    public String jobs(Model model) {
        List<Map<String, Object>> jobs = daas.get().uri("/api/jobs").retrieve().body(LIST_MAP);
        model.addAttribute("jobs", jobs);
        return "jobs";
    }

    @GetMapping("/jobs/new")
    public String newJobForm(Model model) {
        List<Map<String, Object>> companies = daas.get().uri("/api/companies").retrieve().body(LIST_MAP);
        List<Map<String, Object>> sectors = daas.get().uri("/api/sectors").retrieve().body(LIST_MAP);
        List<Map<String, Object>> locations = daas.get().uri("/api/locations").retrieve().body(LIST_MAP);
        List<Map<String, Object>> skills = daas.get().uri("/api/skills").retrieve().body(LIST_MAP);
        model.addAttribute("companies", companies);
        model.addAttribute("sectors", sectors);
        model.addAttribute("locations", locations);
        model.addAttribute("skills", skills);
        model.addAttribute("job", null);
        model.addAttribute("isEdit", false);
        return "job-form";
    }

    @PostMapping("/jobs/new")
    public String createJob(
            @RequestParam String title,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String sector,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean remote,
            @RequestParam(required = false) Integer requiredExperience,
            @RequestParam(required = false) String seniority,
            @RequestParam(required = false) Integer salary,
            @RequestParam(required = false) String postedDate,
            @RequestParam(required = false) Boolean disabilityFriendly,
            @RequestParam(required = false) List<String> skills,
            @RequestParam(required = false) Integer ageRangeMin,
            @RequestParam(required = false) Integer ageRangeMax,
            @RequestParam(required = false) String genderPreference,
            @RequestParam(required = false) String nationalityRequirement,
            @RequestParam(required = false) String source) {

        Map<String, Object> body = buildJobBody(title, companyId, sector, location, remote,
                requiredExperience, seniority, salary, postedDate, disabilityFriendly,
                skills, ageRangeMin, ageRangeMax, genderPreference, nationalityRequirement, source);
        daas.post().uri("/api/jobs").body(body).retrieve().toBodilessEntity();
        return "redirect:/jobs";
    }

    @GetMapping("/jobs/{id}/edit")
    public String editJobForm(@PathVariable String id, Model model) {
        Map<String, Object> job = daas.get().uri("/api/jobs/{id}", id).retrieve().body(MAP);
        List<Map<String, Object>> companies = daas.get().uri("/api/companies").retrieve().body(LIST_MAP);
        List<Map<String, Object>> sectors = daas.get().uri("/api/sectors").retrieve().body(LIST_MAP);
        List<Map<String, Object>> locations = daas.get().uri("/api/locations").retrieve().body(LIST_MAP);
        List<Map<String, Object>> skills = daas.get().uri("/api/skills").retrieve().body(LIST_MAP);
        model.addAttribute("job", job);
        model.addAttribute("companies", companies);
        model.addAttribute("sectors", sectors);
        model.addAttribute("locations", locations);
        model.addAttribute("skills", skills);
        model.addAttribute("isEdit", true);
        return "job-form";
    }

    @PostMapping("/jobs/{id}/edit")
    public String updateJob(
            @PathVariable String id,
            @RequestParam String title,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String sector,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean remote,
            @RequestParam(required = false) Integer requiredExperience,
            @RequestParam(required = false) String seniority,
            @RequestParam(required = false) Integer salary,
            @RequestParam(required = false) String postedDate,
            @RequestParam(required = false) Boolean disabilityFriendly,
            @RequestParam(required = false) List<String> skills,
            @RequestParam(required = false) Integer ageRangeMin,
            @RequestParam(required = false) Integer ageRangeMax,
            @RequestParam(required = false) String genderPreference,
            @RequestParam(required = false) String nationalityRequirement,
            @RequestParam(required = false) String source) {

        Map<String, Object> body = buildJobBody(title, companyId, sector, location, remote,
                requiredExperience, seniority, salary, postedDate, disabilityFriendly,
                skills, ageRangeMin, ageRangeMax, genderPreference, nationalityRequirement, source);
        daas.put().uri("/api/jobs/{id}", id).body(body).retrieve().toBodilessEntity();
        return "redirect:/jobs";
    }

    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable String id) {
        daas.delete().uri("/api/jobs/{id}", id).retrieve().toBodilessEntity();
        return "redirect:/jobs";
    }

    private Map<String, Object> buildJobBody(String title, String companyId, String sector,
            String location, Boolean remote, Integer requiredExperience, String seniority,
            Integer salary, String postedDate, Boolean disabilityFriendly, List<String> skills,
            Integer ageRangeMin, Integer ageRangeMax, String genderPreference,
            String nationalityRequirement, String source) {
        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        body.put("companyId", companyId);
        body.put("sector", sector);
        body.put("location", location);
        body.put("remote", remote != null && remote);
        body.put("requiredExperience", requiredExperience);
        body.put("seniority", seniority);
        body.put("salary", salary);
        body.put("postedDate", postedDate);
        body.put("disabilityFriendly", disabilityFriendly != null && disabilityFriendly);
        body.put("skills", skills != null ? skills : List.of());
        body.put("ageRangeMin", ageRangeMin);
        body.put("ageRangeMax", ageRangeMax);
        body.put("genderPreference", genderPreference);
        body.put("nationalityRequirement", nationalityRequirement);
        body.put("source", source);
        return body;
    }

    // =====================================================================
    // Companies
    // =====================================================================

    @GetMapping("/companies")
    public String companies(Model model) {
        List<Map<String, Object>> companies = daas.get().uri("/api/companies").retrieve().body(LIST_MAP);
        model.addAttribute("companies", companies);
        return "companies";
    }

    @GetMapping("/companies/new")
    public String newCompanyForm(Model model) {
        model.addAttribute("company", null);
        model.addAttribute("isEdit", false);
        return "company-form";
    }

    @PostMapping("/companies/new")
    public String createCompany(
            @RequestParam String name,
            @RequestParam(required = false) String website) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("website", website);
        daas.post().uri("/api/companies").body(body).retrieve().toBodilessEntity();
        return "redirect:/companies";
    }

    @GetMapping("/companies/{id}/edit")
    public String editCompanyForm(@PathVariable String id, Model model) {
        Map<String, Object> company = daas.get().uri("/api/companies/{id}", id).retrieve().body(MAP);
        model.addAttribute("company", company);
        model.addAttribute("isEdit", true);
        return "company-form";
    }

    @PostMapping("/companies/{id}/edit")
    public String updateCompany(@PathVariable String id,
            @RequestParam String name,
            @RequestParam(required = false) String website) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("website", website);
        daas.put().uri("/api/companies/{id}", id).body(body).retrieve().toBodilessEntity();
        return "redirect:/companies";
    }

    @PostMapping("/companies/{id}/delete")
    public String deleteCompany(@PathVariable String id) {
        daas.delete().uri("/api/companies/{id}", id).retrieve().toBodilessEntity();
        return "redirect:/companies";
    }

    // =====================================================================
    // Skills
    // =====================================================================

    @GetMapping("/skills")
    public String skills(Model model) {
        List<Map<String, Object>> skills = daas.get().uri("/api/skills").retrieve().body(LIST_MAP);
        model.addAttribute("skills", skills);
        return "skills";
    }

    @GetMapping("/skills/new")
    public String newSkillForm(Model model) {
        model.addAttribute("skill", null);
        model.addAttribute("isEdit", false);
        return "skill-form";
    }

    @PostMapping("/skills/new")
    public String createSkill(@RequestParam String label) {
        Map<String, Object> body = new HashMap<>();
        body.put("label", label);
        daas.post().uri("/api/skills").body(body).retrieve().toBodilessEntity();
        return "redirect:/skills";
    }

    @GetMapping("/skills/{id}/edit")
    public String editSkillForm(@PathVariable String id, Model model) {
        Map<String, Object> skill = daas.get().uri("/api/skills/{id}", id).retrieve().body(MAP);
        model.addAttribute("skill", skill);
        model.addAttribute("isEdit", true);
        return "skill-form";
    }

    @PostMapping("/skills/{id}/edit")
    public String updateSkill(@PathVariable String id, @RequestParam String label) {
        Map<String, Object> body = new HashMap<>();
        body.put("label", label);
        daas.put().uri("/api/skills/{id}", id).body(body).retrieve().toBodilessEntity();
        return "redirect:/skills";
    }

    @PostMapping("/skills/{id}/delete")
    public String deleteSkill(@PathVariable String id) {
        daas.delete().uri("/api/skills/{id}", id).retrieve().toBodilessEntity();
        return "redirect:/skills";
    }

    // =====================================================================
    // Sectors
    // =====================================================================

    @GetMapping("/sectors")
    public String sectors(Model model) {
        List<Map<String, Object>> sectors = daas.get().uri("/api/sectors").retrieve().body(LIST_MAP);
        model.addAttribute("sectors", sectors);
        return "sectors";
    }

    @GetMapping("/sectors/new")
    public String newSectorForm(Model model) {
        model.addAttribute("sector", null);
        model.addAttribute("isEdit", false);
        return "sector-form";
    }

    @PostMapping("/sectors/new")
    public String createSector(@RequestParam String label) {
        Map<String, Object> body = new HashMap<>();
        body.put("label", label);
        daas.post().uri("/api/sectors").body(body).retrieve().toBodilessEntity();
        return "redirect:/sectors";
    }

    @GetMapping("/sectors/{id}/edit")
    public String editSectorForm(@PathVariable String id, Model model) {
        Map<String, Object> sector = daas.get().uri("/api/sectors/{id}", id).retrieve().body(MAP);
        model.addAttribute("sector", sector);
        model.addAttribute("isEdit", true);
        return "sector-form";
    }

    @PostMapping("/sectors/{id}/edit")
    public String updateSector(@PathVariable String id, @RequestParam String label) {
        Map<String, Object> body = new HashMap<>();
        body.put("label", label);
        daas.put().uri("/api/sectors/{id}", id).body(body).retrieve().toBodilessEntity();
        return "redirect:/sectors";
    }

    @PostMapping("/sectors/{id}/delete")
    public String deleteSector(@PathVariable String id) {
        daas.delete().uri("/api/sectors/{id}", id).retrieve().toBodilessEntity();
        return "redirect:/sectors";
    }

    // =====================================================================
    // Locations
    // =====================================================================

    @GetMapping("/locations")
    public String locations(Model model) {
        List<Map<String, Object>> locations = daas.get().uri("/api/locations").retrieve().body(LIST_MAP);
        model.addAttribute("locations", locations);
        return "locations";
    }

    @GetMapping("/locations/new")
    public String newLocationForm(Model model) {
        model.addAttribute("location", null);
        model.addAttribute("isEdit", false);
        return "location-form";
    }

    @PostMapping("/locations/new")
    public String createLocation(@RequestParam String label) {
        Map<String, Object> body = new HashMap<>();
        body.put("label", label);
        daas.post().uri("/api/locations").body(body).retrieve().toBodilessEntity();
        return "redirect:/locations";
    }

    @GetMapping("/locations/{id}/edit")
    public String editLocationForm(@PathVariable String id, Model model) {
        Map<String, Object> location = daas.get().uri("/api/locations/{id}", id).retrieve().body(MAP);
        model.addAttribute("location", location);
        model.addAttribute("isEdit", true);
        return "location-form";
    }

    @PostMapping("/locations/{id}/edit")
    public String updateLocation(@PathVariable String id, @RequestParam String label) {
        Map<String, Object> body = new HashMap<>();
        body.put("label", label);
        daas.put().uri("/api/locations/{id}", id).body(body).retrieve().toBodilessEntity();
        return "redirect:/locations";
    }

    @PostMapping("/locations/{id}/delete")
    public String deleteLocation(@PathVariable String id) {
        daas.delete().uri("/api/locations/{id}", id).retrieve().toBodilessEntity();
        return "redirect:/locations";
    }

    // =====================================================================
    // Existing routes (match, evaluate, audit, policies)
    // =====================================================================

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
    public String auditById(@PathVariable String id, Model model) {
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
