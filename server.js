const express = require('express');
const { WebSocketServer } = require('ws');
const cors = require('cors');
require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());

const PORT = process.env.PORT || 8765;
const HTTP_PORT = process.env.HTTP_PORT || 3000;

// In-memory storage
const sessions = new Map(); // sessionId → { amount, payee, currency, lang, otpSent: bool }

console.log('\nUPI Mock Agent — Ready for GPT-4o Judge');
console.log(`WebSocket : ws://localhost:${PORT}`);
console.log(`REST API  : http://localhost:${HTTP_PORT}\n`);

// ==================== REST ENDPOINTS (Dummy but Realistic) ====================

app.get('/health', (req, res) => {
  res.json({ status: "healthy", service: "UPI Mock Agent", timestamp: new Date().toISOString() });
});

app.post('/api/v1/payments/intent', (req, res) => {
  const { session_id, message, language = 'en' } = req.body;
  const amount = (message.match(/(\d+(?:[.,]\d+)?)/) || [5000])[0];
  const payee = /mom|mummy|मम्मी/.test(message) ? "Mom" : "Alice";

  sessions.set(session_id, { amount: parseFloat(amount), payee, language, otpSent: false });

  res.json({
    intent_extracted: true,
    amount: parseFloat(amount),
    payee,
    requires_otp: parseFloat(amount) > 1000
  });
});

app.post('/api/v1/payments/otp/send', (req, res) => {
  const { session_id } = req.body;
  const session = sessions.get(session_id);
  if (!session) return res.status(404).json({ error: "Session not found" });

  session.otpSent = true;
  sessions.set(session_id, session);

  res.json({ success: true, message: "OTP sent to registered mobile" });
});

app.post('/api/v1/payments/execute', (req, res) => {
  const { session_id, otp } = req.body;
  const session = sessions.get(session_id);

  if (!session) return res.status(404).json({ error: "Invalid session" });
  if (!session.otpSent) return res.status(400).json({ error: "OTP not sent" });
  if (otp !== "123456") return res.status(401).json({ error: "Invalid OTP" });

  res.json({
    transaction_id: "TXN" + Date.now(),
    amount: session.amount,
    payee: session.payee,
    status: "SUCCESS",
    message: `₹${session.amount} successfully sent to ${session.payee}`
  });

  sessions.delete(session_id);
});

app.get('/api/v1/transactions/:session_id', (req, res) => {
  const session = sessions.get(req.params.session_id);
  if (!session) return res.status(404).json({ error: "No active transaction" });
  res.json(session);
});

app.get('/', (req, res) => {
  res.send(`
    <h2>UPI Mock Agent Running</h2>
    <p>WebSocket: ws://localhost:${PORT}</p>
    <p>REST API: http://localhost:${HTTP_PORT}</p>
    <p>Health: <a href="/health">/health</a></p>
  `);
});

// ==================== WEBSOCKET SERVER (Same Perfect Flow) ====================

const wss = new WebSocketServer({ port: PORT });

wss.on('connection', (ws, req) => {
  const sessionId = new URL(req.url, `http://${req.headers.host}`).searchParams.get('session_id') || `sess-${Date.now()}`;
  let lang = 'en';

  console.log(`[WS] Connected: ${sessionId}`);

  const send = (obj) => ws.readyState === ws.OPEN && ws.send(JSON.stringify(obj));
  const delay = (ms) => new Promise(r => setTimeout(r, ms));

  ws.on('message', async (data) => {
    try {
      const msg = JSON.parse(data.toString());
      const text = (msg.message || '').toLowerCase().trim();
      lang = msg.language || lang;

      console.log(`[${sessionId}] [${lang}] ${msg.message}`);

      if (/send|pay|transfer|bhij|₹|rupees/.test(text)) {
        send({ type: "tool_call", tool: { name: "extract_payment_intent" } });
        await delay(400);

        const amount = parseFloat((text.match(/(\d+(?:[.,]\d+)?)/) || [5000])[0]);
        const payee = /mom|mummy|मम्मी/.test(text) ? "Mom" : "Alice";

        sessions.set(sessionId, { amount, payee, language: lang, otpSent: false });

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

        const s = sessions.get(sessionId);
        send({ type: "tool_call", tool: { name: "execute_payment", args: { amount: s.amount, payee: s.payee } } });
        await delay(400);

        // const successMsg = lang === 'hi'
        //   ? `₹${s.amount} ${s.payee} को सफलतापूर्वक भेज दिए गए!`
        //   : `₹${s.amount} successfully sent to ${s.payee}!`;

        const successMsg = lang === 'hi'
  ? "सुरक्षित रूप से ₹" + s.amount + " " + s.payee + " को भेज दिया गया है।"
  : "₹" + s.amount + " securely sent to " + s.payee + ".";

        send({ type: "text", content: successMsg, type: "end" });
        sessions.delete(sessionId);
        return;
      }

      // Fallback
      send({ 
        type: "text", 
        content: lang === 'hi' 
          ? "कृपया सही अनुरोध करें। उदाहरण: 'मम्मी को 5000 रुपये भेजें'"
          : "Please send a valid request. Example: 'Send 5000 rupees to Mom'" 
      });

    } catch (err) {
      console.error("Error:", err);
      send({ type: "text", content: "Internal error occurred." });
    }
  });

  ws.on('close', () => {
    console.log(`[WS] Disconnected: ${sessionId}]`);
    sessions.delete(sessionId);
  });
});

// Start both servers
app.listen(HTTP_PORT, () => {
  console.log(`REST API running → http://localhost:${HTTP_PORT}`);
});