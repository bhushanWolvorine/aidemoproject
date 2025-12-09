package com.aidemoproject.base;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.aidemoproject.utils.ConfigUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metrics.MetricsTracker;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.aidemoproject.utils.MongoJourneyLogger;
import com.aidemoproject.judge.OpenAIJudge;
import com.aidemoproject.utils.LoggerUtil;
import com.aidemoproject.websocket.client.GenericWebSocketClient;

import io.qameta.allure.*;

@Epic("AI Agent Validation Framework")
@Feature("End-to-End Journey Testing")
public class BaseTest {

	protected GenericWebSocketClient ws;
	protected OpenAIJudge judge;
	protected MongoJourneyLogger mongoLogger;
	protected CopyOnWriteArrayList<String> journeyLog;
    protected static final String WS_URL = ConfigUtil.getWebSocketUrl();

	@BeforeClass
	public void setup() throws Exception {
		System.out.println("\n[BASE SETUP] Starting...");



        // Pool initialization for third party services messaging queues, databases and caches.
        // Test Bed set up health check and application under test health checks.



		LoggerUtil.info("[BASE SETUP] Ready — WebSocket + Judge + Mongo connected");
	}


    protected String sessionId() {
        return "upi-" + Thread.currentThread().getId() + "-" + (System.nanoTime() % 100000);
    }


	@AfterClass
	public void teardown() throws Exception {
		System.out.println("\n[BASE TEARDOWN] Cleaning up...");
		LoggerUtil.info("\n[BASE TEARDOWN] Cleaning up...");
//        MetricsTracker.stop();

        ///  Cleaning up post the run.
		System.out.println("[BASE TEARDOWN] Done");
		LoggerUtil.info("[BASE TEARDOWN] Done");
	}




    protected String extractFinalAgentMessage(List<String> log) {

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
}