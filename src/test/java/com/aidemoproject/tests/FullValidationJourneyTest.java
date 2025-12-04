package com.aidemoproject.tests;





import com.aidemoproject.base.BaseTest;
import com.aidemoproject.base.ExtentReportListener;
import com.aidemoproject.base.validator.AgentResponse;
import com.aidemoproject.base.validator.UpiPaymentValidator;
import com.aidemoproject.base.validator.ValidationReport;
import com.aidemoproject.judge.OpenAIJudge;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class FullValidationJourneyTest extends BaseTest {

 private final OpenAIJudge judge = new OpenAIJudge();

 @Test
 public void testUpiHighValuePayment_FullValidationJourney() throws Exception {
     List<String> journeyLog = new ArrayList<>();
     String sessionId = sessionId();

     System.out.println("\nSTARTING FULL VALIDATION JOURNEY — UPI High-Value Payment");
     ExtentReportListener.logInfo("Starting full validation journey — UPI high-value payment, sessionId=" + sessionId);

     // === STEP 1: User sends high-value payment request ===
     ExtentReportListener.logInfo("Step 1: Sending high-value UPI payment request over WebSocket");
     ws.send(sessionId, "Send 5000 rupees to mom", "hi");
     ws.waitFor("OTP", 20);
     journeyLog.addAll(ws.getConversationLog());

     // === STEP 2: User replies with OTP ===
     ExtentReportListener.logInfo("Step 2: Sending OTP over WebSocket");
     ws.send(sessionId, "123456", "hi");
     ws.waitFor("\"type\":\"end\"", 30);
     journeyLog.addAll(ws.getConversationLog());

     // === FINAL RESPONSE FROM MOCK SERVER ===
     String finalReply = journeyLog.get(journeyLog.size() - 1);
     System.out.println("AGENT FINAL REPLY: " + finalReply);
     ExtentReportListener.logInfo("Final agent reply: " + finalReply);

     // === BUILD UNIFIED RESPONSE OBJECT ===
     AgentResponse response = AgentResponse.builder()
         .statusCode(200)
         .body(finalReply)
         .conversationLog(journeyLog)
         .sessionId(sessionId)
         .userMessage("Send 5000 rupees to mom")
         .journeyType("upi_payment")
         .build();
     ExtentReportListener.logJson("Unified AgentResponse", response.toString());
     
     
     


//     ValidationReport report = UpiPaymentValidator.create()
//    		    .withHallucinationValidator(new UpiHallucinationValidator())
//    		    .withOrchestrationValidator(new UpiOrchestrationValidator())
//    		    .withRetrievalValidator(new UpiRetrievalValidator())
//    		    .withComplianceValidator(new RbiComplianceValidator())
//    		    .verify(response);
     
     
     ValidationReport report = UpiPaymentValidator.create()
    		    .verify(AgentResponse.builder()
    		        .statusCode(200)
    		        .body(finalReply)
    		        .conversationLog(ws.getConversationLog())
    		        .sessionId(sessionId)
    		        .userMessage("Send 5000 rupees to mom")
    		        .journeyType("upi_payment")
    		        .build());


     System.out.println("\n" + "=".repeat(100));
     System.out.println("           FULL VALIDATION REPORT");
     System.out.println("=".repeat(100));
     System.out.println("Journey          : " + report.getJourney());
     System.out.println("Passed           : " + report.isPassed());
     System.out.println("Hallucination    : " + (report.getHallucinationScore() == 1.0 ? "NO" : "YES"));
     System.out.println("Orchestration    : " + (report.getOrchestrationScore() == 1.0 ? "PERFECT" : "BROKEN"));
     System.out.println("Retrieval        : " + (report.getRetrievalAccuracy() == 1.0 ? "ACCURATE" : "STALE"));
     System.out.println("Compliance       : " + (report.getComplianceScore() == 1.0 ? "RBI SAFE" : "VIOLATION"));
     System.out.println("Critical Issues  : " + report.isCritical());
     if (!report.getFailures().isEmpty()) {
         System.out.println("FAILURES:");
         report.getFailures().forEach(f -> System.out.println("  • " + f));
     }
     System.out.println("=".repeat(100));

     ExtentReportListener.logInfo("Validation summary - " +
             "passed=" + report.isPassed() +
             ", hallucinationScore=" + report.getHallucinationScore() +
             ", orchestrationScore=" + report.getOrchestrationScore() +
             ", retrievalAccuracy=" + report.getRetrievalAccuracy() +
             ", complianceScore=" + report.getComplianceScore());


     assert report.isPassed() : "VALIDATION FAILED — DO NOT SHIP";
     assert !report.isCritical() : "CRITICAL ISSUE DETECTED — BLOCKING DEPLOYMENT";


     String verdict = judge.judge(journeyLog);
     System.out.println("\nOpen AI  FINAL JUDGMENT:");
     System.out.println(verdict);
     ExtentReportListener.logInfo("OpenAI final judgment: " + verdict);

     assert verdict.contains("\"overall_grade\": \"A\"") : 
         "Open AI  REJECTED AGENT — NOT PRODUCTION READY";

     // === LOG TO MONGO
//     mongoLogger.logJourney(
//         "testUpiHighValuePayment_FullValidationJourney",
//         "UPI High-Value Payment",
//         journeyLog,
//         true
//     );

     System.out.println("\n10/10 A — ALL VALIDATORS PASSED ");
     ExtentReportListener.logInfo("End of full validation journey - all validators passed with OpenAI grade A");
 }
}