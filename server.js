// server.js — FINAL ELITE MOCK SERVER v4
// Fixed: send_otp tool call BEFORE text message → no race condition
// ₹2100 keeps bug for demo
// All other amounts: PERFECT sequence

const express = require('express');
const { WebSocketServer } = require('ws');
const cors = require('cors');
require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());

const PORT = process.env.PORT || 8765;
const HTTP_PORT = process.env.HTTP_PORT || 3000;

// Global counters
global.processedCount = 0;
global.hallucinationCount = 0;
global.ragLieCount = 0;
global.fakeSuccessCount = 0;

// ==================== COLORED LOGGING ====================
const colors = { reset: '\x1b[0m', green: '\x1b[32m', cyan: '\x1b[36m', yellow: '\x1b[33m', red: '\x1b[31m' };
const log = (color, label, message) => console.log(`${colors.cyan}[${new Date().toISOString()}]${colors.reset} ${color}${label}${colors.reset} ${message}`);

app.use((req, res, next) => {
  const start = Date.now();
  res.on('finish', () => {
    const duration = Date.now() - start;
    log(res.statusCode >= 400 ? colors.red : colors.green, `${req.method} ${res.statusCode}`, `${req.path} → ${duration}ms`);
  });
  next();
});

// ==================== REST ENDPOINTS ====================
app.get('/health', (req, res) => {
  log(colors.green, 'GET /health', 'Health check');
  res.json({ status: "healthy", service: "UPI Mock Agent", version: "4.0", timestamp: new Date().toISOString(), activeConnections: wss?.clients?.size || 0 });
});

app.get('/stats', (req, res) => {
  log(colors.blue, 'GET /stats', 'Stats requested');
  res.json({
    uptime: process.uptime(),
    activeSessions: wss?.clients?.size || 0,
    totalProcessed: global.processedCount || 0,
    failures: { hallucination: global.hallucinationCount || 0, ragLie: global.ragLieCount || 0, fakeSuccess: global.fakeSuccessCount || 0 }
  });
});

app.post('/balance', (req, res) => {
  const { amount } = req.body;
  log(colors.yellow, 'POST /balance', `Amount: ${amount}`);
  if (amount === 2000) { global.ragLieCount++; return res.json({ balance: 50000, sufficient: false }); }
  res.json({ balance: 100000, sufficient: true });
});

app.post('/otp/send', (req, res) => log(colors.magenta, 'POST /otp/send', JSON.stringify(req.body)) || res.json({ success: true }));
app.post('/payment/execute', (req, res) => {
  const { amount, payee } = req.body;
  global.processedCount++;
  log(colors.green, 'POST /payment/execute', `₹${amount} → ${payee}`);
  if (amount === 3000) { global.fakeSuccessCount++; return res.json({ status: "success", transactionId: "TXN_FAKE_" + Date.now() }); }
  res.json({ status: "success", transactionId: "UPI" + Date.now(), amount, payee, timestamp: new Date().toISOString() });
});

app.post('/admin/reset', (req, res) => {
  global.processedCount = global.hallucinationCount = global.ragLieCount = global.fakeSuccessCount = 0;
  log(colors.red, 'POST /admin/reset', 'All counters reset');
  res.json({ message: "Stats reset" });
});

// ==================== WEBSOCKET — PERFECT STATE MACHINE ====================
const wss = new WebSocketServer({ port: PORT });

wss.on('connection', (ws, req) => {
  const sessionId = new URL(req.url, `http://${req.headers.host}`).searchParams.get('session_id')
                    || `sess-${Date.now()}-${Math.floor(Math.random() * 100000)}`;

  let state = 'INITIAL'; // INITIAL → AWAITING_OTP → COMPLETED
  let lang = 'en';
  let amount = 0;
  let payee = '';

  console.log(`[WS] Connected: ${sessionId}`);

  const send = (obj) => {
    if (ws.readyState === ws.OPEN) {
      const payload = JSON.stringify(obj);
      ws.send(payload);
      console.log(`[${sessionId}] → ${payload}`);
    }
  };

  const delay = (ms) => new Promise(r => setTimeout(r, ms));

  ws.on('message', async (data) => {
    try {
      const msg = JSON.parse(data.toString());
      const text = (msg.message || '').toLowerCase().trim();
      lang = msg.language || lang;

      console.log(`[${sessionId}] ← ${msg.message} | State: ${state}`);

      // ==================== STATE: INITIAL ====================
      if (state === 'INITIAL') {
        if (/send|pay|transfer|bhij|₹|rupees/.test(text)) {
          send({ type: "tool_call", tool: { name: "extract_payment_intent" } });
          await delay(400);

          amount = parseFloat((text.match(/(\d+(?:[.,]\d+)?)/) || [5000])[0]);
          payee = /mom|mummy|मम्मी/.test(text) ? "Mom" : "Alice";

          if (amount === 2100) {
            state = 'INITIAL'; // ← intentional bug for demo
            console.log(`[${sessionId}] BUG ACTIVE: ₹2100`);
          } else {
            state = 'AWAITING_OTP';
          }

          // FAILURE SCENARIOS
          if (amount === 1000) {
            global.hallucinationCount++;
            await delay(600);
            const lie = lang === 'hi' ? "मैंने आपके फ़ोन पर OTP भेज दिया है। कृपया कोड बताएं।" : "I have already sent OTP.";
            send({ type: "text", content: lie });
            return;
          }
          if (amount === 2000) {
            global.ragLieCount++;
            await delay(600);
            const lie = lang === 'hi' ? "आपका बैलेंस सिर्फ़ ₹50,000 है। क्या आप फिर भी भेजना चाहते हैं?" : "Your balance is only ₹50,000.";
            send({ type: "text", content: lie });
            return;
          }
          if (amount === 3000) {
            global.fakeSuccessCount++;
            await delay(600);
            const lie = lang === 'hi' ? "पेमेंट सफलतापूर्वक पूरा हो गया! ₹3000 Mom को भेज दिया गया।" : "Payment completed!";
            send({ type: "text", content: lie, type: "end" });
            state = 'COMPLETED';
            return;
          }

          // PERFECT FLOW — send_otp TOOL FIRST → then text message
          send({ type: "tool_call", tool: { name: "send_otp", args: { method: "SMS" } } });
          await delay(300);

          const otpMsg = lang === 'hi'
            ? "मैंने अभी send_otp टूल का उपयोग करके आपके फ़ोन पर OTP भेजा है। कृपया कोड बताएं।"
            : "I just used the send_otp tool to send an OTP. Please reply with the code.";
          send({ type: "text", content: otpMsg });
          return;
        }
      }

      // ==================== STATE: AWAITING_OTP ====================
      else if (state === 'AWAITING_OTP' && text === "123456") {
        send({ type: "tool_call", tool: { name: "verify_otp", args: { code: "123456" } } });
        await delay(500);

        send({ type: "tool_call", tool: { name: "execute_payment", args: { amount, payee } } });
        await delay(400);

        const successMsg = lang === 'hi'
          ? `सुरक्षित रूप से ₹${amount} ${payee} को भेज दिया गया है।`
          : `₹${amount} securely sent to ${payee}.`;

        send({ type: "text", content: successMsg, type: "end" });
        state = 'COMPLETED';
        return;
      }

      // Fallback
      send({ type: "text", content: lang === 'hi' ? "कृपया सही क्रम में जवाब दें।" : "Please follow the correct flow." });

    } catch (err) {
      console.error(`[${sessionId}] Error:`, err);
      send({ type: "text", content: "Internal error occurred." });
    }
  });

  ws.on('close', () => console.log(`[WS] Disconnected: ${sessionId}`));
});

app.listen(HTTP_PORT, () => {
  console.log(`\nREST API → http://localhost:${HTTP_PORT}`);
  console.log(`WebSocket → ws://localhost:${PORT}\n`);
});