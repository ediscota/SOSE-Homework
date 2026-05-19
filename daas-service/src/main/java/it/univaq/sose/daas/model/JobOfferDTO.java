package it.univaq.sose.daas.model;

import java.util.List;

public record JobOfferDTO(
        String id,
        String title,
        String company,
        String sector,
        String location,
        Boolean remote,
        Integer requiredExperience,
        String seniority,
        Integer salary,
        String postedDate,
        Boolean disabilityFriendly,
        List<String> skills,
        // optional "controversial" fields — null when absent
        Integer ageRangeMin,
        Integer ageRangeMax,
        String genderPreference,
        String nationalityRequirement,
        String source
) { }
