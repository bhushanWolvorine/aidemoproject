package com.aidemoproject.tests.dummy;

import com.aidemoproject.base.BaseTest;

public class TestOrchestrationUPI extends BaseTest {

//    @Test
//    public void testUpiHighValuePayment_FullOrchestration() throws Exception {
//        String sessionId = "upi-" + System.currentTimeMillis();
//        List<String> conversationLog = new ArrayList<>();
//
//        // 1. Start journey — send user message
//        ws.send(sessionId, "Send 5000 rupees to mom", "en");
//        conversationLog.add("USER → Send 5000 rupees to mom");
//
//        // 2. Wait for agent to finish the journey (look for "end" message)
//        String finalAgentMessage = waitForEndMessage(sessionId, conversationLog, 30);
//
//        // 3. NOW build the REAL response — no hardcoding!
//        AgentResponse realResponse = AgentResponse.builder()
//                .statusCode(200)
//                .body(finalAgentMessage)                     // ← REAL final message
//                .conversationLog(new ArrayList<>(conversationLog))  // ← Full real log
//                .sessionId(sessionId)
//                .userMessage("Send 5000 rupees to mom")
//                .journeyType("upi")
//                .build();
//
//        // 4. Run all validators on the REAL response
//        assertNoHallucination(realResponse);
//        assertCorrectOrchestration(realResponse);
//        assertRagAccuracy(realResponse);
//        assertRbiCompliance(realResponse);
//        assertGpt4oJudgeGivesA(realResponse);
//
//        System.out.println("ORCHESTRATION → PERFECT");
//    }
//
//    private String waitForEndMessage(String sessionId, List<String> log, int timeoutSec) throws Exception {
//        long deadline = System.currentTimeMillis() + timeoutSec * 1000;
//
//        while (System.currentTimeMillis() < deadline) {
//            String msg = ws.getNextMessage(1000);  // non-blocking poll
//            if (msg == null) continue;
//
//            log.add("AGENT → " + msg);
//            System.out.println("LOGGED → " + msg);
//
//            // Parse JSON safely
//            try {
//                JsonNode node = new ObjectMapper().readTree(msg);
//                if (node.has("type") && "end".equals(node.get("type").asText())) {
//                    return node.has("content") ? node.get("content").asText() : "";
//                }
//            } catch (Exception e) {
//                // Not JSON — could be plain text (some agents do this)
//                if (msg.toLowerCase().contains("sent") || msg.contains("भेज दिया")) {
//                    return msg;
//                }
//            }
//        }
//        throw new AssertionError("Agent never sent 'end' message — journey incomplete!");
//    }
}
