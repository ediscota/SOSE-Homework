package it.univaq.sose.daas.model;

import java.util.List;

public record CandidateDTO(
        String id,
        String name,
        String gender,
        Integer age,
        String nationality,
        Boolean hasDisability,
        String location,
        Integer yearsOfExperience,
        Integer minSalary,
        List<String> skills,
        String source
) { }
