// src/test/java/com/aidemoproject/HighValuePaymentTest.java
package com.aidemoproject;

import com.aidemoproject.judge.OpenAIJudge;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.util.concurrent.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HighValuePaymentTest {

    private WebSocketClient client;
    private final BlockingQueue<String> messages = new LinkedBlockingQueue<>();

    @Autowired
    private OpenAIJudge openAIJudge;

    @BeforeAll
    void connect() throws Exception {
        client = new WebSocketClient(new URI("ws://localhost:8765")) {
            @Override public void onOpen(ServerHandshake handshake) {
                System.out.println("Connected to UPI Agent at ws://localhost:8765");
            }
            @Override public void onMessage(String message) {
                messages.add(message);
                System.out.println("RECEIVED → " + message);
            }
            @Override public void onClose(int code, String reason, boolean remote) {
                System.out.println("Disconnected");
            }
            @Override public void onError(Exception ex) { ex.printStackTrace(); }
        };
        client.connectBlocking();
        Thread.sleep(1000);
    }

    @AfterAll
    void cleanup() throws Exception {
        if (client != null && client.isOpen()) client.closeBlocking();
    }

    @Test
    void testHighValuePaymentFlow_WithRealGPT4oJudge() throws Exception {
        String sessionId = "gpt4o-test-001";

        sendMessage(sessionId, "Send 5000 rupees to my mother using UPI", "hi");
        waitForMessageContaining("OTP");
        sendMessage(sessionId, "123456", "hi");
        waitForMessageContaining("सफल");

        // REAL GPT-4o JUDGE
        String verdict = openAIJudge.judge(messages.stream().toList());

        System.out.println("\n" + "═".repeat(100));
        System.out.println(" " + "REAL GPT-4o JUDGE VERDICT");
        System.out.println("═".repeat(100));
        System.out.println(verdict);  // ← FIXED: was verdict(verdict)
        System.out.println("═".repeat(100));

        Assertions.assertTrue(verdict.contains("\"overall_grade\": \"A\""),
                "GPT-4o did not give A grade! Agent failed judgment.");
    }

    private void sendMessage(String sessionId, String text, String lang) throws Exception {
        String json = String.format(
                "{\"session_id\":\"%s\",\"message\":\"%s\",\"language\":\"%s\"}",
                sessionId, text.replace("\"", "\\\""), lang
        );
        client.send(json);
    }

    private String waitForMessageContaining(String text) throws Exception {
        String msg;
        while ((msg = messages.poll(30, TimeUnit.SECONDS)) != null) {
            if (msg.contains(text)) return msg;
        }
        throw new RuntimeException("Timeout waiting for: " + text);
    }
}