# Gap Analysis & Integration Plan

## 1. Trail Sense (tools-main)
**Target:** Outdoor/Navigation/Environment Category
- [Missing] Beacon Navigation (Radar-style UI, proximity alerts). *Main app has a screen but needs the full logic.*
- [Missing] Path Tracking (Breadcrumbs, backtracking, retrace steps).
- [Missing] Weather Prediction (Barometric trend analysis, storm alerts).
- [Missing] Sighting Compass & Clinometer (AR/Camera integration).
- [Existing] Flashlight, SOS, QR Scanner.

## 2. Image Toolbox (ImageTools-master)
**Target:** Media/Image Category
- [Missing] Batch Processing (Filters, scaling, cropping, rotation in one workflow).
- [Missing] Advanced Filters (Saturation, Contrast, Brightness, Exposure, RGB, Hue, White Balance, etc. - ~20 filters).
- [Missing] Format Conversion (JPEG, PNG, WEBP, AVIF, JXL).
- [Missing] EXIF Editing (Batch edit/remove metadata).
- [Missing] Palette Extraction (Extract colors from images).

## 3. Material Files (MaterialFiles-master)
**Target:** Documents Category (File Explorer)
- [Missing] NAS Support (SMB, SFTP, WebDAV, FTP).
- [Missing] Archive Management (Extract/Create 7z, Tar, Gzip, Bzip2).
- [Missing] Root File Access support.
- [Missing] Linux-aware features (Symlinks, permissions).

## 4. YT Pro (YT-main)
**Target:** Media/Web Category
- [Missing] Integrated Downloader (Support for multiple formats/resolutions).
- [Missing] Gemini Video Summarization (Extract captions and summarize).
- [Missing] Muxing (In-built video/audio muxing for high-res downloads).
- [Missing] PiP & Gesture Controls for playback.

## 5. DocTools (Doctools-main)
**Target:** Documents Category (PDF Toolkit)
- [Missing] PDF Protection (Password encrypt/decrypt).
- [Missing] Rearrange/Rotate/Remove Pages.
- [Missing] PDF to Images (Export pages as high-res images).
- [Missing] Repair corrupted PDFs.

## 6. Notion Ingestion Engine (to-notion-main)
**Target:** Documents/Web Category (Docs Crawler)
- [Missing] Recursive Folder Scanning (Upload whole directory trees).
- [Missing] Smart Chunking (10-page subpage splitting for Notion).
- [Missing] Database Property Mapping (Path, Extension).

## Required Dependencies to Add:
- `com.hierynomus:smbj:0.11.5` (SMB)
- `com.github.mwiede:jsch:0.2.11` (SFTP)
- `com.tom_roush:pdfbox-android:2.0.27.0` (Already present, but need full usage)
- `org.apache.commons:commons-compress:1.21` (Archives)
- `com.github.t8rin:ImageToolbox` (If using as library) or porting the `GPUImage` filters.
- `com.google.ai.client.generativeai` (Already present)

## Required Permissions:
- `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `ACCESS_BACKGROUND_LOCATION` (Trail Sense)
- `ACTIVITY_RECOGNITION` (Pedometer)
- `CAMERA` (QR, Sighting Compass)
- `POST_NOTIFICATIONS` (Alerts)
- `INTERNET` (NAS, Gemini, Downloader)
