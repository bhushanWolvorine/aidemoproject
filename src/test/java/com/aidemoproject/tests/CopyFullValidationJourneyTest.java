package com.aidemoproject.tests;

import com.aidemoproject.base.BaseTest;
import com.aidemoproject.common.listener.ExtentReportListener;
import com.aidemoproject.common.AgentResponse;
import com.aidemoproject.validators.journeyspecfic.upi.UpiPaymentValidator;
import com.aidemoproject.common.validationreport.ValidationReport;
import com.aidemoproject.constants.CommunicationConstants;
import com.aidemoproject.judge.OpenAIJudge;
import com.aidemoproject.websocket.client.GenericWebSocketClient;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class CopyFullValidationJourneyTest extends BaseTest {

    private final OpenAIJudge judge = new OpenAIJudge();

    private GenericWebSocketClient ws;


    @BeforeMethod
    public void setup() throws Exception {
        ws = new GenericWebSocketClient(WS_URL);
        ws.setMessageHandler(message -> {
            System.out.println("LOGGED → " + message);

        });
        ExtentReportListener.logInfo("[TEST SETUP] Ready — Fresh connection for this test");
    }

        @Test
        public void testUpiHighValuePayment_FullValidationJourney() throws Exception {
        List<String> journeyLog = new ArrayList<>();
        String sessionId = sessionId();

        System.out.println("\nSTARTING FULL VALIDATION JOURNEY — UPI High-Value Payment");
        ExtentReportListener.logInfo("Starting copy full validation journey — UPI high-value payment, sessionId=" + sessionId);

        // === STEP 1: User initiates Request
        ExtentReportListener.logInfo("Step 1: Sending high-value UPI request (Hindi) over WebSocket");
        ws.send(sessionId, CommunicationConstants.USER_MESSAGE_HIGH_VALUE, CommunicationConstants.LANGUAGE_HINDI);
        ws.waitFor(CommunicationConstants.EXPECTED_OTP_REQUEST, CommunicationConstants.TIMEOUT_OTP_REQUEST_SECONDS);
       journeyLog.addAll(ws.getConversationLog());

        // === STEP 2: User replies with OTP ===
        ExtentReportListener.logInfo("Step 2: Sending valid OTP (Hindi) over WebSocket");
        ws.send(sessionId, CommunicationConstants.VALID_OTP, CommunicationConstants.LANGUAGE_HINDI);
        ws.waitFor("\"type\":\"end\"", CommunicationConstants.TIMEOUT_JOURNEY_COMPLETION_SECONDS);

        journeyLog.addAll(ws.getConversationLog());

        // === FINAL RESPONSE FROM MOCK SERVER ===
        String finalReply = journeyLog.get(journeyLog.size() - 1);
        System.out.println("AGENT FINAL REPLY: " + finalReply);
        ExtentReportListener.logInfo("Final agent reply (copy journey): " + finalReply);




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
        System.out.println("====================================================================");

        ExtentReportListener.logInfo("Copy journey validation summary - " +
                "passed=" + report.isPassed() +
                ", hallucinationScore=" + report.getHallucinationScore() +
                ", orchestrationScore=" + report.getOrchestrationScore() +
                ", retrievalAccuracy=" + report.getRetrievalAccuracy() +
                ", complianceScore=" + report.getComplianceScore());


        Assert.assertTrue(report.isPassed(),"VALIDATION FAILED");



        String verdict = judge.judge(journeyLog, "upi-high-value.txt");
        System.out.println("\nOpen AI  FINAL JUDGMENT:");
        System.out.println(verdict);
        ExtentReportListener.logInfo("Copy journey OpenAI final judgment: " + verdict);

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
        ExtentReportListener.logInfo("End of copy full validation journey - all validators passed with OpenAI grade A");
    }

    @AfterMethod
    public void teardown() throws Exception {
        ExtentReportListener.logInfo("\n[TEST TEARDOWN] Closing connection...");

        if (ws != null) {
            try {
                ws.close();
            } catch (Exception e) {
                ExtentReportListener.logWarning("Warning: Failed to close WebSocket: " + e.getMessage());
            }
        }
        ExtentReportListener.logInfo("[TEST TEARDOWN] Done — Clean state");
    }



    }





