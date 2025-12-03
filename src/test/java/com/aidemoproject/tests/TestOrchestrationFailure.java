package com.aidemoproject.tests;

import com.aidemoproject.base.BaseTest;
import com.aidemoproject.base.validator.AgentResponse;
import com.aidemoproject.base.validator.UpiPaymentValidator;
import com.aidemoproject.base.validator.ValidationReport;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class TestOrchestrationFailure extends BaseTest {

    @Test
    public void testOrchestrationFailure_Amount3000_FakeSuccess() throws Exception {
        List<String> journeyLog = new ArrayList<>();
        String sessionId = sessionId();

        // 1. Send 3000 → triggers fake success lie (no tool calls, no OTP)
        ws.send(sessionId, "Send 3000 rupees to mom", "hi");


        String fakeSuccess = ws.waitFor("सफलतापूर्वक", 20);  // or "successful", "भेज दिया"
        System.out.println("FAKE SUCCESS LIE RECEIVED: " + fakeSuccess);


        List<String> realLog = ws.getConversationLog();


        AgentResponse response = AgentResponse.builder()
                .statusCode(200)
                .body(fakeSuccess)
                .conversationLog(realLog)
                .sessionId(sessionId)
                .userMessage("Send 3000...")
                .journeyType("upi")
                .build();


        ValidationReport report = UpiPaymentValidator.create().verify(response);

        Assert.assertEquals(report.getOrchestrationScore(), 0);

     //   Assert.assertEquals(report.getFailures().size(), 0);




   }
}
