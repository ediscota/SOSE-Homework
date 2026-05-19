package it.univaq.sose.daas.model;

import java.util.List;

public record MatchDTO(
        String candidateId,
        String candidateName,
        String jobId,
        String jobTitle,
        int sharedSkillCount,
        List<String> sharedSkills,
        boolean locationCompatible,
        boolean experienceSufficient,
        double score
) { }
