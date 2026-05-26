package it.univaq.sose.daas.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload for creating or updating a location")
public record LocationRequest(
        @Schema(description = "Human-readable location label (city or region)", example = "Milan") String label
) {}
