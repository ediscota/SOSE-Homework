package it.univaq.sose.daas.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Represents a job-seeker candidate stored in the employment RDF dataset")
public record CandidateDTO(
        @Schema(description = "Unique identifier assigned by the triplestore", example = "cand-1")
        String id,
        @Schema(description = "Full name of the candidate", example = "Maria Rossi")
        String name,
        @Schema(description = "Gender as a free-text label", example = "female")
        String gender,
        @Schema(description = "Age in years", example = "28")
        Integer age,
        @Schema(description = "Nationality or country of citizenship", example = "Italian")
        String nationality,
        @Schema(description = "Whether the candidate has a recognised disability (L. 68/1999)", example = "false")
        Boolean hasDisability,
        @Schema(description = "City or region where the candidate is based", example = "Milan")
        String location,
        @Schema(description = "Total years of professional experience", example = "4")
        Integer yearsOfExperience,
        @Schema(description = "Minimum acceptable annual salary in EUR", example = "35000")
        Integer minSalary,
        @Schema(description = "List of skills the candidate possesses")
        List<String> skills,
        @Schema(description = "Provenance URI or label for the data record", example = "linkedin-import")
        String source
) { }
