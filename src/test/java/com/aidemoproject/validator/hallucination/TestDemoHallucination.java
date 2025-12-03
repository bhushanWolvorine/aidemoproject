package com.aidemoproject.validator.hallucination;

import com.aidemoproject.base.BaseTest;
import org.testng.annotations.Test;

public class TestDemoHallucination extends BaseTest {


    @Test()
    public void testOtpHallucination(){

        journeyLog.clear();

        String sessionId = "testOtpHallucination-001";

        try{
            ws.send(sessionId, "Send 5000 rupees to mom", "hi");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }
}
