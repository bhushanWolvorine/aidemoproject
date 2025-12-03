package com.aidemoproject.base;

import java.util.concurrent.CopyOnWriteArrayList;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.aidemoproject.MongoJourneyLogger;
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

	@BeforeClass
	public void setup() throws Exception {
		System.out.println("\n[BASE SETUP] Starting...");

		// 1. Create log first
		journeyLog = new CopyOnWriteArrayList<>();

		ws = new GenericWebSocketClient("ws://localhost:8765");

		// 3. Manually hook into the client's message handling
		ws.setMessageHandler(message -> {

			System.out.println("LOGGED → " + message);
		});

		judge = new OpenAIJudge();
		mongoLogger = new MongoJourneyLogger();

		System.out.println("[BASE SETUP] Ready — WebSocket + Judge + Mongo connected");
		LoggerUtil.info("[BASE SETUP] Ready — WebSocket + Judge + Mongo connected");
	}


    protected String sessionId() {
        return "upi-" + Thread.currentThread().getId() + "-" + (System.nanoTime() % 100000);
    }


	@AfterClass
	public void teardown() throws Exception {
		System.out.println("\n[BASE TEARDOWN] Cleaning up...");
		LoggerUtil.info("\n[BASE TEARDOWN] Cleaning up...");
		if (ws != null)
			ws.close();
		System.out.println("[BASE TEARDOWN] Done");
		LoggerUtil.info("[BASE TEARDOWN] Done");
	}

	protected void clearLog() {
		journeyLog.clear();
		LoggerUtil.info("journeyLog cleared");
	}
}