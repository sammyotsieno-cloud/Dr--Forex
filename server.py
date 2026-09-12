import http.server
import socket
import socketserver
import os

PORT = 3000

class ReusableTCPServer(socketserver.TCPServer):
    def server_bind(self):
        self.socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEPORT, 1)
        self.socket.bind(self.server_address)

class ResearchLabHandler(http.server.BaseHTTPRequestHandler):
    def do_HEAD(self):
        self.send_response(200)
        self.send_header('Content-Type', 'text/html; charset=utf-8')
        self.end_headers()

    def do_GET(self):
        if self.path in ('/download-apk', '/app-debug.apk'):
            apk_path = os.path.abspath('.build-outputs/app-debug.apk')
            if not os.path.exists(apk_path):
                apk_path = os.path.abspath('app/build/outputs/apk/debug/app-debug.apk')
            
            if os.path.exists(apk_path):
                with open(apk_path, 'rb') as f:
                    content = f.read()
                self.send_response(200)
                self.send_header('Content-Type', 'application/vnd.android.package-archive')
                self.send_header('Content-Disposition', 'attachment; filename="app-debug.apk"')
                self.send_header('Content-Length', str(len(content)))
                self.end_headers()
                self.wfile.write(content)
                return
            else:
                self.send_response(404)
                self.send_header('Content-Type', 'text/plain')
                self.end_headers()
                self.wfile.write(b'APK not found. Please compile the applet first.')
                return

        html = """<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Dr. Forex Research Lab</title>
  <style>
    * { box-sizing: border-box; }
    body {
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
      background: #090d16;
      color: #f1f5f9;
      margin: 0;
      padding: 2.5rem 1.5rem;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: 100vh;
    }
    .card {
      background: #131c2e;
      border: 1px solid #1e293b;
      border-radius: 12px;
      padding: 2.25rem;
      max-width: 620px;
      width: 100%;
      box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.4);
    }
    .badge {
      display: inline-block;
      padding: 0.25rem 0.75rem;
      border-radius: 9999px;
      font-size: 0.8125rem;
      font-weight: 600;
      background: rgba(16, 185, 129, 0.15);
      color: #10b981;
      border: 1px solid rgba(16, 185, 129, 0.3);
      margin-bottom: 1.25rem;
    }
    h1 {
      font-size: 1.625rem;
      font-weight: 700;
      color: #38bdf8;
      margin: 0 0 0.5rem 0;
      letter-spacing: -0.02em;
    }
    p {
      color: #94a3b8;
      font-size: 0.9375rem;
      line-height: 1.6;
      margin: 0 0 1.5rem 0;
    }
    .grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 0.875rem;
      margin-bottom: 1.75rem;
    }
    .stat {
      background: #0c121e;
      border: 1px solid #1e293b;
      border-radius: 8px;
      padding: 0.875rem 1rem;
    }
    .stat-label {
      font-size: 0.6875rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.06em;
      color: #64748b;
    }
    .stat-val {
      font-size: 0.9375rem;
      font-weight: 600;
      color: #e2e8f0;
      margin-top: 0.25rem;
    }
    .btn-row {
      display: flex;
      gap: 0.75rem;
      align-items: center;
    }
    .btn {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      background: #0284c7;
      color: #ffffff;
      text-decoration: none;
      font-size: 0.875rem;
      font-weight: 600;
      padding: 0.625rem 1.25rem;
      border-radius: 6px;
      transition: background 0.15s ease;
    }
    .btn:hover {
      background: #0369a1;
    }
  </style>
</head>
<body>
  <div class="card">
    <div class="badge">Research Environment Online</div>
    <h1>Dr. Forex Research Lab</h1>
    <p>Quantitative trading research laboratory. Historical market data validation, strategy hypothesis testing, temporal integrity auditing, and experiment tracking engine.</p>

    <div class="grid">
      <div class="stat">
        <div class="stat-label">Architecture</div>
        <div class="stat-val">Native Android / JVM</div>
      </div>
      <div class="stat">
        <div class="stat-label">Build Target</div>
        <div class="stat-val">app-debug.apk</div>
      </div>
      <div class="stat">
        <div class="stat-label">Test Suite</div>
        <div class="stat-val">Step 1 Green (12/12)</div>
      </div>
      <div class="stat">
        <div class="stat-label">Current Pipeline</div>
        <div class="stat-val">Step 2: StrategyObs</div>
      </div>
    </div>

    <div class="btn-row">
      <a href="/download-apk" class="btn">Download Debug APK</a>
    </div>
  </div>
</body>
</html>"""
        encoded = html.encode('utf-8')
        self.send_response(200)
        self.send_header('Content-Type', 'text/html; charset=utf-8')
        self.send_header('Content-Length', str(len(encoded)))
        self.end_headers()
        self.wfile.write(encoded)

    def log_message(self, format, *args):
        pass

if __name__ == '__main__':
    with ReusableTCPServer(('127.0.0.1', PORT), ResearchLabHandler) as httpd:
        print(f"Serving on 127.0.0.1:{PORT}")
        httpd.serve_forever()
