package it.univaq.sose.daas.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload for creating or updating a skill")
public record SkillRequest(
        @Schema(description = "Human-readable skill label", example = "Java") String label
) {}
