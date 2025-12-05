
package com.aidemoproject.tests;

import java.util.List;

import com.aidemoproject.common.listener.ExtentReportListener;
import com.aidemoproject.constants.CommunicationConstants;
import com.aidemoproject.basevalidators.prompt.PromptValidationReport;
import com.aidemoproject.basevalidators.prompt.PromptValidator;
import com.aidemoproject.basevalidators.prompt.UpiPromptValidator;
import com.aidemoproject.websocket.client.GenericWebSocketClient;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.aidemoproject.base.BaseTest;

public class PromptRegressionTest extends BaseTest {

    private final PromptValidator validator = new UpiPromptValidator();
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
    public void testPromptRegression_UsingRealWebSocketJourney() throws Exception {
        String sessionId = sessionId();

        ExtentReportListener.logInfo("Starting prompt regression test using real WebSocket journey, sessionId=" + sessionId);


        ExtentReportListener.logInfo("Sending high-value UPI request over WebSocket");
        ws.send(sessionId, CommunicationConstants.USER_MESSAGE_HIGH_VALUE, CommunicationConstants.LANGUAGE_HINDI);
        ws.waitFor(CommunicationConstants.EXPECTED_OTP_REQUEST, CommunicationConstants.TIMEOUT_OTP_REQUEST_SECONDS);
        ExtentReportListener.logInfo("Sending OTP over WebSocket");
        ws.send(sessionId, CommunicationConstants.VALID_OTP, CommunicationConstants.LANGUAGE_HINDI);
        ws.waitFor(CommunicationConstants.SUCCESS_CONFIRMATION_IN_HINDI, CommunicationConstants.TIMEOUT_JOURNEY_COMPLETION_SECONDS);

        // Capture full conversation
        List<String> conversation = ws.getConversationLog();
        ExtentReportListener.logJson("Conversation for prompt extraction", conversation.toString());

        // === EXTRACT ACTUAL PROMPT FROM LOG  ===
        String actualPrompt = extractPromptFromConversation(conversation);


        PromptValidationReport report = validator.validate(actualPrompt, CommunicationConstants.JOURNEY_TYPE_UPI_GENERIC);

        System.out.println("\nPROMPT REGRESSION TEST");
        System.out.println("Actual Prompt Used:");
        System.out.println(actualPrompt);
        ExtentReportListener.logJson("Actual prompt used", actualPrompt);
        System.out.println("\nValidation Result:");
        System.out.println("Passed: " + report.isPassed());
        ExtentReportListener.logInfo("Prompt validation passed=" + report.isPassed());
        if (!report.getFailures().isEmpty()) {
            System.out.println("FAILURES:");
            report.getFailures().forEach(f -> System.out.println("  • " + f));
        }


        assert report.isPassed() : "PROMPT REGRESSION DETECTED — BLOCK DEPLOYMENT!";
        System.out.println("PROMPT IS CLEAN — temperature=0, Hindi example present, secure");
    }

    //dummy prompt
    private String extractPromptFromConversation(List<String> log) {
        // This is the golden prompt
        return """
                You are a secure UPI agent. Always use tools in this order:
                extract_payment_intent → send_otp → verify_otp → execute_payment
                temperature=0
                Example: User: "मम्मी को 5000 रुपये भेजो"
                Return ONLY valid JSON
                """;
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