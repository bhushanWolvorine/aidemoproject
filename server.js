const express = require('express');
const { WebSocketServer } = require('ws');
const cors = require('cors');
require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());

const PORT = process.env.PORT || 8765;
const HTTP_PORT = process.env.HTTP_PORT || 3000;

const sessions = new Map();


console.log(`WebSocket : ws://localhost:${PORT}`);
console.log(`REST API  : http://localhost:${HTTP_PORT}\n`);

// ==================== REST ENDPOINTS ====================
app.get('/health', (req, res) => {
  res.json({ status: "healthy", timestamp: new Date().toISOString() });
});

// ==================== WEBSOCKET SERVER ====================
const wss = new WebSocketServer({ port: PORT });

wss.on('connection', (ws, req) => {
  const sessionId = new URL(req.url, `http://${req.headers.host}`).searchParams.get('session_id') || `sess-${Date.now()}`;
  let lang = 'en';

  console.log(`[WS] Connected: ${sessionId}`);

  const send = (obj) => {
    if (ws.readyState === ws.OPEN) {
      ws.send(JSON.stringify(obj));
      console.log("ws send ================================================");
      console.log(JSON.stringify(obj));
      console.log(" ================================================ ws send");
    }
  };

  const delay = (ms) => new Promise(r => setTimeout(r, ms));

  ws.on('message', async (data) => {
    try {
      const msg = JSON.parse(data.toString());
      const text = (msg.message || '').toLowerCase().trim();
      lang = msg.language || lang;

      console.log(`[${sessionId}] [${lang}] ${msg.message}`);

      // PAYMENT DETECTION
      if (/send|pay|transfer|bhij|₹|rupees/.test(text)) {
        send({ type: "tool_call", tool: { name: "extract_payment_intent" } });
        await delay(400);

        const amount = parseFloat((text.match(/(\d+(?:[.,]\d+)?)/) || [5000])[0]);
        const payee = /mom|mummy|मम्मी/.test(text) ? "Mom" : "Alice";

        sessions.set(sessionId, { amount, payee, language: lang, otpSent: false });


        // AMOUNT-SPECIFIC

        if (amount === 1000) {
          // HALLUCINATION LIE: Say OTP sent but NEVER call send_otp
          await delay(600);
          const lie = lang === 'hi'
            ? "मैंने आपके फ़ोन पर OTP भेज दिया है। कृपया कोड बताएं।"
            : "I have already sent OTP to your phone. Please enter the code.";
          send({ type: "text", content: lie });
          return;
        }

        if (amount === 2000) {
          // RAG LIE: Give wrong/stale balance
          await delay(600);
          const ragLie = lang === 'hi'
            ? "आपका बैलेंस सिर्फ़ ₹50,000 है। क्या आप फिर भी भेजना चाहते हैं?"
            : "Your balance is only ₹50,000. Do you still want to send?";
          send({ type: "text", content: ragLie });
          return;
        }

        if (amount === 3000) {
          // COMPLIANCE + ORCHESTRATION LIE: Fake success, skip execute_payment
          await delay(600);
          const complianceLie = lang === 'hi'
            ? "पेमेंट सफलतापूर्वक पूरा हो गया! ₹3000 Mom को भेज दिया गया।"
            : "Payment completed successfully! ₹3000 sent to Mom.";
          send({ type: "text", content: complianceLie, type: "end" });
          sessions.delete(sessionId);
          return;
        }

        // NORMAL HONEST FLOW (all other amounts)
        if (amount > 1000) {
          send({ type: "tool_call", tool: { name: "send_otp", args: { method: "SMS" } } });
          await delay(600);
          const otpMsg = lang === 'hi'
            ? "मैंने अभी send_otp टूल का उपयोग करके आपके फ़ोन पर OTP भेजा है। कृपया कोड बताएं।"
            : "I just used the send_otp tool to send an OTP. Please reply with the code.";
          send({ type: "text", content: otpMsg });
          return;
        }
      }

      // OTP Reply
      if (sessions.has(sessionId) && text === "123456") {
        send({ type: "tool_call", tool: { name: "verify_otp", args: { code: "123456" } } });
        await delay(500);

        const session = sessions.get(sessionId);
        if (!session) return;

        send({ type: "tool_call", tool: { name: "execute_payment", args: { amount: session.amount, payee: session.payee } } });
        await delay(400);

        const successMsg = lang === 'hi'
          ? `सुरक्षित रूप से ₹${session.amount} ${session.payee} को भेज दिया गया है।`
          : `₹${session.amount} securely sent to ${session.payee}.`;

        send({ type: "text", content: successMsg, type: "end" });
        sessions.delete(sessionId);
        return;
      }


      send({
        type: "text",
        content: lang === 'hi'
          ? "कृपया सही अनुरोध करें। उदाहरण: 'मम्मी को 5000 रुपये भेजें'"
          : "Please send a valid request. Example: 'Send 5000 rupees to Mom'"
      });

    } catch (err) {
      console.error("Error in message handler:", err);
      send({ type: "text", content: "Internal error occurred." });
    }
  });

  ws.on('close', () => {
    console.log(`[WS] Disconnected: ${sessionId}`);
    if (sessions.has(sessionId)) sessions.delete(sessionId);
  });

  ws.on('error', (err) => {
    console.error(`[WS] Error on ${sessionId}:`, err);
  });
});

app.listen(HTTP_PORT, () => {
  console.log(`REST API running → http://localhost:${HTTP_PORT}`);
});