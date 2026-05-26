package it.univaq.sose.daas.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Payload for creating or updating a candidate")
public record CandidateRequest(
        @Schema(description = "Full name", example = "Maria Rossi") String name,
        @Schema(description = "Gender label", example = "female") String gender,
        @Schema(description = "Age in years", example = "28") Integer age,
        @Schema(description = "Nationality", example = "Italian") String nationality,
        @Schema(description = "Recognised disability flag (L. 68/1999)", example = "false") Boolean hasDisability,
        @Schema(description = "City or region label — auto-created in the dataset if absent", example = "Milan") String location,
        @Schema(description = "Years of professional experience", example = "4") Integer yearsOfExperience,
        @Schema(description = "Minimum acceptable annual salary in EUR", example = "35000") Integer minSalary,
        @Schema(description = "Skill labels — each skill is auto-created in the dataset if absent") List<String> skills,
        @Schema(description = "Provenance URI or label", example = "linkedin-import") String source
) {}
