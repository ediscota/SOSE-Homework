// SoSE Midterm – Employment DaaS + EaaS – Slide generator
// 22 slides, ~30-36 min for 3 members
// Run: node slides/generate.js

"use strict";
const pptxgen = require("pptxgenjs");
const pres = new pptxgen();
pres.layout = "LAYOUT_16x9"; // 10" × 5.625"

// ─────────────── PALETTE ───────────────
const C = {
  navy:    "1D2D44",   // primary dark (title/section bgs)
  blue:    "2C5FDC",   // accent links / highlights
  alert:   "B32424",   // REJECT / CRITICAL
  orange:  "C25A00",   // ESCALATE / HIGH
  amber:   "D8A01A",   // REVISE / MEDIUM
  green:   "2F8F4E",   // PROCEED / LOW
  ice:     "EEF3FF",   // light content bg
  white:   "FFFFFF",
  muted:   "6B7280",
  text:    "1D2D44",
  code:    "1E293B",
};

const F = { title: "Trebuchet MS", body: "Calibri" };

// ─────────────── HELPERS ───────────────
function makeShadow() {
  return { type: "outer", color: "000000", blur: 8, offset: 3, angle: 135, opacity: 0.12 };
}

/** Dark section-divider / title slide */
function darkSlide(slide) {
  slide.background = { color: C.navy };
}

/** Light content slide */
function lightSlide(slide) {
  slide.background = { color: C.white };
}

/** Slide number in bottom-right (light slides only) */
function addPageNum(slide, n) {
  slide.addText(String(n), {
    x: 9.5, y: 5.3, w: 0.4, h: 0.2,
    fontSize: 9, color: C.muted, align: "right", fontFace: F.body,
  });
}

/** Section label chip (top-left on content slides) */
function addSectionChip(slide, label, color) {
  slide.addShape(pres.shapes.ROUNDED_RECTANGLE, {
    x: 0.4, y: 0.22, w: 1.9, h: 0.28,
    fill: { color: color || C.navy }, rectRadius: 0.04, line: { color: color || C.navy, width: 0 },
  });
  slide.addText(label.toUpperCase(), {
    x: 0.4, y: 0.22, w: 1.9, h: 0.28,
    fontSize: 8, bold: true, color: C.white, align: "center",
    fontFace: F.body, margin: 0,
  });
}

/** Slide title on content slides */
function addTitle(slide, text) {
  slide.addText(text, {
    x: 0.4, y: 0.62, w: 9.2, h: 0.65,
    fontSize: 26, bold: true, color: C.text, fontFace: F.title, margin: 0,
  });
}

/** Horizontal rule under title */
function addRule(slide) {
  slide.addShape(pres.shapes.RECTANGLE, {
    x: 0.4, y: 1.3, w: 9.2, h: 0.03, fill: { color: C.blue }, line: { color: C.blue, width: 0 },
  });
}

/** Card / box */
function card(slide, x, y, w, h, fillColor) {
  slide.addShape(pres.shapes.RECTANGLE, {
    x, y, w, h,
    fill: { color: fillColor || C.ice },
    line: { color: "D4DCF0", width: 1 },
    shadow: makeShadow(),
  });
}

/** Colored badge */
function badge(slide, x, y, w, text, fillColor, textColor) {
  slide.addShape(pres.shapes.ROUNDED_RECTANGLE, {
    x, y, w: w, h: 0.3, fill: { color: fillColor }, rectRadius: 0.06,
    line: { color: fillColor, width: 0 },
  });
  slide.addText(text, {
    x, y, w: w, h: 0.3, fontSize: 10, bold: true,
    color: textColor || C.white, align: "center", fontFace: F.body, margin: 0,
  });
}

// ══════════════════════════════════════════════════════════════
//  SLIDE 1 – TITLE
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  darkSlide(s);

  // accent bar left
  s.addShape(pres.shapes.RECTANGLE, {
    x: 0, y: 0, w: 0.22, h: 5.625, fill: { color: C.blue }, line: { color: C.blue, width: 0 },
  });

  s.addText("Employment Platform", {
    x: 0.5, y: 0.9, w: 9.3, h: 0.8,
    fontSize: 14, color: "9FB4CC", fontFace: F.title, italic: true, margin: 0,
  });
  s.addText("DaaS + EaaS", {
    x: 0.5, y: 1.55, w: 9.3, h: 1.3,
    fontSize: 52, bold: true, color: C.white, fontFace: F.title, margin: 0,
  });
  s.addText("Data as a Service & Ethics as a Service", {
    x: 0.5, y: 2.75, w: 9.3, h: 0.55,
    fontSize: 20, color: "CADCFC", fontFace: F.title, margin: 0,
  });

  // divider
  s.addShape(pres.shapes.RECTANGLE, {
    x: 0.5, y: 3.42, w: 3.5, h: 0.04, fill: { color: C.blue }, line: { color: C.blue, width: 0 },
  });

  s.addText([
    { text: "Service-Oriented Software Engineering  ·  A.Y. 2025/2026", options: { breakLine: true } },
    { text: "Università degli Studi dell'Aquila", options: { breakLine: true } },
    { text: "Group of 3 students" },
  ], {
    x: 0.5, y: 3.6, w: 9.3, h: 1.2,
    fontSize: 13, color: "9FB4CC", fontFace: F.body, margin: 0,
  });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 2 – AGENDA
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  darkSlide(s);
  s.addShape(pres.shapes.RECTANGLE, {
    x: 0, y: 0, w: 0.22, h: 5.625, fill: { color: C.blue }, line: { color: C.blue, width: 0 },
  });

  s.addText("Agenda", {
    x: 0.5, y: 0.35, w: 9, h: 0.7,
    fontSize: 32, bold: true, color: C.white, fontFace: F.title, margin: 0,
  });

  const members = [
    { label: "Member 1", color: "2C5FDC", topics: ["Domain & Motivation", "Application Architecture", "RDF Dataset & Ontology", "SPARQL Queries", "DaaS REST APIs"] },
    { label: "Member 2", color: "2F8F4E", topics: ["EaaS – Workflow & Architecture", "Ethical Policies (4 policies)", "Scenario PROCEED", "Scenario REJECT"] },
    { label: "Member 3", color: "9B59B6", topics: ["Audit Trail & Provenance", "DaaS ↔ EaaS Integration", "Live Demo", "Critical Reflection"] },
  ];

  members.forEach((m, i) => {
    const x = 0.5 + i * 3.1;
    s.addShape(pres.shapes.RECTANGLE, {
      x, y: 1.2, w: 2.85, h: 3.9,
      fill: { color: "FFFFFF" }, line: { color: "FFFFFF", width: 0 },
      shadow: makeShadow(),
    });
    s.addShape(pres.shapes.RECTANGLE, {
      x, y: 1.2, w: 2.85, h: 0.42,
      fill: { color: m.color }, line: { color: m.color, width: 0 },
    });
    s.addText(m.label, {
      x, y: 1.2, w: 2.85, h: 0.42,
      fontSize: 12, bold: true, color: C.white, fontFace: F.body, align: "center", margin: 0,
    });
    const items = m.topics.map((t, j) => ({
      text: t,
      options: { bullet: true, fontSize: 11, color: C.code, breakLine: j < m.topics.length - 1 },
    }));
    s.addText(items, {
      x: x + 0.12, y: 1.7, w: 2.6, h: 3.3,
      fontFace: F.body, valign: "top",
    });
  });

  s.addText("~10-12 min each  ·  30-36 min total", {
    x: 0.5, y: 5.2, w: 9, h: 0.3,
    fontSize: 10, color: "9FB4CC", align: "center", fontFace: F.body, italic: true, margin: 0,
  });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 3 – SECTION 1 divider
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  darkSlide(s);
  s.addShape(pres.shapes.RECTANGLE, {
    x: 0, y: 0, w: 0.22, h: 5.625, fill: { color: C.blue }, line: { color: C.blue, width: 0 },
  });
  s.addText("Section 1", { x: 0.5, y: 1.3, w: 9, h: 0.45, fontSize: 13, color: "9FB4CC", fontFace: F.body, italic: true, margin: 0 });
  s.addText("Domain, Dataset\nand DaaS", {
    x: 0.5, y: 1.75, w: 9, h: 2.0,
    fontSize: 40, bold: true, color: C.white, fontFace: F.title, margin: 0,
  });
  s.addText("Member 1  ·  ~10-12 minutes", {
    x: 0.5, y: 4.5, w: 9, h: 0.4,
    fontSize: 12, color: "9FB4CC", fontFace: F.body, margin: 0,
  });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 4 – DOMAIN & MOTIVATION
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  lightSlide(s);
  addSectionChip(s, "Domain");
  addTitle(s, "Chosen Domain: Employment");
  addRule(s);
  addPageNum(s, 4);

  // left column: motivation
  card(s, 0.4, 1.45, 4.35, 3.8, C.ice);
  s.addText("Why Employment?", {
    x: 0.6, y: 1.6, w: 3.9, h: 0.4,
    fontSize: 14, bold: true, color: C.navy, fontFace: F.title, margin: 0,
  });
  s.addText([
    { text: "Rich structured data domain", options: { bullet: true, breakLine: true } },
    { text: "High-impact decisions affecting people's lives", options: { bullet: true, breakLine: true } },
    { text: "High risk of algorithmic bias and discrimination", options: { bullet: true, breakLine: true } },
    { text: "Explicitly mentioned in the assignment specification", options: { bullet: true, breakLine: true } },
    { text: "Relevant to existing regulations (D.Lgs. 198/2006, GDPR, UN CRPD)", options: { bullet: true } },
  ], {
    x: 0.6, y: 2.1, w: 4.0, h: 3.0,
    fontSize: 12, color: C.text, fontFace: F.body, valign: "top",
  });

  // right column: use case
  card(s, 4.9, 1.45, 4.7, 3.8, C.ice);
  s.addText("Project Use Case", {
    x: 5.1, y: 1.6, w: 4.3, h: 0.4,
    fontSize: 14, bold: true, color: C.navy, fontFace: F.title, margin: 0,
  });

  const steps = [
    { n: "1", t: "CANDIDATE", d: "Searches for jobs in the system" },
    { n: "2", t: "DaaS", d: "Recommends offers via SPARQL" },
    { n: "3", t: "EaaS", d: "Evaluates the recommendation" },
    { n: "4", t: "AUDIT", d: "Every decision is tracked" },
  ];
  steps.forEach((st, i) => {
    const y = 2.05 + i * 0.82;
    s.addShape(pres.shapes.OVAL, {
      x: 5.1, y: y, w: 0.38, h: 0.38,
      fill: { color: C.blue }, line: { color: C.blue, width: 0 },
    });
    s.addText(st.n, { x: 5.1, y: y, w: 0.38, h: 0.38, fontSize: 11, bold: true, color: C.white, align: "center", fontFace: F.body, margin: 0 });
    s.addText([
      { text: st.t + "  ", options: { bold: true, color: C.navy } },
      { text: st.d, options: { color: C.muted } },
    ], { x: 5.6, y: y, w: 3.8, h: 0.38, fontSize: 12, fontFace: F.body, valign: "middle", margin: 0 });
    if (i < 3) {
      s.addShape(pres.shapes.LINE, { x: 5.28, y: y + 0.38, w: 0, h: 0.44, line: { color: C.blue, width: 1.5 } });
    }
  });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 5 – ARCHITECTURE
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  lightSlide(s);
  addSectionChip(s, "Architecture");
  addTitle(s, "Application Architecture");
  addRule(s);
  addPageNum(s, 5);

  // Three service boxes
  const services = [
    { label: "CLIENT", port: ":8080", tech: "Spring Boot\nThymeleaf", color: "9B59B6", desc: "Web UI: candidate browsing, recommendations, evaluation" },
    { label: "DaaS", port: ":8081", tech: "Spring Boot\nApache Jena 5.1\nRDF/XML + SPARQL", color: C.blue, desc: "RDF dataset, SPARQL queries, 8 REST JSON endpoints" },
    { label: "EaaS", port: ":8082", tech: "Spring Boot\nPolicy engine\nAudit JSONL", color: C.green, desc: "Loads JSON policies, evaluates requests, produces decisions and audit" },
  ];

  services.forEach((svc, i) => {
    const x = 0.4 + i * 3.1;
    s.addShape(pres.shapes.RECTANGLE, { x, y: 1.45, w: 2.8, h: 3.8, fill: { color: C.ice }, line: { color: "D4DCF0", width: 1 }, shadow: makeShadow() });
    s.addShape(pres.shapes.RECTANGLE, { x, y: 1.45, w: 2.8, h: 0.5, fill: { color: svc.color }, line: { color: svc.color, width: 0 } });
    s.addText(svc.label + "  " + svc.port, { x, y: 1.45, w: 2.8, h: 0.5, fontSize: 14, bold: true, color: C.white, align: "center", fontFace: F.title, margin: 0 });
    s.addText(svc.tech, { x: x + 0.1, y: 2.05, w: 2.6, h: 0.8, fontSize: 11, color: svc.color, fontFace: "Consolas", bold: true, margin: 0 });
    s.addShape(pres.shapes.LINE, { x: x + 0.1, y: 2.9, w: 2.6, h: 0, line: { color: "D4DCF0", width: 1 } });
    s.addText(svc.desc, { x: x + 0.1, y: 3.0, w: 2.6, h: 2.1, fontSize: 11, color: C.text, fontFace: F.body, valign: "top", margin: 0 });
  });

  // Arrows between boxes
  [[0.4 + 2.8, "HTTP/JSON\n↔ DaaS"], [0.4 + 2 * 3.1 - 0.3, "HTTP/JSON\n↔ EaaS"]].forEach(([x, label], i) => {
    s.addShape(pres.shapes.LINE, { x: x, y: 3.3, w: 0.3, h: 0, line: { color: C.muted, width: 2 } });
    s.addText(label, { x: x - 0.3, y: 3.45, w: 0.9, h: 0.5, fontSize: 8, color: C.muted, align: "center", fontFace: F.body, italic: true, margin: 0 });
  });

  s.addText("⚠  EaaS always re-fetches data from DaaS: it never trusts the caller", {
    x: 0.4, y: 5.15, w: 9.2, h: 0.3,
    fontSize: 10, color: C.orange, italic: true, fontFace: F.body, margin: 0,
  });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 6 – RDF DATASET – ONTOLOGY
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  lightSlide(s);
  addSectionChip(s, "RDF Dataset");
  addTitle(s, "RDF/XML Dataset — Ontology");
  addRule(s);
  addPageNum(s, 6);

  // stats row
  const stats = [
    { n: "635", l: "RDF Triples" }, { n: "25", l: "Candidates" },
    { n: "18", l: "Offers" },       { n: "12", l: "Skills" },
    { n: "5",  l: "Sectors" },      { n: "7",  l: "Companies" },
  ];
  stats.forEach((st, i) => {
    const x = 0.4 + i * 1.55;
    card(s, x, 1.45, 1.35, 0.9, C.navy);
    s.addText(st.n, { x, y: 1.5, w: 1.35, h: 0.5, fontSize: 22, bold: true, color: C.white, align: "center", fontFace: F.title, margin: 0 });
    s.addText(st.l, { x, y: 2.0, w: 1.35, h: 0.28, fontSize: 8, color: "9FB4CC", align: "center", fontFace: F.body, margin: 0 });
  });

  // namespace
  card(s, 0.4, 2.55, 4.2, 0.65, "F0F8FF");
  s.addText("Namespace: ", { x: 0.55, y: 2.62, w: 1.0, h: 0.4, fontSize: 10, color: C.muted, fontFace: F.body, margin: 0 });
  s.addText("http://sose.univaq.it/employment#  (prefix emp:)", {
    x: 1.4, y: 2.62, w: 3.1, h: 0.4, fontSize: 10, color: C.blue, fontFace: "Consolas", bold: true, margin: 0,
  });
  s.addText("Reused: foaf: · schema: · dct: · xsd:", {
    x: 0.55, y: 2.88, w: 4.0, h: 0.28, fontSize: 9, color: C.muted, fontFace: F.body, italic: true, margin: 0,
  });

  // classes list
  const classes = [
    ["emp:Candidate", "subClassOf foaf:Person", C.blue],
    ["emp:JobOffer",  "job offer",              C.blue],
    ["emp:Company",  "subClassOf schema:Organization", C.blue],
    ["emp:Skill",    "skill / competence",      C.blue],
    ["emp:Sector",   "work sector",             C.blue],
    ["emp:Location", "geographic location",     C.blue],
  ];
  classes.forEach(([cls, note, col], i) => {
    const row = i < 3 ? i : i - 3;
    const col2 = i < 3 ? 0 : 1;
    const x = 0.4 + col2 * 4.7;
    const y = 3.35 + row * 0.56;
    s.addShape(pres.shapes.RECTANGLE, { x, y, w: 4.45, h: 0.45, fill: { color: C.ice }, line: { color: "D4DCF0", width: 1 } });
    s.addText(cls, { x: x + 0.1, y, w: 2.2, h: 0.45, fontSize: 11, bold: true, color: col, fontFace: "Consolas", valign: "middle", margin: 0 });
    s.addText(note, { x: x + 2.3, y, w: 2.1, h: 0.45, fontSize: 10, color: C.muted, fontFace: F.body, valign: "middle", italic: true, margin: 0 });
  });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 7 – DATASET: DISCRIMINATORY DATA
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  lightSlide(s);
  addSectionChip(s, "RDF Dataset", C.alert);
  addTitle(s, "Discriminatory Proxies in the Dataset");
  addRule(s);
  addPageNum(s, 7);

  s.addText("Some offers intentionally contain problematic fields — designed to exercise EaaS policies", {
    x: 0.4, y: 1.4, w: 9.2, h: 0.35,
    fontSize: 11, italic: true, color: C.muted, fontFace: F.body, margin: 0,
  });

  const rows = [
    ["job-003", "Junior Frontend Dev", "emp:ageRangeMax = 30",                "Ageism",                   C.alert],
    ["job-004", "Logistics Manager",   "emp:genderPreference = male",          "Gender discrimination",    C.alert],
    ["job-008", "Gov. Contractor Dev", "emp:nationalityReq = IT",              "Nationality discrimination",C.alert],
    ["job-016", "Trainee Sw. Eng.",    "emp:ageRangeMin=20, ageRangeMax=26",   "Ageism (narrow age band)", C.alert],
    ["job-012", "Sales Representative","salary missing, source=unknown",       "Poor data quality",        C.orange],
  ];

  // header
  const headers = ["Offer ID", "Title", "Problematic field", "Violation type"];
  const colW = [1.1, 2.1, 3.1, 2.7];
  const startX = 0.4;
  let y = 1.88;
  let x = startX;
  headers.forEach((h, i) => {
    s.addShape(pres.shapes.RECTANGLE, { x, y, w: colW[i], h: 0.38, fill: { color: C.navy }, line: { color: C.navy, width: 0 } });
    s.addText(h, { x, y, w: colW[i], h: 0.38, fontSize: 10, bold: true, color: C.white, align: "center", fontFace: F.body, margin: 0 });
    x += colW[i];
  });

  rows.forEach((row, ri) => {
    y = 2.3 + ri * 0.52;
    x = startX;
    const bg = ri % 2 === 0 ? C.ice : C.white;
    row.slice(0, 4).forEach((cell, ci) => {
      s.addShape(pres.shapes.RECTANGLE, { x, y, w: colW[ci], h: 0.48, fill: { color: bg }, line: { color: "D4DCF0", width: 1 } });
      const isCode = ci <= 2;
      s.addText(cell, {
        x: x + 0.05, y, w: colW[ci] - 0.1, h: 0.48,
        fontSize: 10, color: ci === 3 ? row[4] : (isCode && ci === 2 ? C.alert : C.text),
        bold: ci === 3, fontFace: ci === 2 ? "Consolas" : F.body, valign: "middle", margin: 0,
      });
      x += colW[ci];
    });
  });

  badge(s, 0.4, 5.15, 3.0, "5 offers flagged by /api/jobs/risky", C.alert, C.white);
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 8 – SPARQL QUERIES
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  lightSlide(s);
  addSectionChip(s, "SPARQL");
  addTitle(s, "SPARQL Queries — Examples");
  addRule(s);
  addPageNum(s, 8);

  // Query 1 – basic
  s.addText("Query 1 · Offers by sector", {
    x: 0.4, y: 1.45, w: 9.2, h: 0.35,
    fontSize: 12, bold: true, color: C.navy, fontFace: F.title, margin: 0,
  });
  s.addShape(pres.shapes.RECTANGLE, { x: 0.4, y: 1.85, w: 9.2, h: 1.3, fill: { color: C.code }, line: { color: C.code, width: 0 }, shadow: makeShadow() });
  s.addText(
    "SELECT ?j ?title ?sal ?loc WHERE {\n" +
    "  ?j a emp:JobOffer ; dct:title ?title ; emp:hasSector ?sec ;\n" +
    "     emp:hasLocation ?locR ; emp:salary ?sal .\n" +
    "  ?sec rdfs:label \"Information Technology\" .\n" +
    "  ?locR rdfs:label ?loc\n" +
    "}",
    { x: 0.55, y: 1.9, w: 9.0, h: 1.2, fontSize: 10, color: "86EFAC", fontFace: "Consolas", valign: "top", margin: 0 }
  );

  // Query 2 – multi-condition
  s.addText("Query 2 · Multi-condition match (skill overlap + location + experience)", {
    x: 0.4, y: 3.28, w: 9.2, h: 0.35,
    fontSize: 12, bold: true, color: C.navy, fontFace: F.title, margin: 0,
  });
  s.addShape(pres.shapes.RECTANGLE, { x: 0.4, y: 3.68, w: 9.2, h: 1.7, fill: { color: C.code }, line: { color: C.code, width: 0 }, shadow: makeShadow() });
  s.addText(
    "SELECT ?j ?title ?candLoc ?jobLoc ?remote ?reqExp ?yoe\n" +
    "       (COUNT(DISTINCT ?sharedSkill) AS ?shared)\n" +
    "WHERE {\n" +
    "  emp:cand-001 emp:hasLocation ?cLoc ; emp:yearsOfExperience ?yoe ; emp:hasSkill ?sharedSkill .\n" +
    "  ?cLoc rdfs:label ?candLoc .\n" +
    "  ?j a emp:JobOffer ; dct:title ?title ; emp:hasSkill ?sharedSkill ;\n" +
    "     emp:requiredExperience ?reqExp ; emp:remote ?remote ; emp:hasLocation ?jLoc .\n" +
    "  ?jLoc rdfs:label ?jobLoc\n" +
    "} GROUP BY ... ORDER BY DESC(?shared)",
    { x: 0.55, y: 3.73, w: 9.0, h: 1.6, fontSize: 9.5, color: "86EFAC", fontFace: "Consolas", valign: "top", margin: 0 }
  );
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 9 – DaaS ENDPOINTS
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  lightSlide(s);
  addSectionChip(s, "DaaS REST");
  addTitle(s, "DaaS — REST Endpoints (base URL :8081)");
  addRule(s);
  addPageNum(s, 9);

  const endpoints = [
    ["GET", "/api/candidates",           "List all candidates"],
    ["GET", "/api/candidates/{id}",       "Single candidate by ID"],
    ["GET", "/api/jobs",                  "Search offers (sector, location, minSalary, remote) — multi-condition SPARQL"],
    ["GET", "/api/jobs/{id}",             "Single offer by ID"],
    ["GET", "/api/jobs/sector/{sector}",  "Offers by sector"],
    ["GET", "/api/jobs/location/{loc}",   "Offers by city"],
    ["GET", "/api/jobs/risky",            "Offers with discriminatory proxies or unverified provenance"],
    ["GET", "/api/match/candidate/{id}",  "Recommendations for candidate (skill + location + experience)"],
  ];

  endpoints.forEach((e, i) => {
    const y = 1.45 + i * 0.5;
    const bg = i % 2 === 0 ? C.ice : C.white;
    s.addShape(pres.shapes.RECTANGLE, { x: 0.4, y, w: 9.2, h: 0.46, fill: { color: bg }, line: { color: "D4DCF0", width: 1 } });
    // badge GET
    s.addShape(pres.shapes.ROUNDED_RECTANGLE, { x: 0.5, y: y + 0.08, w: 0.55, h: 0.28, fill: { color: "0D9488" }, rectRadius: 0.04, line: { color: "0D9488", width: 0 } });
    s.addText("GET", { x: 0.5, y: y + 0.08, w: 0.55, h: 0.28, fontSize: 9, bold: true, color: C.white, align: "center", fontFace: F.body, margin: 0 });
    s.addText(e[1], { x: 1.15, y: y + 0.04, w: 3.5, h: 0.38, fontSize: 10, color: C.blue, bold: true, fontFace: "Consolas", valign: "middle", margin: 0 });
    s.addText(e[2], { x: 4.75, y: y + 0.04, w: 4.75, h: 0.38, fontSize: 10, color: C.text, fontFace: F.body, valign: "middle", margin: 0 });
    if (i === 6) { // risky special
      s.addShape(pres.shapes.ROUNDED_RECTANGLE, { x: 4.75, y: y + 0.1, w: 0.5, h: 0.25, fill: { color: C.alert }, rectRadius: 0.04, line: { color: C.alert, width: 0 } });
      s.addText("⚠", { x: 4.75, y: y + 0.1, w: 0.5, h: 0.25, fontSize: 10, color: C.white, align: "center", fontFace: F.body, margin: 0 });
    }
  });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 10 – SECTION 2 divider
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  darkSlide(s);
  s.addShape(pres.shapes.RECTANGLE, { x: 0, y: 0, w: 0.22, h: 5.625, fill: { color: C.green }, line: { color: C.green, width: 0 } });
  s.addText("Section 2", { x: 0.5, y: 1.3, w: 9, h: 0.45, fontSize: 13, color: "9FB4CC", fontFace: F.body, italic: true, margin: 0 });
  s.addText("EaaS — Ethics\nas a Service", {
    x: 0.5, y: 1.75, w: 9, h: 2.0,
    fontSize: 40, bold: true, color: C.white, fontFace: F.title, margin: 0,
  });
  s.addText("Member 2  ·  ~10-12 minutes", {
    x: 0.5, y: 4.5, w: 9, h: 0.4,
    fontSize: 12, color: "9FB4CC", fontFace: F.body, margin: 0,
  });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 11 – EAAS WORKFLOW
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  lightSlide(s);
  addSectionChip(s, "EaaS", C.green);
  addTitle(s, "EaaS — Evaluation Workflow");
  addRule(s);
  addPageNum(s, 11);

  const steps = [
    { n: "1", t: "Structured Request", d: "The client sends: action, candidateId, jobId,\nrequester, purpose, declaredRisk (ignored)", color: C.blue },
    { n: "2", t: "Fetch Canonical Data from DaaS", d: "EaaS fetches candidate and offer directly\nfrom DaaS — never trust the caller", color: C.orange },
    { n: "3", t: "Case Analysis — Policy Engine", d: "Each policy is applied to the real data.\nProduces findings with severity and evidence", color: C.amber },
    { n: "4", t: "Governance Decision", d: "DecisionMaker aggregates findings:\nCRITICAL→REJECT, HIGH→ESCALATE, MEDIUM→REVISE, none→PROCEED", color: C.green },
    { n: "5", t: "Audit and Response", d: "AuditRecord saved to memory and JSONL.\nFull response with provenance and required actions", color: "9B59B6" },
  ];

  steps.forEach((st, i) => {
    const y = 1.44 + i * 0.78;
    s.addShape(pres.shapes.OVAL, { x: 0.4, y: y, w: 0.48, h: 0.48, fill: { color: st.color }, line: { color: st.color, width: 0 } });
    s.addText(st.n, { x: 0.4, y, w: 0.48, h: 0.48, fontSize: 14, bold: true, color: C.white, align: "center", fontFace: F.title, margin: 0 });
    if (i < 4) {
      s.addShape(pres.shapes.LINE, { x: 0.63, y: y + 0.48, w: 0, h: 0.3, line: { color: C.muted, width: 1.5 } });
    }
    card(s, 1.1, y, 8.5, 0.68, C.ice);
    s.addText(st.t, { x: 1.2, y: y + 0.04, w: 8.3, h: 0.28, fontSize: 12, bold: true, color: C.navy, fontFace: F.title, margin: 0 });
    s.addText(st.d, { x: 1.2, y: y + 0.3, w: 8.3, h: 0.35, fontSize: 10, color: C.text, fontFace: F.body, margin: 0 });
  });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 12 – EVALUATIONREQUEST STRUCTURE
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  lightSlide(s);
  addSectionChip(s, "EaaS", C.green);
  addTitle(s, "EvaluationRequest — Structure");
  addRule(s);
  addPageNum(s, 12);

  // JSON example
  s.addShape(pres.shapes.RECTANGLE, { x: 0.4, y: 1.45, w: 5.1, h: 3.9, fill: { color: C.code }, line: { color: C.code, width: 0 }, shadow: makeShadow() });
  s.addText(
    "{\n" +
    "  \"action\": \"recommend-job\",\n" +
    "  \"candidateId\": \"cand-001\",\n" +
    "  \"jobId\": \"job-003\",\n" +
    "  \"requester\": \"ui-user:edoardo\",\n" +
    "  \"purpose\": \"show recommendation\",\n" +
    "\n" +
    "  // EaaS ignores this field!\n" +
    "  \"declaredRisk\": \"LOW\"\n" +
    "}",
    { x: 0.55, y: 1.5, w: 4.8, h: 3.75, fontSize: 11, color: "86EFAC", fontFace: "Consolas", valign: "top", margin: 0 }
  );

  // annotations
  const notes = [
    { y: 1.6, text: "Type of requested action", color: C.blue },
    { y: 2.1, text: "IDs — EaaS uses them to fetch\nthe real data from DaaS", color: C.blue },
    { y: 2.9, text: "Transparency: who requests and why", color: C.green },
    { y: 3.65, text: "⚠ Not trusted — EaaS\nrecalculates risk autonomously", color: C.alert },
  ];
  notes.forEach(n => {
    s.addShape(pres.shapes.LINE, { x: 5.5, y: n.y + 0.05, w: 0.6, h: 0, line: { color: n.color, width: 1, dashType: "dash" } });
    s.addText(n.text, { x: 6.2, y: n.y, w: 3.4, h: 0.5, fontSize: 10, color: n.color, fontFace: F.body, margin: 0 });
  });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 13 – THE 4 POLICIES
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  lightSlide(s);
  addSectionChip(s, "Policy", C.green);
  addTitle(s, "The 4 Ethical Policies — External JSON Files");
  addRule(s);
  addPageNum(s, 13);

  s.addText("Editable without recompiling · loaded at startup from policies/*.json", {
    x: 0.4, y: 1.38, w: 9.2, h: 0.28,
    fontSize: 10, italic: true, color: C.muted, fontFace: F.body, margin: 0,
  });

  const policies = [
    {
      id: "P-DISCR-001", sev: "CRITICAL", sevColor: C.alert,
      name: "Discriminatory Proxies",
      desc: "Forbidden: ageRangeMin/Max, genderPreference, nationalityReq\nin offers. Regulation: D.Lgs. 198/2006, EU Dir. 2000/78/EC.",
    },
    {
      id: "P-PROV-002", sev: "HIGH", sevColor: C.orange,
      name: "Provenance & Data Quality",
      desc: "Only trusted sources (company-portal, synthetic).\nIf source = third-party-scraper / unknown → ESCALATE.",
    },
    {
      id: "P-ACC-003", sev: "MEDIUM", sevColor: C.amber,
      name: "Accessibility & Disability",
      desc: "If the candidate has a disability, the offer must be\nflagged disabilityFriendly. Otherwise → REVISE.\nRegulation: UN CRPD Art.27, L. 68/1999.",
    },
    {
      id: "P-TRANS-004", sev: "LOW", sevColor: "2C5FDC",
      name: "Transparency & Purpose",
      desc: "requester and purpose must be declared.\nWithout them the recommendation is not transparent.\nRegulation: GDPR Art.13/22.",
    },
  ];

  policies.forEach((p, i) => {
    const col = i < 2 ? 0 : 1;
    const row = i % 2;
    const x = 0.4 + col * 4.75;
    const y = 1.78 + row * 1.85;
    card(s, x, y, 4.55, 1.72, C.ice);
    s.addShape(pres.shapes.RECTANGLE, { x, y, w: 1.0, h: 0.32, fill: { color: p.sevColor }, line: { color: p.sevColor, width: 0 } });
    s.addText(p.sev, { x, y, w: 1.0, h: 0.32, fontSize: 8, bold: true, color: C.white, align: "center", fontFace: F.body, margin: 0 });
    s.addText(p.id, { x: 1.05 + (col * 4.75), y, w: 1.4, h: 0.32, fontSize: 9, bold: true, color: p.sevColor, fontFace: "Consolas", margin: 0 });
    s.addText(p.name, { x: x + 0.1, y: y + 0.38, w: 4.3, h: 0.35, fontSize: 13, bold: true, color: C.navy, fontFace: F.title, margin: 0 });
    s.addText(p.desc, { x: x + 0.1, y: y + 0.75, w: 4.3, h: 0.88, fontSize: 10, color: C.text, fontFace: F.body, margin: 0 });
  });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 14 – SCENARIO PROCEED
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  lightSlide(s);
  addSectionChip(s, "Scenario 1", C.green);
  addTitle(s, "Scenario PROCEED — cand-001 + job-001");
  addRule(s);
  addPageNum(s, 14);

  // Request
  card(s, 0.4, 1.45, 4.3, 1.5, C.ice);
  s.addText("Request", { x: 0.55, y: 1.5, w: 3.9, h: 0.32, fontSize: 11, bold: true, color: C.navy, fontFace: F.title, margin: 0 });
  s.addText([
    { text: "candidateId: ", options: { color: C.muted } }, { text: "cand-001", options: { bold: true, color: C.text } }, { text: "  (Alice Bianchi)", options: { color: C.muted, breakLine: true } },
    { text: "jobId: ", options: { color: C.muted } },       { text: "job-001", options: { bold: true, color: C.text } },  { text: "  (Backend Java Dev)", options: { color: C.muted, breakLine: true } },
    { text: "source: ", options: { color: C.muted } },      { text: "company-portal", options: { bold: true, color: C.green } },
  ], { x: 0.55, y: 1.88, w: 4.0, h: 1.0, fontSize: 11, fontFace: F.body, margin: 0 });

  // Evaluation
  card(s, 0.4, 3.1, 4.3, 1.1, C.ice);
  s.addText("EaaS Evaluation", { x: 0.55, y: 3.16, w: 3.9, h: 0.32, fontSize: 11, bold: true, color: C.navy, fontFace: F.title, margin: 0 });
  s.addText("No policy violated — 4 policies evaluated", { x: 0.55, y: 3.52, w: 4.0, h: 0.28, fontSize: 11, color: C.muted, fontFace: F.body, margin: 0 });
  s.addText("Findings: 0", { x: 0.55, y: 3.82, w: 4.0, h: 0.28, fontSize: 11, color: C.green, bold: true, fontFace: F.body, margin: 0 });

  // Decision big badge
  card(s, 4.85, 1.45, 4.75, 3.0, "F0FFF4");
  s.addShape(pres.shapes.RECTANGLE, { x: 4.85, y: 1.45, w: 4.75, h: 0.42, fill: { color: C.green }, line: { color: C.green, width: 0 } });
  s.addText("DECISION", { x: 4.85, y: 1.45, w: 4.75, h: 0.42, fontSize: 11, bold: true, color: C.white, align: "center", fontFace: F.body, margin: 0 });
  s.addText("PROCEED", { x: 4.85, y: 2.0, w: 4.75, h: 0.9, fontSize: 46, bold: true, color: C.green, align: "center", fontFace: F.title, margin: 0 });
  s.addText("Risk: LOW", { x: 4.85, y: 2.85, w: 4.75, h: 0.35, fontSize: 14, color: C.green, align: "center", bold: true, fontFace: F.body, margin: 0 });
  s.addText('"No policy was triggered: the recommendation\nis consistent with the active ethical policies."', {
    x: 4.95, y: 3.3, w: 4.55, h: 0.8,
    fontSize: 10, color: C.muted, italic: true, fontFace: F.body, align: "center", margin: 0,
  });

  s.addText("Applied policies: P-ACC-003 · P-DISCR-001 · P-PROV-002 · P-TRANS-004", {
    x: 0.4, y: 4.3, w: 9.2, h: 0.3,
    fontSize: 10, color: C.muted, fontFace: F.body, margin: 0,
  });
  s.addText("Required actions: none  ·  The offer can be shown to the candidate", {
    x: 0.4, y: 4.65, w: 9.2, h: 0.3,
    fontSize: 11, bold: true, color: C.green, fontFace: F.body, margin: 0,
  });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 15 – SCENARIO REJECT
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  lightSlide(s);
  addSectionChip(s, "Scenario 2", C.alert);
  addTitle(s, "Scenario REJECT — cand-001 + job-003");
  addRule(s);
  addPageNum(s, 15);

  card(s, 0.4, 1.45, 4.3, 1.5, C.ice);
  s.addText("Request", { x: 0.55, y: 1.5, w: 3.9, h: 0.32, fontSize: 11, bold: true, color: C.navy, fontFace: F.title, margin: 0 });
  s.addText([
    { text: "candidateId: ", options: { color: C.muted } }, { text: "cand-001", options: { bold: true, color: C.text } }, { text: "  (Alice Bianchi, 28 years old)", options: { color: C.muted, breakLine: true } },
    { text: "jobId: ", options: { color: C.muted } },       { text: "job-003", options: { bold: true, color: C.text } },  { text: "  (Junior Frontend Dev)", options: { color: C.muted, breakLine: true } },
    { text: "source: ", options: { color: C.muted } },      { text: "third-party-scraper", options: { bold: true, color: C.alert } },
  ], { x: 0.55, y: 1.88, w: 4.0, h: 1.0, fontSize: 11, fontFace: F.body, margin: 0 });

  // Findings
  const findings = [
    { id: "P-DISCR-001", sev: "CRITICAL", color: C.alert, evidence: 'Offer carries "ageRangeMax" = 30' },
    { id: "P-PROV-002", sev: "HIGH",     color: C.orange, evidence: 'Source "third-party-scraper" not trusted' },
  ];
  findings.forEach((f, i) => {
    const y = 3.1 + i * 1.0;
    card(s, 0.4, y, 4.3, 0.88, i === 0 ? "FFF5F5" : "FFF8F0");
    s.addShape(pres.shapes.RECTANGLE, { x: 0.4, y, w: 0.8, h: 0.34, fill: { color: f.color }, line: { color: f.color, width: 0 } });
    s.addText(f.sev, { x: 0.4, y, w: 0.8, h: 0.34, fontSize: 8, bold: true, color: C.white, align: "center", fontFace: F.body, margin: 0 });
    s.addText(f.id, { x: 1.25, y, w: 2.0, h: 0.34, fontSize: 9, bold: true, color: f.color, fontFace: "Consolas", margin: 0 });
    s.addText(f.evidence, { x: 0.55, y: y + 0.38, w: 4.0, h: 0.44, fontSize: 10, color: C.text, fontFace: F.body, italic: true, margin: 0 });
  });

  // Decision
  card(s, 4.85, 1.45, 4.75, 3.0, "FFF5F5");
  s.addShape(pres.shapes.RECTANGLE, { x: 4.85, y: 1.45, w: 4.75, h: 0.42, fill: { color: C.alert }, line: { color: C.alert, width: 0 } });
  s.addText("DECISION", { x: 4.85, y: 1.45, w: 4.75, h: 0.42, fontSize: 11, bold: true, color: C.white, align: "center", fontFace: F.body, margin: 0 });
  s.addText("REJECT", { x: 4.85, y: 1.95, w: 4.75, h: 0.9, fontSize: 48, bold: true, color: C.alert, align: "center", fontFace: F.title, margin: 0 });
  s.addText("Risk: CRITICAL", { x: 4.85, y: 2.85, w: 4.75, h: 0.35, fontSize: 14, color: C.alert, align: "center", bold: true, fontFace: F.body, margin: 0 });
  s.addText("Required actions:", { x: 4.95, y: 3.3, w: 4.55, h: 0.28, fontSize: 10, bold: true, color: C.navy, fontFace: F.body, margin: 0 });
  s.addText([
    { text: "Hide the offer from recommendations", options: { bullet: true, breakLine: true } },
    { text: "Notify the responsible for the discriminatory field", options: { bullet: true, breakLine: true } },
    { text: "Escalate to compliance officer", options: { bullet: true } },
  ], { x: 4.95, y: 3.6, w: 4.55, h: 0.8, fontSize: 10, color: C.text, fontFace: F.body, margin: 0 });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 16 – SECTION 3 divider
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  darkSlide(s);
  s.addShape(pres.shapes.RECTANGLE, { x: 0, y: 0, w: 0.22, h: 5.625, fill: { color: "9B59B6" }, line: { color: "9B59B6", width: 0 } });
  s.addText("Section 3", { x: 0.5, y: 1.3, w: 9, h: 0.45, fontSize: 13, color: "9FB4CC", fontFace: F.body, italic: true, margin: 0 });
  s.addText("Audit, Integration\nand Reflection", {
    x: 0.5, y: 1.75, w: 9, h: 2.0,
    fontSize: 40, bold: true, color: C.white, fontFace: F.title, margin: 0,
  });
  s.addText("Member 3  ·  ~10-12 minutes", {
    x: 0.5, y: 4.5, w: 9, h: 0.4,
    fontSize: 12, color: "9FB4CC", fontFace: F.body, margin: 0,
  });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 17 – AUDIT TRAIL
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  lightSlide(s);
  addSectionChip(s, "Audit Trail", "9B59B6");
  addTitle(s, "Audit Trail & Provenance");
  addRule(s);
  addPageNum(s, 17);

  // JSON example
  s.addShape(pres.shapes.RECTANGLE, { x: 0.4, y: 1.45, w: 5.5, h: 3.95, fill: { color: C.code }, line: { color: C.code, width: 0 }, shadow: makeShadow() });
  s.addText(
    "{\n" +
    "  \"auditId\": \"audit-8f43...\",\n" +
    "  \"timestamp\": \"2026-05-19T08:07:50Z\",\n" +
    "  \"request\": { \"candidateId\": \"cand-001\",\n" +
    "               \"jobId\": \"job-003\" ... },\n" +
    "  \"evaluatedCandidate\": { ... },\n" +
    "  \"evaluatedJob\": { ... },\n" +
    "  \"riskLevel\": \"CRITICAL\",\n" +
    "  \"decision\": \"REJECT\",\n" +
    "  \"findings\": [ { ... } ],\n" +
    "  \"appliedPolicies\": [\n" +
    "    \"P-ACC-003\", \"P-DISCR-001\",\n" +
    "    \"P-PROV-002\", \"P-TRANS-004\"\n" +
    "  ]\n" +
    "}",
    { x: 0.55, y: 1.52, w: 5.2, h: 3.8, fontSize: 10.5, color: "86EFAC", fontFace: "Consolas", valign: "top", margin: 0 }
  );

  // annotations
  const ann = [
    { y: 1.55, t: "Unique UUID per evaluation",           c: "9B59B6" },
    { y: 2.0,  t: "Timestamp ISO 8601",                   c: "9B59B6" },
    { y: 2.35, t: "Original request (snapshot)",          c: C.blue },
    { y: 2.7,  t: "Real data fetched from DaaS\n→ provenance tracked", c: C.blue },
    { y: 3.55, t: "Final decision",                       c: C.alert },
    { y: 4.15, t: "All policies evaluated\n(including those not violated)", c: C.green },
  ];
  ann.forEach(a => {
    s.addShape(pres.shapes.LINE, { x: 5.9, y: a.y + 0.05, w: 0.5, h: 0, line: { color: a.c, width: 1, dashType: "dash" } });
    s.addText(a.t, { x: 6.5, y: a.y, w: 3.1, h: 0.5, fontSize: 10, color: a.c, fontFace: F.body, margin: 0 });
  });

  s.addText("Persistence: in-memory (session) + append-only JSONL on disk", {
    x: 0.4, y: 5.2, w: 9.2, h: 0.3,
    fontSize: 10, color: C.muted, italic: true, fontFace: F.body, margin: 0,
  });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 18 – DaaS ↔ EaaS INTEGRATION FLOW
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  lightSlide(s);
  addSectionChip(s, "Integration", "9B59B6");
  addTitle(s, "Full DaaS ↔ EaaS Flow");
  addRule(s);
  addPageNum(s, 18);

  const flow = [
    { actor: "USER",   desc: "Selects candidate in browser",                              color: "9B59B6" },
    { actor: "CLIENT", desc: "GET /api/match/candidate/{id} → DaaS",                color: C.blue },
    { actor: "DaaS",   desc: "Multi-condition SPARQL → list of MatchDTOs",           color: C.blue },
    { actor: "CLIENT", desc: "Shows list · User clicks 'Evaluate'",                 color: C.blue },
    { actor: "CLIENT", desc: "POST /api/ethics/evaluate → EaaS",                    color: "9B59B6" },
    { actor: "EaaS",   desc: "GET /api/candidates/{id} + /api/jobs/{id} → DaaS",    color: C.green },
    { actor: "EaaS",   desc: "PolicyEngine + DecisionMaker → EvaluationResponse",   color: C.green },
    { actor: "EaaS",   desc: "AuditService.save() → JSONL",                         color: C.green },
    { actor: "CLIENT", desc: "Displays: decision, findings, audit link",                 color: "9B59B6" },
  ];

  flow.forEach((f, i) => {
    const y = 1.45 + i * 0.45;
    const actorW = 1.1;
    s.addShape(pres.shapes.RECTANGLE, { x: 0.4, y, w: actorW, h: 0.37, fill: { color: f.color }, line: { color: f.color, width: 0 } });
    s.addText(f.actor, { x: 0.4, y, w: actorW, h: 0.37, fontSize: 9, bold: true, color: C.white, align: "center", fontFace: F.body, margin: 0 });
    const bg = i % 2 === 0 ? C.ice : C.white;
    s.addShape(pres.shapes.RECTANGLE, { x: 1.6, y, w: 8.0, h: 0.37, fill: { color: bg }, line: { color: "D4DCF0", width: 1 } });
    s.addText(f.desc, { x: 1.7, y, w: 7.8, h: 0.37, fontSize: 11, color: C.text, fontFace: F.body, valign: "middle", margin: 0 });
    if (i < flow.length - 1) {
      s.addShape(pres.shapes.LINE, { x: 0.93, y: y + 0.37, w: 0, h: 0.08, line: { color: C.muted, width: 1 } });
    }
  });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 19 – LIVE DEMO
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  darkSlide(s);
  s.addShape(pres.shapes.RECTANGLE, { x: 0, y: 0, w: 0.22, h: 5.625, fill: { color: "9B59B6" }, line: { color: "9B59B6", width: 0 } });

  s.addText("Live Demo", {
    x: 0.5, y: 1.5, w: 9, h: 1.2,
    fontSize: 52, bold: true, color: C.white, fontFace: F.title, margin: 0,
  });

  const steps = [
    "1  Start DaaS (:8081) · EaaS (:8082) · Client (:8080)",
    "2  Select cand-001 (Alice Bianchi) → view matches",
    "3  Evaluate job-001 → PROCEED",
    "4  Evaluate job-003 → REJECT  (ageRangeMax + scraper)",
    "5  Open /audit → verify JSONL on disk",
    "6  Edit a JSON policy → restart EaaS → review",
  ];
  s.addText(steps.join("\n"), {
    x: 0.5, y: 2.85, w: 9, h: 2.5,
    fontSize: 13, color: "CADCFC", fontFace: F.body, valign: "top", margin: 0,
    lineSpacingMultiple: 1.5,
  });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 20 – CRITICAL REFLECTION
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  lightSlide(s);
  addSectionChip(s, "Critical Reflection", C.alert);
  addTitle(s, "System Limitations");
  addRule(s);
  addPageNum(s, 20);

  const limits = [
    { t: "Indirect proxies not detected",   d: "The system checks explicit fields (age, gender, nationality). Subtle proxies like postcode, university, or name are not intercepted.", color: C.alert },
    { t: "Synthetic dataset",               d: "Data is artificially generated. In production, GDPR consent management, encryption, and right-to-explanation flows would be required.", color: C.orange },
    { t: "Policies written in Java",        d: "JSON rules declare severity and parameters, but the logic is Java code. Changing behavior requires a developer.", color: C.amber },
    { t: "No aggregate fairness metrics",   d: "Each offer is checked individually, but no systematic disparities across candidate groups are measured over time.", color: C.amber },
  ];

  limits.forEach((l, i) => {
    const col = i < 2 ? 0 : 1;
    const row = i % 2;
    const x = 0.4 + col * 4.75;
    const y = 1.45 + row * 1.9;
    card(s, x, y, 4.55, 1.75, C.ice);
    s.addShape(pres.shapes.RECTANGLE, { x, y, w: 0.22, h: 1.75, fill: { color: l.color }, line: { color: l.color, width: 0 } });
    s.addText(l.t, { x: x + 0.32, y: y + 0.1, w: 4.1, h: 0.38, fontSize: 12, bold: true, color: C.navy, fontFace: F.title, margin: 0 });
    s.addText(l.d, { x: x + 0.32, y: y + 0.5, w: 4.1, h: 1.15, fontSize: 11, color: C.text, fontFace: F.body, margin: 0 });
  });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 21 – WHAT NOT TO AUTOMATE
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  lightSlide(s);
  addSectionChip(s, "Critical Reflection", C.alert);
  addTitle(s, "What Should NOT Be Decided Automatically");
  addRule(s);
  addPageNum(s, 21);

  const items = [
    { icon: "✗", t: "Final candidate selection",         d: "The system recommends and evaluates ethically, but the hiring decision must always be made by a human." },
    { icon: "✗", t: "Permanent exclusion of an offer",   d: "REJECT blocks the recommendation, not the offer from the system. Human review is required before permanently removing it." },
    { icon: "✗", t: "Interpreting complex legal contexts",d: "A nationalityReq may be legal (e.g. national security requirements). EaaS lacks context: ESCALATE → human." },
    { icon: "✗", t: "Evaluating capabilities and soft skills", d: "The dataset captures years of experience and technical skills. Motivation, communication, and company culture remain outside the model." },
  ];

  items.forEach((it, i) => {
    const y = 1.45 + i * 0.97;
    s.addShape(pres.shapes.OVAL, { x: 0.4, y: y + 0.1, w: 0.45, h: 0.45, fill: { color: C.alert }, line: { color: C.alert, width: 0 } });
    s.addText(it.icon, { x: 0.4, y: y + 0.1, w: 0.45, h: 0.45, fontSize: 14, bold: true, color: C.white, align: "center", fontFace: F.body, margin: 0 });
    s.addText(it.t, { x: 1.0, y, w: 8.6, h: 0.38, fontSize: 13, bold: true, color: C.navy, fontFace: F.title, margin: 0 });
    s.addText(it.d, { x: 1.0, y: y + 0.38, w: 8.6, h: 0.5, fontSize: 11, color: C.text, fontFace: F.body, margin: 0 });
    if (i < 3) {
      s.addShape(pres.shapes.LINE, { x: 0.4, y: y + 0.93, w: 9.2, h: 0, line: { color: "E5E7EB", width: 0.5 } });
    }
  });
})();

// ══════════════════════════════════════════════════════════════
//  SLIDE 22 – CONCLUSIONS / Q&A
// ══════════════════════════════════════════════════════════════
(function () {
  const s = pres.addSlide();
  darkSlide(s);
  s.addShape(pres.shapes.RECTANGLE, { x: 0, y: 0, w: 0.22, h: 5.625, fill: { color: C.blue }, line: { color: C.blue, width: 0 } });

  s.addText("Conclusions", { x: 0.5, y: 0.5, w: 9, h: 0.5, fontSize: 14, color: "9FB4CC", fontFace: F.body, italic: true, margin: 0 });
  s.addText([
    { text: "Data + Ethics + Architecture", options: { bold: true, breakLine: true } },
    { text: "as a unified system", options: {} },
  ], { x: 0.5, y: 0.95, w: 9, h: 1.4, fontSize: 36, color: C.white, fontFace: F.title, margin: 0 });

  const points = [
    { t: "DaaS",   d: "635 RDF/XML triples · 8 REST endpoints · multi-condition SPARQL",       color: C.blue },
    { t: "EaaS",   d: "4 external JSON policies · PROCEED / REVISE / ESCALATE / REJECT",           color: C.green },
    { t: "Audit",  d: "Every decision is tracked, justified, and persisted to disk",                     color: "9B59B6" },
    { t: "Ethics", d: "Not a final checkbox: ethics is an integral part of the architecture",            color: C.amber },
  ];
  points.forEach((p, i) => {
    const y = 2.5 + i * 0.66;
    s.addShape(pres.shapes.RECTANGLE, { x: 0.5, y, w: 0.9, h: 0.42, fill: { color: p.color }, line: { color: p.color, width: 0 } });
    s.addText(p.t, { x: 0.5, y, w: 0.9, h: 0.42, fontSize: 10, bold: true, color: C.white, align: "center", fontFace: F.body, margin: 0 });
    s.addText(p.d, { x: 1.55, y, w: 8.0, h: 0.42, fontSize: 12, color: "CADCFC", fontFace: F.body, valign: "middle", margin: 0 });
  });

  s.addShape(pres.shapes.LINE, { x: 0.5, y: 5.1, w: 4.0, h: 0, line: { color: C.blue, width: 1 } });
  s.addText("Questions?", { x: 0.5, y: 5.15, w: 9, h: 0.3, fontSize: 12, color: "9FB4CC", fontFace: F.body, italic: true, margin: 0 });
})();

// ─── WRITE FILE ───
pres.writeFile({ fileName: "slides/SoSE-Employment-DaaS-EaaS.pptx" })
  .then(() => console.log("✅  slides/SoSE-Employment-DaaS-EaaS.pptx created"))
  .catch(e => { console.error(e); process.exit(1); });
