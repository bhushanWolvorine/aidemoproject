package com.aidemoproject.validator.retrieval;

import java.util.List;

public class InsuranceRetrievalValidator implements RetrievalValidator {
	@Override
	public String getJourneyName() {
		return "Insurance Policy";
	}
	// Validate policy number, premium, expiry from insurer DB

	@Override
	public boolean isRetrievalAccurate(String agentReply, List<String> conversationLog, String journeyContext) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getRetrievalScore(String agentReply, List<String> conversationLog, String journeyContext) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> getRetrievalIssues(String agentReply, List<String> conversationLog, String journeyContext) {
		// TODO Auto-generated method stub
		return null;
	}
}