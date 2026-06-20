# 🌿 Nature Toolbox (Ultimate Edition)

**The most comprehensive, organic-inspired tool suite for modern digital explorers.**

Nature Toolbox is a beautifully crafted application that blends high-performance utility with a lush, nature-inspired aesthetic. From AI-powered species identification to scientific monitoring and daily productivity, every tool is designed to feel like a natural extension of your workflow.

---

## ✨ Features

### 🍃 Lush Nature UI (Elegant Dark)
A premium "Elegant Dark" design theme featuring high-contrast charcoal backgrounds, vibrant moss-green accents, and sharp typography.

### 🧪 Advanced Science Tools
- **Nature Dex (AI)**: Identify any plant, insect, or animal instantly using the integrated **Gemini AI API**.
- **Sound Meter**: Real-time decibel monitoring with live telemetry charts.
- **Compass & Level**: Technical-grade orientation tools for the modern explorer.
- **Periodic Table**: A minimalist reference for fundamental chemical elements.

### 🛠️ Core Utilities
- **Scientific Calculator**: Large-scale math with an organic feel.
- **Unit Converter**: Swift translations for mass, length, and more.
- **Stopwatch & Flashlight**: Essential quick-access tools for any situation.
- **System Monitor**: Real-time tracking of CPU load and stability.

### 📝 Productivity
- **Notepad & To-Do**: Quick, persistent notes and priority tracking to keep you focused.
- **Budget Tracker**: Track your seedlings (expenses) with clear visual feedback.
- **Weather Forecast**: Live atmospheric monitoring and 4-day outlook.

---

## 🚀 Tech Stack

- **Framework**: [React 19](https://react.dev/) + [Vite](https://vitejs.dev/)
- **Language**: [TypeScript](https://www.typescriptlang.org/)
- **AI Engine**: [Google Gemini Pro Vision API](https://ai.google.dev/)
- **Styling**: [Tailwind CSS v4](https://tailwindcss.com/)
- **Animations**: [Motion](https://motion.dev/) (formerly Framer Motion)
- **Charts**: [Recharts](https://recharts.org/)
- **Icons**: [Lucide React](https://lucide.dev/)

---

## 📲 Android Deployment

This project is prepared for **Android deployment** using **Capacitor**. To build your own debug APK, you can use the integrated GitHub Actions workflow.

### Building Locally
1. Install dependencies: `npm install`
2. Build the web app: `npm run build`
3. Sync with Android: `npx cap sync android`
4. Open in Android Studio: `npx cap open android`

---

## ⚙️ Environment Variables

To use the **Nature Dex (AI)**, you must provide a Gemini API Key:

```env
GEMINI_API_KEY="YOUR_GEMINI_API_KEY"
```

---

## 👨‍💻 Developer Guide

### Getting Started
```bash
npm install
npm run dev
```

### GitHub Actions
The repository includes an automatic CI/CD pipeline in `.github/workflows/android-release.yml` that builds a **Debug APK** every time you push to the `main` branch.

---

*Crafted with 🌿 by the Nature Toolbox Team.*
