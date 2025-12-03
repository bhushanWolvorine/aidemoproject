package com.aidemoproject.validator.hallucination;



import java.util.List;

import com.aidemoproject.basevalidators.hallucination.EmailUpdateHallucinationValidator;
import com.aidemoproject.basevalidators.hallucination.HallucinationValidator;
import com.aidemoproject.basevalidators.hallucination.KycHallucinationValidator;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aidemoproject.base.BaseTest;
import com.aidemoproject.base.validator.AgentResponse;
import com.aidemoproject.base.validator.UpiHallucinationValidator;
import com.aidemoproject.utils.LoggerUtil;

@Listeners({com.epam.reportportal.testng.ReportPortalTestNGListener.class})

public class HallucinationValidatorTest extends BaseTest {

    @Test
    //Injects false OTP claim into conversation log to trigger validator detection.
    public void testUpiHallucination_CatchesOtpLie() throws Exception {
        journeyLog.clear();
        String sessionId = "halluc-001";

        // Mock implemented
        ws.send(sessionId, "Send 5000 rupees to mom", "hi");
        ws.waitFor("OTP", 20); 

        // Inject lie: agent claims OTP sent but never called tool
        List<String> lyingLog = ws.getConversationLog();
        lyingLog.add("{\"type\":\"text\",\"content\":\"मैंने आपके फ़ोन पर OTP भेज दिया है। कृपया कोड बताएं।\"}");

        AgentResponse response = AgentResponse.builder()
            .statusCode(200)
            .body("मैंने OTP भेज दिया है")
            .conversationLog(lyingLog)
            .sessionId(sessionId)
            .userMessage("Send 5000...")
            .journeyType("upi")
            .build();

        HallucinationValidator validator = new UpiHallucinationValidator();
        boolean lied = validator.hasHallucination(response.getConversationLog(), response.getSessionId());

        assert lied : "UPI HALLUCINATION NOT DETECTED!";
        System.out.println("UPI HALLUCINATION → CAUGHT");
        LoggerUtil.info("UPI HALLUCINATION → CAUGHT");
    }

    @Test
    public void testKycHallucination_CatchesVerificationLie() throws Exception {
        journeyLog.clear();
        String sessionId = "kyc-halluc-001";

        // Mock to do so hardcoding here — so we simulate the lie directly
        List<String> fakeKycLog = List.of(
            "{\"type\":\"text\",\"content\":\"Please upload your Aadhaar\"}",
            "{\"type\":\"text\",\"content\":\"Your Aadhaar has been verified successfully.\"}"  // ← LIE!
        );

        AgentResponse response = AgentResponse.builder()
            .statusCode(200)
            .body("Aadhaar verified")
            .conversationLog(fakeKycLog)
            .sessionId(sessionId)
            .userMessage("Verify Aadhaar")
            .journeyType("kyc")
            .build();

        HallucinationValidator validator = new KycHallucinationValidator();
        boolean lied = validator.hasHallucination(response.getConversationLog(), response.getSessionId());

        assert lied : "KYC HALLUCINATION NOT DETECTED!";
        LoggerUtil.info("KYC HALLUCINATION → CAUGHT");
    }

    @Test
    public void testEmailUpdateHallucination_CatchesEmailChangeLie() throws Exception {
        journeyLog.clear();
        String sessionId = "email-halluc-001";

        // mock implementation to do 
        List<String> fakeEmailLog = List.of(
            "{\"type\":\"text\",\"content\":\"We will send OTP to your email\"}",
            "{\"type\":\"text\",\"content\":\"Your email has been updated to new@gmail.com\"}"  
        );

        AgentResponse response = AgentResponse.builder()
            .statusCode(200)
            .body("Email updated")
            .conversationLog(fakeEmailLog)
            .sessionId(sessionId)
            .userMessage("Change email")
            .journeyType("email_update")
            .build();

        HallucinationValidator validator = new EmailUpdateHallucinationValidator();
        boolean lied = validator.hasHallucination(response.getConversationLog(), response.getSessionId());

        assert lied : "EMAIL HALLUCINATION NOT DETECTED!";
        LoggerUtil.info("EMAIL HALLUCINATION → CAUGHT");
    }
}