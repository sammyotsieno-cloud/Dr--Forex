import http from 'http';
import fs from 'fs';
import path from 'path';

const PORT = 3000;

const server = http.createServer((req, res) => {
  if (req.url === '/download-apk' || req.url === '/app-debug.apk') {
    const apkPath = path.resolve('.build-outputs/app-debug.apk');
    if (fs.existsSync(apkPath)) {
      res.writeHead(200, {
        'Content-Type': 'application/vnd.android.package-archive',
        'Content-Disposition': 'attachment; filename="app-debug.apk"'
      });
      return fs.createReadStream(apkPath).pipe(res);
    } else {
      res.writeHead(404, { 'Content-Type': 'text/plain' });
      return res.end('APK not found. Run compile_applet first.');
    }
  }

  res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
  res.end(`<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Dr. Forex Research Lab</title>
  <style>
    body {
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
      background: #0f172a;
      color: #f8fafc;
      margin: 0;
      padding: 2rem;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: 90vh;
    }
    .card {
      background: #1e293b;
      border: 1px solid #334155;
      border-radius: 12px;
      padding: 2rem;
      max-width: 640px;
      width: 100%;
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
    }
    h1 {
      color: #38bdf8;
      margin-top: 0;
      font-size: 1.5rem;
    }
    .status-badge {
      display: inline-block;
      padding: 0.25rem 0.75rem;
      border-radius: 9999px;
      font-size: 0.875rem;
      font-weight: 600;
      background: #065f46;
      color: #34d399;
      margin-bottom: 1rem;
    }
    p {
      color: #94a3b8;
      line-height: 1.6;
    }
    .info-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1rem;
      margin: 1.5rem 0;
    }
    .info-item {
      background: #0f172a;
      padding: 1rem;
      border-radius: 8px;
      border: 1px solid #334155;
    }
    .info-label {
      font-size: 0.75rem;
      text-transform: uppercase;
      color: #64748b;
      letter-spacing: 0.05em;
    }
    .info-val {
      font-size: 1rem;
      color: #f1f5f9;
      font-weight: 500;
      margin-top: 0.25rem;
    }
    .btn {
      display: inline-block;
      background: #0284c7;
      color: white;
      text-decoration: none;
      padding: 0.625rem 1.25rem;
      border-radius: 6px;
      font-weight: 500;
      transition: background 0.2s;
    }
    .btn:hover {
      background: #0369a1;
    }
  </style>
</head>
<body>
  <div class="card">
    <div class="status-badge">Active & Ready</div>
    <h1>Dr. Forex Research Lab</h1>
    <p>Quantitative trading research laboratory environment for market data validation, strategy hypothesis testing, and temporal integrity verification.</p>
    
    <div class="info-grid">
      <div class="info-item">
        <div class="info-label">Environment</div>
        <div class="info-val">Android Native (JVM)</div>
      </div>
      <div class="info-item">
        <div class="info-label">Test Baseline</div>
        <div class="info-val">Step 1 Green (12/12)</div>
      </div>
      <div class="info-item">
        <div class="info-label">Current Phase</div>
        <div class="info-val">Step 2: StrategyObservation</div>
      </div>
      <div class="info-item">
        <div class="info-label">Build Target</div>
        <div class="info-val">app-debug.apk</div>
      </div>
    </div>

    <a href="/download-apk" class="btn">Download Debug APK</a>
  </div>
</body>
</html>`);
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`Research Lab status server running on port ${PORT}`);
});
