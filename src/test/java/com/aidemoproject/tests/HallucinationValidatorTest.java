package com.aidemoproject.tests;


import java.util.List;

import com.aidemoproject.common.listener.ExtentReportListener;
import com.aidemoproject.basevalidators.hallucination.HallucinationValidator;
import com.aidemoproject.constants.CommunicationConstants;
import com.aidemoproject.websocket.client.GenericWebSocketClient;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.aidemoproject.base.BaseTest;
import com.aidemoproject.common.AgentResponse;
import com.aidemoproject.validators.journeyspecfic.upi.UpiHallucinationValidator;
import com.aidemoproject.utils.LoggerUtil;

public class HallucinationValidatorTest extends BaseTest {

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
    public void testUpiHallucination_CatchesOtpLie() throws Exception {
        String sessionId = sessionId();

        ExtentReportListener.logInfo("Starting UPI hallucination test for OTP lie, sessionId=" + sessionId);

        // Mock implemented
        ExtentReportListener.logInfo("Sending high-value UPI payment request over WebSocket");
        ws.send(sessionId, CommunicationConstants.USER_MESSAGE_HIGH_VALUE, CommunicationConstants.LANGUAGE_HINDI);
        ws.waitFor(CommunicationConstants.EXPECTED_OTP_REQUEST, CommunicationConstants.TIMEOUT_OTP_REQUEST_SECONDS);


        List<String> lyingLog = ws.getConversationLog();
        ExtentReportListener.logJson("Conversation log before injected lie", lyingLog.toString());
        lyingLog.add("{\"type\":\"text\",\"content\":\"" + CommunicationConstants.HINDI_OTP_LIE_TEXT + "\"}");

        AgentResponse response = AgentResponse.builder()
                .statusCode(200)
                .body(CommunicationConstants.HINDI_OTP_LIE_BODY)
                .conversationLog(lyingLog)
                .sessionId(sessionId)
                .userMessage(CommunicationConstants.USER_MESSAGE_HIGH_VALUE)
                .journeyType(CommunicationConstants.JOURNEY_TYPE_UPI_GENERIC)
                .build();

        ExtentReportListener.logJson("AgentResponse for hallucination check", response.toString());

        HallucinationValidator validator = new UpiHallucinationValidator();
        boolean lied = validator.hasHallucination(response.getConversationLog(), response.getSessionId());

        assert lied : "UPI HALLUCINATION NOT DETECTED!";
        System.out.println("UPI HALLUCINATION → CAUGHT");
        LoggerUtil.info("UPI HALLUCINATION → CAUGHT");
        ExtentReportListener.logInfo("UPI hallucination successfully detected for sessionId=" + sessionId);
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