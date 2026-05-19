# SoSE Midterm Homework — Data-as-a-Service + Ethics-as-a-Service

**Domain:** employment / job matching
**Stack:** Java 21 · Spring Boot 3.3 · Apache Jena 5.1 (TDB/RDF/SPARQL) · Thymeleaf
**Team:** 3 students
**Course:** Service-Oriented Software Engineering — A.Y. 2025/2026 · Università dell'Aquila

---

## 1. Idea

A small **job-matching application** that recommends offers to candidates by combining their skills, location and experience with available job offers. The recommendation is then submitted to an independent **Ethics-as-a-Service** which decides whether the suggestion may be shown, must be revised, escalated, or rejected — based on **external, editable policies** about discrimination, data quality, accessibility and transparency.

This is exactly the *"recruiting app uses candidate data, while EaaS evaluates discriminatory proxies and accountability"* example listed in the assignment.

## 2. Architecture

```
┌───────────────┐   HTTP / JSON   ┌──────────────────┐
│  Web client   │ ──────────────▶ │  daas-service    │  RDF + SPARQL
│  (Thymeleaf)  │                 │  :8081           │  Apache Jena
│  :8080        │                 └──────────────────┘
│               │                          ▲
│               │   HTTP / JSON            │ HTTP (re-fetches
│               │                          │  canonical data —
│               │  ┌──────────────────┐    │  caller is never
│               │  │  eaas-service    │────┘  trusted)
│               └─▶│  :8082           │
│                  │  policy engine + │
│                  │  audit trail     │
│                  └──────────────────┘
```

Three Maven modules:

| Module           | Port | Responsibility                                                            |
|------------------|------|---------------------------------------------------------------------------|
| `daas-service`   | 8081 | Loads the RDF dataset, runs SPARQL, exposes REST endpoints with JSON      |
| `eaas-service`   | 8082 | Loads JSON policies, fetches the canonical data, decides, writes audit    |
| `client`         | 8080 | Web UI: browse candidates → see recommendations → ask EaaS → see audit    |

## 3. Build & run

Requires Java 21 + Maven 3.9.

```bash
# from the project root
mvn clean install                 # builds all three modules

# in three separate terminals (DaaS first, EaaS second, Client last)
mvn -pl daas-service spring-boot:run
mvn -pl eaas-service spring-boot:run
mvn -pl client       spring-boot:run

# then open
http://localhost:8080
```

## 4. RDF dataset

File: [`daas-service/src/main/resources/dataset/employment.ttl`](daas-service/src/main/resources/dataset/employment.ttl)

| Class            | Examples                                  |
|------------------|-------------------------------------------|
| `emp:Candidate`  | 25 candidates, with skills, location, age, gender, nationality, disability, source |
| `emp:JobOffer`   | 18 offers, posted by companies, with skills, sector, location, salary, source |
| `emp:Company`    | 7 companies                               |
| `emp:Skill`      | 12 skills                                 |
| `emp:Sector`     | 5 sectors                                 |
| `emp:Location`   | 7 locations including Remote              |

Some offers intentionally carry **problematic fields** (`emp:ageRangeMax`, `emp:genderPreference`, `emp:nationalityReq`) or come from `third-party-scraper` / `unknown` sources — these are the cases that the EaaS will flag.

Custom namespace: `http://sose.univaq.it/employment#` (prefix `emp:`). Re-uses `foaf:`, `schema:`, `dct:`.

## 5. DaaS — REST endpoints

All responses are JSON. Base URL `http://localhost:8081`.

| Method | URI                                  | Purpose                                                     |
|--------|--------------------------------------|-------------------------------------------------------------|
| GET    | `/api/candidates`                    | List all candidates                                         |
| GET    | `/api/candidates/{id}`               | Single candidate                                            |
| GET    | `/api/jobs`                          | Search offers (filters: `sector`, `location`, `minSalary`, `remote`) — **multi-condition SPARQL** |
| GET    | `/api/jobs/{id}`                     | Single offer                                                |
| GET    | `/api/jobs/sector/{sector}`          | Offers in a sector                                          |
| GET    | `/api/jobs/location/{location}`      | Offers in a location                                        |
| GET    | `/api/jobs/risky`                    | Offers flagged as risky by the dataset (proxies/unknown source) |
| GET    | `/api/match/candidate/{id}`          | **Recommendation**: skill overlap + location + experience   |

See [`docs/endpoints.md`](docs/endpoints.md) for example calls.

## 6. EaaS — workflow

1. The client POSTs a structured `EvaluationRequest` (action, candidateId, jobId, requester, purpose).
2. EaaS **ignores** any `declaredRisk` from the caller and **re-fetches** the canonical candidate and job from the DaaS.
3. The `PolicyEngine` runs every loaded policy (case analysis).
4. The `DecisionMaker` aggregates the findings into a governance decision (`PROCEED`/`REVISE`/`ESCALATE`/`REJECT`).
5. An `AuditRecord` is persisted to memory **and** to JSONL on disk.
6. The full `EvaluationResponse` with rationale + applied policies + required actions + provenance is returned.

Endpoints (base URL `http://localhost:8082`):

| Method | URI                       | Purpose                                  |
|--------|---------------------------|------------------------------------------|
| POST   | `/api/ethics/evaluate`    | Evaluate a request                       |
| GET    | `/api/ethics/policies`    | List loaded policies                     |
| GET    | `/api/audit`              | All audit records of the session         |
| GET    | `/api/audit/{id}`         | Single audit record                      |

## 7. Policies

Four external policies in [`eaas-service/src/main/resources/policies/`](eaas-service/src/main/resources/policies/):

| File                              | Severity   | What it checks                                                              |
|-----------------------------------|------------|-----------------------------------------------------------------------------|
| `01-discriminatory-proxies.json`  | CRITICAL   | Offers using age / gender / nationality fields → **REJECT**                 |
| `02-data-quality-provenance.json` | HIGH       | Untrusted source or missing critical fields → **ESCALATE**                  |
| `03-accessibility.json`           | MEDIUM     | Candidate has disability AND offer not flagged friendly → **REVISE**        |
| `04-transparency-purpose.json`    | LOW        | Request without requester/purpose → **PROCEED + remediation**               |

Each policy can be edited (or new ones added) **without redeploying**: the file is reloaded at every restart.

## 8. Example requests

Both example payloads in [`docs/example-requests/`](docs/example-requests/):

- `req-proceed.json` → candidate `cand-001` + job `job-001` → **PROCEED**
- `req-reject.json`  → candidate `cand-001` + job `job-003` → **REJECT** (job has `ageRangeMax`)

A trimmed example audit record is in [`docs/example-audit.json`](docs/example-audit.json).

```bash
# PROCEED
curl -X POST http://localhost:8082/api/ethics/evaluate \
     -H 'Content-Type: application/json' \
     -d @docs/example-requests/req-proceed.json

# REJECT (discriminatory proxy)
curl -X POST http://localhost:8082/api/ethics/evaluate \
     -H 'Content-Type: application/json' \
     -d @docs/example-requests/req-reject.json
```

## 9. Demo script (≈ 30 minutes for 3 people)

1. **Member 1** — domain & RDF: walk through the ontology, show 2-3 SPARQL queries in `EmploymentService`, hit `/api/match/candidate/cand-005` from the browser.
2. **Member 2** — EaaS: open `/policies`, change a policy file live to show externality, run the two example requests, open the audit trail.
3. **Member 3** — end-to-end: pick a candidate in the UI, recommend, evaluate two jobs (one PROCEED, one REJECT), open the audit detail.
4. Close with the critical reflection: limits of the system, what should never be automated.

## 10. Team split

| Member | Owns                                                  |
|--------|-------------------------------------------------------|
| 1      | DaaS: ontology, SPARQL queries, REST endpoints, dataset documentation |
| 2      | EaaS: policy engine, JSON policies, audit, decision logic |
| 3      | Client + integration + slides + demo script           |

## 11. Critical reflection (to put in slides)

- The system **suggests** matches — it does not decide hiring. A human always remains in the loop after `REVISE` / `ESCALATE`.
- Policies are written in JSON and can be edited by non-developers, but the underlying **rules** are still Java code: governance over the policy *catalog* still needs human review.
- The dataset is synthetic. A real deployment would need GDPR-grade consent management and right-to-explanation flows beyond what the audit trail offers today.
- Disability/age/gender are explicitly checked: but discriminatory proxies can be subtler (postcode, university, name). A v2 would add fairness metrics across candidate groups, not only field-presence rules.
