package it.univaq.sose.daas.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents a professional skill stored in the employment RDF dataset")
public record SkillDTO(
        @Schema(description = "Unique identifier assigned by the triplestore", example = "skill-1") String id,
        @Schema(description = "Human-readable skill label", example = "Java") String label
) {}
