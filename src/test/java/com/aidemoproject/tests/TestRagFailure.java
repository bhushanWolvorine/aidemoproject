package com.aidemoproject.tests;

import com.aidemoproject.base.BaseTest;
import com.aidemoproject.common.listener.ExtentReportListener;
import com.aidemoproject.common.AgentResponse;
import com.aidemoproject.validators.journeyspecfic.upi.UpiRetrievalValidator;
import com.aidemoproject.constants.CommunicationConstants;
import com.aidemoproject.utils.LoggerUtil;
import com.aidemoproject.websocket.client.GenericWebSocketClient;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class TestRagFailure extends BaseTest {

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
    public void testRagFailure_Amount2000_StaleBalance() throws Exception {
        String sessionId = sessionId();

        ExtentReportListener.logInfo("Starting RAG failure test (stale balance), sessionId=" + sessionId);

        ws.send(sessionId, CommunicationConstants.USER_MESSAGE_RAG_FAILURE, CommunicationConstants.LANGUAGE_HINDI);
        ExtentReportListener.logInfo("Sent 2000 INR request expected to expose stale balance");

        String ragLie = ws.waitFor(CommunicationConstants.HINDI_BALANCE_TOKEN,
                CommunicationConstants.TIMEOUT_OTP_REQUEST_SECONDS);


        List<String> log = ws.getConversationLog();
        ExtentReportListener.logJson("Conversation log for RAG failure", log.toString());


        AgentResponse response = AgentResponse.builder()
                .statusCode(200)
                .body(ragLie)
                .conversationLog(log)
                .sessionId(sessionId)
                .userMessage(CommunicationConstants.USER_MESSAGE_RAG_FAILURE)
                .journeyType(CommunicationConstants.JOURNEY_TYPE_UPI_GENERIC)
                .build();


        List<String> issues = new UpiRetrievalValidator().getRetrievalIssues(
                response.getBody(), response.getConversationLog(), CommunicationConstants.RAG_KEY_BALANCE
        );


//        Assert.assertTrue(issues.isEmpty(), "Their are failures detected for RAG");

        Assert.assertFalse(issues.isEmpty(), "Their are failures detected for RAG");


        ExtentReportListener.logInfo("RAG retrieval issues size=" + issues.size());

        ///  pushing to report portal for stale data and expected data

        LoggerUtil.info("RAG FAILURE TEST → PASSED (caught stale data)");
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
