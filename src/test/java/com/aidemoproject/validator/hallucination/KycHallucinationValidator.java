package com.aidemoproject.validator.hallucination;

import java.util.List;

public class KycHallucinationValidator implements HallucinationValidator {
	@Override
	public String getJourneyName() {
		return "KYC";
	}
	// Different rules: "Aadhaar verified" without verify_document tool

	@Override
	public boolean hasHallucination(List<String> conversationLog, String journeyContext) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getHallucinationScore(List<String> conversationLog, String journeyContext) {
		// TODO Auto-generated method stub
		return 0;
	}
}