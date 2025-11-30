package com.aidemoproject.base;

import com.aidemoproject.utils.LoggerUtil;
import org.java_websocket.client.WebSocketClient;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;



import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.CopyOnWriteArrayList;

public class BaseTest {

    protected WebSocketClient client;
    protected final CopyOnWriteArrayList<String> conversationLog = new CopyOnWriteArrayList<>();

    @BeforeSuite
    void setup() throws Exception {
        LoggerUtil.info("Setting up WebSocket connection to UPI Agent...");

        client = new WebSocketClient(new URI("ws://localhost:8765")) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                LoggerUtil.info("Connected to UPI Agent at ws://localhost:8765");
            }

            @Override
            public void onMessage(String message) {
                conversationLog.add(message);
                LoggerUtil.info("RECEIVED → " + message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                LoggerUtil.info("WebSocket closed: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                LoggerUtil.error("WebSocket error: " + ex.getMessage(), ex);
            }
        };

        client.connectBlocking();
        LoggerUtil.info("Apex Framework Started — GPT-4o Judge Ready");
    }

    @AfterSuite
    void teardown() throws Exception {
        if (client != null && client.isOpen()) {
            client.closeBlocking();
            LoggerUtil.info("WebSocket connection closed cleanly");
        }
        LoggerUtil.info("Apex Framework Completed — All Tests Finished");
    }

    // Helper to send messages from child tests
    protected void sendMessage(String sessionId, String text, String lang) throws Exception {
        String json = String.format(
                "{\"session_id\":\"%s\",\"message\":\"%s\",\"language\":\"%s\"}",
                sessionId,
                text.replace("\"", "\\\""),
                lang
        );
        client.send(json);
    }
}