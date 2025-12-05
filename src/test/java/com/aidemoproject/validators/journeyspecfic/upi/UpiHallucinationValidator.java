package com.aidemoproject.validators.journeyspecfic.upi;

import java.util.List;
import java.util.Set;

import com.aidemoproject.basevalidators.hallucination.HallucinationValidator;

/**
 * UPI-specific hallucination detector Can be swapped with
 * KycHallucinationValidator, EmailHallucinationValidator, etc.
 */
public class UpiHallucinationValidator implements HallucinationValidator {

	private static final Set<String> OTP_CLAIM_WORDS = Set.of("sent", "bheja", "bhaj", "dispatch", "भेज", "पहुंच",
			"गया");

	private static final Set<String> PAYMENT_CLAIM_WORDS = Set.of("successful", "safal", "ho gaya", "complete", "किया",
			"हो गया");

//	@Override
//	public boolean hasHallucination(List<String> conversationLog, String journeyContext) {
//		boolean sentOtpTool = containsToolCall(conversationLog, "send_otp");
//		boolean executedPaymentTool = containsToolCall(conversationLog, "execute_payment");
//
//		for (String message : conversationLog) {
//			String lower = message.toLowerCase();
//
//			// Claimed OTP was sent but never called tool
//			if (lower.contains("otp") && containsAny(lower, OTP_CLAIM_WORDS) && !sentOtpTool) {
//				System.out.println("HALLUCINATION: Agent lied about sending OTP");
//				return true;
//			}
//
//			// Claimed payment success without tool
//			if (containsAny(lower, PAYMENT_CLAIM_WORDS) && !executedPaymentTool) {
//				System.out.println("HALLUCINATION: Agent lied about payment execution");
//				return true;
//			}
//		}
//		return false;
//	}
	
	
	// Only flag hallucination if agent CLAIMS action but tool was NEVER called
    // journeyContext is sessionid for mongo queries
    // We check that the socket messages contains for the keywords and the tool call for that specific
    // word is also made.
	@Override
	public boolean hasHallucination(List<String> conversationLog, String journeyContext) {
	    boolean sentOtpTool = containsToolCall(conversationLog, "send_otp");
	    boolean executedPayment = containsToolCall(conversationLog, "execute_payment");

	    for (String msg : conversationLog) {
	        String lower = msg.toLowerCase();

	        // Only trigger if agent says "sent" AND tool was NOT called
	        if (lower.contains("otp") && 
	            (lower.contains("भेज") || lower.contains("sent") || lower.contains("dispatch")) &&
	            !sentOtpTool) {
	            return true;
	        }

	        if ((lower.contains("सफल") || lower.contains("successfully") || lower.contains("भेज दिया")) &&
	            !executedPayment) {
	            return true;
	        }
	    }
	    return false;
	}

	@Override
	public double getHallucinationScore(List<String> conversationLog, String journeyContext) {
		return hasHallucination(conversationLog, journeyContext) ? 1.0 : 0.0;
	}

	@Override
	public String getJourneyName() {
		return "UPI Payment";
	}

	// Helper methods
	private boolean containsToolCall(List<String> log, String toolName) {
		return log.stream().anyMatch(m -> m.contains("\"name\":\"" + toolName + "\""));
	}

	private boolean containsAny(String text, Set<String> words) {
		return words.stream().anyMatch(text::contains);
	}
}