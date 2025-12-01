package com.aidemoproject.validator.orchestration;

import java.util.List;

/**
 * Validates that the agent follows the correct tool sequence for a given
 * journey. One implementation per journey (UPI, KYC, Email Update, etc.)
 */
public interface OrchestrationValidator {

	/**
	 * Checks if the conversation followed the exact required tool order.
	 * 
	 * @param conversationLog Full list of messages (tool calls + text)
	 * @param journeyContext  Optional context (session ID, user intent)
	 * @return true if orchestration is perfect
	 */
	boolean isValidSequence(List<String> conversationLog, String journeyContext);

	/**
	 * Returns list of tools that were called (for reporting)
	 */
	List<String> getActualToolSequence(List<String> conversationLog);

	/**
	 * Returns missing or unexpected tools (for debugging)
	 */
	OrchestrationIssue getIssues(List<String> conversationLog);

	/**
	 * Score from 0.0 (broken) to 1.0 (perfect)
	 */
	double getOrchestrationScore(List<String> conversationLog);

	/**
	 * Human-readable name of the journey this validator handles
	 */
	String getJourneyName();
}