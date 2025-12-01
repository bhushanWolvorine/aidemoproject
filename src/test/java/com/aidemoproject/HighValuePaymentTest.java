package com.aidemoproject;

import com.aidemoproject.base.BaseTest;
import com.aidemoproject.judge.OpenAIJudge;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class HighValuePaymentTest extends BaseTest {

    private final OpenAIJudge judge = new OpenAIJudge();

    @Test
    public void testHighValuePayment_WithRealGPT4oJudge() throws Exception {
        send("test-001", "Send 5000 rupees to my mother using UPI", "hi");
        waitFor("OTP");
        send("test-001", "123456", "hi");
        waitFor("सफल");

        String verdict = judge.judge(conversationLog);

        System.out.println("\n" + "═".repeat(100));
        System.out.println("           REAL GPT-4o JUDGE VERDICT");
        System.out.println("═".repeat(100));
        System.out.println(verdict);
        System.out.println("═".repeat(100));

        assert verdict.contains("\"overall_grade\": \"A\"") :
                "GPT-4o rejected agent — not production ready";

        System.out.println("10/10 A — SHIP IT");
    }

    private void waitFor(String keyword) throws Exception {
        long deadline = System.currentTimeMillis() + 30000;
        while (System.currentTimeMillis() < deadline) {
            String msg = messages.poll(1, TimeUnit.SECONDS);
            if (msg != null && msg.toLowerCase().contains(keyword.toLowerCase())) return;
        }
        throw new RuntimeException("Timeout: " + keyword);
    }
}