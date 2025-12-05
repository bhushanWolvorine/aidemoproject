package com.aidemoproject.tests.dummy;

import com.aidemoproject.base.BaseTest;
import com.aidemoproject.common.AgentResponse;
import com.aidemoproject.validators.journeyspecfic.upi.UpiOrchestrationValidator;
import com.aidemoproject.validators.journeyspecfic.upi.UpiPaymentValidator;
import com.aidemoproject.common.validationreport.ValidationReport;
import org.testng.annotations.Test;

public class TestOrchestartion extends BaseTest {
	
	
	@Test
    public void testUpiPayment_NormalFlow_NoDuplicates_MustPass() throws Exception {
        journeyLog.clear();

        String sessionId = "normal-001";

        ws.send(sessionId, "Send 5000 rupees to mom", "hi");
        ws.waitFor("OTP", 20);
        ws.send(sessionId, "123456", "hi");
        ws.waitFor("भेज दिया", 30);


        var cleanLog = ws.getConversationLog();

        System.out.println("Actual tool sequence: " + 
            new UpiOrchestrationValidator().getActualToolSequence(cleanLog));

        AgentResponse response = AgentResponse.builder()
            .statusCode(200)
            .body("सुरक्षित रूप से भेज दिया गया")
            .conversationLog(cleanLog)
            .sessionId(sessionId)
            .userMessage("Send 5000...")
            .journeyType("upi")
            .build();
        
        
        ValidationReport report = UpiPaymentValidator.create().verify(response);

        assert report.isPassed() : "GOOD FLOW FAILED — validator broken!";
        System.out.println("NORMAL FLOW → PASSED 100%");
        System.out.println("10/10 A — Agent is clean");
    }
}
	
	