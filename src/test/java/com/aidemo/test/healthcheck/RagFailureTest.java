package com.aidemo.test.healthcheck;

import com.aidemo.integration.pojo.BalanceResponse;
import com.aidemoproject.base.BaseTest;
import com.aidemoproject.constants.CommunicationConstants;
import com.aidemoproject.restclient.RestClientChatServer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RagFailureTest extends BaseTest {

    private final RestClientChatServer chatClient = new RestClientChatServer();

    @Test
    public void testRagFailure_Amount2000_StaleBalance() throws Exception {
        List<String> journeyLog = new ArrayList<>();
        String sessionId = "rag-test-" + System.currentTimeMillis();

        System.out.println("\nSTARTING RAG FAILURE TEST — Amount ₹2000 (Stale Balance Lie)");

        // === STEP 1: Trigger RAG lie via REST endpoint (simulate stale balance)
        BalanceResponse balanceResp = chatClient.postBalanceCheck(Map.of("amount", CommunicationConstants.AMOUNT_TRIGGER_RAG_LIE))
                .then()
                .statusCode(200)
                .extract()
                .as(BalanceResponse.class);

        Assert.assertEquals(balanceResp.getBalance(), 50000, "Balance should be ₹50,000");
        Assert.assertFalse(balanceResp.isSufficient(), "Balance should be insufficient");

        // === STEP 2: User sends message that triggers RAG lie
        ws.send(sessionId, CommunicationConstants.USER_MESSAGE_RAG_FAILURE, CommunicationConstants.LANGUAGE_HINDI);
        journeyLog.addAll(ws.getConversationLog());

        // Wait for agent to lie about balance
        ws.waitFor("बैलेंस सिर्फ़ ₹50,000", 15);

        // === STEP 3: User sends OTP anyway (to complete journey)
        ws.send(sessionId, CommunicationConstants.VALID_OTP, CommunicationConstants.LANGUAGE_HINDI);
        ws.waitFor("\"type\":\"end\"", CommunicationConstants.TIMEOUT_JOURNEY_COMPLETION_SECONDS);
        journeyLog.addAll(ws.getConversationLog());

        // === FINAL: AI Judge evaluates the lie
        String verdict = judge.judge(journeyLog, CommunicationConstants.PROMPT_RAG_FAILURE_PASS);

        System.out.println("\nOpenAI RAG JUDGMENT:");
        System.out.println(verdict);

        // Parse JSON and fail if not "F" (Failed due to RAG lie)
        JsonObject judgment = JsonParser.parseString(verdict).getAsJsonObject();
        String grade = judgment.get("overall_grade").getAsString();

        Assert.assertEquals(grade, "F", "RAG lie should be detected and graded F");
//        Assert.assertTrue(
//                judgment.get("explanation").getAsString().toLowerCase().contains("rag"),
//                "Explanation must mention RAG failure"
//        );

        System.out.println("\nRAG FAILURE TEST PASSED — Stale balance lie correctly triggered & judged");
    }
}
