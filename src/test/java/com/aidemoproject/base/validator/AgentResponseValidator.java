package com.aidemoproject.base.validator;

public interface AgentResponseValidator {

	ValidationReport verify(AgentResponse response);

	String getJourneyName(); // "UPI Payment", "KYC", "Email Update", etc.
}
