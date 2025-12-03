package com.aidemoproject.basevalidators.prompt;

import java.util.Collections;
import java.util.List;

public class PromptValidationReport {
	private final boolean passed;
	private final List<String> failures;
	private final String templateName;
	private final double score; // 0.0 = broken, 1.0 = perfect

	public PromptValidationReport(boolean passed, List<String> failures, String templateName, double score) {
		this.passed = passed;
		this.failures = failures != null ? Collections.unmodifiableList(failures) : List.of();
		this.templateName = templateName;
		this.score = score;
	}

	public boolean isPassed() {
		return passed;
	}

	public List<String> getFailures() {
		return failures;
	}

	public String getTemplateName() {
		return templateName;
	}

	public double getScore() {
		return score;
	}

	public boolean isCritical() {
		return !failures.isEmpty();
	}
}