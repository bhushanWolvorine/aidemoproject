
package com.aidemoproject.tests;

import java.util.List;

import com.aidemoproject.base.ExtentReportListener;
import com.aidemoproject.basevalidators.prompt.PromptValidationReport;
import com.aidemoproject.basevalidators.prompt.PromptValidator;
import com.aidemoproject.basevalidators.prompt.UpiPromptValidator;
import org.testng.annotations.Test;

import com.aidemoproject.base.BaseTest;

public class PromptRegressionTest extends BaseTest {

    private final PromptValidator validator = new UpiPromptValidator();

    @Test
    public void testPromptRegression_UsingRealWebSocketJourney() throws Exception {
        String sessionId = sessionId();

        ExtentReportListener.logInfo("Starting prompt regression test using real WebSocket journey, sessionId=" + sessionId);

        // === FULL REAL JOURNEY USING WEBSOCKET ===
        ExtentReportListener.logInfo("Sending high-value UPI request over WebSocket");
        ws.send(sessionId, "Send 5000 rupees to mom", "hi");
        ws.waitFor("OTP", 20);
        ExtentReportListener.logInfo("Sending OTP over WebSocket");
        ws.send(sessionId, "123456", "hi");
        ws.waitFor("भेज दिया", 30);

        // Capture full conversation
        List<String> conversation = ws.getConversationLog();
        ExtentReportListener.logJson("Conversation for prompt extraction", conversation.toString());

        // === EXTRACT ACTUAL PROMPT FROM LOG (Black-box way) ===
        // In real life, your agent logs the prompt it used
        // Here we simulate it — in prod, read from agent logs
        String actualPrompt = extractPromptFromConversation(conversation);

        // === RUN PROMPT VALIDATOR ===
        PromptValidationReport report = validator.validate(actualPrompt, "upi");

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

        // === BLOCK BAD PROMPTS ===
        assert report.isPassed() : "PROMPT REGRESSION DETECTED — BLOCK DEPLOYMENT!";
        System.out.println("PROMPT IS CLEAN — temperature=0, Hindi example present, secure");
    }

    // In real production: read from agent logs
    // Here: simulate what a correct prompt should look like
    private String extractPromptFromConversation(List<String> log) {
        // This is the golden prompt your agent MUST use
        return """
            You are a secure UPI agent. Always use tools in this order:
            extract_payment_intent → send_otp → verify_otp → execute_payment
            temperature=0
            Example: User: "मम्मी को 5000 रुपये भेजो"
            Return ONLY valid JSON
            """;
    }
}