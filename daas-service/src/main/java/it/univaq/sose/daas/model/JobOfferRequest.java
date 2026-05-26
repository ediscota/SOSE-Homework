package it.univaq.sose.daas.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Payload for creating or updating a job offer")
public record JobOfferRequest(
        @Schema(description = "Job title", example = "Senior Java Developer") String title,
        @Schema(description = "ID of the employer company — must already exist", example = "company-1") String companyId,
        @Schema(description = "Industry sector label — auto-created if absent", example = "Information Technology") String sector,
        @Schema(description = "City or region label — auto-created if absent", example = "Rome") String location,
        @Schema(description = "Whether the position is fully remote", example = "true") Boolean remote,
        @Schema(description = "Minimum years of experience required", example = "3") Integer requiredExperience,
        @Schema(description = "Seniority level", example = "Senior") String seniority,
        @Schema(description = "Annual salary in EUR", example = "55000") Integer salary,
        @Schema(description = "ISO-8601 date the offer was posted", example = "2024-03-15") String postedDate,
        @Schema(description = "Open to candidates with disabilities (L. 68/1999)", example = "true") Boolean disabilityFriendly,
        @Schema(description = "Required skill labels — auto-created if absent") List<String> skills,
        @Schema(description = "[Controversial] Minimum preferred candidate age; leave null to omit", example = "25") Integer ageRangeMin,
        @Schema(description = "[Controversial] Maximum preferred candidate age; leave null to omit", example = "40") Integer ageRangeMax,
        @Schema(description = "[Controversial] Gender preference; leave null to omit", example = "male") String genderPreference,
        @Schema(description = "[Controversial] Nationality requirement; leave null to omit", example = "EU citizen") String nationalityRequirement,
        @Schema(description = "Provenance URI or label", example = "indeed-scrape") String source
) {}
