package com.aidemoproject.validator.retrieval;

import java.util.List;

/**
 * Validates that the agent used correct, up-to-date facts from the retrieval
 * system (RAG/Vector DB) One implementation per journey — swap anytime
 */
public interface RetrievalValidator {

	/**
	 * Checks if all facts in the agent reply match the ground-truth source
	 * 
	 * @param agentReply      The final text sent to user
	 * @param conversationLog Full log (to see retrieved chunks if logged)
	 * @param journeyContext  Session/user context
	 * @return true if retrieval was accurate
	 */
	boolean isRetrievalAccurate(String agentReply, List<String> conversationLog, String journeyContext);

	/**
	 * Returns accuracy score 0.0 (all wrong) to 1.0 (perfect)
	 */
	double getRetrievalScore(String agentReply, List<String> conversationLog, String journeyContext);

	/**
	 * Returns list of incorrect or stale facts found
	 */
	List<String> getRetrievalIssues(String agentReply, List<String> conversationLog, String journeyContext);

	/**
	 * Name of the journey this validator supports
	 */
	String getJourneyName();
}