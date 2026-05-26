package it.univaq.sose.daas.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Represents a job offer stored in the employment RDF dataset")
public record JobOfferDTO(
        @Schema(description = "Unique identifier assigned by the triplestore", example = "job-1")
        String id,
        @Schema(description = "Job title", example = "Senior Java Developer")
        String title,
        @Schema(description = "Employer company name", example = "Acme Corp")
        String company,
        @Schema(description = "Industry sector", example = "Information Technology")
        String sector,
        @Schema(description = "City or region where the role is based", example = "Rome")
        String location,
        @Schema(description = "Whether the position is fully remote", example = "true")
        Boolean remote,
        @Schema(description = "Minimum years of experience required", example = "3")
        Integer requiredExperience,
        @Schema(description = "Seniority level", example = "Senior",
                allowableValues = {"Junior", "Mid", "Senior", "Lead", "Executive"})
        String seniority,
        @Schema(description = "Annual salary in EUR", example = "55000")
        Integer salary,
        @Schema(description = "ISO-8601 date the offer was posted", example = "2024-03-15")
        String postedDate,
        @Schema(description = "Whether the position is open to candidates with disabilities (L. 68/1999)",
                example = "true")
        Boolean disabilityFriendly,
        @Schema(description = "Required skills for the role")
        List<String> skills,
        @Schema(description = "[Controversial] Minimum preferred candidate age — present only if the original "
                + "posting included an explicit age preference", example = "25")
        Integer ageRangeMin,
        @Schema(description = "[Controversial] Maximum preferred candidate age", example = "40")
        Integer ageRangeMax,
        @Schema(description = "[Controversial] Gender preference expressed in the original posting", example = "male")
        String genderPreference,
        @Schema(description = "[Controversial] Nationality requirement expressed in the original posting",
                example = "EU citizen")
        String nationalityRequirement,
        @Schema(description = "Provenance URI or label for the data record", example = "indeed-scrape")
        String source
) { }
