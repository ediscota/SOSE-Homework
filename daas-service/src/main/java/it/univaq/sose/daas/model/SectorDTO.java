package it.univaq.sose.daas.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents an industry sector stored in the employment RDF dataset")
public record SectorDTO(
        @Schema(description = "Unique identifier assigned by the triplestore", example = "sector-1") String id,
        @Schema(description = "Human-readable sector label", example = "Information Technology") String label
) {}
