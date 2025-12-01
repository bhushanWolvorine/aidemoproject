package com.aidemoproject.validator.hallucination;

import java.util.List;

public class EmailUpdateHallucinationValidator implements HallucinationValidator {
	@Override
	public String getJourneyName() {
		return "Email Update";
	}
	// "Email changed" without verify_email_otp

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