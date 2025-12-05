package com.aidemoproject.tests;

import com.aidemoproject.base.BaseTest;
import com.aidemoproject.common.listener.ExtentReportListener;
import com.aidemoproject.common.AgentResponse;
import com.aidemoproject.validators.journeyspecfic.upi.UpiPaymentValidator;
import com.aidemoproject.common.validationreport.ValidationReport;
import com.aidemoproject.constants.CommunicationConstants;
import com.aidemoproject.websocket.client.GenericWebSocketClient;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class TestOrchestrationFailure extends BaseTest {

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
    public void testOrchestrationFailure_Amount3000_FakeSuccess() throws Exception {
        String sessionId = sessionId();

        ExtentReportListener.logInfo("Starting orchestration failure test (fake success), sessionId=" + sessionId);

        // 1. Send 3000 → triggers fake success lie (no tool calls, no OTP)
        ws.send(sessionId, CommunicationConstants.USER_MESSAGE_ORCHESTRATION_FAILURE, CommunicationConstants.LANGUAGE_HINDI);
        ExtentReportListener.logInfo("Sent 3000 INR request expected to trigger fake success (no tools, no OTP)");


        String fakeSuccess = ws.waitFor("सफलतापूर्वक", CommunicationConstants.TIMEOUT_OTP_REQUEST_SECONDS);  // or "successful", "भेज दिया"
        System.out.println("FAKE SUCCESS LIE RECEIVED: " + fakeSuccess);
        ExtentReportListener.logInfo("Fake success lie received: " + fakeSuccess);


        List<String> realLog = ws.getConversationLog();
        ExtentReportListener.logJson("Real log for orchestration failure", realLog.toString());


        AgentResponse response = AgentResponse.builder()
                .statusCode(200)
                .body(fakeSuccess)
                .conversationLog(realLog)
                .sessionId(sessionId)
                .userMessage(CommunicationConstants.USER_MESSAGE_ORCHESTRATION_FAILURE)
                .journeyType(CommunicationConstants.JOURNEY_TYPE_UPI_GENERIC)
                .build();


        ValidationReport report = UpiPaymentValidator.create().verify(response);

        Assert.assertEquals(report.getOrchestrationScore(), 0);

        ExtentReportListener.logJson("Full orchestration failure ValidationReport", report.toString());
        ExtentReportListener.logInfo("Orchestration failure report - orchestrationScore=" + report.getOrchestrationScore());

        //   Assert.assertEquals(report.getFailures().size(), 0);


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
