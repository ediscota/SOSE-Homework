package it.univaq.sose.daas.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents an employer company stored in the employment RDF dataset")
public record CompanyDTO(
        @Schema(description = "Unique identifier assigned by the triplestore", example = "company-1") String id,
        @Schema(description = "Legal or trade name of the company", example = "Acme Corp") String name,
        @Schema(description = "Company website URL (optional)", example = "https://www.acme.com") String website
) {}
