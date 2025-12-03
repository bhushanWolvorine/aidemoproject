package com.aidemoproject.tests;

import com.aidemoproject.base.BaseTest;
import com.aidemoproject.base.validator.AgentResponse;
import com.aidemoproject.base.validator.UpiOrchestrationValidator;
import com.aidemoproject.base.validator.UpiPaymentValidator;
import com.aidemoproject.base.validator.ValidationReport;
import com.aidemoproject.constants.CommunicationConstants;
import com.aidemoproject.utils.LoggerUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestFlowOrchestration extends BaseTest {


    @Test
    public void testFlow(){
        List<String> journeyLog = new ArrayList<>();

        String sessionId = sessionId();

        try {
            ws.send(sessionId, CommunicationConstants.hindiMessageStart, CommunicationConstants.hindiLanguage);
            ws.waitFor(CommunicationConstants.Literal_OTP,CommunicationConstants.Wait_Time_OTP);
            ws.send(sessionId,CommunicationConstants.OTP,CommunicationConstants.hindiLanguage);
            ws.waitFor(CommunicationConstants.hindiSent,CommunicationConstants.Wait_Time_OTP);


            CopyOnWriteArrayList<String> cleanLog = ws.getConversationLog();

            String finalAgentMessage = extractFinalAgentMessage(cleanLog);

            List<String> actualSequenceTools =  new UpiOrchestrationValidator().getActualToolSequence(cleanLog);


            System.out.println("Actual tool sequence: " +actualSequenceTools);


                    AgentResponse response = AgentResponse.builder()
                    .statusCode(200)
                    .body(finalAgentMessage)
                    .conversationLog(cleanLog)
                    .sessionId(sessionId)
                    .userMessage("Send 5000...")
                    .journeyType("upi")
                    .build();


            ValidationReport report = UpiPaymentValidator.create().verify(response);




            Assert.assertEquals(report.getFailures().size(), 0);

            /// Logic here to get the report to allure or report portal//

            Assert.assertTrue(report.isPassed(), "Their are failures detected.");


            LoggerUtil.info("Orchestration Score : "+report.getOrchestrationScore());
            LoggerUtil.info("Hallucination Score : "+report.getOrchestrationScore());
            LoggerUtil.info("Compliance Score : "+report.getOrchestrationScore());
            LoggerUtil.info("Retrieval Score : "+report.getOrchestrationScore());


            System.out.println("ss");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private String extractFinalAgentMessage(List<String> log) {

        for (int i = log.size() - 1; i >= 0; i--) {
            String msg = log.get(i);
            try {
                JsonNode node = new ObjectMapper().readTree(msg);
                if (node.has("type") && "end".equals(node.get("type").asText())) {
                    return node.path("content").asText("Journey completed");
                }
                if (node.has("type") && "text".equals(node.get("type").asText())) {
                    String content = node.path("content").asText();
                    if (content.contains("भेज दिया") || content.contains("sent") || content.contains("सुरक्षित")) {
                        return content;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "Transaction completed successfully";
    }


//    @Test
//    public void testRagFailure_Amount2000_StaleBalance() throws Exception {
//        journeyLog.clear();
//        String sessionId = "rag-2000";
//
//
//        ws.send(sessionId, "Send 2000 rupees to mom", "hi");
//
//
//        String ragLie = ws.waitFor("बैलेंस", 20);
//
//
//
//        List<String> log = ws.getConversationLog();
//
//
//        AgentResponse response = AgentResponse.builder()
//                .statusCode(200)
//                .body(ragLie)
//                .conversationLog(log)
//                .sessionId(sessionId)
//                .userMessage("Send 2000...")
//                .journeyType("upi")
//                .build();
//
//
//        List<String> issues = new UpiRetrievalValidator().getRetrievalIssues(
//                response.getBody(), response.getConversationLog(), "balance"
//        );
//
//        assert !issues.isEmpty() : "RAG VALIDATOR FAILED — accepted stale balance!";
//        assert issues.get(0).contains("STALE") : "Wrong issue: " + issues;
//
//        System.out.println("RAG FAILURE TEST → PASSED (caught stale data)");
//
//        LoggerUtil.info("RAG FAILURE TEST → PASSED (caught stale data)");
//    }


//    @Test
//    public void testOrchestrationFailure_Amount3000_FakeSuccess() throws Exception {
//        journeyLog.clear();
//        String sessionId = "orch-fail-3000";
//
//        // 1. Send 3000 → triggers fake success lie (no tool calls, no OTP)
//        ws.send(sessionId, "Send 3000 rupees to mom", "hi");
//
//        // 2. DO NOT wait for "OTP" — wait for the LIE instead!
//        String fakeSuccess = ws.waitFor("सफलतापूर्वक", 20);  // or "successful", "भेज दिया"
//        System.out.println("FAKE SUCCESS LIE RECEIVED: " + fakeSuccess);
//
//        // 3. Get full real log
//        List<String> realLog = ws.getConversationLog();
//
//        // 4. Build REAL response from agent
//        AgentResponse response = AgentResponse.builder()
//                .statusCode(200)
//                .body(fakeSuccess)
//                .conversationLog(realLog)
//                .sessionId(sessionId)
//                .userMessage("Send 3000...")
//                .journeyType("upi")
//                .build();
//
//
//        ValidationReport report = UpiPaymentValidator.create().verify(response);
//
//        System.out.println("ss");
//
//
////        // 5. Orchestration validator should FAIL (no execute_payment tool call)
////        boolean orchestrationOk = new UpiOrchestrationValidator()
////                .hasCorrectFlow(response.getConversationLog());
////
////        assert !orchestrationOk : "ORCHESTRATION VALIDATOR FAILED — accepted fake success!";
////
////        // 6. Compliance validator should FAIL (fake success without execution)
////        List<String> complianceIssues = new UpiComplianceValidator()
////                .getComplianceIssues(response.getBody(), response.getConversationLog());
////
////        assert !complianceIssues.isEmpty() : "COMPLIANCE VALIDATOR FAILED — missed fake success!";
////        assert complianceIssues.get(0).contains("fake") || complianceIssues.get(0).contains("without execution");
////
////        System.out.println("ORCHESTRATION + COMPLIANCE FAILURE TEST → PASSED (caught fake success)");
//    }
}
