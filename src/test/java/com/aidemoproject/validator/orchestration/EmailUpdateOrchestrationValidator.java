package com.aidemoproject.validator.orchestration;

import java.util.List;

public class EmailUpdateOrchestrationValidator implements OrchestrationValidator {
	private static final List<String> SEQ = List.of("validate_email", "send_otp", "verify_otp");

	@Override
	public String getJourneyName() {
		return "Email Update";
	}

	@Override
	public boolean isValidSequence(List<String> conversationLog, String journeyContext) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getActualToolSequence(List<String> conversationLog) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrchestrationIssue getIssues(List<String> conversationLog) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getOrchestrationScore(List<String> conversationLog) {
		// TODO Auto-generated method stub
		return 0;
	}
}