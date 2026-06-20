# 🌐 Omni Web Browser

A powerful, feature-rich web browser built entirely in the browser using React, TypeScript, and the Google Gemini API. Browse the web, extract content, and leverage AI capabilities—all in one unified interface.

**Live Demo:** [https://omni-web-five.vercel.app](https://omni-web-five.vercel.app)

## ✨ Features

- **🔍 Web Browsing** - Full-featured web proxy with support for most websites
- **🤖 AI-Powered Analysis** - Leverage Google Gemini API for intelligent content analysis
- **📄 Multiple View Modes**:
  - 🌍 Normal browsing mode
  - 📖 Reader mode for distraction-free reading
  - 💻 Source code viewer
  - 🔄 Markdown conversion
- **🎯 Media Detection** - Automatically detect and extract images, videos, and audio
- **🚫 Ad Blocking** - Built-in ad blocker to enhance browsing experience
- **📸 Screenshot & PDF Export** - Capture web pages and export as PDF
- **⚡ High Performance** - Built with Vite for lightning-fast development and production builds
- **🎨 Beautiful UI** - Modern, responsive interface with Tailwind CSS and Motion animations
- **🔒 Privacy-Focused** - All processing happens in your browser

## 🚀 Quick Start

### Prerequisites
- Node.js 16+ 
- npm or yarn

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/Subbarao6338/Omni-Web.git
cd Omni-Web
```

2. **Install dependencies**
```bash
npm install
```

3. **Set up environment variables**
```bash
cp .env.example .env.local
```

Edit `.env.local` and add your Gemini API key:
```
VITE_GEMINI_API_KEY=your_gemini_api_key_here
```

Get your free API key from [Google AI Studio](https://aistudio.google.com/app/apikey)

4. **Run the development server**
```bash
npm run dev
```

The application will start at `http://localhost:3000`

## 📦 Available Scripts

```bash
# Start development server with hot reload
npm run dev

# Build for production
npm run build

# Preview production build locally
npm run preview

# Start production server
npm start

# Clean build artifacts
npm clean

# Type checking (without emitting)
npm run lint
```

## 🏗️ Project Structure

```
Omni-Web/
├── src/                    # React components and frontend logic
├── server.ts              # Express server with proxy and API endpoints
├── public/                # Static assets
├── index.html             # Entry HTML file
├── vite.config.ts         # Vite configuration
├── tsconfig.json          # TypeScript configuration
├── package.json           # Dependencies and scripts
└── README.md              # This file
```

## 🔧 Tech Stack

- **Frontend**: React 19, TypeScript, Tailwind CSS
- **Backend**: Express.js, Vite
- **AI**: Google Generative AI (@google/genai)
- **Web Processing**: 
  - Cheerio - HTML parsing
  - Axios - HTTP client
  - Turndown - HTML to Markdown conversion
- **Export**: 
  - html2canvas - Screenshot capture
  - jsPDF - PDF generation
- **UI/UX**: 
  - Lucide React - Icons
  - Motion - Animations
  - clsx - Utility class management
- **Performance**: Vercel Speed Insights

## 🌟 Key Endpoints

### API Routes
- `GET /api/proxy?url=<URL>` - Proxy any website with frame-busting prevention and ad blocking
- `GET /api/reader?url=<URL>` - Reader mode for clean article reading
- `GET /api/source?url=<URL>` - View page source code
- `GET /api/markdown?url=<URL>` - Extract and convert content to markdown

## 🎮 Usage

1. **Enter a URL** - Type any website URL in the address bar
2. **Choose View Mode**:
   - Normal: Full website browsing
   - Reader: Clean reading experience
   - Source: View HTML source
3. **Extract Media** - Auto-detected media files appear in sidebar
4. **Use AI Tools** - Analyze page content with Gemini AI
5. **Export** - Screenshot or save as PDF

## 🛡️ Security & Privacy

- All browsing happens in your browser
- No data is stored on servers (except temporary processing)
- Ad blocker removes tracking scripts
- CSP and framing controls are bypassed for usability
- User scripts are sandboxed and executed safely

## 🚀 Deployment

### Deploy to Vercel (Recommended)

```bash
npm install -g vercel
vercel
```

### Deploy to Other Platforms

The app can be deployed to any Node.js hosting platform:
- Heroku
- Railway
- Render
- AWS
- DigitalOcean

Build command: `npm run build`
Start command: `npm start`

## 📝 Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `VITE_GEMINI_API_KEY` | Google Gemini API key | Yes |

## 🐛 Troubleshooting

### "API key not found" error
- Ensure `.env.local` file exists in root directory
- Verify `VITE_GEMINI_API_KEY` is set correctly
- Restart dev server after changing env variables

### Page doesn't load in proxy
- Some websites may have additional security measures
- Try Reader mode instead
- Check browser console for error details

### Build fails
```bash
npm clean
npm install
npm run build
```

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is open source and available under the MIT License.

## 🙏 Acknowledgments

- [Google Generative AI](https://ai.google.dev/) - AI capabilities
- [Vercel](https://vercel.com/) - Hosting and deployment
- [React](https://react.dev/) - UI framework
- [Tailwind CSS](https://tailwindcss.com/) - Styling
- All the open-source libraries that make this possible

## 📞 Support

If you encounter any issues or have questions:
- 📧 Email: subbarao6338@gmail.com
- 🐛 [GitHub Issues](https://github.com/Subbarao6338/Omni-Web/issues)
- 💬 [GitHub Discussions](https://github.com/Subbarao6338/Omni-Web/discussions)

---

**Made with ❤️ by [Subbarao6338](https://github.com/Subbarao6338)**

⭐ If you find this project helpful, please consider giving it a star!