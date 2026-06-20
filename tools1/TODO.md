# Omni Toolbox - Implementation Roadmap

## Core Modules & Functional Logic
- [ ] **Cloud Sync Hub**: Implement actual OAuth2/API integration for Google Drive, Mega, OneDrive, and Nextcloud.
- [ ] **Web Scraper & Notion Sync**:
    - Implement actual web crawling using a WebView-based or OkHttp-based scraper.
    - Implement Notion API integration to push extracted content.
    - Add rule configurator logic for multi-level sites (forums, threads, pages).
    - Implement document chunking (10-page subpages) for Notion uploads.
- [ ] **Gemini AI Hub**:
    - Integrate actual Google Gemini API for Chat, Code Review, and Summarization.
    - Implement Text-to-Video and Text-to-Music generation simulators/proxies.
- [ ] **Developer & Hardware Console**:
    - Improve App Manager: Implement actual APK extraction and cache cleaning logic (rootless).
    - Implement actual hardware inspection for all specs (CPU, Kernel, etc.).
- [ ] **Security Vault**:
    - Ensure AES-256 logic is robust.
    - Implement actual QR code generation and camera scanning.
- [ ] **Automation & Macros**:
    - Implement rule triggers (timers, shake gestures) using Android WorkManager or Services.
- [ ] **Data Tools**:
    - Implement actual logic for CSV/JSON/Excel processing (Statistics, Cleaning, Anomaly Detection).

## Media & Utility Tools
- [ ] **PDF Toolkit**: Implement actual PDF processing (Merge, Split, Compress, Rotate, OCR, PDF to MDX, PDF to MHTML) using a library like PdfBox-Android (already in requirements).
- [ ] **Image Tools**: Implement batch processing, filters, background removal (AI), and format conversion.
- [ ] **Audio & Video Lab**:
    - Implement actual audio trimming, merging, and mixing.
    - Implement AI-based vocal remover and stem separation (heuristic/local model).
    - Video-to-audio and frame extraction logic.
- [ ] **Social Media Suite**: Bulk media downloader for Instagram, Facebook, Twitter, Pinterest.
- [ ] **Web-to-App Converter**: WebView-based standalone app wrapper with profile support.

## System & Monitoring
- [ ] **Omni Dashboard**: Replace simulated stats with actual Android System API calls (CPU load, RAM usage, Thermals, Battery).
- [ ] **Telemetry & Stats**: Implement actual call/SMS statistics and database backup/restore to GDrive.
- [ ] **PowerBench**: Implement actual benchmark tests (Prime search, Mandelbrot, I/O speed).
- [ ] **Quick Tiles**: Implement DynamicTileService integration with user-defined actions.

## UI/UX Improvements
- [ ] Standardize Material 3 "Slate-Carbon" theme with "Neon-Mint" accents across all screens.
- [ ] Ensure responsive layouts using LazyColumn/LazyVerticalGrid where appropriate.
- [ ] Add transition animations and haptic feedback.

## Branding
- [x] Rename from Nature Tools to Omni Toolbox.
- [x] Update package name to omni.toolbox.
