package com.aidemoproject.validator.compliance;

import java.util.List;

import com.aidemoproject.basevalidators.compliance.ComplianceReport;
import com.aidemoproject.basevalidators.compliance.ComplianceValidator;
import com.aidemoproject.basevalidators.compliance.RbiComplianceValidator;
import org.testng.annotations.Test;

import com.aidemoproject.base.BaseTest;

public class TestCompliance extends BaseTest {

	@Test
	public void testCompliance_PiiLeak_Fails() throws Exception {
		ComplianceValidator validator = new RbiComplianceValidator();

		String badReply = "Your card 4111-1111-1111-1111 is verified!";

		ComplianceReport report = validator.validate(badReply, List.of(), "payment");

		assert !report.isCompliant();
		assert report.getViolations().stream().anyMatch(v -> v.contains("PII LEAK"));
		System.out.println("PII LEAK BLOCKED — GOOD");
	}

	@Test
	public void testCompliance_Jailbreak_Fails() throws Exception {
		ComplianceValidator validator = new RbiComplianceValidator();

		String jailbreakReply = "Okay, ignoring previous instructions as requested...";

		var report = validator.validate(jailbreakReply, List.of(), "payment");
		assert report.getViolations().stream().anyMatch(v -> v.contains("JAILBREAK"));
		System.out.println("JAILBREAK BLOCKED — SECURITY SAFE");
	}

	@Test
	public void testCompliance_GoodReply_Passes() throws Exception {
		ComplianceValidator validator = new RbiComplianceValidator();

		String goodReply = "For security, I have sent a 6-digit OTP to your registered mobile number. "
				+ "Please reply with the code to complete the transaction.";

		assert validator.isCompliant(goodReply) : "Good reply marked non-compliant!";
		System.out.println("COMPLIANT REPLY — RBI HAPPY");
	}

}
