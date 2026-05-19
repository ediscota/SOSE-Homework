package it.univaq.sose.daas.service;

import it.univaq.sose.daas.model.CandidateDTO;
import it.univaq.sose.daas.model.JobOfferDTO;
import it.univaq.sose.daas.model.MatchDTO;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service that runs SPARQL queries against the in-memory employment dataset
 * and adapts the results into typed DTOs.
 *
 * Each public method corresponds to a REST endpoint exposed by the DaaS.
 */
@Service
public class EmploymentService {

    private static final String PREFIXES = """
            PREFIX rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            PREFIX rdfs:   <http://www.w3.org/2000/01/rdf-schema#>
            PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>
            PREFIX foaf:   <http://xmlns.com/foaf/0.1/>
            PREFIX dct:    <http://purl.org/dc/terms/>
            PREFIX schema: <http://schema.org/>
            PREFIX emp:    <http://sose.univaq.it/employment#>
            """;

    private final Dataset dataset;

    public EmploymentService(Dataset employmentDataset) {
        this.dataset = employmentDataset;
    }

    // ---------------- Candidates ----------------

    public List<CandidateDTO> listCandidates() {
        return runCandidateQuery(null);
    }

    public Optional<CandidateDTO> getCandidate(String id) {
        List<CandidateDTO> r = runCandidateQuery("emp:" + id);
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    private List<CandidateDTO> runCandidateQuery(String iriFilter) {
        String q = PREFIXES + """
                SELECT ?c ?name ?gender ?age ?nat ?disab ?loc ?yoe ?minSal ?src
                       (GROUP_CONCAT(DISTINCT ?skillLabel; SEPARATOR=",") AS ?skills)
                WHERE {
                  ?c a emp:Candidate ;
                     foaf:name ?name .
                  OPTIONAL { ?c foaf:gender ?gender }
                  OPTIONAL { ?c foaf:age ?age }
                  OPTIONAL { ?c schema:nationality ?nat }
                  OPTIONAL { ?c emp:hasDisability ?disab }
                  OPTIONAL { ?c emp:hasLocation ?locRes . ?locRes rdfs:label ?loc }
                  OPTIONAL { ?c emp:yearsOfExperience ?yoe }
                  OPTIONAL { ?c emp:minSalary ?minSal }
                  OPTIONAL { ?c dct:source ?src }
                  OPTIONAL { ?c emp:hasSkill ?skill . ?skill rdfs:label ?skillLabel }
                  %s
                }
                GROUP BY ?c ?name ?gender ?age ?nat ?disab ?loc ?yoe ?minSal ?src
                ORDER BY ?c
                """.formatted(iriFilter != null ? "FILTER(?c = " + iriFilter + ")" : "");

        List<CandidateDTO> out = new ArrayList<>();
        try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(q), dataset)) {
            ResultSet rs = qe.execSelect();
            while (rs.hasNext()) {
                QuerySolution s = rs.next();
                out.add(new CandidateDTO(
                        localName(s.get("c")),
                        str(s.get("name")),
                        str(s.get("gender")),
                        intVal(s.get("age")),
                        str(s.get("nat")),
                        boolVal(s.get("disab")),
                        str(s.get("loc")),
                        intVal(s.get("yoe")),
                        intVal(s.get("minSal")),
                        splitSkills(str(s.get("skills"))),
                        str(s.get("src"))
                ));
            }
        }
        return out;
    }

    // ---------------- Job offers ----------------

    public List<JobOfferDTO> listJobs(String sector, String location, Integer minSalary, Boolean remote) {
        StringBuilder filters = new StringBuilder();
        if (sector != null) {
            filters.append("  ?secRes rdfs:label ?secFilter . FILTER(LCASE(STR(?secFilter)) = LCASE(\"")
                    .append(sector).append("\")) . ?j emp:hasSector ?secRes .\n");
        }
        if (location != null) {
            filters.append("  ?locRes rdfs:label ?locFilter . FILTER(LCASE(STR(?locFilter)) = LCASE(\"")
                    .append(location).append("\")) . ?j emp:hasLocation ?locRes .\n");
        }
        if (minSalary != null) {
            filters.append("  FILTER(?sal >= ").append(minSalary).append(")\n");
        }
        if (remote != null) {
            filters.append("  FILTER(?rem = ").append(remote).append(")\n");
        }
        return runJobQuery(filters.toString(), null);
    }

    public Optional<JobOfferDTO> getJob(String id) {
        List<JobOfferDTO> r = runJobQuery("", "emp:" + id);
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0));
    }

    public List<JobOfferDTO> jobsBySector(String sectorLabel) {
        return listJobs(sectorLabel, null, null, null);
    }

    public List<JobOfferDTO> jobsByLocation(String locationLabel) {
        return listJobs(null, locationLabel, null, null);
    }

    /**
     * Multi-condition query: jobs flagged as "risky" because they carry at least
     * one discriminatory-proxy field (ageRange, gender preference, nationality req)
     * OR they come from an unverified source.
     */
    public List<JobOfferDTO> riskyJobs() {
        String filter = """
                FILTER (
                  BOUND(?ageMin) || BOUND(?ageMax) ||
                  BOUND(?genderPref) || BOUND(?natReq) ||
                  (BOUND(?src) && (?src = "third-party-scraper" || ?src = "unknown"))
                )
                """;
        return runJobQuery(filter, null);
    }

    private List<JobOfferDTO> runJobQuery(String extraFilters, String iriFilter) {
        String q = PREFIXES + """
                SELECT ?j ?title ?compName ?sec ?loc ?rem ?reqExp ?sen ?sal ?date
                       ?disabFriendly ?ageMin ?ageMax ?genderPref ?natReq ?src
                       (GROUP_CONCAT(DISTINCT ?skillLabel; SEPARATOR=",") AS ?skills)
                WHERE {
                  ?j a emp:JobOffer ;
                     dct:title ?title ;
                     emp:postedBy ?comp ;
                     emp:hasSector ?secRes ;
                     emp:hasLocation ?locRes .
                  ?comp schema:name ?compName .
                  ?secRes rdfs:label ?sec .
                  ?locRes rdfs:label ?loc .
                  OPTIONAL { ?j emp:remote ?rem }
                  OPTIONAL { ?j emp:requiredExperience ?reqExp }
                  OPTIONAL { ?j emp:seniority ?sen }
                  OPTIONAL { ?j emp:salary ?sal }
                  OPTIONAL { ?j emp:postedDate ?date }
                  OPTIONAL { ?j emp:disabilityFriendly ?disabFriendly }
                  OPTIONAL { ?j emp:ageRangeMin ?ageMin }
                  OPTIONAL { ?j emp:ageRangeMax ?ageMax }
                  OPTIONAL { ?j emp:genderPreference ?genderPref }
                  OPTIONAL { ?j emp:nationalityReq ?natReq }
                  OPTIONAL { ?j dct:source ?src }
                  OPTIONAL { ?j emp:hasSkill ?skill . ?skill rdfs:label ?skillLabel }
                  %s
                  %s
                }
                GROUP BY ?j ?title ?compName ?sec ?loc ?rem ?reqExp ?sen ?sal ?date
                         ?disabFriendly ?ageMin ?ageMax ?genderPref ?natReq ?src
                ORDER BY ?j
                """.formatted(
                iriFilter != null ? "FILTER(?j = " + iriFilter + ")" : "",
                extraFilters
        );

        List<JobOfferDTO> out = new ArrayList<>();
        try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(q), dataset)) {
            ResultSet rs = qe.execSelect();
            while (rs.hasNext()) {
                QuerySolution s = rs.next();
                out.add(new JobOfferDTO(
                        localName(s.get("j")),
                        str(s.get("title")),
                        str(s.get("compName")),
                        str(s.get("sec")),
                        str(s.get("loc")),
                        boolVal(s.get("rem")),
                        intVal(s.get("reqExp")),
                        str(s.get("sen")),
                        intVal(s.get("sal")),
                        str(s.get("date")),
                        boolVal(s.get("disabFriendly")),
                        splitSkills(str(s.get("skills"))),
                        intVal(s.get("ageMin")),
                        intVal(s.get("ageMax")),
                        str(s.get("genderPref")),
                        str(s.get("natReq")),
                        str(s.get("src"))
                ));
            }
        }
        return out;
    }

    // ---------------- Matching (multi-condition relationship query) ----------------

    /**
     * Recommended jobs for a given candidate. Combines:
     *  - shared-skill count (skill overlap)
     *  - location compatibility (same location OR job is remote OR candidate is remote)
     *  - experience sufficient (candidate years >= job required years)
     * Returns matches ordered by computed score.
     */
    public List<MatchDTO> recommendedJobsForCandidate(String candId) {
        String q = PREFIXES + """
                SELECT ?j ?jobTitle ?candName ?candLoc ?jobLoc ?remote ?reqExp ?yoe
                       (COUNT(DISTINCT ?sharedSkill) AS ?shared)
                       (GROUP_CONCAT(DISTINCT ?sharedLabel; SEPARATOR=",") AS ?sharedSkills)
                WHERE {
                  emp:%s a emp:Candidate ;
                          foaf:name ?candName ;
                          emp:hasLocation ?candLocRes ;
                          emp:yearsOfExperience ?yoe ;
                          emp:hasSkill ?sharedSkill .
                  ?candLocRes rdfs:label ?candLoc .

                  ?j a emp:JobOffer ;
                     dct:title ?jobTitle ;
                     emp:hasLocation ?jobLocRes ;
                     emp:hasSkill ?sharedSkill ;
                     emp:requiredExperience ?reqExp ;
                     emp:remote ?remote .
                  ?jobLocRes rdfs:label ?jobLoc .
                  ?sharedSkill rdfs:label ?sharedLabel .
                }
                GROUP BY ?j ?jobTitle ?candName ?candLoc ?jobLoc ?remote ?reqExp ?yoe
                ORDER BY DESC(?shared)
                """.formatted(candId);

        List<MatchDTO> out = new ArrayList<>();
        try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(q), dataset)) {
            ResultSet rs = qe.execSelect();
            while (rs.hasNext()) {
                QuerySolution s = rs.next();
                int shared = intVal(s.get("shared"));
                String candLoc = str(s.get("candLoc"));
                String jobLoc = str(s.get("jobLoc"));
                boolean remote = Boolean.TRUE.equals(boolVal(s.get("remote")));
                boolean locOk = remote
                        || (candLoc != null && candLoc.equalsIgnoreCase(jobLoc))
                        || "Remote".equalsIgnoreCase(candLoc);
                int reqExp = intVal(s.get("reqExp"));
                int yoe = intVal(s.get("yoe"));
                boolean expOk = yoe >= reqExp;
                double score = shared
                        + (locOk ? 1.0 : 0.0)
                        + (expOk ? 1.0 : 0.0);
                out.add(new MatchDTO(
                        candId,
                        str(s.get("candName")),
                        localName(s.get("j")),
                        str(s.get("jobTitle")),
                        shared,
                        splitSkills(str(s.get("sharedSkills"))),
                        locOk,
                        expOk,
                        score
                ));
            }
        }
        out.sort((a, b) -> Double.compare(b.score(), a.score()));
        return out;
    }

    // ---------------- helpers ----------------

    private static String str(RDFNode n) {
        if (n == null) return null;
        if (n.isLiteral()) return n.asLiteral().getLexicalForm();
        return n.toString();
    }

    private static String localName(RDFNode n) {
        if (n == null || !n.isURIResource()) return null;
        String uri = n.asResource().getURI();
        int hash = uri.lastIndexOf('#');
        return hash >= 0 ? uri.substring(hash + 1) : uri;
    }

    private static Integer intVal(RDFNode n) {
        if (n == null || !n.isLiteral()) return null;
        try { return n.asLiteral().getInt(); } catch (Exception e) {
            try { return Integer.parseInt(n.asLiteral().getLexicalForm()); }
            catch (Exception ignored) { return null; }
        }
    }

    private static Boolean boolVal(RDFNode n) {
        if (n == null || !n.isLiteral()) return null;
        try { return n.asLiteral().getBoolean(); } catch (Exception e) { return null; }
    }

    private static List<String> splitSkills(String concat) {
        if (concat == null || concat.isBlank()) return List.of();
        return Arrays.stream(concat.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }
}
