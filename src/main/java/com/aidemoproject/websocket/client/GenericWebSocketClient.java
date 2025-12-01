package com.aidemoproject.websocket.client;



import com.aidemoproject.utils.LoggerUtil;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class GenericWebSocketClient {

 private final WebSocketClient client;
 private final BlockingQueue<String> messages = new LinkedBlockingQueue<>();
 private final CopyOnWriteArrayList<String> fullLog = new CopyOnWriteArrayList<>();
 private Consumer<String> messageHandler;  // External handler for BaseTest

 public GenericWebSocketClient(String url) throws Exception {
     LoggerUtil.info("Connecting to WebSocket: " + url);

     client = new WebSocketClient(new URI(url)) {

         @Override
         public void onOpen(ServerHandshake handshake) {
             LoggerUtil.info("WebSocket connection established");
         }

         @Override
         public void onMessage(String message) {
             messages.add(message);
             fullLog.add(message);
             LoggerUtil.info("RECV → " + message);

             // Forward to BaseTest or any other listener
             if (messageHandler != null) {
                 messageHandler.accept(message);
             }
         }

         @Override
         public void onClose(int code, String reason, boolean remote) {
             LoggerUtil.warn("WebSocket closed | Code: " + code + " | Reason: " + reason);
         }

         @Override
         public void onError(Exception ex) {
             LoggerUtil.error("WebSocket error occurred", ex);
         }
     };

     boolean connected = client.connectBlocking(15, TimeUnit.SECONDS);
     if (!connected) {
         throw new RuntimeException("Failed to connect to WebSocket: " + url);
     }
     LoggerUtil.info("WebSocket ready for testing");
 }

 // === PUBLIC API ===

 public void send(String sessionId, String text, String language) throws Exception {
     String json = String.format(
         "{\"session_id\":\"%s\",\"message\":\"%s\",\"language\":\"%s\"}",
         sessionId,
         text.replace("\"", "\\\""),
         language
     );
     client.send(json);
     LoggerUtil.info("SEND → " + text);
 }

 public void send(String text) throws Exception {
     send("default-session", text, "hi");
 }

 public String waitFor(String keyword, int timeoutSec) throws Exception {
     long deadline = System.currentTimeMillis() + timeoutSec * 1000L;

     while (System.currentTimeMillis() < deadline) {
         String msg = messages.poll(1, TimeUnit.SECONDS);
         if (msg != null) {
             System.out.println("CHECKING: " + msg);
             if (msg.toLowerCase().contains(keyword.toLowerCase())) {
                 System.out.println("MATCHED keyword: " + keyword);
                 return msg;
             }
         }
     }
     throw new RuntimeException("TIMEOUT after " + timeoutSec + "s waiting for keyword: '" + keyword + "'");
 }

 public CopyOnWriteArrayList<String> getConversationLog() {
     return new CopyOnWriteArrayList<>(fullLog);
 }

 public void close() throws Exception {
     if (client != null && client.isOpen()) {
         client.closeBlocking();
         LoggerUtil.info("WebSocket connection closed");
     }
 }

 // === FOR BaseTest TO HOOK INTO ===
 public void setMessageHandler(Consumer<String> handler) {
     this.messageHandler = handler;
 }
}