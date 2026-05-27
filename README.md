# SoSE Midterm Homework — Data-as-a-Service + Ethics-as-a-Service

**Domain:** employment / job matching
**Stack:** Java 21 · Spring Boot 3.3 · Apache Jena 5.1 (RDF/XML + SPARQL) · Thymeleaf · springdoc-openapi (Swagger UI)
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

**Interactive API docs (Swagger UI):**

| Service | URL                                              |
|---------|--------------------------------------------------|
| DaaS    | http://localhost:8081/swagger-ui.html            |
| EaaS    | http://localhost:8082/swagger-ui.html            |

Each Swagger page lets you explore every endpoint and try it out from the browser.
## 4. RDF dataset

File: [`daas-service/src/main/resources/dataset/employment.rdf`](daas-service/src/main/resources/dataset/employment.rdf) — serialised as **RDF/XML**, ~635 triples, loaded in-memory by Apache Jena at startup.

Writes from the REST API (POST / PUT / DELETE) are persisted back to the same file via `DatasetPersistenceService`, so changes survive a restart.

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

All responses are JSON. Base URL `http://localhost:8081`. Interactive docs at `/swagger-ui.html`.

The DaaS exposes **7 controllers** with **full CRUD** for every domain resource, plus a SPARQL-powered matching endpoint.

### 5.1 Candidates — `/api/candidates`

| Method | URI                          | Purpose                                  |
|--------|------------------------------|------------------------------------------|
| GET    | `/api/candidates`            | List all candidates                      |
| GET    | `/api/candidates/{id}`       | Single candidate by ID                   |
| POST   | `/api/candidates`            | Create a new candidate                   |
| PUT    | `/api/candidates/{id}`       | Update an existing candidate             |
| DELETE | `/api/candidates/{id}`       | Delete a candidate                       |

### 5.2 Job offers — `/api/jobs`

| Method | URI                                  | Purpose                                                                   |
|--------|--------------------------------------|---------------------------------------------------------------------------|
| GET    | `/api/jobs`                          | Search offers (filters: `sector`, `location`, `minSalary`, `remote`) — **multi-condition SPARQL** |
| GET    | `/api/jobs/{id}`                     | Single offer                                                              |
| GET    | `/api/jobs/sector/{sector}`          | Offers in a sector                                                        |
| GET    | `/api/jobs/location/{location}`      | Offers in a location                                                      |
| GET    | `/api/jobs/risky`                    | Offers flagged as risky (discriminatory proxies / unverified source)      |
| POST   | `/api/jobs`                          | Create a new offer                                                        |
| PUT    | `/api/jobs/{id}`                     | Update an existing offer                                                  |
| DELETE | `/api/jobs/{id}`                     | Delete an offer                                                           |

### 5.3 Matching — `/api/match` (read-only)

| Method | URI                                  | Purpose                                                       |
|--------|--------------------------------------|---------------------------------------------------------------|
| GET    | `/api/match/candidate/{id}`          | **Recommendation**: skill overlap + location + experience     |

### 5.4 Lookup resources — full CRUD

Same `GET / GET {id} / POST / PUT {id} / DELETE {id}` pattern on:

| Base path          | Resource                |
|--------------------|-------------------------|
| `/api/companies`   | Hiring companies        |
| `/api/skills`      | Skill catalogue         |
| `/api/sectors`     | Work sectors            |
| `/api/locations`   | Geographic locations    |

See [`docs/endpoints.md`](docs/endpoints.md) for example payloads, or open Swagger UI at http://localhost:8081/swagger-ui.html to try every endpoint from the browser.

## 6. EaaS — workflow

1. The client POSTs a structured `EvaluationRequest` (action, candidateId, jobId, requester, purpose).
2. EaaS **ignores** any `declaredRisk` from the caller and **re-fetches** the canonical candidate and job from the DaaS.
3. The `PolicyEngine` runs every loaded policy (case analysis).
4. The `DecisionMaker` aggregates the findings into a governance decision (`PROCEED`/`REVISE`/`ESCALATE`/`REJECT`).
5. An `AuditRecord` is persisted to memory **and** to JSONL on disk.
6. The full `EvaluationResponse` with rationale + applied policies + required actions + provenance is returned.

Endpoints (base URL `http://localhost:8082`, Swagger UI at `/swagger-ui.html`):

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

.
