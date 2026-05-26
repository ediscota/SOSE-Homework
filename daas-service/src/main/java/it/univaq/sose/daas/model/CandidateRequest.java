package it.univaq.sose.daas.model;

import java.util.List;

public record CandidateRequest(String name, String gender, Integer age, String nationality,
        Boolean hasDisability, String location, Integer yearsOfExperience, Integer minSalary,
        List<String> skills, String source) {}
