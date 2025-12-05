package com.aidemoproject.common;

import com.aidemoproject.common.validationreport.ValidationReport;

public interface AgentResponseValidator {

	ValidationReport verify(AgentResponse response);

	String getJourneyName(); // "UPI Payment", "KYC", "Email Update", etc.
}
