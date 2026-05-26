package it.univaq.sose.daas.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload for creating or updating a company")
public record CompanyRequest(
        @Schema(description = "Legal or trade name", example = "Acme Corp") String name,
        @Schema(description = "Company website URL (optional)", example = "https://www.acme.com") String website
) {}
