package com.aidemoproject.basevalidators.retrieval;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates retrieval accuracy for banking/payment journeys Compares agent
 * claims against ground truth (mocked or real DB)
 */
public class UpiRetrievalValidator implements RetrievalValidator {

	// Simulated ground truth — in real life: call core banking system
	private static final String GROUND_TRUTH_BALANCE = "₹2000";
	private static final String GROUND_TRUTH_LAST_TXN = "₹8,500 to Rent on 04 Apr 2025";

	@Override
	public boolean isRetrievalAccurate(String agentReply, List<String> conversationLog, String journeyContext) {
		List<String> issues = getRetrievalIssues(agentReply, conversationLog, journeyContext);
		return issues.isEmpty();
	}

	@Override
	public double getRetrievalScore(String agentReply, List<String> conversationLog, String journeyContext) {
		List<String> issues = getRetrievalIssues(agentReply, conversationLog, journeyContext);
		return issues.isEmpty() ? 1.0 : 0.0; //  any mismatch = fail
	}

	@Override
	public List<String> getRetrievalIssues(String agentReply, List<String> conversationLog, String journeyContext) {
		List<String> issues = new ArrayList<>();
		String lowerReply = agentReply.toLowerCase();

		// Check balance
		if (lowerReply.contains("balance") || lowerReply.contains("बैलेंस")) {
			if (!agentReply.contains(GROUND_TRUTH_BALANCE)) {
				issues.add("STALE BALANCE: Agent said wrong amount. Expected: " + GROUND_TRUTH_BALANCE);
			}
		}

		// Check last transaction
		if (lowerReply.contains("last") && lowerReply.contains("transaction")) {
			if (!agentReply.contains("8,500") || !agentReply.contains("Rent")) {
				issues.add("STALE LAST TXN: Agent used outdated transaction data. Expected: " + GROUND_TRUTH_LAST_TXN);
			}
		}

		// Check for hallucinated account numbers, IFSC, etc.
//		Pattern fakeIfsc = Pattern.compile("\\b[A-Z]{4}0[A-Z0-9]{6}\\b");
//		Matcher m = fakeIfsc.matcher(agentReply);
//		if (m.find() && !m.group().equals("SBIN0001234")) { // real IFSC
//			issues.add("HALLUCINATED IFSC CODE: " + m.group());
//		}

		return issues;
	}

	@Override
	public String getJourneyName() {
		return "UPI / Banking Retrieval";
	}
}