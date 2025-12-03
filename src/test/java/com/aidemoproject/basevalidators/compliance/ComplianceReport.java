package com.aidemoproject.basevalidators.compliance;

import java.util.Collections;
import java.util.List;

public class ComplianceReport {
	private final boolean compliant;
	private final List<String> violations;
	private final String domain;
	private final double complianceScore;

	public ComplianceReport(boolean compliant, List<String> violations, String domain, double score) {
		this.compliant = compliant;
		this.violations = violations != null ? Collections.unmodifiableList(violations) : List.of();
		this.domain = domain;
		this.complianceScore = score;
	}

	public boolean isCompliant() {
		return compliant;
	}

	public List<String> getViolations() {
		return violations;
	}

	public String getDomain() {
		return domain;
	}

	public double getComplianceScore() {
		return complianceScore;
	}

	public boolean hasCriticalViolation() {
		return !violations.isEmpty();
	}
}