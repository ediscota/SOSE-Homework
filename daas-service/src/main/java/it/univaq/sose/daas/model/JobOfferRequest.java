package it.univaq.sose.daas.model;

import java.util.List;

public record JobOfferRequest(String title, String companyId, String sector, String location,
        Boolean remote, Integer requiredExperience, String seniority, Integer salary,
        String postedDate, Boolean disabilityFriendly, List<String> skills,
        Integer ageRangeMin, Integer ageRangeMax, String genderPreference,
        String nationalityRequirement, String source) {}
