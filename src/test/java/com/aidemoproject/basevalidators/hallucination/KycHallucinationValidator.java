package com.aidemoproject.basevalidators.hallucination;

import java.util.List;

public class KycHallucinationValidator implements HallucinationValidator {

 @Override
 public boolean hasHallucination(List<String> conversationLog, String journeyContext) {
     boolean hasVerifyTool = conversationLog.stream()
         .anyMatch(msg -> msg.contains("verify_document") || msg.contains("verify_aadhaar"));

     boolean claimsVerified = conversationLog.stream()
         .anyMatch(msg -> {
             String lower = msg.toLowerCase();
             return lower.contains("verified") || 
                    lower.contains("सत्यापित") || 
                    lower.contains("successfully") || 
                    lower.contains("हो गया");
         });

     return claimsVerified && !hasVerifyTool;
 }

 @Override
 public double getHallucinationScore(List<String> conversationLog, String journeyContext) {
     return hasHallucination(conversationLog, journeyContext) ? 1.0 : 0.0;
 }

 @Override
 public String getJourneyName() {
     return "KYC";
 }
}