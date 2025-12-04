package com.aidemoproject.tests;

import com.aidemoproject.base.BaseTest;
import com.aidemoproject.base.validator.AgentResponse;
import com.aidemoproject.base.validator.UpiPaymentValidator;
import com.aidemoproject.base.validator.ValidationReport;
import com.aidemoproject.constants.CommunicationConstants;
import com.aidemoproject.judge.AiJudge;
import com.aidemoproject.judge.OpenAIJudge;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class CopyFullValidationJourneyTest extends BaseTest {



        private final OpenAIJudge judge = new OpenAIJudge();

        @Test
        public void testUpiHighValuePayment_FullValidationJourney() throws Exception {
        List<String> journeyLog = new ArrayList<>();
        String sessionId = sessionId();

        System.out.println("\nSTARTING FULL VALIDATION JOURNEY — UPI High-Value Payment");

        // === STEP 1: User initiates Request
        ws.send(sessionId, CommunicationConstants.USER_MESSAGE_HIGH_VALUE, CommunicationConstants.LANGUAGE_HINDI);
        ws.waitFor("OTP", 20);
        journeyLog.addAll(ws.getConversationLog());

        // === STEP 2: User replies with OTP ===

        ws.send(sessionId, CommunicationConstants.VALID_OTP, CommunicationConstants.LANGUAGE_HINDI);
        ws.waitFor("\"type\":\"end\"", CommunicationConstants.TIMEOUT_JOURNEY_COMPLETION_SECONDS);

        journeyLog.addAll(ws.getConversationLog());

        // === FINAL RESPONSE FROM MOCK SERVER ===
        String finalReply = journeyLog.get(journeyLog.size() - 1);
        System.out.println("AGENT FINAL REPLY: " + finalReply);




        ValidationReport report = UpiPaymentValidator.create()
                .verify(AgentResponse.builder()
                        .statusCode(200)
                        .body(finalReply)
                        .conversationLog(ws.getConversationLog())
                        .sessionId(sessionId)
                        .userMessage(CommunicationConstants.USER_MESSAGE_HIGH_VALUE)
                        .journeyType(CommunicationConstants.JOURNEY_TYPE_UPI)
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


        Assert.assertTrue(report.isPassed(),"VALIDATION FAILED");



        String verdict = judge.judge(journeyLog, "upi-high-value.txt");
        System.out.println("\nOpen AI  FINAL JUDGMENT:");
        System.out.println(verdict);

        assert verdict.contains("\"overall_grade\": \"A\"") :
                "Open AI  REJECTED AGENT";



        // === LOG TO MONGO
//     mongoLogger.logJourney(
//         "testUpiHighValuePayment_FullValidationJourney",
//         "UPI High-Value Payment",
//         journeyLog,
//         true
//     );

        System.out.println("\n10/10 A — ALL VALIDATORS PASSED ");
    }
    }



