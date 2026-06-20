# Building Omni Web as an Android APK

Since Omni Web is a PWA (Progressive Web App), the easiest and most "native" way to turn it into an APK is using **Trusted Web Activity (TWA)** via **Bubblewrap** or using **Capacitor**.

## Method 1: Bubblewrap (Easiest & Recommended for TWA)
Bubblewrap converts your PWA manifest into a real Android Studio project and builds an APK for you.

### 1. Install Bubblewrap CLI
You'll need Node.js installed.
```bash
npm install -g @bubblewrap/cli
```

### 2. Initialize the Project
Run this in a new folder (not inside this repo):
```bash
bubblewrap init --manifest https://your-deployed-app.vercel.app/manifest.json
```
*Replace the URL with your actual deployed URL.*

### 3. Build the APK
```bash
bubblewrap build
```
This will generate a `base-release.apk` which you can install on your Android device.

---

## Method 2: Capacitor (More control, feels like a native app)
Capacitor wraps your web app in a native WebView and allows you to add native features easily.

### 1. Install Capacitor
Inside this repository:
```bash
npm install @capacitor/core @capacitor/cli @capacitor/android
npx cap init
```

### 2. Build your web app
```bash
npm run build
```

### 3. Add Android platform
```bash
npx cap add android
```

### 4. Sync your code to the Android project
```bash
npx cap copy
```

### 5. Open in Android Studio to build APK
```bash
npx cap open android
```
In Android Studio: **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

---

## Method 3: Website to APK (Web Services)
If you don't want to use the command line, you can use online tools like:
1. **PWA2APK** (pwa-to-apk.com)
2. **Progressier**
3. **CloudAPK**

Just enter your deployed URL and download the generated APK.

### Important Notes:
- To remove the browser address bar in the APK, ensure your PWA is served over **HTTPS** and you have verified your **Digital Asset Links** (Assetlinks.json).
- The APK will behave exactly like a native browser, but it will use the system's Chrome WebView.
