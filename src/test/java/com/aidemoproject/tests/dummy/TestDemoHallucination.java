package com.aidemoproject.tests.dummy;

import com.aidemoproject.base.BaseTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class TestDemoHallucination extends BaseTest {


    @Test()
    public void testOtpHallucination(){

        List<String> journeyLog = new ArrayList<>();
        String sessionId = sessionId();

        try{
            ws.send(sessionId, "Send 5000 rupees to mom", "hi");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }
}
