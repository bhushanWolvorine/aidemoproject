package com.aidemoproject.tests;


import com.aidemoproject.base.BaseTest;
import com.aidemoproject.common.listener.ExtentReportListener;
import com.aidemoproject.common.AgentResponse;
import com.aidemoproject.validators.journeyspecfic.upi.UpiOrchestrationValidator;
import com.aidemoproject.validators.journeyspecfic.upi.UpiPaymentValidator;
import com.aidemoproject.common.validationreport.ValidationReport;
import com.aidemoproject.basevalidators.retrieval.UpiRetrievalValidator;
import com.aidemoproject.constants.CommunicationConstants;
import com.aidemoproject.websocket.client.GenericWebSocketClient;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestFlowOrchestrationCopy extends BaseTest {

    private GenericWebSocketClient ws;


    @BeforeMethod
    public void setup() throws Exception {
        ws = new GenericWebSocketClient(WS_URL);
        ws.setMessageHandler(message -> {
            System.out.println("LOGGED → " + message);

        });
        ExtentReportListener.logInfo("[TEST SETUP] Ready — Fresh connection for this test");
    }

    @Test()
    public void testRag() {

        String sessionId = sessionId();

        try {
            ws.send(sessionId, CommunicationConstants.USER_MESSAGE_RAG_FAILURE, CommunicationConstants.LANGUAGE_HINDI);

            String ragLie = ws.waitFor(CommunicationConstants.HINDI_BALANCE_TOKEN, CommunicationConstants.TIMEOUT_OTP_REQUEST_SECONDS);

            CopyOnWriteArrayList<String> conversationLog = ws.getConversationLog();


            AgentResponse response = AgentResponse.builder()
                    .statusCode(200)
                    .sessionId(sessionId)
                    .body(ragLie)
                    .conversationLog(conversationLog)
                    .userMessage(CommunicationConstants.USER_MESSAGE_RAG_FAILURE)
                    .journeyType(CommunicationConstants.JOURNEY_TYPE_UPI_GENERIC)
                    .build();


            List<String> issues = new UpiRetrievalValidator().getRetrievalIssues(response.getBody(), response.getConversationLog(), CommunicationConstants.HINDI_BALANCE_TOKEN);


            // Assert.assertEquals(issues.size(),0, "Issues Detected");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test()
    public void copyTestOrchestartion() {

        String sessionId = sessionId();


        try {
            ws.send(sessionId, CommunicationConstants.USER_MESSAGE_HIGH_VALUE, CommunicationConstants.LANGUAGE_HINDI);
            ExtentReportListener.logInfo("Initiating the transaction communications");

            ws.waitFor(CommunicationConstants.EXPECTED_OTP_REQUEST, CommunicationConstants.TIMEOUT_OTP_REQUEST_SECONDS);

            ws.send(sessionId, CommunicationConstants.VALID_OTP, CommunicationConstants.LANGUAGE_HINDI);

            ws.waitFor(CommunicationConstants.SUCCESS_CONFIRMATION_IN_HINDI, CommunicationConstants.TIMEOUT_JOURNEY_COMPLETION_SECONDS);


            CopyOnWriteArrayList<String> conversationLog = ws.getConversationLog();


            String lastMessage = extractFinalAgentMessage(conversationLog);

            List<String> onlyToolCalls = new UpiOrchestrationValidator().getActualToolSequence(conversationLog);


            AgentResponse response = AgentResponse.builder().statusCode(200)
                    .body(lastMessage)
                    .conversationLog(conversationLog)
                    .sessionId(sessionId)
                    .userMessage(CommunicationConstants.USER_MESSAGE_HIGH_VALUE)
                    .journeyType(CommunicationConstants.JOURNEY_TYPE_UPI_GENERIC)
                    .build();


            ValidationReport report = UpiPaymentValidator.create().verify(response);


            Assert.assertEquals(report.getFailures().size(), 0);

            ExtentReportListener.logInfo(report.toString());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
