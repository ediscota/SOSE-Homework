package it.univaq.sose.daas.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents a geographic location stored in the employment RDF dataset")
public record LocationDTO(
        @Schema(description = "Unique identifier assigned by the triplestore", example = "location-1") String id,
        @Schema(description = "Human-readable location label (city or region)", example = "Milan") String label
) {}
