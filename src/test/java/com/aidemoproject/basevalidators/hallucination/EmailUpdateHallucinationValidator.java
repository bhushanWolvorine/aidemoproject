package com.aidemoproject.basevalidators.hallucination;


import java.util.List;

public class EmailUpdateHallucinationValidator implements HallucinationValidator {

 @Override
 public boolean hasHallucination(List<String> conversationLog, String journeyContext) {
     boolean hasVerifyOtpTool = conversationLog.stream()
         .anyMatch(msg -> msg.contains("verify_email_otp") || msg.contains("verify_otp"));

     boolean claimsUpdated = conversationLog.stream()
         .anyMatch(msg -> {
             String lower = msg.toLowerCase();
             return lower.contains("updated") || 
                    lower.contains("changed") || 
                    lower.contains("बदल") || 
                    lower.contains("अपडेट");
         });

     return claimsUpdated && !hasVerifyOtpTool;
 }

 @Override
 public double getHallucinationScore(List<String> conversationLog, String journeyContext) {
     return hasHallucination(conversationLog, journeyContext) ? 1.0 : 0.0;
 }

 @Override
 public String getJourneyName() {
     return "Email Update";
 }
}