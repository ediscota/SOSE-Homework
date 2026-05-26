package it.univaq.sose.daas.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload for creating or updating a sector")
public record SectorRequest(
        @Schema(description = "Human-readable sector label", example = "Information Technology") String label
) {}
