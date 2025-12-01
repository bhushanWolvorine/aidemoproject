package com.aidemoproject.validator.retrieval;

import java.util.List;

public class KycRetrievalValidator implements RetrievalValidator {
	@Override
	public String getJourneyName() {
		return "KYC Retrieval";
	}
	// Validate Aadhaar name, DOB, address from DigiLocker

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