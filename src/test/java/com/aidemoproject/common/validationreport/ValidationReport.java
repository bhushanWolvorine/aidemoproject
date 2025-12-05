package com.aidemoproject.common.validationreport;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder

@AllArgsConstructor
public class ValidationReport {
    private final boolean passed;
    private final List<String> failures;
    private final String journey;
    private final double hallucinationScore;
    private final double orchestrationScore;
    private final double retrievalAccuracy;
    private final double complianceScore;

    public boolean isCritical() {
        return failures.stream()
            .anyMatch(f -> f.toLowerCase().contains("hallucination") || f.toLowerCase().contains("jailbreak"));
    }

    @Override
    public String toString() {
        return "ValidationReport{" +
                "journey='" + journey + '\'' +
                ", passed=" + passed +
                ", hallucinationScore=" + hallucinationScore +
                ", orchestrationScore=" + orchestrationScore +
                ", retrievalAccuracy=" + retrievalAccuracy +
                ", complianceScore=" + complianceScore +
                ", critical=" + isCritical() +
                ", failures=" + failures +
                '}';
    }
}