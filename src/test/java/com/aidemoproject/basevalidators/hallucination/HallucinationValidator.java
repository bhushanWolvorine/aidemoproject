package com.aidemoproject.basevalidators.hallucination;

import java.util.List;

/**
 * Interface for detecting hallucinations in AI agent responses. Designed to
 * support multiple journeys (UPI, KYC, Email, etc.) Future implementations can
 * be added without changing existing code.
 */
public interface HallucinationValidator {

	/**
	 * Detects if the agent claimed to do something it didn't actually do.
	 * 
	 * @param conversationLog Full list of messages (tool calls + text)
	 * @param journeyContext  Optional context (e.g., session ID, user message)
	 * @return true if hallucination detected
	 */
	boolean hasHallucination(List<String> conversationLog, String journeyContext);

	/**
	 * Returns a score from 0.0 (clean) to 1.0 (full hallucination)
	 */
	double getHallucinationScore(List<String> conversationLog, String journeyContext);

	/**
	 * Name of the journey this validator is for (for logging/reporting)
	 */
	String getJourneyName();
}