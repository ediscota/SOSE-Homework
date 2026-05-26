package it.univaq.sose.daas.service;

import it.univaq.sose.daas.model.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
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

    private static final String EMP_NS    = "http://sose.univaq.it/employment#";
    private static final String FOAF_NS   = "http://xmlns.com/foaf/0.1/";
    private static final String DCT_NS    = "http://purl.org/dc/terms/";
    private static final String SCHEMA_NS = "http://schema.org/";

    private final Dataset dataset;
    private final DatasetPersistenceService persistenceService;

    public EmploymentService(Dataset employmentDataset, DatasetPersistenceService persistenceService) {
        this.dataset = employmentDataset;
        this.persistenceService = persistenceService;
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

    // =====================================================================
    // Write helpers
    // =====================================================================

    private Model model() {
        return dataset.getDefaultModel();
    }

    private Property p(String ns, String local) {
        return model().createProperty(ns, local);
    }

    private void persist() {
        persistenceService.save(dataset);
    }

    /**
     * Generates a new URI id by scanning all existing resources of the given
     * emp: type and finding the next free sequential number.
     * prefix  – e.g. "cand", "job", "company"
     * typeLocalName – e.g. "Candidate", "JobOffer", "Company"
     */
    private String generateId(String prefix, String typeLocalName) {
        String q = PREFIXES + "SELECT ?r WHERE { ?r a emp:" + typeLocalName + " }";
        int max = 0;
        try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(q), dataset)) {
            ResultSet rs = qe.execSelect();
            while (rs.hasNext()) {
                String ln = localName(rs.next().get("r"));
                if (ln != null && ln.startsWith(prefix + "-")) {
                    try {
                        int n = Integer.parseInt(ln.substring(prefix.length() + 1));
                        if (n > max) max = n;
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return String.format("%s-%03d", prefix, max + 1);
    }

    /**
     * Finds the first resource of the given emp: type with rdfs:label = labelValue.
     * Returns null if not found.
     */
    private Resource findByLabel(String typeLocalName, String labelValue) {
        if (labelValue == null || labelValue.isBlank()) return null;
        String q = PREFIXES + """
                SELECT ?r WHERE {
                  ?r a emp:%s ;
                     rdfs:label ?lbl .
                  FILTER(STR(?lbl) = "%s")
                }
                LIMIT 1
                """.formatted(typeLocalName, labelValue.replace("\"", "\\\""));
        try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(q), dataset)) {
            ResultSet rs = qe.execSelect();
            if (rs.hasNext()) {
                RDFNode n = rs.next().get("r");
                if (n != null && n.isURIResource()) return n.asResource();
            }
        }
        return null;
    }

    private Resource findCompanyById(String id) {
        if (id == null || id.isBlank()) return null;
        String uri = EMP_NS + id;
        Model m = model();
        Resource r = m.getResource(uri);
        return m.containsResource(r) ? r : null;
    }

    // =====================================================================
    // Companies
    // =====================================================================

    public List<CompanyDTO> listCompanies() {
        String q = PREFIXES + """
                SELECT ?c ?name ?website WHERE {
                  ?c a emp:Company ;
                     schema:name ?name .
                  OPTIONAL { ?c schema:url ?website }
                }
                ORDER BY ?c
                """;
        List<CompanyDTO> out = new ArrayList<>();
        try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(q), dataset)) {
            ResultSet rs = qe.execSelect();
            while (rs.hasNext()) {
                QuerySolution s = rs.next();
                out.add(new CompanyDTO(localName(s.get("c")), str(s.get("name")), str(s.get("website"))));
            }
        }
        return out;
    }

    public Optional<CompanyDTO> getCompany(String id) {
        String q = PREFIXES + """
                SELECT ?c ?name ?website WHERE {
                  emp:%s a emp:Company ;
                         schema:name ?name .
                  OPTIONAL { emp:%s schema:url ?website }
                  BIND(emp:%s AS ?c)
                }
                LIMIT 1
                """.formatted(id, id, id);
        try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(q), dataset)) {
            ResultSet rs = qe.execSelect();
            if (rs.hasNext()) {
                QuerySolution s = rs.next();
                return Optional.of(new CompanyDTO(id, str(s.get("name")), str(s.get("website"))));
            }
        }
        return Optional.empty();
    }

    public CompanyDTO createCompany(CompanyRequest req) {
        synchronized (dataset) {
            String id = generateId("company", "Company");
            Model m = model();
            Resource r = m.createResource(EMP_NS + id);
            r.addProperty(RDF.type, m.createResource(EMP_NS + "Company"));
            r.addProperty(p(SCHEMA_NS, "name"), req.name());
            if (req.website() != null && !req.website().isBlank())
                r.addProperty(p(SCHEMA_NS, "url"), req.website());
            persist();
            return new CompanyDTO(id, req.name(), req.website());
        }
    }

    public Optional<CompanyDTO> updateCompany(String id, CompanyRequest req) {
        synchronized (dataset) {
            Model m = model();
            Resource r = m.getResource(EMP_NS + id);
            if (!m.containsResource(r)) return Optional.empty();
            m.removeAll(r, p(SCHEMA_NS, "name"), null);
            m.removeAll(r, p(SCHEMA_NS, "url"), null);
            r.addProperty(p(SCHEMA_NS, "name"), req.name());
            if (req.website() != null && !req.website().isBlank())
                r.addProperty(p(SCHEMA_NS, "url"), req.website());
            persist();
            return Optional.of(new CompanyDTO(id, req.name(), req.website()));
        }
    }

    public boolean deleteCompany(String id) {
        synchronized (dataset) {
            Model m = model();
            Resource r = m.getResource(EMP_NS + id);
            if (!m.containsResource(r)) return false;
            m.removeAll(r, null, null);
            m.removeAll(null, null, r);
            persist();
            return true;
        }
    }

    // =====================================================================
    // Skills
    // =====================================================================

    public List<SkillDTO> listSkills() {
        String q = PREFIXES + """
                SELECT ?s ?lbl WHERE {
                  ?s a emp:Skill ;
                     rdfs:label ?lbl .
                }
                ORDER BY ?s
                """;
        List<SkillDTO> out = new ArrayList<>();
        try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(q), dataset)) {
            ResultSet rs = qe.execSelect();
            while (rs.hasNext()) {
                QuerySolution s = rs.next();
                out.add(new SkillDTO(localName(s.get("s")), str(s.get("lbl"))));
            }
        }
        return out;
    }

    public Optional<SkillDTO> getSkill(String id) {
        String q = PREFIXES + """
                SELECT ?lbl WHERE {
                  emp:%s a emp:Skill ;
                         rdfs:label ?lbl .
                }
                LIMIT 1
                """.formatted(id);
        try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(q), dataset)) {
            ResultSet rs = qe.execSelect();
            if (rs.hasNext())
                return Optional.of(new SkillDTO(id, str(rs.next().get("lbl"))));
        }
        return Optional.empty();
    }

    public SkillDTO createSkill(SkillRequest req) {
        synchronized (dataset) {
            String id = generateId("skill", "Skill");
            Model m = model();
            Resource r = m.createResource(EMP_NS + id);
            r.addProperty(RDF.type, m.createResource(EMP_NS + "Skill"));
            r.addProperty(RDFS.label, req.label());
            persist();
            return new SkillDTO(id, req.label());
        }
    }

    public Optional<SkillDTO> updateSkill(String id, SkillRequest req) {
        synchronized (dataset) {
            Model m = model();
            Resource r = m.getResource(EMP_NS + id);
            if (!m.containsResource(r)) return Optional.empty();
            m.removeAll(r, RDFS.label, null);
            r.addProperty(RDFS.label, req.label());
            persist();
            return Optional.of(new SkillDTO(id, req.label()));
        }
    }

    public boolean deleteSkill(String id) {
        synchronized (dataset) {
            Model m = model();
            Resource r = m.getResource(EMP_NS + id);
            if (!m.containsResource(r)) return false;
            m.removeAll(r, null, null);
            m.removeAll(null, null, r);
            persist();
            return true;
        }
    }

    // =====================================================================
    // Sectors
    // =====================================================================

    public List<SectorDTO> listSectors() {
        String q = PREFIXES + """
                SELECT ?s ?lbl WHERE {
                  ?s a emp:Sector ;
                     rdfs:label ?lbl .
                }
                ORDER BY ?s
                """;
        List<SectorDTO> out = new ArrayList<>();
        try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(q), dataset)) {
            ResultSet rs = qe.execSelect();
            while (rs.hasNext()) {
                QuerySolution s = rs.next();
                out.add(new SectorDTO(localName(s.get("s")), str(s.get("lbl"))));
            }
        }
        return out;
    }

    public Optional<SectorDTO> getSector(String id) {
        String q = PREFIXES + """
                SELECT ?lbl WHERE {
                  emp:%s a emp:Sector ;
                         rdfs:label ?lbl .
                }
                LIMIT 1
                """.formatted(id);
        try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(q), dataset)) {
            ResultSet rs = qe.execSelect();
            if (rs.hasNext())
                return Optional.of(new SectorDTO(id, str(rs.next().get("lbl"))));
        }
        return Optional.empty();
    }

    public SectorDTO createSector(SectorRequest req) {
        synchronized (dataset) {
            String id = generateId("sector", "Sector");
            Model m = model();
            Resource r = m.createResource(EMP_NS + id);
            r.addProperty(RDF.type, m.createResource(EMP_NS + "Sector"));
            r.addProperty(RDFS.label, req.label());
            persist();
            return new SectorDTO(id, req.label());
        }
    }

    public Optional<SectorDTO> updateSector(String id, SectorRequest req) {
        synchronized (dataset) {
            Model m = model();
            Resource r = m.getResource(EMP_NS + id);
            if (!m.containsResource(r)) return Optional.empty();
            m.removeAll(r, RDFS.label, null);
            r.addProperty(RDFS.label, req.label());
            persist();
            return Optional.of(new SectorDTO(id, req.label()));
        }
    }

    public boolean deleteSector(String id) {
        synchronized (dataset) {
            Model m = model();
            Resource r = m.getResource(EMP_NS + id);
            if (!m.containsResource(r)) return false;
            m.removeAll(r, null, null);
            m.removeAll(null, null, r);
            persist();
            return true;
        }
    }

    // =====================================================================
    // Locations
    // =====================================================================

    public List<LocationDTO> listLocations() {
        String q = PREFIXES + """
                SELECT ?l ?lbl WHERE {
                  ?l a emp:Location ;
                     rdfs:label ?lbl .
                }
                ORDER BY ?l
                """;
        List<LocationDTO> out = new ArrayList<>();
        try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(q), dataset)) {
            ResultSet rs = qe.execSelect();
            while (rs.hasNext()) {
                QuerySolution s = rs.next();
                out.add(new LocationDTO(localName(s.get("l")), str(s.get("lbl"))));
            }
        }
        return out;
    }

    public Optional<LocationDTO> getLocation(String id) {
        String q = PREFIXES + """
                SELECT ?lbl WHERE {
                  emp:%s a emp:Location ;
                         rdfs:label ?lbl .
                }
                LIMIT 1
                """.formatted(id);
        try (QueryExecution qe = QueryExecutionFactory.create(QueryFactory.create(q), dataset)) {
            ResultSet rs = qe.execSelect();
            if (rs.hasNext())
                return Optional.of(new LocationDTO(id, str(rs.next().get("lbl"))));
        }
        return Optional.empty();
    }

    public LocationDTO createLocation(LocationRequest req) {
        synchronized (dataset) {
            String id = generateId("loc", "Location");
            Model m = model();
            Resource r = m.createResource(EMP_NS + id);
            r.addProperty(RDF.type, m.createResource(EMP_NS + "Location"));
            r.addProperty(RDFS.label, req.label());
            persist();
            return new LocationDTO(id, req.label());
        }
    }

    public Optional<LocationDTO> updateLocation(String id, LocationRequest req) {
        synchronized (dataset) {
            Model m = model();
            Resource r = m.getResource(EMP_NS + id);
            if (!m.containsResource(r)) return Optional.empty();
            m.removeAll(r, RDFS.label, null);
            r.addProperty(RDFS.label, req.label());
            persist();
            return Optional.of(new LocationDTO(id, req.label()));
        }
    }

    public boolean deleteLocation(String id) {
        synchronized (dataset) {
            Model m = model();
            Resource r = m.getResource(EMP_NS + id);
            if (!m.containsResource(r)) return false;
            m.removeAll(r, null, null);
            m.removeAll(null, null, r);
            persist();
            return true;
        }
    }

    // =====================================================================
    // Candidates – write
    // =====================================================================

    private void applyCandidateProps(Model m, Resource r, CandidateRequest req) {
        // Remove old values for all mutable properties
        m.removeAll(r, p(FOAF_NS, "name"), null);
        m.removeAll(r, p(FOAF_NS, "gender"), null);
        m.removeAll(r, p(FOAF_NS, "age"), null);
        m.removeAll(r, p(SCHEMA_NS, "nationality"), null);
        m.removeAll(r, p(EMP_NS, "hasDisability"), null);
        m.removeAll(r, p(EMP_NS, "hasLocation"), null);
        m.removeAll(r, p(EMP_NS, "yearsOfExperience"), null);
        m.removeAll(r, p(EMP_NS, "minSalary"), null);
        m.removeAll(r, p(DCT_NS, "source"), null);
        m.removeAll(r, p(EMP_NS, "hasSkill"), null);

        if (req.name() != null)              r.addProperty(p(FOAF_NS, "name"), req.name());
        if (req.gender() != null)            r.addProperty(p(FOAF_NS, "gender"), req.gender());
        if (req.age() != null)               r.addLiteral(p(FOAF_NS, "age"), m.createTypedLiteral(req.age()));
        if (req.nationality() != null)       r.addProperty(p(SCHEMA_NS, "nationality"), req.nationality());
        if (req.hasDisability() != null)     r.addLiteral(p(EMP_NS, "hasDisability"), m.createTypedLiteral(req.hasDisability()));
        if (req.yearsOfExperience() != null) r.addLiteral(p(EMP_NS, "yearsOfExperience"), m.createTypedLiteral(req.yearsOfExperience()));
        if (req.minSalary() != null)         r.addLiteral(p(EMP_NS, "minSalary"), m.createTypedLiteral(req.minSalary()));
        if (req.source() != null)            r.addProperty(p(DCT_NS, "source"), req.source());

        // location: find or create
        if (req.location() != null && !req.location().isBlank()) {
            Resource loc = findByLabel("Location", req.location());
            if (loc == null) {
                String locId = generateId("loc", "Location");
                loc = m.createResource(EMP_NS + locId);
                loc.addProperty(RDF.type, m.createResource(EMP_NS + "Location"));
                loc.addProperty(RDFS.label, req.location());
            }
            r.addProperty(p(EMP_NS, "hasLocation"), loc);
        }

        // skills: find or create each
        if (req.skills() != null) {
            for (String skillLabel : req.skills()) {
                if (skillLabel == null || skillLabel.isBlank()) continue;
                Resource skill = findByLabel("Skill", skillLabel);
                if (skill == null) {
                    String sId = generateId("skill", "Skill");
                    skill = m.createResource(EMP_NS + sId);
                    skill.addProperty(RDF.type, m.createResource(EMP_NS + "Skill"));
                    skill.addProperty(RDFS.label, skillLabel);
                }
                r.addProperty(p(EMP_NS, "hasSkill"), skill);
            }
        }
    }

    public CandidateDTO createCandidate(CandidateRequest req) {
        synchronized (dataset) {
            String id = generateId("cand", "Candidate");
            Model m = model();
            Resource r = m.createResource(EMP_NS + id);
            r.addProperty(RDF.type, m.createResource(EMP_NS + "Candidate"));
            applyCandidateProps(m, r, req);
            persist();
            return getCandidate(id).orElseThrow();
        }
    }

    public Optional<CandidateDTO> updateCandidate(String id, CandidateRequest req) {
        synchronized (dataset) {
            Model m = model();
            Resource r = m.getResource(EMP_NS + id);
            if (!m.containsResource(r)) return Optional.empty();
            applyCandidateProps(m, r, req);
            persist();
            return getCandidate(id);
        }
    }

    public boolean deleteCandidate(String id) {
        synchronized (dataset) {
            Model m = model();
            Resource r = m.getResource(EMP_NS + id);
            if (!m.containsResource(r)) return false;
            m.removeAll(r, null, null);
            m.removeAll(null, null, r);
            persist();
            return true;
        }
    }

    // =====================================================================
    // Jobs – write
    // =====================================================================

    private void applyJobProps(Model m, Resource r, JobOfferRequest req) {
        m.removeAll(r, p(DCT_NS, "title"), null);
        m.removeAll(r, p(EMP_NS, "postedBy"), null);
        m.removeAll(r, p(EMP_NS, "hasSector"), null);
        m.removeAll(r, p(EMP_NS, "hasLocation"), null);
        m.removeAll(r, p(EMP_NS, "remote"), null);
        m.removeAll(r, p(EMP_NS, "requiredExperience"), null);
        m.removeAll(r, p(EMP_NS, "seniority"), null);
        m.removeAll(r, p(EMP_NS, "salary"), null);
        m.removeAll(r, p(EMP_NS, "postedDate"), null);
        m.removeAll(r, p(EMP_NS, "disabilityFriendly"), null);
        m.removeAll(r, p(EMP_NS, "ageRangeMin"), null);
        m.removeAll(r, p(EMP_NS, "ageRangeMax"), null);
        m.removeAll(r, p(EMP_NS, "genderPreference"), null);
        m.removeAll(r, p(EMP_NS, "nationalityReq"), null);
        m.removeAll(r, p(DCT_NS, "source"), null);
        m.removeAll(r, p(EMP_NS, "hasSkill"), null);

        if (req.title() != null)                r.addProperty(p(DCT_NS, "title"), req.title());
        if (req.remote() != null)               r.addLiteral(p(EMP_NS, "remote"), m.createTypedLiteral(req.remote()));
        if (req.requiredExperience() != null)   r.addLiteral(p(EMP_NS, "requiredExperience"), m.createTypedLiteral(req.requiredExperience()));
        if (req.seniority() != null)            r.addProperty(p(EMP_NS, "seniority"), req.seniority());
        if (req.salary() != null)               r.addLiteral(p(EMP_NS, "salary"), m.createTypedLiteral(req.salary()));
        if (req.postedDate() != null)           r.addProperty(p(EMP_NS, "postedDate"), req.postedDate());
        if (req.disabilityFriendly() != null)   r.addLiteral(p(EMP_NS, "disabilityFriendly"), m.createTypedLiteral(req.disabilityFriendly()));
        if (req.ageRangeMin() != null)          r.addLiteral(p(EMP_NS, "ageRangeMin"), m.createTypedLiteral(req.ageRangeMin()));
        if (req.ageRangeMax() != null)          r.addLiteral(p(EMP_NS, "ageRangeMax"), m.createTypedLiteral(req.ageRangeMax()));
        if (req.genderPreference() != null)     r.addProperty(p(EMP_NS, "genderPreference"), req.genderPreference());
        if (req.nationalityRequirement() != null) r.addProperty(p(EMP_NS, "nationalityReq"), req.nationalityRequirement());
        if (req.source() != null)               r.addProperty(p(DCT_NS, "source"), req.source());

        // company
        if (req.companyId() != null && !req.companyId().isBlank()) {
            Resource comp = findCompanyById(req.companyId());
            if (comp != null) r.addProperty(p(EMP_NS, "postedBy"), comp);
        }

        // sector: find or create
        if (req.sector() != null && !req.sector().isBlank()) {
            Resource sec = findByLabel("Sector", req.sector());
            if (sec == null) {
                String secId = generateId("sector", "Sector");
                sec = m.createResource(EMP_NS + secId);
                sec.addProperty(RDF.type, m.createResource(EMP_NS + "Sector"));
                sec.addProperty(RDFS.label, req.sector());
            }
            r.addProperty(p(EMP_NS, "hasSector"), sec);
        }

        // location: find or create
        if (req.location() != null && !req.location().isBlank()) {
            Resource loc = findByLabel("Location", req.location());
            if (loc == null) {
                String locId = generateId("loc", "Location");
                loc = m.createResource(EMP_NS + locId);
                loc.addProperty(RDF.type, m.createResource(EMP_NS + "Location"));
                loc.addProperty(RDFS.label, req.location());
            }
            r.addProperty(p(EMP_NS, "hasLocation"), loc);
        }

        // skills
        if (req.skills() != null) {
            for (String skillLabel : req.skills()) {
                if (skillLabel == null || skillLabel.isBlank()) continue;
                Resource skill = findByLabel("Skill", skillLabel);
                if (skill == null) {
                    String sId = generateId("skill", "Skill");
                    skill = m.createResource(EMP_NS + sId);
                    skill.addProperty(RDF.type, m.createResource(EMP_NS + "Skill"));
                    skill.addProperty(RDFS.label, skillLabel);
                }
                r.addProperty(p(EMP_NS, "hasSkill"), skill);
            }
        }
    }

    public JobOfferDTO createJob(JobOfferRequest req) {
        synchronized (dataset) {
            String id = generateId("job", "JobOffer");
            Model m = model();
            Resource r = m.createResource(EMP_NS + id);
            r.addProperty(RDF.type, m.createResource(EMP_NS + "JobOffer"));
            applyJobProps(m, r, req);
            persist();
            return getJob(id).orElseThrow();
        }
    }

    public Optional<JobOfferDTO> updateJob(String id, JobOfferRequest req) {
        synchronized (dataset) {
            Model m = model();
            Resource r = m.getResource(EMP_NS + id);
            if (!m.containsResource(r)) return Optional.empty();
            applyJobProps(m, r, req);
            persist();
            return getJob(id);
        }
    }

    public boolean deleteJob(String id) {
        synchronized (dataset) {
            Model m = model();
            Resource r = m.getResource(EMP_NS + id);
            if (!m.containsResource(r)) return false;
            m.removeAll(r, null, null);
            m.removeAll(null, null, r);
            persist();
            return true;
        }
    }
}
