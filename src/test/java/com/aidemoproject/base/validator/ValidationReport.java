package com.aidemoproject.base.validator;


import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
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
}