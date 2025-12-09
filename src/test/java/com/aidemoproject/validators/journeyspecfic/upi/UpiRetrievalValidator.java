package com.aidemoproject.validators.journeyspecfic.upi;

import com.aidemoproject.basevalidators.retrieval.RetrievalValidator;

import java.util.ArrayList;
import java.util.List;


public class UpiRetrievalValidator implements RetrievalValidator {


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
		return issues.isEmpty() ? 1.0 : 0.0;
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



		return issues;
	}

	@Override
	public String getJourneyName() {
		return "UPI / Banking Retrieval";
	}
}