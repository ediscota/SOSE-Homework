package it.univaq.sose.daas.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Result of the SPARQL-based job-recommendation query for a single candidate–job pair")
public record MatchDTO(
        @Schema(description = "Candidate identifier", example = "cand-1")
        String candidateId,
        @Schema(description = "Candidate full name", example = "Maria Rossi")
        String candidateName,
        @Schema(description = "Job offer identifier", example = "job-3")
        String jobId,
        @Schema(description = "Job offer title", example = "Senior Java Developer")
        String jobTitle,
        @Schema(description = "Number of skills shared between candidate and job offer", example = "3")
        int sharedSkillCount,
        @Schema(description = "Labels of the skills shared between candidate and job offer")
        List<String> sharedSkills,
        @Schema(description = "True when candidate's location matches the job location or the job is remote",
                example = "true")
        boolean locationCompatible,
        @Schema(description = "True when candidate's years of experience meets the job's minimum requirement",
                example = "true")
        boolean experienceSufficient,
        @Schema(description = "Composite match score in [0, 1] used for ranking; higher is better",
                example = "0.85")
        double score
) { }
