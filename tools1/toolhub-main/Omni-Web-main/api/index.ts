import express from "express";
import axios from "axios";
import https from "https";
import * as cheerio from "cheerio";
import path from "path";
import { fileURLToPath } from "url";
import "dotenv/config";
import { GoogleGenerativeAI } from "@google/generative-ai";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const PORT = process.env.PORT || 3000;

let networkLogs: any[] = [];
const MAX_LOGS = 100;

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Network logs endpoint
app.get("/api/v1/network-logs", (req, res) => {
  res.json(networkLogs);
});

// Markdown conversion endpoint
app.get("/api/markdown", async (req, res) => {
  const targetUrl = req.query.u as string;
  if (!targetUrl) return res.status(400).send("URL is required");
  try {
    const response = await axios.get(targetUrl, { timeout: 15000 });
    const $ = cheerio.load(response.data);

    // Remove noise
    $("script, style, nav, footer, header, ads, .ads, #ads").remove();

    const html = $("article").html() || $("main").html() || $("body").html() || "";
    res.send(html); // We'll convert to MD on client side for better control
  } catch (error: any) {
    res.status(500).send(`Error fetching for markdown: ${error.message}`);
  }
});

// Helper to decode URL from id or u parameter
function getTargetUrl(req: express.Request): string | null {
  let url = (req.query.id || req.query.u || req.body?.id || req.body?.u) as string;

  if (!url && req.method === 'GET') {
    // Fallback for when query params are stripped by forms
    const rawUrl = req.url;
    const match = rawUrl.match(/[?&](id|u)=([^&]+)/);
    if (match) {
      try {
        url = decodeURIComponent(match[2]);
      } catch (e) {
        url = match[2];
      }
    }
  }

  if (!url) return null;

  if (url.startsWith('b64:')) {
    try {
      return Buffer.from(url.substring(4), 'base64').toString('utf-8');
    } catch (e) {
      return url;
    }
  }

  return url;
}

// Proxy endpoint
app.all(["/api/v1/content", "/api/browse", "/browse"], async (req, res) => {
  let targetUrl = getTargetUrl(req);
  const adBlock = (req.query.adblock === 'true' || req.body?.adblock === 'true');

  if (!targetUrl || targetUrl === 'undefined' || targetUrl === 'null' || targetUrl.trim() === '') {
    console.error(`[Proxy] Missing URL. Method: ${req.method}, Query:`, req.query, `Body:`, req.body, `Raw URL:`, req.url);
    return res.status(400).send("URL is required. Please try refreshing the page or re-entering the URL.");
  }

  // Reconstruct the full target URL by including all query parameters except 'id', 'u', 'adblock'
  // Only for GET requests where the targetUrl doesn't already have these params
  if (req.method === 'GET') {
    const queryParams = { ...req.query };
    delete queryParams.id;
    delete queryParams.u;
    delete queryParams.adblock;

    if (Object.keys(queryParams).length > 0) {
      try {
        const urlObj = new URL(targetUrl);
        Object.entries(queryParams).forEach(([key, value]) => {
          if (value !== undefined && !urlObj.searchParams.has(key)) {
            urlObj.searchParams.append(key, String(value));
          }
        });
        targetUrl = urlObj.toString();
      } catch (e) {
        // Not a full URL yet
      }
    }
  }

  // Clean up the URL (sometimes it gets double encoded or has trailing junk)
  targetUrl = targetUrl.trim();
  if (targetUrl.startsWith('"') && targetUrl.endsWith('"')) {
    targetUrl = targetUrl.substring(1, targetUrl.length - 1);
  }

  // Prevent self-proxying recursion
  const host = req.headers.host;
  if (targetUrl && host) {
    try {
      const targetUrlObj = new URL(targetUrl);
      const targetHost = targetUrlObj.host;
      if (targetHost === host || (process.env.NODE_ENV !== 'production' && (targetHost === 'localhost' || targetHost.startsWith('localhost:')))) {
        const urlPath = targetUrlObj.pathname;
        if (urlPath === '/' || urlPath === '/index.html' || urlPath.startsWith('/browse') || urlPath.startsWith('/api/browse') || urlPath.startsWith('/api/v1/content')) {
          return res.status(400).send("Circular proxy detected. Cannot proxy the browser itself.");
        }
      }
    } catch (e) {
      // Invalid URL, continue to axios which will handle it
    }
  }

  try {
    console.log(`[Proxy] [${req.method}] Fetching:`, targetUrl);

    const axiosConfig: any = {
      method: req.method,
      url: targetUrl,
      headers: {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
        "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8",
        "Accept-Language": "en-US,en;q=0.9",
      },
      responseType: "arraybuffer",
      maxRedirects: 10,
      validateStatus: () => true,
      timeout: 15000,
    };

    if (req.method === 'POST') {
      axiosConfig.data = req.body;
      // Forward content-type for POST
      if (req.headers['content-type']) {
        axiosConfig.headers['Content-Type'] = req.headers['content-type'];
      }
    }

    // Forward cookies from the client
    if (req.headers.cookie) {
      axiosConfig.headers['Cookie'] = req.headers.cookie;
    }

    const response = await axios(axiosConfig);

    // Log the request
    const logEntry = {
      method: req.method,
      url: targetUrl,
      status: response.status,
      size: response.headers['content-length'] ? `${(parseInt(response.headers['content-length']) / 1024).toFixed(1)} KB` : 'N/A',
      type: response.headers['content-type']?.split(';')[0] || 'unknown',
      timestamp: Date.now()
    };
    networkLogs.unshift(logEntry);
    if (networkLogs.length > MAX_LOGS) networkLogs.pop();

    // Forward Set-Cookie headers from the target back to the client
    if (response.headers['set-cookie']) {
      res.setHeader('Set-Cookie', response.headers['set-cookie']);
    }

    // Get the final URL after redirects
    const finalUrl = response.request.res.responseUrl || targetUrl;
    const baseUrl = new URL(finalUrl);
    const contentType = response.headers['content-type'] || 'text/html';

    // Aggressively set permissive security headers
    res.setHeader('X-Frame-Options', 'ALLOWALL');
    res.setHeader('Content-Security-Policy', "frame-ancestors *;");
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('X-Content-Type-Options', 'nosniff');
    res.setHeader('Content-Type', contentType);

    // Remove any potentially conflicting headers
    res.removeHeader('X-Content-Security-Policy');
    res.removeHeader('X-WebKit-CSP');
    res.removeHeader('Strict-Transport-Security');

    // If not HTML, just send the data as is
    if (!contentType.includes('text/html')) {
      return res.send(response.data);
    }

    const html = Buffer.from(response.data).toString('utf-8');
    const $ = cheerio.load(html);

    // Remove ALL meta tags that could prevent framing
    $('meta').each((_, el) => {
      const httpEquiv = $(el).attr('http-equiv');
      const name = $(el).attr('name');
      const content = $(el).attr('content');

      if (httpEquiv && /^(content-security-policy|x-frame-options|frame-options|x-ua-compatible)$/i.test(httpEquiv)) {
        $(el).remove();
      }
      if (name && /^(content-security-policy|x-frame-options|frame-options)$/i.test(name)) {
        $(el).remove();
      }
      if (content && /frame-ancestors/i.test(content)) {
        $(el).remove();
      }
    });

    // Anti-frame-busting and location interception
    $('head').prepend(`
      <script>
        (function() {
          // Anti-frame-busting
          try {
            Object.defineProperty(window, "top", { get: function() { return window.self; } });
            Object.defineProperty(window, "parent", { get: function() { return window.self; } });
          } catch (e) {
            window.top = window.self;
            window.parent = window.self;
          }
          window.onbeforeunload = function() { return null; };

          // Intercept location changes (best effort)
          const originalLocation = window.location;
          // We can't easily intercept window.location = '...' but we can try to catch some
        })();
      </script>
    `);

    if (adBlock) {
      // Remove common ad selectors
      const adSelectors = [
        'ins.adsbygoogle', 'div[id^="google_ads"]', 'div[id^="div-gpt-ad"]',
        '.ad-unit', '.ad-box', '.ad-container', '.ad-wrapper', '.adsbox',
        'aside.sidebar-ads', 'div.sponsor-content', 'div.promoted-content',
        'iframe[src*="doubleclick.net"]', 'iframe[src*="googlesyndication.com"]',
        'iframe[src*="adnxs.com"]', 'iframe[src*="taboola.com"]',
        'iframe[src*="outbrain.com"]', 'iframe[src*="amazon-adsystem.com"]',
        'div[class*="ad-"]', 'div[id*="ad-"]', 'div[class*="sponsor"]',
        'section[class*="ad-"]', 'iframe[id*="google_ads"]', 'div[data-ad-unit]'
      ];
      $(adSelectors.join(', ')).remove();
    }

    // Rewrite URLs to stay within proxy
    $("[href], [src], [action], [data-href], [data-src], [srcset]").each((_, el) => {
      const attrs = ["href", "src", "action", "data-href", "data-src", "srcset"];
      attrs.forEach(attr => {
        let val = $(el).attr(attr);
        if (!val || val.startsWith('javascript:') || val.startsWith('data:') || val.startsWith('#')) return;

        // Prevent double proxying
        if (val.startsWith('/api/v1/content') || val.startsWith('/api/browse') || val.startsWith('/browse') ||
            (host && (val.startsWith(`//${host}/api/v1/content`) || val.startsWith(`http://${host}/api/v1/content`) || val.startsWith(`https://${host}/api/v1/content`)))) {
          return;
        }

        if (attr === 'srcset') {
          const parts = val.split(',').map(part => {
            const [url, size] = part.trim().split(/\s+/);
            try {
              const absUrl = new URL(url, finalUrl).href;
              const sidParam = sessionId ? `&sid=${sessionId}` : '';
              return `//${host}/api/v1/content?id=b64:${Buffer.from(absUrl).toString('base64')}${adBlock ? '&adblock=true' : ''}${sidParam}${size ? ' ' + size : ''}`;
            } catch (e) {
              return part;
            }
          });
          $(el).attr(attr, parts.join(', '));
          return;
        }

        try {
          // Use URL constructor with finalUrl as base to handle relative and protocol-relative URLs
          const absoluteUrl = new URL(val, finalUrl).href;

          const encodedUrl = `b64:${Buffer.from(absoluteUrl).toString('base64')}`;
          // Preserve sessionId if present
          const sidParam = sessionId ? `&sid=${sessionId}` : '';
          $(el).attr(attr, `//${host}/api/v1/content?id=${encodedUrl}${adBlock ? '&adblock=true' : ''}${sidParam}`);
        } catch (e) {
          // Ignore invalid URLs
        }
      });
    });

    // Handle GET forms by injecting a hidden 'id' field
    $('form[method="GET"], form:not([method])').each((_, el) => {
      let action = $(el).attr('action') || '';
      try {
        // If the action was already rewritten by the general rewriter, unwrap it
        if (action.includes('/api/v1/content?id=')) {
          const urlObj = new URL(action, `http://${host}`);
          const id = urlObj.searchParams.get('id');
          if (id) {
            if (id.startsWith('b64:')) {
              action = Buffer.from(id.substring(4), 'base64').toString('utf-8');
            } else {
              action = id;
            }
          }
        }

        const absoluteAction = new URL(action, finalUrl).href;
        const encodedAction = `b64:${Buffer.from(absoluteAction).toString('base64')}`;

        // Change action to our proxy
        $(el).attr('action', `//${host}/api/v1/content`);

        // Inject hidden input if not already present
        if ($(el).find('input[name="id"]').length === 0) {
          $(el).prepend(`<input type="hidden" name="id" value="${encodedAction}">`);
        }
        if (adBlock && $(el).find('input[name="adblock"]').length === 0) {
          $(el).prepend('<input type="hidden" name="adblock" value="true">');
        }
        if (sessionId && $(el).find('input[name="sid"]').length === 0) {
          $(el).prepend(`<input type="hidden" name="sid" value="${sessionId}">`);
        }
      } catch (e) {}
    });

    // Handle meta refresh
    $('meta[http-equiv="refresh"]').each((_, el) => {
      const content = $(el).attr('content');
      if (content) {
        const parts = content.split('url=');
        if (parts.length > 1) {
          try {
            const refreshUrl = new URL(parts[1].trim(), finalUrl).href;
            const encodedUrl = `b64:${Buffer.from(refreshUrl).toString('base64')}`;
            const sidParam = sessionId ? `&sid=${sessionId}` : '';
            $(el).attr('content', `${parts[0]}url=//${host}/api/v1/content?id=${encodedUrl}${adBlock ? '&adblock=true' : ''}${sidParam}`);
          } catch (e) {}
        }
      }
    });

    // Inject sniffer script
    const snifferScript = '<script>' +
      '(function() {' +
      '  function sniff() {' +
      '    const media = [];' +
      '    document.querySelectorAll("video, audio, source, [data-src]").forEach(el => {' +
      '      const src = el.src || el.getAttribute("src") || el.getAttribute("data-src");' +
      '      if (src && src.startsWith("http")) {' +
      '        const type = (el.tagName.toLowerCase().includes("video") || src.match(/\\.(mp4|webm|ogg|mov)$|video/i)) ? "video" : "audio";' +
      '        media.push({' +
        '          id: Math.random().toString(36).substr(2, 9),' +
        '          type: type,' +
        '          src: src,' +
        '          title: el.getAttribute("title") || el.getAttribute("aria-label") || document.title || (type.charAt(0).toUpperCase() + type.slice(1) + " File")' +
        '        });' +
        '      }' +
        '    });' +
        '    document.querySelectorAll("img, [style*=\'background-image\'], picture source").forEach(el => {' +
        '      let src = "";' +
        '      if (el.tagName === "IMG") src = el.src || el.getAttribute("src");' +
        '      else if (el.tagName === "SOURCE") src = el.srcset || el.getAttribute("srcset");' +
        '      else {' +
        '        const bg = window.getComputedStyle(el).backgroundImage;' +
        '        if (bg && bg !== "none") {' +
        '          const match = bg.match(/url\\(["\']?(.*?)["\']?\\)/);' +
        '          if (match) src = match[1];' +
        '        }' +
        '      }' +
        '      if (src && src.startsWith("http")) {' +
        '        const isLarge = el.naturalWidth > 100 || el.naturalHeight > 100 || !el.naturalWidth;' +
        '        if (isLarge) {' +
        '          media.push({' +
        '            id: Math.random().toString(36).substr(2, 9),' +
        '            type: "image",' +
        '            src: src,' +
        '            title: el.alt || el.title || "Image File"' +
        '          });' +
        '        }' +
        '      }' +
        '    });' +
        '    const uniqueMedia = [];' +
        '    const seen = new Set();' +
        '    for (const item of media) {' +
        '      if (!seen.has(item.src)) {' +
        '        seen.add(item.src);' +
        '        uniqueMedia.push(item);' +
        '      }' +
        '    }' +
        '    if (uniqueMedia.length > 0) {' +
        '      window.parent.postMessage({ type: "MEDIA_DETECTED", media: uniqueMedia }, "*");' +
        '    }' +
        '  }' +
        '  setInterval(sniff, 3000);' +
        '  sniff();' +
        '  ' +
        '  let lastY = window.scrollY;' +
        '  window.addEventListener("scroll", () => {' +
        '    const currentY = window.scrollY;' +
        '    if (Math.abs(currentY - lastY) > 10) {' +
        '      window.parent.postMessage({ type: "SCROLL", direction: currentY > lastY ? "down" : "up", y: currentY }, "*");' +
        '      lastY = currentY;' +
        '    }' +
        '  }, { passive: true });' +
        '  ' +
        '  document.addEventListener("click", (e) => {' +
        '    const link = e.target.closest("a");' +
        '    if (link && link.href && !link.href.startsWith("javascript:") && !link.href.startsWith("#")) {' +
        '      e.preventDefault();' +
        '      let targetUrl = link.href;' +
        '      if (targetUrl.includes("/api/v1/content?id=") || targetUrl.includes("/api/browse?u=")) {' +
        '        try {' +
        '          const urlObj = new URL(targetUrl, window.location.origin);' +
        '          const extracted = urlObj.searchParams.get("id") || urlObj.searchParams.get("u");' +
        '          if (extracted) {' +
        '            if (extracted.startsWith("b64:")) {' +
        '              targetUrl = atob(extracted.substring(4));' +
        '            } else {' +
        '              targetUrl = extracted;' +
        '            }' +
        '          }' +
        '        } catch (err) {' +
        '          const match = targetUrl.match(/[?&](id|u)=([^&]+)/);' +
        '          if (match) {' +
        '            const extracted = decodeURIComponent(match[2]);' +
        '            if (extracted.startsWith("b64:")) {' +
        '              targetUrl = atob(extracted.substring(4));' +
        '            } else {' +
        '              targetUrl = extracted;' +
        '            }' +
        '          }' +
        '        }' +
        '      }' +
          '      if (targetUrl) {' +
          '        window.parent.postMessage({ type: "NAVIGATE_TO", url: targetUrl }, "*");' +
          '      }' +
          '    }' +
          '  }, true);' +
          '  ' +
          '  document.addEventListener("submit", (e) => {' +
          '    const form = e.target;' +
          '    if (form.method.toUpperCase() === "GET") {' +
          '      e.preventDefault();' +
          '      const formData = new FormData(form);' +
          '      const params = new URLSearchParams();' +
          '      for (const [key, value] of formData.entries()) {' +
          '        params.append(key, value);' +
          '      }' +
          '      let action = form.getAttribute("action") || "";' +
          '      if (action.includes("/api/v1/content?id=") || action.includes("/api/browse?u=")) {' +
          '        try {' +
          '          const urlObj = new URL(action, window.location.origin);' +
          '          const extracted = urlObj.searchParams.get("id") || urlObj.searchParams.get("u");' +
          '          if (extracted) {' +
          '            if (extracted.startsWith("b64:")) {' +
          '              action = atob(extracted.substring(4));' +
          '            } else {' +
          '              action = extracted;' +
          '            }' +
          '          }' +
          '        } catch (err) {}' +
          '      }' +
          '      const targetUrl = new URL(action, window.location.href);' +
          '      params.forEach((v, k) => { if (k !== "id" && k !== "u" && k !== "adblock") targetUrl.searchParams.set(k, v); });' +
          '      window.parent.postMessage({ type: "NAVIGATE_TO", url: targetUrl.href }, "*");' +
          '    }' +
          '  }, true);' +
          '  ' +
          '  window.addEventListener("message", (event) => {' +
          '    if (event.data?.type === "EXECUTE_SCRIPT") {' +
          '      try { eval(event.data.code); } catch (e) { console.error("User Script Error:", e); }' +
          '    }' +
          '  });' +
          '})();' +
          '</script>';
      $("body").append(snifferScript);

      res.send($.html());
    } catch (error: any) {
      console.error("[Proxy] Error:", error.message);
      res.status(500).send(`Proxy error: ${error.message}`);
    }
});

// Source view endpoint
app.get("/api/source", async (req, res) => {
    const targetUrl = req.query.u as string;
    if (!targetUrl) return res.status(400).send("URL is required");
    try {
      const response = await axios.get(targetUrl, { timeout: 15000 });
      res.set("Content-Type", "text/plain");
      res.send(response.data);
    } catch (error: any) {
      res.status(500).send(`Error fetching source: ${error.message}`);
    }
});

// Reader mode endpoint (simplified)
app.get(["/api/reader", "/reader"], async (req, res) => {
    const targetUrl = req.query.u as string;
    const adBlock = req.query.adblock === 'true';
    if (!targetUrl) return res.status(400).send("URL is required");
    try {
      const response = await axios.get(targetUrl, { timeout: 15000 });
      const $ = cheerio.load(response.data);

      // Remove noise
      const noiseSelectors = ["script", "style", "nav", "footer", "header"];
      if (adBlock) {
        noiseSelectors.push("ads", ".ads", "#ads", "ins.adsbygoogle", "div[id^='google_ads']");
      }
      $(noiseSelectors.join(', ')).remove();

      const title = $("title").text() || $("h1").first().text();
      const content = $("article").html() || $("main").html() || $("body").html();

      const readerHtml = `
        <!DOCTYPE html>
        <html>
        <head>
          <title>${title}</title>
          <style>
            body { font-family: 'Georgia', serif; line-height: 1.6; max-width: 800px; margin: 40px auto; padding: 20px; background: #fdfdfd; color: #1a1a1a; }
            h1 { font-size: 2.5em; margin-bottom: 0.5em; }
            img { max-width: 100%; height: auto; border-radius: 8px; }
            pre { background: #f4f4f4; padding: 15px; overflow-x: auto; border-radius: 4px; }
          </style>
          <script>
            window.addEventListener('message', (event) => {
              if (event.data?.type === 'EXECUTE_SCRIPT') {
                try {
                  eval(event.data.code);
                } catch (e) {
                  console.error('User Script Error:', e);
                }
              }
            });
          </script>
        </head>
        <body>
          <h1>${title}</h1>
          <hr>
          ${content}
        </body>
        </html>
      `;
      res.send(readerHtml);
    } catch (error: any) {
      res.status(500).send(`Error fetching reader mode: ${error.message}`);
    }
  });

// Security check endpoint
app.get("/api/v1/security-check", async (req, res) => {
  const targetUrl = req.query.u as string;
  if (!targetUrl) return res.status(400).send("URL is required");

  try {
    const response = await axios.get(targetUrl, {
      headers: {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
      },
      timeout: 10000,
      validateStatus: () => true,
    });

    const headers = response.headers;
    const isHttps = targetUrl.startsWith('https://');

    const check = {
      isHttps,
      hsts: !!headers['strict-transport-security'],
      csp: !!headers['content-security-policy'],
      xFrameOptions: headers['x-frame-options'] || 'Not set',
      contentTypeOptions: headers['x-content-type-options'] || 'Not set',
      poweredBy: headers['x-powered-by'] || 'Hidden',
      server: headers['server'] || 'Hidden'
    };

    res.json(check);
  } catch (error: any) {
    res.status(500).send(`Security check error: ${error.message}`);
  }
});

// AI Analysis endpoint
app.get("/api/analyze", async (req, res) => {
  const targetUrl = req.query.u as string;
  if (!targetUrl) return res.status(400).send("URL is required");

  const apiKey = process.env.VITE_GEMINI_API_KEY;
  if (!apiKey) return res.status(500).send("Gemini API key not configured");

  try {
    const response = await axios.get(targetUrl, {
      headers: {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
      },
      timeout: 15000
    });
    const $ = cheerio.load(response.data);

    // Extract main content
    $("script, style, nav, footer, header").remove();
    const text = $("article").text() || $("main").text() || $("body").text();
    const cleanText = text.replace(/\s+/g, ' ').trim().substring(0, 10000);

    const genAI = new GoogleGenerativeAI(apiKey);
    const model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });

    const prompt = `Summarize the following web page content in a concise way with key highlights. Content: ${cleanText}`;

    const result = await model.generateContent(prompt);
    const summary = result.response.text();

    res.json({ summary });
  } catch (error: any) {
    res.status(500).send(`AI Analysis error: ${error.message}`);
  }
});

// Setup development or production environment
async function setupApp() {
  if (process.env.NODE_ENV !== "production") {
    try {
      // @ts-ignore
      const { createServer: createViteServer } = await import("vite");
      const vite = await createViteServer({
        server: { middlewareMode: true },
        appType: "spa",
      });
      app.use(vite.middlewares);
    } catch (e) {
      console.warn("Vite not found, skipping development middleware");
    }
  } else {
    const distPath = path.join(process.cwd(), "dist");
    app.use(express.static(distPath));
    app.get("*", (req, res) => {
      res.sendFile(path.join(distPath, "index.html"));
    });
  }
}

setupApp();

// Only listen if not on Vercel
if (process.env.NODE_ENV !== "production" || !process.env.VERCEL) {
  app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
  });
}

export default app;
