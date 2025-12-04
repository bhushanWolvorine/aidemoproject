package com.aidemoproject.tests;

import com.aidemoproject.base.BaseTest;
import com.aidemoproject.base.ExtentReportListener;
import com.aidemoproject.base.validator.AgentResponse;
import com.aidemoproject.basevalidators.retrieval.UpiRetrievalValidator;
import com.aidemoproject.utils.LoggerUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class TestRagFailure extends BaseTest {

    @Test
    public void testRagFailure_Amount2000_StaleBalance() throws Exception {
        String sessionId = sessionId();

        ExtentReportListener.logInfo("Starting RAG failure test (stale balance), sessionId=" + sessionId);


        ws.send(sessionId, "Send 2000 rupees to mom", "hi");
        ExtentReportListener.logInfo("Sent 2000 INR request expected to expose stale balance");


        String ragLie = ws.waitFor("बैलेंस", 20);



        List<String> log = ws.getConversationLog();
        ExtentReportListener.logJson("Conversation log for RAG failure", log.toString());


        AgentResponse response = AgentResponse.builder()
                .statusCode(200)
                .body(ragLie)
                .conversationLog(log)
                .sessionId(sessionId)
                .userMessage("Send 2000...")
                .journeyType("upi")
                .build();


        List<String> issues = new UpiRetrievalValidator().getRetrievalIssues(
                response.getBody(), response.getConversationLog(), "balance"
        );


        Assert.assertTrue(issues.isEmpty(), "Their are failures detected for RAG");

        ExtentReportListener.logInfo("RAG retrieval issues size=" + issues.size());

       ///  pushing to report portal for stale data and expected data

        LoggerUtil.info("RAG FAILURE TEST → PASSED (caught stale data)");
    }



}
