# DaaS REST endpoints — quick reference

Base URL: `http://localhost:8081`

## Candidates

### `GET /api/candidates`
Returns every candidate in the dataset.

### `GET /api/candidates/{id}`
Example: `GET /api/candidates/cand-001`

```json
{
  "id": "cand-001",
  "name": "Alice Bianchi",
  "gender": "female",
  "age": 28,
  "nationality": "IT",
  "hasDisability": null,
  "location": "Milan",
  "yearsOfExperience": 4,
  "minSalary": 32000,
  "skills": ["Java", "SQL", "Docker"],
  "source": "synthetic"
}
```

## Job offers

### `GET /api/jobs`
Supports the following optional query parameters (combined in a single SPARQL):

- `sector` — exact label (case-insensitive), e.g. `Information Technology`
- `location` — exact label, e.g. `Milan`
- `minSalary` — integer
- `remote` — `true` / `false`

Example: `GET /api/jobs?location=Milan&minSalary=40000&remote=false`

### `GET /api/jobs/{id}`
Example: `GET /api/jobs/job-003`

```json
{
  "id": "job-003",
  "title": "Junior Frontend Developer",
  "company": "StartupLab",
  "sector": "Information Technology",
  "location": "L'Aquila",
  "remote": false,
  "requiredExperience": 1,
  "seniority": "junior",
  "salary": 24000,
  "postedDate": "2026-04-20",
  "disabilityFriendly": null,
  "skills": ["React"],
  "ageRangeMin": null,
  "ageRangeMax": 30,
  "genderPreference": null,
  "nationalityRequirement": null,
  "source": "third-party-scraper"
}
```

### `GET /api/jobs/sector/{sector}`
Convenience shortcut. Example: `GET /api/jobs/sector/Healthcare`

### `GET /api/jobs/location/{location}`
Example: `GET /api/jobs/location/Milan`

### `GET /api/jobs/risky`
Multi-condition SPARQL: offers carrying any discriminatory proxy field *or* an unverified provenance.

## Matching

### `GET /api/match/candidate/{id}`
Recommendation pipeline. Single SPARQL combining three relations:

1. skill overlap between candidate and offer,
2. location compatibility (same location OR job is remote OR candidate is remote),
3. experience sufficiency (`yearsOfExperience >= requiredExperience`).

Each match returns a score and the shared skills used to build it.

Example: `GET /api/match/candidate/cand-001`

```json
[
  {
    "candidateId": "cand-001",
    "candidateName": "Alice Bianchi",
    "jobId": "job-014",
    "jobTitle": "Full-stack Developer",
    "sharedSkillCount": 3,
    "sharedSkills": ["Java", "React", "SQL"],
    "locationCompatible": true,
    "experienceSufficient": true,
    "score": 5.0
  }
]
```
