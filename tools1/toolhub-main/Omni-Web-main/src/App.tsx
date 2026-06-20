/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useEffect, useRef } from 'react';
import { App as CapApp } from '@capacitor/app';
import { Device } from '@capacitor/device';
import { 
  Search, 
  ChevronLeft, 
  ChevronRight, 
  RotateCcw, 
  Plus, 
  Layers, 
  Download, 
  MoreVertical, 
  X, 
  Globe,
  Link,
  FileText,
  Video,
  Music,
  Settings,
  History,
  Bookmark,
  ArrowDownToLine,
  ExternalLink,
  Trash2,
  CheckCircle2,
  Circle,
  AlertCircle,
  Pause,
  Play,
  Copy,
  Share2,
  RefreshCw,
  Monitor,
  Smartphone,
  SearchCode,
  Wand2,
  Calculator,
  Languages,
  QrCode,
  Key,
  StickyNote,
  Terminal,
  Cpu,
  ShieldCheck,
  Eye,
  EyeOff,
  Scale,
  Timer,
  Type,
  Braces,
  ChevronDown,
  ChevronUp,
  Hash,
  Clock,
  Activity,
  User,
  Calendar,
  Palette,
  Cookie
} from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import { cn } from './lib/utils';
import { Tab, DownloadItem, MediaItem, HistoryItem, BookmarkItem, UserScript, Bookmarklet, AppSettings } from './types';
import TurndownService from 'turndown';
import { jsPDF } from 'jspdf';
import html2canvas from 'html2canvas';

const DEFAULT_URL = 'about:home';

const toBase64Url = (url: string) => {
  if (!url || url === 'about:blank') return url;
  try {
    return `b64:${btoa(url)}`;
  } catch (e) {
    return url;
  }
};

const fromBase64Url = (url: string) => {
  if (url.startsWith('b64:')) {
    try {
      return atob(url.substring(4));
    } catch (e) {
      return url;
    }
  }
  return url;
};

const HomeView = ({ onNavigate }: { onNavigate: (url: string) => void }) => {
  const [query, setQuery] = React.useState('');
  const [shortcuts, setShortcuts] = React.useState(() => {
    const saved = localStorage.getItem('browser_shortcuts');
    return saved ? JSON.parse(saved) : [
      { title: 'Google', url: 'https://www.google.com' },
      { title: 'YouTube', url: 'https://www.youtube.com' },
      { title: 'GitHub', url: 'https://www.github.com' },
      { title: 'Reddit', url: 'https://www.reddit.com' },
      { title: 'Wikipedia', url: 'https://www.wikipedia.org' },
      { title: 'Amazon', url: 'https://www.amazon.com' },
      { title: 'X', url: 'https://x.com' },
      { title: 'Instagram', url: 'https://www.instagram.com' },
    ];
  });

  const addShortcut = () => {
    const title = prompt('Shortcut Title:');
    const url = prompt('Shortcut URL:');
    if (title && url) {
      const newShortcuts = [...shortcuts, { title, url }];
      setShortcuts(newShortcuts);
      localStorage.setItem('browser_shortcuts', JSON.stringify(newShortcuts));
    }
  };

  const removeShortcut = (index: number) => {
    if (confirm('Remove this shortcut?')) {
      const newShortcuts = shortcuts.filter((_: any, i: number) => i !== index);
      setShortcuts(newShortcuts);
      localStorage.setItem('browser_shortcuts', JSON.stringify(newShortcuts));
    }
  };

  return (
    <div className="flex flex-col items-center pt-24 px-8 h-full bg-surface overflow-y-auto scrollbar-hide">
      <motion.div
        initial={{ scale: 0.8, opacity: 0, rotate: -10 }}
        animate={{ scale: 1, opacity: 1, rotate: 0 }}
        transition={{ type: 'spring', damping: 15 }}
        className="w-24 h-24 bg-primary rounded-[36px] flex items-center justify-center mb-10 shadow-expressive"
      >
        <Globe size={56} className="text-on-primary" />
      </motion.div>
      <motion.h1
        initial={{ y: 20, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ delay: 0.1, type: 'spring' }}
        className="text-3xl font-bold mb-10 text-on-surface tracking-tighter"
      >
        Omni Browser
      </motion.h1>

      <motion.div
        initial={{ y: 20, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ delay: 0.2, type: 'spring' }}
        className="w-full max-w-lg relative mb-16"
      >
        <div className="absolute left-6 top-1/2 -translate-y-1/2 text-primary">
          <Search size={24} />
        </div>
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && onNavigate(query)}
          placeholder="Search or type URL"
          className="w-full pl-16 pr-8 py-5 bg-surface-container-high rounded-[32px] shadow-sm border-none focus:ring-8 focus:ring-primary/5 focus:bg-surface-container-lowest text-lg transition-all"
        />
      </motion.div>

      <motion.div
        initial={{ y: 20, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ delay: 0.3, type: 'spring' }}
        className="grid grid-cols-4 gap-y-10 gap-x-6 w-full max-w-lg pb-32"
      >
        {shortcuts.map((s: any, i: number) => (
          <div key={i} className="flex flex-col items-center gap-3 group relative">
            <button
              onClick={() => onNavigate(s.url)}
              onContextMenu={(e) => { e.preventDefault(); removeShortcut(i); }}
              className="w-16 h-16 bg-surface-container-low rounded-[24px] flex items-center justify-center shadow-sm group-active:scale-[0.85] group-hover:shadow-md transition-all duration-300"
            >
              <img
                src={`https://www.google.com/s2/favicons?domain=${s.url}&sz=128`}
                alt={s.title}
                className="w-10 h-10 rounded-xl"
              />
            </button>
            <span className="text-xs font-bold text-on-surface-variant truncate w-full text-center px-1">{s.title}</span>
            <button
              onClick={() => removeShortcut(i)}
              className="absolute -top-2 -right-2 w-6 h-6 bg-red-500 text-white rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity shadow-sm"
            >
              <X size={12} />
            </button>
          </div>
        ))}
        <button
          onClick={addShortcut}
          className="flex flex-col items-center gap-3 group"
        >
          <div className="w-16 h-16 bg-primary-container text-on-primary-container rounded-[24px] flex items-center justify-center shadow-sm group-active:scale-[0.85] transition-all duration-300">
            <Plus size={32} />
          </div>
          <span className="text-xs font-bold text-primary truncate w-full text-center px-1">Add</span>
        </button>
      </motion.div>
    </div>
  );
};

export default function App() {
  const isFramed = typeof window !== 'undefined' && window.self !== window.top;

  const [tabs, setTabs] = useState<Tab[]>(() => {
    const saved = localStorage.getItem('browser_settings');
    const initialUrl = saved ? JSON.parse(saved).homepageUrl : DEFAULT_URL;
    return [{ id: '1', url: initialUrl, title: 'Home', loading: false }];
  });
  const [activeTabId, setActiveTabId] = useState<string>('1');
  const [urlInput, setUrlInput] = useState<string>(() => {
    const saved = localStorage.getItem('browser_settings');
    return saved ? JSON.parse(saved).homepageUrl : DEFAULT_URL;
  });
  const [isTabsOpen, setIsTabsOpen] = useState(false);
  const [isDownloadsOpen, setIsDownloadsOpen] = useState(false);
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [downloads, setDownloads] = useState<DownloadItem[]>([]);
  const downloadBlobs = useRef<Record<string, Blob>>({});
  const downloadControllers = useRef<Record<string, AbortController>>({});
  const [detectedMedia, setDetectedMedia] = useState<MediaItem[]>([]);
  const [showMediaGrabber, setShowMediaGrabber] = useState(false);
  const [history, setHistory] = useState<HistoryItem[]>(() => {
    const saved = localStorage.getItem('browser_history');
    return saved ? JSON.parse(saved) : [];
  });
  const [isHistoryOpen, setIsHistoryOpen] = useState(false);
  const [isMediaSnifferOpen, setIsMediaSnifferOpen] = useState(false);
  const [isToolboxOpen, setIsToolboxOpen] = useState(false);
  const [activeTool, setActiveTool] = useState<string | null>(null);
  const [aiSummary, setAiSummary] = useState<string>('');
  const [isAiLoading, setIsAiLoading] = useState(false);
  const [collapsedCategories, setCollapsedCategories] = useState<string[]>([]);
  const [calcValue, setCalcValue] = useState('0');
  const [qrText, setQrText] = useState('');
  const [notes, setNotes] = useState<string>(() => localStorage.getItem('browser_notes') || '');
  const [laps, setLaps] = useState<number[]>([]);
  
  // New Tools State
  const [unitFrom, setUnitFrom] = useState('cm');
  const [unitTo, setUnitTo] = useState('inch');
  const [unitValue, setUnitValue] = useState('1');
  const [stopwatchTime, setStopwatchTime] = useState(0);
  const [isStopwatchRunning, setIsStopwatchRunning] = useState(false);
  const [timerTime, setTimerTime] = useState(0);
  const [timerInput, setTimerInput] = useState('5');
  const [isTimerRunning, setIsTimerRunning] = useState(false);
  const [textInput, setTextInput] = useState('');
  const [textInput2, setTextInput2] = useState('');
  const [jsonInput, setJsonInput] = useState('');
  const [jsonOutput, setJsonOutput] = useState('');
  const [bookmarks, setBookmarks] = useState<BookmarkItem[]>(() => {
    const saved = localStorage.getItem('browser_bookmarks');
    return saved ? JSON.parse(saved) : [];
  });
  const [isBookmarksOpen, setIsBookmarksOpen] = useState(false);
  const [isFindOnPageOpen, setIsFindOnPageOpen] = useState(false);
  const [findText, setFindText] = useState('');
  const [isOnline, setIsOnline] = useState(navigator.onLine);
  const [isAndroid, setIsAndroid] = useState(false);

  const [showNav, setShowNav] = useState(true);
  const lastScrollY = useRef(0);

  const [mediaFilter, setMediaFilter] = useState<'all' | 'video' | 'audio' | 'image'>('all');
  const [mediaSearch, setMediaSearch] = useState('');

  const [downloadFilter, setDownloadFilter] = useState<'all' | 'downloading' | 'completed' | 'paused' | 'queued'>('all');
  const [downloadTypeFilter, setDownloadTypeFilter] = useState<'all' | 'video' | 'audio' | 'image' | 'other'>('all');
  const [downloadSort, setDownloadSort] = useState<'date' | 'name' | 'type'>('date');
  const [isContextMenuOpen, setIsContextMenuOpen] = useState(false);
  const [contextMenuPos, setContextMenuPos] = useState({ x: 0, y: 0 });

  const [settings, setSettings] = useState<AppSettings>(() => {
    const saved = localStorage.getItem('browser_settings');
    const defaults: AppSettings = {
      homepageUrl: DEFAULT_URL,
      downloadFolder: '/Downloads',
      themeColor: '#3B82F6',
      enableUserScripts: true,
      searchEngine: 'google',
      fontSize: 'medium',
      showBottomNav: true,
      enableAdBlock: true,
      privacyMode: false,
      darkMode: false,
      compactMode: false,
      desktopMode: false,
      proxyBaseUrl: window.location.origin
    };
    try {
      return saved ? { ...defaults, ...JSON.parse(saved) } : defaults;
    } catch (e) {
      return defaults;
    }
  });
  const [isSettingsOpen, setIsSettingsOpen] = useState(false);
  const [hubUrl, setHubUrl] = useState(() => localStorage.getItem('omni_hub_url') || '');
  const [hubLinks, setHubLinks] = useState<{title: string, url?: string, urls: (string | {label: string, url: string})[], category?: string}[]>(() => {
    const saved = localStorage.getItem('omni_hub_links');
    return saved ? JSON.parse(saved) : [];
  });
  const [hubSearch, setHubSearch] = useState('');
  const [hubCategory, setHubCategory] = useState('All');
  const [longPressedLink, setLongPressedLink] = useState<{title: string, url?: string, urls: (string | {label: string, url: string})[]} | null>(null);
  const longPressTimer = useRef<NodeJS.Timeout | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [userScripts, setUserScripts] = useState<UserScript[]>(() => {
    const saved = localStorage.getItem('browser_scripts');
    return saved ? JSON.parse(saved) : [];
  });

  const [cookies, setCookies] = useState<{name: string, value: string}[]>([]);
  const [sessionId] = useState(() => {
    const saved = localStorage.getItem('browser_session_id');
    if (saved) return saved;
    const newId = Math.random().toString(36).substring(2, 15);
    localStorage.setItem('browser_session_id', newId);
    return newId;
  });
  const [networkLogs, setNetworkLogs] = useState<any[]>([]);
  const [securityData, setSecurityData] = useState<any>(null);
  const [isSecurityLoading, setIsSecurityLoading] = useState(false);
  const [newCookie, setNewCookie] = useState({ name: '', value: '' });
  const [bookmarklets, setBookmarklets] = useState<Bookmarklet[]>(() => {
    const saved = localStorage.getItem('browser_bookmarklets');
    return saved ? JSON.parse(saved) : [];
  });

  const [pageToolView, setPageToolView] = useState<'normal' | 'reader' | 'source'>('normal');

  useEffect(() => {
    const checkPlatform = async () => {
      const info = await Device.getInfo();
      setIsAndroid(info.platform === 'android');
    };
    checkPlatform();

    CapApp.addListener('backButton', ({ canGoBack }) => {
      if (isTabsOpen) setIsTabsOpen(false);
      else if (isDownloadsOpen) setIsDownloadsOpen(false);
      else if (isMenuOpen) setIsMenuOpen(false);
      else if (isToolboxOpen) setIsToolboxOpen(false);
      else if (isMediaSnifferOpen) setIsMediaSnifferOpen(false);
      else if (isHistoryOpen) setIsHistoryOpen(false);
      else if (isBookmarksOpen) setIsBookmarksOpen(false);
      else if (isSettingsOpen) setIsSettingsOpen(false);
      else if (activeTab.url !== 'about:home') {
        window.history.back();
      } else {
        CapApp.exitApp();
      }
    });

    document.documentElement.setAttribute('data-font-size', settings.fontSize || 'medium');
    document.documentElement.setAttribute('data-compact', (settings.compactMode ?? false).toString());
    document.documentElement.style.setProperty('--color-primary', settings.themeColor || '#3B82F6');
    if (settings.darkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [settings.fontSize, settings.compactMode, settings.themeColor, settings.darkMode]);

  useEffect(() => {
    let interval: any;
    if (isStopwatchRunning) {
      interval = setInterval(() => {
        setStopwatchTime(prev => prev + 10);
      }, 10);
    }
    return () => clearInterval(interval);
  }, [isStopwatchRunning]);

  useEffect(() => {
    let interval: any;
    if (isTimerRunning && timerTime > 0) {
      interval = setInterval(() => {
        setTimerTime(prev => {
          if (prev <= 10) {
            setIsTimerRunning(false);
            return 0;
          }
          return prev - 10;
        });
      }, 10);
    } else if (timerTime === 0) {
      setIsTimerRunning(false);
    }
    return () => clearInterval(interval);
  }, [isTimerRunning, timerTime]);

  const formatTime = (ms: number) => {
    const minutes = Math.floor(ms / 60000);
    const seconds = Math.floor((ms % 60000) / 1000);
    const centiseconds = Math.floor((ms % 1000) / 10);
    return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}.${centiseconds.toString().padStart(2, '0')}`;
  };

  const convertUnits = (val: string, from: string, to: string) => {
    const num = parseFloat(val);
    if (isNaN(num)) return '0';
    
    // Simple conversion map
    const rates: any = {
      'cm_inch': 0.393701,
      'inch_cm': 2.54,
      'kg_lb': 2.20462,
      'lb_kg': 0.453592,
      'm_ft': 3.28084,
      'ft_m': 0.3048,
      'c_f': (n: number) => (n * 9/5) + 32,
      'f_c': (n: number) => (n - 32) * 5/9,
      'kmh_mph': 0.621371,
      'mph_kmh': 1.60934,
      'sqm_sqft': 10.7639,
      'sqft_sqm': 0.092903
    };
    
    if (from === to) return val;
    const key = `${from}_${to}`;
    const rate = rates[key];
    if (typeof rate === 'function') return rate(num).toFixed(2);
    if (rate) return (num * rate).toFixed(2);
    return val;
  };

  const toggleCategory = (cat: string) => {
    setCollapsedCategories(prev => 
      prev.includes(cat) ? prev.filter(c => c !== cat) : [...prev, cat]
    );
  };

  const ToolButton = ({ id, label, icon: Icon, color, bg, onClick }: any) => (
    <button 
      onClick={onClick || (() => setActiveTool(id))}
      className="flex flex-col items-center gap-2 p-4 md-card hover:bg-primary/5 transition-all active:scale-95"
    >
      <div className={cn("w-12 h-12 rounded-2xl flex items-center justify-center shadow-sm", bg, color)}>
        <Icon size={24} />
      </div>
      <span className="text-[10px] font-bold text-on-surface text-center">{label}</span>
    </button>
  );

  const CategorySection = ({ title, id, children }: any) => {
    const isCollapsed = collapsedCategories.includes(id);
    return (
      <section className="space-y-3">
        <button 
          onClick={() => toggleCategory(id)}
          className="w-full flex items-center justify-between px-2 py-1 group"
        >
          <h3 className="text-[10px] font-bold text-primary uppercase tracking-widest">{title}</h3>
          <div className="text-outline group-hover:text-primary transition-colors">
            {isCollapsed ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
          </div>
        </button>
        {!isCollapsed && (
          <div className="grid grid-cols-3 gap-4 animate-in fade-in slide-in-from-top-2 duration-200">
            {children}
          </div>
        )}
      </section>
    );
  };

  const activeTab = tabs.find(t => t.id === activeTabId) || tabs[0];

  const translatePage = (targetLang: string = 'en') => {
    const currentUrl = activeTab.url;
    const translateUrl = `https://translate.google.com/translate?sl=auto&tl=${targetLang}&u=${encodeURIComponent(currentUrl)}`;
    navigate(translateUrl);
    setIsMenuOpen(false);
  };

  const [translateText, setTranslateText] = useState('');
  const [translateResult, setTranslateResult] = useState('');
  const [targetLanguage, setTargetLanguage] = useState('en');

  const handleTranslateText = async () => {
    if (!translateText.trim()) return;
    setTranslateResult('Translating...');
    try {
      // Using a public translation API or simulated for now
      const res = await fetch(`https://api.mymemory.translated.net/get?q=${encodeURIComponent(translateText)}&langpair=auto|${targetLanguage}`);
      const data = await res.json();
      setTranslateResult(data.responseData.translatedText);
    } catch (e) {
      setTranslateResult('Translation failed. Please try again.');
    }
  };

  const handleAiAnalyze = async () => {
    setIsAiLoading(true);
    setAiSummary('');
    try {
      const res = await fetch(`/api/analyze?u=${encodeURIComponent(activeTab.url)}`);
      if (!res.ok) throw new Error(await res.text());
      const data = await res.json();
      setAiSummary(data.summary);
    } catch (e: any) {
      setAiSummary(`AI Analysis failed: ${e.message}`);
    } finally {
      setIsAiLoading(false);
    }
  };

  const iframeRef = useRef<HTMLIFrameElement>(null);

  const getIframeSrc = () => {
    if (!activeTab.url || activeTab.url === 'about:blank' || activeTab.url === 'about:home') return 'about:blank';
    
    const adBlock = settings.enableAdBlock ? '&adblock=true' : '';
    const url = activeTab.url;
    
    // If it's already a proxy URL, don't wrap it again
    const proxyRegex = new RegExp(`^(https?:)?//${window.location.host}/(api/v1/content|api/browse|browse)`);
    if (url.startsWith('/api/v1/content') || url.includes('/api/v1/content?id=') || url.startsWith('/api/browse') || url.includes('/api/browse?u=') || proxyRegex.test(url)) {
      return url;
    }
    
    const baseUrl = settings.proxyBaseUrl || window.location.origin;
    const sid = `&sid=${sessionId}`;
    if (pageToolView === 'reader') return `${baseUrl}/api/reader?u=${encodeURIComponent(url)}${adBlock}${sid}`;
    if (pageToolView === 'source') return `${baseUrl}/api/source?u=${encodeURIComponent(url)}${sid}`;
    return `${baseUrl}/api/v1/content?id=${toBase64Url(url)}${adBlock}${sid}`;
  };

  // Handle messages from the proxied page (Media Sniffing & Navigation)
  useEffect(() => {
    const handleMessage = (event: MessageEvent) => {
      if (event.data?.type === 'MEDIA_DETECTED') {
        const newMedia = event.data.media as MediaItem[];
        // Filter unique media
        setDetectedMedia(prev => {
          const combined = [...prev, ...newMedia];
          const unique = Array.from(new Set(combined.map(m => m.src))).map(src => combined.find(m => m.src === src)!);
          return unique;
        });
        if (newMedia.length > 0) setShowMediaGrabber(true);
      } else if (event.data?.type === 'NAVIGATE_TO') {
        navigate(event.data.url);
      } else if (event.data?.type === 'SCROLL') {
        if (event.data.direction === 'down' && event.data.y > 50) {
          setShowNav(false);
        } else {
          setShowNav(true);
        }
      }
    };

    window.addEventListener('message', handleMessage);
    return () => window.removeEventListener('message', handleMessage);
  }, []);

  useEffect(() => {
    localStorage.setItem('browser_history', JSON.stringify(history));
  }, [history]);

  useEffect(() => {
    localStorage.setItem('browser_bookmarks', JSON.stringify(bookmarks));
  }, [bookmarks]);

  useEffect(() => {
    localStorage.setItem('browser_settings', JSON.stringify(settings));
  }, [settings]);

  useEffect(() => {
    localStorage.setItem('browser_scripts', JSON.stringify(userScripts));
  }, [userScripts]);

  useEffect(() => {
    localStorage.setItem('browser_bookmarklets', JSON.stringify(bookmarklets));
  }, [bookmarklets]);

  useEffect(() => {
    if (activeTool === 'Cookie Manager') {
      refreshCookies();
    }
    if (activeTool === 'Network Monitor') {
      const fetchLogs = async () => {
        try {
          const res = await fetch(`/api/v1/network-logs?sid=${sessionId}`);
          if (res.ok) {
            const data = await res.json();
            setNetworkLogs(data);
          }
        } catch (e) {
          console.error("Failed to fetch network logs:", e);
        }
      };
      fetchLogs();
      const interval = setInterval(fetchLogs, 3000);
      return () => clearInterval(interval);
    }
    if (activeTool === 'Security Scan') {
      const runSecurityScan = async () => {
        setIsSecurityLoading(true);
        try {
          const res = await fetch(`/api/v1/security-check?u=${encodeURIComponent(activeTab.url)}`);
          if (res.ok) {
            const data = await res.json();
            setSecurityData(data);
          }
        } catch (e) {
          console.error("Security scan failed:", e);
        } finally {
          setIsSecurityLoading(false);
        }
      };
      runSecurityScan();
    }
  }, [activeTool, activeTab.url]);

  useEffect(() => {
    const handleScroll = () => {
      const currentScrollY = window.scrollY;
      if (currentScrollY > lastScrollY.current && currentScrollY > 50) {
        setShowNav(false);
      } else {
        setShowNav(true);
      }
      lastScrollY.current = currentScrollY;
    };

    window.addEventListener('scroll', handleScroll, { passive: true });
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  // Execute User Scripts on page load
  const handleIframeLoad = () => {
    setTabs(prev => prev.map(t => t.id === activeTabId ? { ...t, loading: false } : t));
    
    if (settings.enableUserScripts) {
      userScripts.filter(s => s.enabled).forEach(script => {
        iframeRef.current?.contentWindow?.postMessage({ type: 'EXECUTE_SCRIPT', code: script.code }, '*');
      });
    }
  };

  const isSocialUrl = (url: string) => {
    return url.includes('youtube.com') || 
           url.includes('youtu.be') || 
           url.includes('instagram.com') || 
           url.includes('twitter.com') || 
           url.includes('x.com') || 
           url.includes('pinterest.com') ||
           url.includes('threads.net');
  };

  const navigate = (url: string) => {
    if (!url) return;
    const trimmedUrl = url.trim();
    if (!trimmedUrl || trimmedUrl === 'undefined' || trimmedUrl === 'null' || trimmedUrl === 'about:blank') return;
    
    let finalUrl = trimmedUrl;
    
    // Recursive unwrapping of proxied URLs
    const unwrap = (u: string): string => {
      if (u.includes('/api/v1/content') || u.includes('/api/browse')) {
        try {
          const urlObj = new URL(u, window.location.origin);
          const extracted = urlObj.searchParams.get('id') || urlObj.searchParams.get('u');
          if (extracted) {
            let realUrl = fromBase64Url(extracted);
            try {
              const targetUrlObj = new URL(realUrl);
              urlObj.searchParams.forEach((value, key) => {
                if (key !== 'id' && key !== 'u' && key !== 'adblock') {
                  targetUrlObj.searchParams.set(key, value);
                }
              });
              return unwrap(targetUrlObj.toString());
            } catch (e) {
              return unwrap(realUrl);
            }
          }
        } catch (e) {
          const match = u.match(/[?&](id|u)=([^&]+)/);
          if (match) {
            try {
              return unwrap(fromBase64Url(decodeURIComponent(match[2])));
            } catch (de) {
              return unwrap(fromBase64Url(match[2]));
            }
          }
        }
      }
      return u;
    };

    finalUrl = unwrap(finalUrl);

    if (!finalUrl || finalUrl === 'undefined' || finalUrl === 'null' || finalUrl === '') return;

    if (!finalUrl.startsWith('http') && !finalUrl.startsWith('data:')) {
      if (finalUrl.includes('.') && !finalUrl.includes(' ')) {
        finalUrl = `https://${finalUrl}`;
      } else {
        if (settings.searchEngine === 'custom' && settings.customSearchUrl) {
          finalUrl = settings.customSearchUrl.replace('%s', encodeURIComponent(finalUrl));
        } else {
          const searchUrls = {
            google: 'https://www.google.com/search?q=',
            bing: 'https://www.bing.com/search?q=',
            duckduckgo: 'https://duckduckgo.com/?q='
          };
          const baseUrl = (searchUrls as any)[settings.searchEngine] || searchUrls.google;
          finalUrl = `${baseUrl}${encodeURIComponent(finalUrl)}`;
        }
      }
    }
    
    if (settings.privacyMode) {
      setTabs(prev => prev.map(t => t.id === activeTabId ? { ...t, url: finalUrl, loading: true } : t));
      setUrlInput(finalUrl);
      setDetectedMedia([]);
      setShowMediaGrabber(false);
      setPageToolView('normal');
      return;
    }

    setTabs(prev => prev.map(t => t.id === activeTabId ? { ...t, url: finalUrl, loading: true } : t));
    setUrlInput(finalUrl);
    setDetectedMedia([]);
    setShowMediaGrabber(false);
    setPageToolView('normal');

    // Add to history
    const newHistoryItem: HistoryItem = {
      id: Math.random().toString(36).substr(2, 9),
      url: finalUrl,
      title: finalUrl.replace(/^https?:\/\/(www\.)?/, '').split('/')[0],
      timestamp: Date.now()
    };
    setHistory(prev => [newHistoryItem, ...prev.filter(h => h.url !== finalUrl)].slice(0, 100));
  };

  const addTab = () => {
    const newId = Math.random().toString(36).substr(2, 9);
    const newTab = { id: newId, url: settings.homepageUrl, title: 'New Tab', loading: false };
    setTabs([...tabs, newTab]);
    setActiveTabId(newId);
    setUrlInput(settings.homepageUrl);
    setIsTabsOpen(false);
  };

  const closeTab = (id: string) => {
    if (tabs.length === 1) return;
    const newTabs = tabs.filter(t => t.id !== id);
    setTabs(newTabs);
    if (activeTabId === id) {
      setActiveTabId(newTabs[newTabs.length - 1].id);
      setUrlInput(newTabs[newTabs.length - 1].url);
    }
  };

  const handleContextMenu = (e: React.MouseEvent) => {
    e.preventDefault();
    // Ensure menu stays within viewport
    const x = Math.min(e.clientX, window.innerWidth - 230);
    const y = Math.min(e.clientY, window.innerHeight - 350);
    setContextMenuPos({ x, y });
    setIsContextMenuOpen(true);
  };

  const closeContextMenu = () => setIsContextMenuOpen(false);

  useEffect(() => {
    window.addEventListener('click', closeContextMenu);
    window.addEventListener('scroll', closeContextMenu, true);

    const handleOnline = () => setIsOnline(true);
    const handleOffline = () => setIsOnline(false);
    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    return () => {
      window.removeEventListener('click', closeContextMenu);
      window.removeEventListener('scroll', closeContextMenu, true);
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, []);

  const toggleBookmark = () => {
    const isBookmarked = bookmarks.find(b => b.url === activeTab.url);
    if (isBookmarked) {
      setBookmarks(prev => prev.filter(b => b.url !== activeTab.url));
    } else {
      const newBookmark: BookmarkItem = {
        id: Math.random().toString(36).substr(2, 9),
        url: activeTab.url,
        title: activeTab.title,
        timestamp: Date.now()
      };
      setBookmarks(prev => [...prev, newBookmark]);
    }
  };

  const saveAsPdf = async () => {
    const iframe = iframeRef.current;
    if (!iframe || !iframe.contentDocument) return;
    try {
      const canvas = await html2canvas(iframe.contentDocument.body);
      const imgData = canvas.toDataURL('image/png');
      const pdf = new jsPDF();
      const imgProps = pdf.getImageProperties(imgData);
      const pdfWidth = pdf.internal.pageSize.getWidth();
      const pdfHeight = (imgProps.height * pdfWidth) / imgProps.width;
      pdf.addImage(imgData, 'PNG', 0, 0, pdfWidth, pdfHeight);
      pdf.save(`${activeTab.title || 'page'}.pdf`);
    } catch (e) {
      console.error('PDF generation failed:', e);
    }
  };

  const saveAsHtml = () => {
    const iframe = iframeRef.current;
    if (!iframe || !iframe.contentDocument) return;
    const html = iframe.contentDocument.documentElement.outerHTML;
    const blob = new Blob([html], { type: 'text/html' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${activeTab.title || 'page'}.html`;
    a.click();
  };

  const toDataURL = async (url: string): Promise<string> => {
    try {
      const response = await fetch(`/api/v1/content?id=${toBase64Url(url)}`);
      const blob = await response.blob();
      return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onloadend = () => resolve(reader.result as string);
        reader.onerror = reject;
        reader.readAsDataURL(blob);
      });
    } catch (e) {
      return url;
    }
  };

  const saveAsMhtml = async () => {
    const iframe = iframeRef.current;
    if (!iframe || !iframe.contentDocument) return;
    
    let html = iframe.contentDocument.documentElement.outerHTML;
    const images = Array.from(iframe.contentDocument.querySelectorAll('img')) as HTMLImageElement[];
    
    // Attempt to embed images as data URLs for MHTML
    for (const img of images) {
      if (img.src && img.src.startsWith('http')) {
        const dataUrl = await toDataURL(img.src);
        html = html.replace(img.src, dataUrl);
      }
    }

    const mhtml = `From: <Saved by Omni Web>\nSubject: ${activeTab.title}\nDate: ${new Date().toUTCString()}\nMIME-Version: 1.0\nContent-Type: text/html; charset="utf-8"\n\n${html}`;
    const blob = new Blob([mhtml], { type: 'message/rfc822' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${activeTab.title || 'page'}.mhtml`;
    a.click();
  };

  const saveAsMarkdown = async () => {
    try {
      const response = await fetch(`/api/markdown?u=${encodeURIComponent(activeTab.url)}`);
      let html = await response.text();
      
      const turndownService = new TurndownService();
      
      // Custom rule to embed images as data URLs in Markdown
      turndownService.addRule('embedImages', {
        filter: 'img',
        replacement: function (content, node: any) {
          const alt = node.alt || '';
          const src = node.getAttribute('src');
          return `![${alt}](${src})`;
        }
      });

      const markdown = turndownService.turndown(html);
      const blob = new Blob([markdown], { type: 'text/markdown' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${activeTab.title || 'page'}.md`;
      a.click();
    } catch (e) {
      console.error('Markdown conversion failed:', e);
    }
  };

  const toggleMediaSelection = (id: string) => {
    setDetectedMedia(prev => prev.map(m => m.id === id ? { ...m, selected: !m.selected } : m));
  };

  const selectAllMedia = () => {
    const allSelected = detectedMedia.every(m => m.selected);
    setDetectedMedia(prev => prev.map(m => ({ ...m, selected: !allSelected })));
  };

  const downloadSelectedMedia = () => {
    detectedMedia.filter(m => m.selected).forEach(m => startDownload(m));
    setDetectedMedia(prev => prev.map(m => ({ ...m, selected: false })));
  };

  const executeBookmarklet = (code: string) => {
    iframeRef.current?.contentWindow?.postMessage({ type: 'EXECUTE_SCRIPT', code }, '*');
    setIsMenuOpen(false);
  };

  const exportData = () => {
    const data = {
      settings,
      bookmarks,
      history,
      userScripts,
      bookmarklets
    };
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `droidsurf_backup_${new Date().toISOString().split('T')[0]}.json`;
    a.click();
  };

  const importData = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (event) => {
      try {
        const data = JSON.parse(event.target?.result as string);
        if (data.settings) setSettings(prev => ({ ...prev, ...data.settings }));
        if (data.bookmarks) setBookmarks(data.bookmarks);
        if (data.history) setHistory(data.history);
        if (data.userScripts) setUserScripts(data.userScripts);
        if (data.bookmarklets) setBookmarklets(data.bookmarklets);
        alert('Data imported successfully!');
      } catch (err) {
        alert('Failed to import data. Invalid file format.');
      }
    };
    reader.readAsText(file);
  };

  const fetchHubLinks = async () => {
    if (!hubUrl) return;
    try {
      const cleanUrl = hubUrl.endsWith('/') ? hubUrl : hubUrl + '/';
      const targetUrl = `${cleanUrl}links.json`;
      const response = await fetch(`/api/v1/content?id=${toBase64Url(targetUrl)}`);
      if (!response.ok) throw new Error('Failed to fetch links.json');
      const data = await response.json();
      if (Array.isArray(data)) {
        setHubLinks(data);
        localStorage.setItem('omni_hub_links', JSON.stringify(data));
        localStorage.setItem('omni_hub_url', hubUrl);
        alert(`Successfully synced ${data.length} links from Omni Hub!`);
      }
    } catch (err) {
      console.error('Hub sync failed:', err);
      alert('Failed to sync with Omni Hub. Make sure the URL is correct and links.json exists.');
    }
  };

  const handleLinkPressStart = (link: {title: string, url?: string, urls: (string | {label: string, url: string})[]}) => {
    longPressTimer.current = setTimeout(() => {
      setLongPressedLink(link);
    }, 600);
  };

  const handleLinkPressEnd = () => {
    if (longPressTimer.current) {
      clearTimeout(longPressTimer.current);
      longPressTimer.current = null;
    }
  };

  const refreshCookies = () => {
    const cookieStr = document.cookie;
    if (!cookieStr) {
      setCookies([]);
      return;
    }
    const parsed = cookieStr.split(';').map(c => {
      const parts = c.trim().split('=');
      const name = parts[0];
      const value = parts.slice(1).join('=');
      return { name, value };
    }).filter(c => c.name);
    setCookies(parsed);
  };

  const deleteCookie = (name: string) => {
    document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
    refreshCookies();
  };

  const addCookie = (name: string, value: string) => {
    if (!name) return;
    document.cookie = `${name}=${value}; path=/; max-age=31536000`;
    refreshCookies();
    setNewCookie({ name: '', value: '' });
  };

  const findOnPage = (text: string) => {
    setFindText(text);
    if (!text) return;
    try {
      const iframe = iframeRef.current;
      if (iframe && iframe.contentWindow) {
        // @ts-ignore
        iframe.contentWindow.find(text, false, false, true, false, true, false);
      }
    } catch (e) {
      console.error("Find on page failed:", e);
    }
  };

  const startDownload = async (media: MediaItem) => {
    const id = Math.random().toString(36).substr(2, 9);
    
    // Smart Detection for YouTube/Social Media
    const isSocial = media.src.includes('youtube.com') || 
                     media.src.includes('youtu.be') || 
                     media.src.includes('instagram.com') || 
                     media.src.includes('twitter.com') || 
                     media.src.includes('x.com') || 
                     media.src.includes('pinterest.com') ||
                     media.src.includes('threads.net');

    if (isSocial) {
      // Open a smart downloader service in a new tab
      window.open(`https://cobalt.tools/?url=${encodeURIComponent(media.src)}`, '_blank');
      return;
    }

    const filename = media.src.split('/').pop()?.split('?')[0] || `media_${id}.${media.type === 'video' ? 'mp4' : media.type === 'audio' ? 'mp3' : 'jpg'}`;
    
    const newDownload: DownloadItem = {
      id,
      url: media.src,
      filename,
      fileType: media.type === 'video' ? 'video' : media.type === 'audio' ? 'audio' : media.type === 'image' ? 'image' : 'other',
      progress: 0,
      status: 'downloading',
      timestamp: Date.now(),
      size: 'Starting...',
      downloadedSize: 0
    };

    setDownloads(prev => [newDownload, ...prev]);
    setIsDownloadsOpen(true);

    try {
      const controller = new AbortController();
      downloadControllers.current[id] = controller;

      const response = await fetch(media.src, { signal: controller.signal });
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

      const contentLength = response.headers.get('content-length');
      const total = contentLength ? parseInt(contentLength, 10) : 0;

      const reader = response.body?.getReader();
      if (!reader) throw new Error('ReadableStream not supported');

      let receivedLength = 0;
      const chunks = [];
      const startTime = Date.now();

      while(true) {
        const {done, value} = await reader.read();
        if (done) break;
        chunks.push(value);
        receivedLength += value.length;

        const now = Date.now();
        const duration = (now - startTime) / 1000;
        const speed = duration > 0 ? receivedLength / duration : 0;
        const progress = total ? (receivedLength / total) * 100 : 0;
        const eta = (total && speed > 0) ? (total - receivedLength) / speed : 0;

        const sizeStr = total
          ? `${(receivedLength / (1024 * 1024)).toFixed(1)} / ${(total / (1024 * 1024)).toFixed(1)} MB`
          : `${(receivedLength / (1024 * 1024)).toFixed(1)} MB`;

        setDownloads(prev => prev.map(d => d.id === id ? {
          ...d,
          progress,
          downloadedSize: receivedLength,
          totalSize: total,
          size: sizeStr,
          speed,
          eta
        } : d));
      }

      const blob = new Blob(chunks);
      downloadBlobs.current[id] = blob;

      setDownloads(prev => prev.map(d => d.id === id ? {
        ...d,
        status: 'completed',
        progress: 100,
        size: `${(blob.size / (1024 * 1024)).toFixed(1)} MB`
      } : d));

    } catch (error: any) {
      if (error.name === 'AbortError') return;
      setDownloads(prev => prev.map(d => d.id === id ? {
        ...d,
        status: 'failed',
        error: error.message,
        size: 'Failed'
      } : d));
    } finally {
      delete downloadControllers.current[id];
    }
  };

  const cancelDownload = (id: string) => {
    if (downloadControllers.current[id]) {
      downloadControllers.current[id].abort();
      delete downloadControllers.current[id];
    }
    setDownloads(prev => prev.filter(d => d.id !== id));
  };

  const removeDownload = (id: string) => {
    delete downloadBlobs.current[id];
    setDownloads(prev => prev.filter(d => d.id !== id));
  };

  const openDownload = (id: string) => {
    const blob = downloadBlobs.current[id];
    if (blob) {
      const url = URL.createObjectURL(blob);
      window.open(url, '_blank');
    } else {
      const download = downloads.find(d => d.id === id);
      if (download) window.open(download.url, '_blank');
    }
  };

  const toggleDownload = (id: string) => {
    const download = downloads.find(d => d.id === id);
    if (download?.status === 'downloading') {
      if (downloadControllers.current[id]) {
        downloadControllers.current[id].abort();
        delete downloadControllers.current[id];
      }
      setDownloads(prev => prev.map(d => d.id === id ? { ...d, status: 'paused' } : d));
    } else if (download?.status === 'paused') {
      const media: MediaItem = {
        id: download.id,
        src: download.url,
        type: download.fileType === 'other' ? 'image' : download.fileType as any,
        title: download.filename
      };
      setDownloads(prev => prev.filter(d => d.id !== id));
      startDownload(media);
    }
  };

  const formatSpeed = (bytesPerSec?: number) => {
    if (!bytesPerSec) return '0 B/s';
    if (bytesPerSec > 1024 * 1024) return `${(bytesPerSec / (1024 * 1024)).toFixed(1)} MB/s`;
    if (bytesPerSec > 1024) return `${(bytesPerSec / 1024).toFixed(1)} KB/s`;
    return `${Math.round(bytesPerSec)} B/s`;
  };

  const formatETA = (seconds?: number) => {
    if (!seconds || seconds <= 0) return '0s';
    if (seconds > 3600) return `${Math.floor(seconds / 3600)}h ${Math.floor((seconds % 3600) / 60)}m`;
    if (seconds > 60) return `${Math.floor(seconds / 60)}m ${Math.floor(seconds % 60)}s`;
    return `${Math.round(seconds)}s`;
  };

  const filteredMedia = detectedMedia.filter(m => {
    const matchesFilter = mediaFilter === 'all' || m.type === mediaFilter;
    const matchesSearch = m.title.toLowerCase().includes(mediaSearch.toLowerCase()) || m.src.toLowerCase().includes(mediaSearch.toLowerCase());
    return matchesFilter && matchesSearch;
  });

  if (isFramed) {
    return (
      <div className="w-full h-screen bg-white overflow-hidden relative">
        <iframe
          ref={iframeRef}
          src={getIframeSrc()}
          className="w-full h-full border-none"
          onLoad={handleIframeLoad}
          referrerPolicy="no-referrer"
          allow="autoplay; camera; clipboard-read; clipboard-write; encrypted-media; fullscreen; geolocation; microphone; midi; screen-wake-lock; web-share"
        />
        {activeTab.loading && (
          <div className="absolute inset-0 bg-white/80 flex items-center justify-center z-50">
            <div className="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
          </div>
        )}
      </div>
    );
  }

  return (
    <div 
      className="flex flex-col h-screen bg-[#F5F5F5] dark:bg-zinc-950 font-sans text-[#1A1A1A] dark:text-zinc-100 overflow-hidden select-none"
      onContextMenu={handleContextMenu}
    >
      {/* Address Bar */}
      <motion.div
        animate={{ y: showNav ? 0 : -100 }}
        transition={{ duration: 0.2 }}
        className="bg-surface/95 dark:bg-zinc-900/95 backdrop-blur-lg px-4 py-3 flex flex-col gap-2 z-30 border-b border-outline-variant/20 dark:border-zinc-800 shadow-sm sticky top-0"
      >
        <div className="flex items-center gap-3">
          <button onClick={() => { if (activeTab.url === 'about:home') window.history.back(); else navigate('about:home'); }} className="p-2 hover:bg-primary/10 rounded-full transition-colors text-primary">
            {activeTab.url === 'about:home' ? <ChevronLeft size={24} /> : <Globe size={24} />}
          </button>
          {!isOnline && (
            <div className="bg-red-500 text-white text-[8px] font-bold px-1.5 py-0.5 rounded-full animate-pulse">
              OFFLINE
            </div>
          )}
          <div className="flex-1 relative group">
            <input 
              type="text"
              value={urlInput === 'about:home' ? '' : urlInput}
              onChange={(e) => setUrlInput(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && navigate(urlInput)}
              className="w-full pl-5 pr-12 py-3 bg-surface-container-highest dark:bg-zinc-800 border-none rounded-2xl text-on-surface dark:text-zinc-100 placeholder:text-on-surface/40 focus:bg-white dark:focus:bg-zinc-700 focus:ring-2 focus:ring-primary/20 text-sm transition-all shadow-inner"
              placeholder="Search or type URL"
            />
            <div className="absolute right-4 top-1/2 -translate-y-1/2 flex items-center gap-2">
              {isSocialUrl(activeTab.url) && (
                <button 
                  onClick={() => window.open(`https://cobalt.tools/?url=${encodeURIComponent(activeTab.url)}`, '_blank')}
                  className="text-primary hover:scale-110 transition-all p-1 bg-primary/10 rounded-lg"
                  title="Smart Download"
                >
                  <ArrowDownToLine size={16} />
                </button>
              )}
              <button 
                onClick={toggleBookmark}
                className="text-primary/60 hover:text-primary transition-colors"
              >
                <Bookmark size={16} fill={bookmarks.find(b => b.url === activeTab.url) ? "currentColor" : "none"} className={bookmarks.find(b => b.url === activeTab.url) ? "text-primary" : ""} />
              </button>
            </div>
          </div>
          <button onClick={() => navigate(activeTab.url)} className="p-2 hover:bg-primary/10 rounded-full transition-colors text-primary">
            <RotateCcw size={22} />
          </button>
        </div>

        {/* Find on Page Bar */}
        <AnimatePresence>
          {isFindOnPageOpen && (
            <motion.div 
              initial={{ height: 0, opacity: 0 }}
              animate={{ height: 'auto', opacity: 1 }}
              exit={{ height: 0, opacity: 0 }}
              className="flex items-center gap-3 overflow-hidden mt-3"
            >
              <div className="flex-1 relative">
                <input 
                  autoFocus
                  type="text"
                  value={findText}
                  onChange={(e) => findOnPage(e.target.value)}
                  className="md-input w-full py-2"
                  placeholder="Find on page..."
                />
              </div>
              <button onClick={() => setIsFindOnPageOpen(false)} className="p-2 hover:bg-surface-variant rounded-full text-on-surface-variant transition-colors">
                <X size={20} />
              </button>
            </motion.div>
          )}
        </AnimatePresence>
      </motion.div>

      {/* Browser Viewport */}
      <div className="flex-1 relative bg-white overflow-hidden">
        {activeTab.url === 'about:home' ? (
          <HomeView onNavigate={navigate} />
        ) : (
          <div className={cn(
            "w-full h-full transition-all duration-500",
            settings.desktopMode ? "origin-top-left" : ""
          )}
          style={settings.desktopMode ? {
            width: '1280px',
            height: `${100 / (1280 / window.innerWidth)}%`,
            transform: `scale(${window.innerWidth / 1280})`,
          } : {}}
          >
            <iframe
              ref={iframeRef}
              src={getIframeSrc()}
              className="w-full h-full border-none"
              onLoad={handleIframeLoad}
              referrerPolicy="no-referrer"
              allow="autoplay; camera; clipboard-read; clipboard-write; encrypted-media; fullscreen; geolocation; microphone; midi; screen-wake-lock; web-share"
            />
          </div>
        )}
        
        {/* Loading Overlay */}
        {activeTab.loading && !isFramed && (
          <div className="absolute inset-0 bg-white/80 flex items-center justify-center z-10">
            <div className="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
          </div>
        )}
      </div>

      {/* Bottom Navigation Bar */}
      <motion.div
        animate={{ y: showNav ? 0 : 100 }}
        transition={{ duration: 0.2 }}
        className="md-bottom-nav sticky bottom-0 bg-surface/95 dark:bg-zinc-900/95 backdrop-blur-lg border-t border-outline-variant/20 dark:border-zinc-800 pb-safe shadow-2xl"
      >
        <button 
          onClick={() => setIsTabsOpen(true)} 
          className={cn(
            "flex flex-col items-center gap-1 transition-all active:scale-90",
            isTabsOpen ? "text-primary" : "text-on-surface/60"
          )}
        >
          <div className={cn(
            "relative w-16 h-8 flex items-center justify-center rounded-2xl transition-all",
            isTabsOpen ? "bg-primary text-white shadow-lg shadow-primary/20 scale-105" : "hover:bg-primary/5"
          )}>
            <Layers size={18} />
            <span className="absolute -top-1 -right-1 bg-primary text-on-primary text-[7px] font-bold w-3.5 h-3.5 rounded-full flex items-center justify-center border border-surface">
              {tabs.length}
            </span>
          </div>
          <span className="text-[10px] font-bold tracking-tight">Tabs</span>
        </button>
        <button 
          onClick={() => setIsDownloadsOpen(true)} 
          className={cn(
            "flex flex-col items-center gap-1 transition-all active:scale-90",
            isDownloadsOpen ? "text-primary" : "text-on-surface/60"
          )}
        >
          <div className={cn(
            "w-16 h-8 flex items-center justify-center rounded-2xl transition-all",
            isDownloadsOpen ? "bg-primary text-white shadow-lg shadow-primary/20 scale-105" : "hover:bg-primary/5"
          )}>
            <Download size={18} />
          </div>
          <span className="text-[10px] font-bold tracking-tight">Files</span>
        </button>
        <button 
          onClick={() => setIsMenuOpen(!isMenuOpen)} 
          className={cn(
            "flex flex-col items-center gap-1 transition-all active:scale-90",
            isMenuOpen ? "text-primary" : "text-on-surface/60"
          )}
        >
          <div className={cn(
            "w-16 h-8 flex items-center justify-center rounded-2xl transition-all",
            isMenuOpen ? "bg-primary text-white shadow-lg shadow-primary/20 scale-105" : "hover:bg-primary/5"
          )}>
            <MoreVertical size={24} />
          </div>
          <span className="text-[10px] font-bold tracking-tight">Menu</span>
        </button>
        <button 
          onClick={() => setIsMediaSnifferOpen(true)} 
          className={cn(
            "flex flex-col items-center gap-1 transition-all active:scale-90",
            isMediaSnifferOpen ? "text-primary" : "text-on-surface/60"
          )}
        >
          <div className={cn(
            "relative w-16 h-8 flex items-center justify-center rounded-2xl transition-all",
            isMediaSnifferOpen ? "bg-primary text-white shadow-lg shadow-primary/20 scale-105" : "hover:bg-primary/5"
          )}>
            <Video size={18} />
            {detectedMedia.length > 0 && (
              <span className="absolute -top-1 -right-1 bg-primary text-on-primary text-[7px] font-bold w-3.5 h-3.5 rounded-full flex items-center justify-center border border-surface">
                {detectedMedia.length}
              </span>
            )}
          </div>
          <span className="text-[10px] font-bold tracking-tight">Media</span>
        </button>
        <button 
          onClick={() => { setActiveTool('Omni Hub'); setIsToolboxOpen(true); }} 
          className={cn(
            "flex flex-col items-center gap-1 transition-all active:scale-90",
            activeTool === 'Omni Hub' && isToolboxOpen ? "text-primary" : "text-on-surface/60"
          )}
        >
          <div className={cn(
            "w-16 h-8 flex items-center justify-center rounded-2xl transition-all",
            activeTool === 'Omni Hub' && isToolboxOpen ? "bg-primary text-white shadow-lg shadow-primary/20 scale-105" : "hover:bg-primary/5"
          )}>
            <Globe size={18} />
          </div>
          <span className="text-[10px] font-bold tracking-tight">Hub</span>
        </button>
        <button 
          onClick={() => { setActiveTool(null); setIsToolboxOpen(true); }} 
          className={cn(
            "flex flex-col items-center gap-1 transition-all active:scale-90",
            isToolboxOpen && !activeTool ? "text-primary" : "text-on-surface/60"
          )}
        >
          <div className={cn(
            "w-16 h-8 flex items-center justify-center rounded-2xl transition-all",
            isToolboxOpen && !activeTool ? "bg-primary text-white shadow-lg shadow-primary/20 scale-105" : "hover:bg-primary/5"
          )}>
            <Wand2 size={18} />
          </div>
          <span className="text-[10px] font-bold tracking-tight">Tools</span>
        </button>
      </motion.div>

      {/* Tabs Overlay */}
      <AnimatePresence>
        {isTabsOpen && (
          <motion.div 
            initial={{ opacity: 0, y: '100%' }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: '100%' }}
            className="fixed inset-0 bg-gray-100 z-50 flex flex-col"
          >
            <div className="p-4 flex items-center justify-between bg-surface border-b border-outline-variant/30">
              <h2 className="font-bold text-xl text-on-surface">Tabs</h2>
              <div className="flex gap-3">
                <button 
                  onClick={() => {
                    if (confirm('Close all tabs?')) {
                      setTabs([{ id: '1', url: DEFAULT_URL, title: 'Home', loading: false }]);
                      setActiveTabId('1');
                      setUrlInput(DEFAULT_URL);
                      setIsTabsOpen(false);
                    }
                  }}
                  className="p-3 bg-red-100 text-red-600 rounded-2xl hover:opacity-80 transition-all active:scale-90"
                >
                  <Trash2 size={28} />
                </button>
                <button onClick={addTab} className="p-3 bg-primary-container text-on-primary-container rounded-2xl hover:opacity-80 transition-all active:scale-90">
                  <Plus size={28} />
                </button>
                <button onClick={() => setIsTabsOpen(false)} className="p-3 hover:bg-surface-variant rounded-full transition-colors text-on-surface-variant">
                  <X size={28} />
                </button>
              </div>
            </div>
            <div className="flex-1 overflow-y-auto p-6 grid grid-cols-2 gap-6">
              {tabs.map(tab => (
                <div 
                  key={tab.id}
                  onClick={() => {
                    setActiveTabId(tab.id);
                    setUrlInput(tab.url);
                    setIsTabsOpen(false);
                  }}
                  className={cn(
                    "relative aspect-[3/4] md-card overflow-hidden cursor-pointer group transition-all active:scale-95",
                    activeTabId === tab.id ? "ring-4 ring-primary border-transparent" : ""
                  )}
                >
                  <div className="p-3 bg-surface-container flex items-center justify-between">
                    <span className="text-xs font-bold truncate pr-4 text-on-surface">{tab.title}</span>
                    <div className="flex gap-1">
                      <button 
                        onClick={(e) => {
                          e.stopPropagation();
                          const newTab = { ...tab, id: Math.random().toString(36).substr(2, 9) };
                          setTabs(prev => [...prev, newTab]);
                          setActiveTabId(newTab.id);
                          setIsTabsOpen(false);
                        }}
                        className="p-1.5 hover:bg-on-surface/10 rounded-full text-on-surface-variant"
                      >
                        <Copy size={16} />
                      </button>
                      <button 
                        onClick={(e) => {
                          e.stopPropagation();
                          closeTab(tab.id);
                        }}
                        className="p-1.5 hover:bg-on-surface/10 rounded-full text-on-surface-variant"
                      >
                        <X size={16} />
                      </button>
                    </div>
                  </div>
                  <div className="flex-1 bg-surface-container-low flex flex-col items-center justify-center p-4 text-center">
                    <div className="w-12 h-12 bg-surface-container-high rounded-full flex items-center justify-center mb-3 shadow-inner">
                      <Globe size={24} className="text-primary" />
                    </div>
                    <p className="text-[10px] font-bold text-on-surface-variant truncate w-full px-2">{new URL(tab.url).hostname}</p>
                  </div>
                </div>
              ))}
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Downloads Overlay */}
      <AnimatePresence>
        {isDownloadsOpen && (
          <motion.div 
            initial={{ opacity: 0, x: '100%' }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: '100%' }}
            className="fixed inset-0 bg-surface z-50 flex flex-col"
          >
            <div className="p-4 flex items-center justify-between border-b border-outline-variant/30 bg-surface">
              <h2 className="font-bold text-xl text-on-surface">Downloads</h2>
              <button onClick={() => setIsDownloadsOpen(false)} className="p-3 hover:bg-surface-variant rounded-full transition-colors text-on-surface-variant">
                <X size={28} />
              </button>
            </div>

            <div className="flex-1 overflow-y-auto">
              {/* Downloads List */}
              <div className="p-6 space-y-6">
                <div className="flex items-center justify-between">
                  <h3 className="text-xs font-bold text-outline uppercase tracking-widest">Recent Downloads</h3>
                  <div className="flex flex-wrap gap-3">
                    <select 
                      value={downloadFilter}
                      onChange={(e) => setDownloadFilter(e.target.value as any)}
                      className="text-[11px] font-bold text-on-surface-variant bg-surface-container rounded-full px-4 py-2 outline-none border-none shadow-sm"
                    >
                      <option value="all">All Status</option>
                      <option value="downloading">Downloading</option>
                      <option value="completed">Completed</option>
                      <option value="paused">Paused</option>
                      <option value="queued">Queued</option>
                    </select>
                    <select 
                      value={downloadTypeFilter}
                      onChange={(e) => setDownloadTypeFilter(e.target.value as any)}
                      className="text-[11px] font-bold text-on-surface-variant bg-surface-container rounded-full px-4 py-2 outline-none border-none shadow-sm"
                    >
                      <option value="all">All Types</option>
                      <option value="video">Videos</option>
                      <option value="audio">Audio</option>
                      <option value="image">Images</option>
                      <option value="other">Other</option>
                    </select>
                    <select 
                      value={downloadSort}
                      onChange={(e) => setDownloadSort(e.target.value as any)}
                      className="text-[11px] font-bold text-on-surface-variant bg-surface-container rounded-full px-4 py-2 outline-none border-none shadow-sm"
                    >
                      <option value="date">Sort by Date</option>
                      <option value="name">Sort by Name</option>
                      <option value="type">Sort by Type</option>
                    </select>
                  </div>
                </div>

                {downloads.length === 0 ? (
                  <div className="flex flex-col items-center justify-center py-24 text-outline">
                    <div className="w-24 h-24 bg-surface-container rounded-full flex items-center justify-center mb-6">
                      <Download size={48} strokeWidth={1.5} />
                    </div>
                    <p className="font-bold text-lg">No downloads yet</p>
                    <p className="text-sm opacity-60 mt-1">Your files will appear here</p>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {downloads
                      .filter(d => (downloadFilter === 'all' || d.status === downloadFilter) && (downloadTypeFilter === 'all' || d.fileType === downloadTypeFilter))
                      .sort((a, b) => {
                        if (downloadSort === 'date') return b.timestamp - a.timestamp;
                        if (downloadSort === 'name') return a.filename.localeCompare(b.filename);
                        if (downloadSort === 'type') return a.fileType.localeCompare(b.fileType);
                        return 0;
                      })
                      .map(download => (
                      <div key={download.id} className="md-card p-5 flex items-start gap-5">
                        <div className={cn(
                          "w-14 h-14 rounded-[20px] flex items-center justify-center shrink-0 shadow-inner",
                          download.status === 'completed' ? "bg-green-100 text-green-700" : "bg-primary-container text-primary"
                        )}>
                          {download.status === 'completed' ? <CheckCircle2 size={28} /> : 
                           download.fileType === 'video' ? <Video size={28} /> :
                           download.fileType === 'audio' ? <Music size={28} /> :
                           download.fileType === 'image' ? <Globe size={28} /> : <FileText size={28} />}
                        </div>
                        <div className="flex-1 min-w-0">
                          <div className="flex items-center justify-between mb-2">
                            <p className="text-base font-bold text-on-surface truncate pr-4">{download.filename}</p>
                            <span className="text-[11px] font-bold text-outline">
                              {new Date(download.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                            </span>
                          </div>
                          
                          {download.status === 'downloading' && (
                            <div className="space-y-3">
                              <div className="w-full bg-surface-container-high rounded-full h-2 overflow-hidden">
                                <motion.div 
                                  className="bg-primary h-full"
                                  initial={{ width: 0 }}
                                  animate={{ width: `${download.progress}%` }}
                                />
                              </div>
                              <div className="flex flex-col gap-1">
                                <div className="flex justify-between text-[11px] font-bold text-on-surface-variant">
                                  <span>{Math.round(download.progress)}% • {download.size}</span>
                                  <span className="text-primary">{formatSpeed(download.speed)}</span>
                                </div>
                                <div className="flex justify-between text-[10px] font-bold text-outline uppercase tracking-wider">
                                  <span>ETA: {formatETA(download.eta)}</span>
                                </div>
                              </div>
                              <div className="flex gap-3">
                                <button 
                                  onClick={() => toggleDownload(download.id)} 
                                  className="flex-1 py-2 bg-primary/10 text-primary rounded-xl text-[11px] font-bold flex items-center justify-center gap-2 active:scale-95 transition-all"
                                >
                                  <Pause size={14} /> Pause
                                </button>
                                <button
                                  onClick={() => cancelDownload(download.id)}
                                  className="flex-1 py-2 bg-red-50 text-red-600 rounded-xl text-[11px] font-bold flex items-center justify-center gap-2 active:scale-95 transition-all"
                                >
                                  <X size={14} /> Cancel
                                </button>
                              </div>
                            </div>
                          )}

                          {download.status === 'paused' && (
                            <div className="space-y-3">
                              <div className="w-full bg-surface-container-high rounded-full h-2 overflow-hidden opacity-50">
                                <div 
                                  className="bg-outline h-full"
                                  style={{ width: `${download.progress}%` }}
                                />
                              </div>
                              <div className="flex justify-between text-[11px] font-bold text-outline">
                                <span>Paused • {Math.round(download.progress)}%</span>
                                <span>{download.size}</span>
                              </div>
                              <div className="flex gap-3">
                                <button 
                                  onClick={() => toggleDownload(download.id)} 
                                  className="flex-1 py-2 bg-primary text-on-primary rounded-xl text-[11px] font-bold flex items-center justify-center gap-2 active:scale-95 transition-all"
                                >
                                  <Play size={14} /> Resume
                                </button>
                                <button
                                  onClick={() => removeDownload(download.id)}
                                  className="flex-1 py-2 bg-red-50 text-red-600 rounded-xl text-[11px] font-bold flex items-center justify-center gap-2 active:scale-95 transition-all"
                                >
                                  <Trash2 size={14} /> Remove
                                </button>
                              </div>
                            </div>
                          )}

                          {download.status === 'queued' && (
                            <div className="flex items-center justify-between mt-2">
                              <span className="text-[11px] font-bold text-outline italic">Waiting in queue...</span>
                              <button
                                onClick={() => cancelDownload(download.id)}
                                className="text-red-600 text-[11px] font-bold uppercase tracking-widest"
                              >
                                Cancel
                              </button>
                            </div>
                          )}

                          {download.status === 'completed' && (
                            <div className="flex items-center justify-between mt-2">
                              <span className="text-[11px] font-bold text-on-surface-variant opacity-60">{download.size} • Completed</span>
                              <div className="flex gap-4">
                                <button
                                  onClick={() => openDownload(download.id)}
                                  className="text-primary text-[11px] font-bold uppercase tracking-widest"
                                >
                                  Open
                                </button>
                                <button
                                  onClick={() => removeDownload(download.id)}
                                  className="text-outline hover:text-red-500 transition-colors"
                                >
                                  <Trash2 size={18} />
                                </button>
                              </div>
                            </div>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
      {/* Toolbox Overlay */}
      <AnimatePresence>
        {isToolboxOpen && (
          <motion.div 
            initial={{ opacity: 0, y: '100%' }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: '100%' }}
            className="fixed inset-0 bg-surface z-50 flex flex-col"
          >
            <div className="p-4 flex items-center justify-between border-b border-outline-variant/30 bg-surface">
              <div className="flex items-center gap-3">
                {activeTool && (
                  <button onClick={() => setActiveTool(null)} className="p-2 hover:bg-surface-variant rounded-full transition-colors">
                    <ChevronLeft size={24} />
                  </button>
                )}
                <h2 className="font-bold text-xl text-on-surface">{activeTool || 'Toolbox'}</h2>
              </div>
              <button onClick={() => setIsToolboxOpen(false)} className="p-3 hover:bg-surface-variant rounded-full transition-colors text-on-surface-variant">
                <X size={28} />
              </button>
            </div>

            <div className="flex-1 overflow-y-auto p-6">
              {!activeTool ? (
                <div className="space-y-10 pb-24">
                  <CategorySection title="Status" id="status">
                    <div className="col-span-3 grid grid-cols-2 gap-4">
                      <div className="md-card p-4 flex items-center gap-4 bg-primary/5 border-primary/20">
                        <div className="w-10 h-10 bg-primary text-on-primary rounded-xl flex items-center justify-center font-bold">
                          {tabs.length}
                        </div>
                        <div>
                          <p className="text-xs font-bold text-on-surface">Active Tabs</p>
                          <p className="text-[10px] text-outline font-bold">Open windows</p>
                        </div>
                      </div>
                      <div className="md-card p-4 flex items-center gap-4 bg-secondary/5 border-secondary/20">
                        <div className="w-10 h-10 bg-secondary text-on-secondary rounded-xl flex items-center justify-center font-bold">
                          {detectedMedia.length}
                        </div>
                        <div>
                          <p className="text-xs font-bold text-on-surface">Media Found</p>
                          <p className="text-[10px] text-outline font-bold">Sniffed assets</p>
                        </div>
                      </div>
                    </div>
                  </CategorySection>

                  <CategorySection title="Favorites" id="favs">
                    <ToolButton 
                      label="History" 
                      icon={History} 
                      color="text-blue-600" 
                      bg="bg-blue-50" 
                      onClick={() => { setIsHistoryOpen(true); setIsToolboxOpen(false); }}
                    />
                    <ToolButton 
                      label="Saved" 
                      icon={Bookmark} 
                      color="text-amber-600" 
                      bg="bg-amber-50" 
                      onClick={() => { setIsBookmarksOpen(true); setIsToolboxOpen(false); }}
                    />
                    <ToolButton 
                      label="Notes" 
                      icon={StickyNote} 
                      color="text-teal-600" 
                      bg="bg-teal-50" 
                      id="Notes"
                    />
                  </CategorySection>

                  <CategorySection title="Utilities" id="utils">
                    <ToolButton label="AI Summary" icon={Wand2} color="text-purple-600" bg="bg-purple-50" id="AI Summary" />
                    <ToolButton label="Calculator" icon={Calculator} color="text-green-600" bg="bg-green-50" id="Calculator" />
                    <ToolButton label="Translate" icon={Languages} color="text-blue-600" bg="bg-blue-50" id="Translate" />
                    <ToolButton label="QR Gen" icon={QrCode} color="text-rose-600" bg="bg-rose-50" id="QR Generator" />
                    <ToolButton label="Password" icon={Key} color="text-indigo-600" bg="bg-indigo-50" id="Password Gen" />
                    <ToolButton label="Converter" icon={Scale} color="text-orange-600" bg="bg-orange-50" id="Converter" />
                    <ToolButton label="Stopwatch" icon={Timer} color="text-cyan-600" bg="bg-cyan-50" id="Stopwatch" />
                    <ToolButton label="Timer" icon={Clock} color="text-indigo-600" bg="bg-indigo-50" id="Timer" />
                    <ToolButton label="Diff" icon={Braces} color="text-slate-600" bg="bg-slate-50" id="Diff Viewer" />
                    <ToolButton label="Speech" icon={Music} color="text-emerald-600" bg="bg-emerald-50" id="TTS" />
                    <ToolButton label="Lorem" icon={StickyNote} color="text-amber-600" bg="bg-amber-50" id="Lorem Ipsum" />
                    <ToolButton label="Morse" icon={Activity} color="text-amber-600" bg="bg-amber-50" id="Morse Code" />
                    <ToolButton label="BMI" icon={User} color="text-blue-600" bg="bg-blue-50" id="BMI Calc" />
                    <ToolButton label="Age" icon={Calendar} color="text-emerald-600" bg="bg-emerald-50" id="Age Calc" />
                    <ToolButton label="Color" icon={Palette} color="text-pink-600" bg="bg-pink-50" id="Color Picker" />
                  </CategorySection>

                  <CategorySection title="Advanced" id="advanced">
                    <ToolButton label="Omni Hub" icon={Globe} color="text-rose-600" bg="bg-rose-50" id="Omni Hub" />
                    <ToolButton label="Network" icon={Activity} color="text-purple-600" bg="bg-purple-50" id="Network Monitor" />
                    <ToolButton label="Cookies" icon={Cookie} color="text-amber-600" bg="bg-amber-50" id="Cookie Manager" />
                    <ToolButton label="Translator" icon={Globe} color="text-indigo-600" bg="bg-indigo-50" onClick={() => translatePage()} />
                  </CategorySection>

                  <CategorySection title="Page Tools" id="page">
                    <ToolButton 
                      label="Inspect" 
                      icon={SearchCode} 
                      color="text-orange-600" 
                      bg="bg-orange-50" 
                      onClick={() => { setPageToolView('source'); setIsToolboxOpen(false); }}
                    />
                    <ToolButton 
                      label="Reader" 
                      icon={FileText} 
                      color="text-gray-600" 
                      bg="bg-gray-50" 
                      onClick={() => { setPageToolView('reader'); setIsToolboxOpen(false); }}
                    />
                    <ToolButton label="Text Tools" icon={Type} color="text-purple-600" bg="bg-purple-50" id="Text Tools" />
                    <ToolButton label="JSON" icon={Braces} color="text-emerald-600" bg="bg-emerald-50" id="JSON Formatter" />
                  </CategorySection>

                  <CategorySection title="System" id="system">
                    <ToolButton 
                      label="Device" 
                      icon={Cpu} 
                      color="text-indigo-600" 
                      bg="bg-indigo-50" 
                      id="Device Info"
                    />
                    <ToolButton 
                      label="Security" 
                      icon={ShieldCheck} 
                      color="text-green-600" 
                      bg="bg-green-50" 
                      id="Security Scan"
                    />
                  </CategorySection>

                  <CategorySection title="Developer" id="dev">
                    <div className="col-span-3 space-y-4">
                      <div className="md-card p-5">
                        <div className="flex items-center justify-between mb-4">
                          <p className="text-sm font-bold text-on-surface">User Scripts</p>
                          <button 
                            onClick={() => {
                              const name = prompt('Script Name:');
                              const code = prompt('JS Code:');
                              if (name && code) setUserScripts([...userScripts, { id: Math.random().toString(36).substr(2, 9), name, code, enabled: true }]);
                            }}
                            className="p-2 bg-primary-container text-on-primary-container rounded-xl hover:opacity-80 transition-all active:scale-90"
                          >
                            <Plus size={20} />
                          </button>
                        </div>
                        <div className="space-y-3">
                          {userScripts.length === 0 && <p className="text-[10px] text-outline text-center py-4">No scripts added</p>}
                          {userScripts.map(script => (
                            <div key={script.id} className="flex items-center justify-between p-4 bg-surface-container rounded-2xl">
                              <span className="text-xs font-bold text-on-surface">{script.name}</span>
                              <div className="flex gap-3">
                                <button onClick={() => setUserScripts(userScripts.map(s => s.id === script.id ? { ...s, enabled: !s.enabled } : s))}>
                                  {script.enabled ? <CheckCircle2 size={24} className="text-primary" /> : <Circle size={24} className="text-outline-variant" />}
                                </button>
                                <button onClick={() => setUserScripts(userScripts.filter(s => s.id !== script.id))}>
                                  <Trash2 size={24} className="text-red-500 opacity-60 hover:opacity-100 transition-opacity" />
                                </button>
                              </div>
                            </div>
                          ))}
                        </div>
                      </div>

                      <div className="md-card p-5">
                        <div className="flex items-center justify-between mb-4">
                          <p className="text-sm font-bold text-on-surface">Bookmarklets</p>
                          <button 
                            onClick={() => {
                              const name = prompt('Bookmarklet Name:');
                              const code = prompt('JS Code:');
                              if (name && code) setBookmarklets([...bookmarklets, { id: Math.random().toString(36).substr(2, 9), name, code }]);
                            }}
                            className="p-2 bg-primary-container text-on-primary-container rounded-xl hover:opacity-80 transition-all active:scale-90"
                          >
                            <Plus size={20} />
                          </button>
                        </div>
                        <div className="space-y-3">
                          {bookmarklets.length === 0 && <p className="text-[10px] text-outline text-center py-4">No bookmarklets added</p>}
                          {bookmarklets.map(b => (
                            <div key={b.id} className="flex items-center justify-between p-4 bg-surface-container rounded-2xl">
                              <span className="text-xs font-bold text-on-surface">{b.name}</span>
                              <div className="flex gap-3">
                                <button onClick={() => executeBookmarklet(b.code)} className="text-primary hover:scale-110 transition-transform"><Play size={24} /></button>
                                <button onClick={() => setBookmarklets(bookmarklets.filter(s => s.id !== b.id))}><Trash2 size={24} className="text-red-500 opacity-60 hover:opacity-100 transition-opacity" /></button>
                              </div>
                            </div>
                          ))}
                        </div>
                      </div>
                    </div>
                  </CategorySection>
                </div>
              ) : (
                <div className="space-y-6 pb-24">
                  {activeTool === 'Calculator' && (
                    <div className="md-card p-6">
                      <div className="bg-surface-container rounded-2xl p-6 mb-6 text-right">
                        <p className="text-3xl font-mono font-bold truncate">{calcValue}</p>
                      </div>
                      <div className="grid grid-cols-4 gap-3">
                        {['C','(',')','/','7','8','9','*','4','5','6','-','1','2','3','+','0','.','%','='].map(btn => (
                          <button 
                            key={btn}
                            onClick={() => {
                              if (btn === 'C') setCalcValue('0');
                              else if (btn === '=') {
                                try {
                                  // Basic sanitization and evaluation
                                  const expression = calcValue.replace(/%/g, '/100');
                                  setCalcValue(eval(expression).toString());
                                } catch { setCalcValue('Error'); }
                              } else {
                                setCalcValue(prev => prev === '0' ? btn : prev + btn);
                              }
                            }}
                            className={cn(
                              "h-14 rounded-2xl font-bold transition-all active:scale-90",
                              ['/','*','-','+','=','(',')','%'].includes(btn) ? "bg-primary text-on-primary" : "bg-surface-container text-on-surface",
                              btn === '=' ? "bg-orange-500 text-white" : ""
                            )}
                          >
                            {btn}
                          </button>
                        ))}
                      </div>
                    </div>
                  )}

                  {activeTool === 'Timer' && (
                    <div className="md-card p-8 flex flex-col items-center gap-8">
                      <div className="text-6xl font-mono font-bold text-indigo-600 tabular-nums">
                        {formatTime(timerTime)}
                      </div>
                      <div className="flex flex-col gap-4 w-full">
                        {!isTimerRunning && timerTime === 0 && (
                          <div className="flex items-center gap-3">
                            <input
                              type="number"
                              value={timerInput}
                              onChange={(e) => setTimerInput(e.target.value)}
                              className="md-input flex-1 py-3 text-center text-xl font-bold"
                              placeholder="Minutes"
                            />
                            <button
                              onClick={() => setTimerTime(parseInt(timerInput) * 60000)}
                              className="md-button-filled px-6"
                            >
                              Set
                            </button>
                          </div>
                        )}
                        <div className="flex gap-4">
                          <button
                            onClick={() => setIsTimerRunning(!isTimerRunning)}
                            disabled={timerTime === 0}
                            className={cn(
                              "flex-1 py-4 rounded-2xl font-bold transition-all active:scale-95 flex items-center justify-center gap-2",
                              isTimerRunning ? "bg-red-100 text-red-600" : "bg-green-100 text-green-600"
                            )}
                          >
                            {isTimerRunning ? <Pause size={24} /> : <Play size={24} />}
                            {isTimerRunning ? 'Pause' : 'Start'}
                          </button>
                          <button
                            onClick={() => { setTimerTime(0); setIsTimerRunning(false); }}
                            className="flex-1 py-4 bg-surface-container text-on-surface rounded-2xl font-bold transition-all active:scale-95"
                          >
                            Reset
                          </button>
                        </div>
                      </div>
                    </div>
                  )}

                  {activeTool === 'Converter' && (
                    <div className="md-card p-6 space-y-6">
                      <div className="space-y-4">
                        <input 
                          type="number" 
                          value={unitValue}
                          onChange={(e) => setUnitValue(e.target.value)}
                          className="md-input w-full text-2xl font-bold text-center"
                        />
                        <div className="grid grid-cols-2 gap-4">
                          <select value={unitFrom} onChange={(e) => setUnitFrom(e.target.value)} className="md-input">
                            <option value="cm">CM</option>
                            <option value="inch">Inch</option>
                            <option value="kg">KG</option>
                            <option value="lb">LB</option>
                            <option value="m">Meter</option>
                            <option value="ft">Feet</option>
                            <option value="c">Celsius</option>
                            <option value="f">Fahrenheit</option>
                            <option value="kmh">KM/H</option>
                            <option value="mph">MPH</option>
                            <option value="sqm">Sq Meter</option>
                            <option value="sqft">Sq Feet</option>
                          </select>
                          <select value={unitTo} onChange={(e) => setUnitTo(e.target.value)} className="md-input">
                            <option value="cm">CM</option>
                            <option value="inch">Inch</option>
                            <option value="kg">KG</option>
                            <option value="lb">LB</option>
                            <option value="m">Meter</option>
                            <option value="ft">Feet</option>
                            <option value="c">Celsius</option>
                            <option value="f">Fahrenheit</option>
                            <option value="kmh">KM/H</option>
                            <option value="mph">MPH</option>
                            <option value="sqm">Sq Meter</option>
                            <option value="sqft">Sq Feet</option>
                          </select>
                        </div>
                        <div className="bg-primary/10 rounded-2xl p-8 text-center">
                          <p className="text-xs font-bold text-primary uppercase tracking-widest mb-2">Result</p>
                          <p className="text-4xl font-bold text-primary">{convertUnits(unitValue, unitFrom, unitTo)}</p>
                        </div>
                      </div>
                    </div>
                  )}

                  {activeTool === 'Stopwatch' && (
                    <div className="md-card p-8 flex flex-col items-center gap-8">
                      <div className="text-6xl font-mono font-bold text-primary tabular-nums">
                        {formatTime(stopwatchTime)}
                      </div>
                      <div className="flex gap-4 w-full">
                        <button 
                          onClick={() => setIsStopwatchRunning(!isStopwatchRunning)}
                          className={cn(
                            "flex-1 py-4 rounded-2xl font-bold transition-all active:scale-95 flex items-center justify-center gap-2",
                            isStopwatchRunning ? "bg-red-100 text-red-600" : "bg-green-100 text-green-600"
                          )}
                        >
                          {isStopwatchRunning ? <Pause size={24} /> : <Play size={24} />}
                          {isStopwatchRunning ? (stopwatchTime > 0 ? 'Resume' : 'Start') : 'Pause'}
                        </button>
                        <button 
                          onClick={() => {
                            if (isStopwatchRunning) {
                              setLaps([stopwatchTime, ...laps]);
                            } else {
                              setStopwatchTime(0);
                              setIsStopwatchRunning(false);
                              setLaps([]);
                            }
                          }}
                          className="flex-1 py-4 bg-surface-container text-on-surface rounded-2xl font-bold transition-all active:scale-95"
                        >
                          {isStopwatchRunning ? 'Lap' : 'Reset'}
                        </button>
                      </div>
                      {laps.length > 0 && (
                        <div className="w-full max-h-40 overflow-y-auto space-y-2">
                          {laps.map((lap, i) => (
                            <div key={i} className="flex justify-between items-center p-3 bg-surface-container rounded-xl text-sm font-bold">
                              <span className="text-outline">Lap {laps.length - i}</span>
                              <span className="font-mono">{formatTime(lap)}</span>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  )}

                  {activeTool === 'Text Tools' && (
                    <div className="md-card p-6 space-y-6">
                      <textarea 
                        value={textInput}
                        onChange={(e) => setTextInput(e.target.value)}
                        className="md-input w-full h-40 resize-none"
                        placeholder="Paste text here..."
                      />
                      <div className="grid grid-cols-2 gap-3">
                        <button onClick={() => setTextInput(textInput.toUpperCase())} className="md-button-tonal text-xs py-2">UPPERCASE</button>
                        <button onClick={() => setTextInput(textInput.toLowerCase())} className="md-button-tonal text-xs py-2">lowercase</button>
                        <button onClick={() => setTextInput(btoa(textInput))} className="md-button-tonal text-xs py-2">Base64 Enc</button>
                        <button onClick={() => { try { setTextInput(atob(textInput)); } catch { alert('Invalid Base64'); } }} className="md-button-tonal text-xs py-2">Base64 Dec</button>
                      </div>
                      <div className="flex justify-between text-[10px] font-bold text-outline uppercase tracking-widest">
                        <span>Words: {textInput.trim() ? textInput.trim().split(/\s+/).length : 0}</span>
                        <span>Chars: {textInput.length}</span>
                      </div>
                    </div>
                  )}

                  {activeTool === 'JSON Formatter' && (
                    <div className="md-card p-6 space-y-6">
                      <textarea 
                        value={jsonInput}
                        onChange={(e) => setJsonInput(e.target.value)}
                        className="md-input w-full h-40 font-mono text-xs resize-none"
                        placeholder="Paste JSON here..."
                      />
                      <div className="flex gap-3">
                        <button 
                          onClick={() => {
                            try {
                              const parsed = JSON.parse(jsonInput);
                              setJsonOutput(JSON.stringify(parsed, null, 2));
                            } catch (e) {
                              setJsonOutput('Invalid JSON');
                            }
                          }}
                          className="md-button-filled flex-1"
                        >
                          Format
                        </button>
                        <button 
                          onClick={() => {
                            try {
                              const parsed = JSON.parse(jsonInput);
                              setJsonOutput(JSON.stringify(parsed));
                            } catch (e) {
                              setJsonOutput('Invalid JSON');
                            }
                          }}
                          className="md-button-tonal flex-1"
                        >
                          Minify
                        </button>
                      </div>
                      {jsonOutput && (
                        <div className="relative">
                          <pre className="md-input w-full h-60 overflow-auto text-[10px] font-mono whitespace-pre bg-surface-container-low p-4">
                            {jsonOutput}
                          </pre>
                          <button 
                            onClick={() => navigator.clipboard.writeText(jsonOutput)}
                            className="absolute top-2 right-2 p-2 bg-white/80 rounded-lg shadow-sm"
                          >
                            <Copy size={14} />
                          </button>
                        </div>
                      )}
                    </div>
                  )}

                  {activeTool === 'Diff Viewer' && (
                    <div className="md-card p-6 space-y-6">
                      <div className="grid grid-cols-1 gap-4">
                        <textarea
                          value={textInput}
                          onChange={(e) => setTextInput(e.target.value)}
                          className="md-input w-full h-32 font-mono text-xs"
                          placeholder="Original text..."
                        />
                        <textarea
                          value={textInput2}
                          onChange={(e) => setTextInput2(e.target.value)}
                          className="md-input w-full h-32 font-mono text-xs"
                          placeholder="Changed text..."
                        />
                      </div>
                      <div className="bg-slate-50 p-4 rounded-xl overflow-auto max-h-60 font-mono text-[10px] whitespace-pre-wrap">
                        {textInput.split('\n').map((line, i) => {
                          const line2 = textInput2.split('\n')[i];
                          if (line === line2) return <div key={i} className="text-slate-500">{line}</div>;
                          return (
                            <div key={i}>
                              <div className="bg-red-50 text-red-600">- {line}</div>
                              <div className="bg-green-50 text-green-600">+ {line2}</div>
                            </div>
                          );
                        })}
                      </div>
                    </div>
                  )}

                  {activeTool === 'TTS' && (
                    <div className="md-card p-6 space-y-6">
                      <textarea
                        value={textInput}
                        onChange={(e) => setTextInput(e.target.value)}
                        className="md-input w-full h-40"
                        placeholder="Enter text to speak..."
                      />
                      <div className="flex gap-4">
                        <button
                          onClick={() => {
                            const utterance = new SpeechSynthesisUtterance(textInput);
                            window.speechSynthesis.speak(utterance);
                          }}
                          className="md-button-filled flex-1 py-3 flex items-center justify-center gap-2"
                        >
                          <Play size={18} /> Speak
                        </button>
                        <button
                          onClick={() => window.speechSynthesis.cancel()}
                          className="md-button-tonal flex-1 py-3 flex items-center justify-center gap-2"
                        >
                          <Pause size={18} /> Stop
                        </button>
                      </div>
                    </div>
                  )}

                  {activeTool === 'Lorem Ipsum' && (
                    <div className="md-card p-6 space-y-6">
                      <div className="flex items-center gap-4">
                        <input
                          type="number"
                          value={calcValue === '0' ? '3' : calcValue}
                          onChange={(e) => setCalcValue(e.target.value)}
                          className="md-input w-24"
                        />
                        <span className="text-sm font-bold text-on-surface">Paragraphs</span>
                        <button
                          onClick={() => {
                            const lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. ";
                            setJsonOutput(Array(parseInt(calcValue || '3')).fill(lorem).join('\n\n'));
                          }}
                          className="md-button-filled flex-1"
                        >
                          Generate
                        </button>
                      </div>
                      {jsonOutput && (
                        <div className="relative">
                          <textarea
                            readOnly
                            value={jsonOutput}
                            className="md-input w-full h-60 text-sm leading-relaxed"
                          />
                          <button
                            onClick={() => navigator.clipboard.writeText(jsonOutput)}
                            className="absolute top-2 right-2 p-2 bg-white/80 rounded-lg shadow-sm"
                          >
                            <Copy size={14} />
                          </button>
                        </div>
                      )}
                    </div>
                  )}

                  {activeTool === 'QR Generator' && (
                    <div className="md-card p-6 space-y-6">
                      <input 
                        type="text" 
                        placeholder="Enter text or URL"
                        value={qrText}
                        onChange={(e) => setQrText(e.target.value)}
                        className="md-input w-full"
                      />
                      {qrText && (
                        <div className="flex flex-col items-center gap-4">
                          <img 
                            src={`https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(qrText)}`} 
                            alt="QR Code"
                            className="w-48 h-48 bg-white p-2 rounded-xl shadow-inner"
                          />
                          <button 
                            onClick={() => window.open(`https://api.qrserver.com/v1/create-qr-code/?size=500x500&data=${encodeURIComponent(qrText)}`, '_blank')}
                            className="md-button-tonal w-full"
                          >
                            Download QR
                          </button>
                        </div>
                      )}
                    </div>
                  )}

                  {activeTool === 'Password Gen' && (
                    <div className="md-card p-6 space-y-6">
                      <button 
                        onClick={() => {
                          const charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+";
                          let retVal = "";
                          for (let i = 0; i < 16; ++i) {
                            retVal += charset.charAt(Math.floor(Math.random() * charset.length));
                          }
                          setQrText(retVal); // Reuse qrText for display
                        }}
                        className="md-button-filled w-full"
                      >
                        Generate Secure Password
                      </button>
                      {qrText && (
                        <div className="bg-surface-container p-4 rounded-xl flex items-center justify-between">
                          <p className="font-mono font-bold text-lg truncate">{qrText}</p>
                          <button onClick={() => navigator.clipboard.writeText(qrText)} className="text-primary p-2">
                            <Copy size={20} />
                          </button>
                        </div>
                      )}
                    </div>
                  )}

                  {activeTool === 'AI Summary' && (
                    <div className="md-card p-6 space-y-6">
                      <div className="flex flex-col items-center gap-4">
                        <div className={cn(
                          "w-16 h-16 bg-purple-100 text-purple-600 rounded-2xl flex items-center justify-center transition-all",
                          isAiLoading ? "animate-pulse" : ""
                        )}>
                          <Wand2 size={32} />
                        </div>
                        <div className="text-center">
                          <p className="text-lg font-bold">AI Page Summary</p>
                          <p className="text-xs text-outline font-bold">Powered by Gemini 1.5 Flash</p>
                        </div>
                        <button
                          onClick={handleAiAnalyze}
                          disabled={isAiLoading}
                          className="md-button-filled w-full py-3 flex items-center justify-center gap-2"
                        >
                          {isAiLoading ? <RefreshCw className="animate-spin" size={18} /> : <Wand2 size={18} />}
                          {isAiLoading ? 'Analyzing...' : 'Generate Summary'}
                        </button>
                      </div>

                      {aiSummary && (
                        <div className="bg-purple-50/50 border border-purple-100 p-5 rounded-2xl animate-in fade-in slide-in-from-top-4 duration-300">
                          <div className="flex items-center gap-2 mb-3 text-purple-600">
                            <FileText size={16} />
                            <span className="text-[10px] font-bold uppercase tracking-widest">Summary Result</span>
                          </div>
                          <p className="text-sm leading-relaxed text-on-surface whitespace-pre-wrap">
                            {aiSummary}
                          </p>
                          <button
                            onClick={() => navigator.clipboard.writeText(aiSummary)}
                            className="mt-4 text-[10px] font-bold text-purple-600 flex items-center gap-1 hover:underline"
                          >
                            <Copy size={12} /> Copy Summary
                          </button>
                        </div>
                      )}
                    </div>
                  )}

                  {activeTool === 'Translate' && (
                    <div className="md-card p-6 space-y-6">
                      <div className="flex gap-3">
                        <select 
                          value={targetLanguage} 
                          onChange={(e) => setTargetLanguage(e.target.value)}
                          className="md-input flex-1"
                        >
                          <option value="en">English</option>
                          <option value="es">Spanish</option>
                          <option value="fr">French</option>
                          <option value="de">German</option>
                          <option value="it">Italian</option>
                          <option value="pt">Portuguese</option>
                          <option value="ru">Russian</option>
                          <option value="ja">Japanese</option>
                          <option value="ko">Korean</option>
                          <option value="zh">Chinese</option>
                        </select>
                        <button onClick={handleTranslateText} className="md-button-filled">
                          Translate
                        </button>
                      </div>
                      <textarea 
                        value={translateText}
                        onChange={(e) => setTranslateText(e.target.value)}
                        className="md-input w-full h-32 resize-none"
                        placeholder="Enter text to translate..."
                      />
                      {translateResult && (
                        <div className="bg-surface-container p-4 rounded-xl min-h-[100px] text-sm leading-relaxed">
                          {translateResult}
                        </div>
                      )}
                    </div>
                  )}

                  {activeTool === 'Omni Hub' && (
                    <div className="md-card p-6 space-y-6">
                      <div className="space-y-4">
                        <p className="text-xs text-on-surface-variant leading-relaxed">
                          Integrate your GitHub Pages URL Hub. Enter the base URL of your static site to sync links from <code className="bg-surface-container px-1 rounded">links.json</code>.
                        </p>
                        <div className="flex gap-3">
                          <input 
                            type="text" 
                            placeholder="https://user.github.io/hub/"
                            value={hubUrl}
                            onChange={(e) => setHubUrl(e.target.value)}
                            className="md-input flex-1"
                          />
                          <button onClick={fetchHubLinks} className="md-button-filled">
                            Sync
                          </button>
                        </div>
                      </div>

                      {hubLinks.length > 0 && (
                        <div className="space-y-4">
                          <div className="flex items-center justify-between">
                            <h3 className="text-[10px] font-bold text-outline uppercase tracking-widest">Hub Links ({hubLinks.length})</h3>
                            <button 
                              onClick={() => { setHubLinks([]); localStorage.removeItem('omni_hub_links'); }}
                              className="text-[10px] font-bold text-red-500 uppercase tracking-widest"
                            >
                              Clear
                            </button>
                          </div>

                          {/* Search and Categories */}
                          <div className="space-y-3">
                            <div className="relative">
                              <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-outline" size={14} />
                              <input 
                                type="text" 
                                placeholder="Search hub links..." 
                                value={hubSearch}
                                onChange={(e) => setHubSearch(e.target.value)}
                                className="w-full pl-10 pr-4 py-2 bg-surface-container rounded-xl text-xs focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all"
                              />
                            </div>
                            <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-hide">
                              {['All', ...Array.from(new Set(hubLinks.map(l => l.category || 'Uncategorized'))).sort()].map(cat => (
                                <button
                                  key={cat}
                                  onClick={() => setHubCategory(cat)}
                                  className={cn(
                                    "px-3 py-1.5 rounded-lg text-[10px] font-bold whitespace-nowrap transition-all",
                                    hubCategory === cat 
                                      ? "bg-primary text-on-primary shadow-md" 
                                      : "bg-surface-container-high text-on-surface-variant hover:bg-surface-container-highest"
                                  )}
                                >
                                  {cat}
                                </button>
                              ))}
                            </div>
                          </div>

                          <div className="flex flex-col gap-3 max-h-[400px] overflow-y-auto pr-2 scrollbar-hide pb-2">
                            {hubLinks
                              .filter(l => {
                                const matchesSearch = l.title.toLowerCase().includes(hubSearch.toLowerCase()) ||
                                  l.urls.some(u => {
                                    const urlStr = typeof u === 'string' ? u : u.url;
                                    return urlStr.toLowerCase().includes(hubSearch.toLowerCase());
                                  });
                                const matchesCategory = hubCategory === 'All' || (l.category || 'Uncategorized') === hubCategory;
                                return matchesSearch && matchesCategory;
                              })
                              .sort((a, b) => a.title.localeCompare(b.title))
                              .map((link, i) => (
                                <button 
                                  key={i}
                                  onClick={() => {
                                    if (link.url) {
                                      navigate(link.url);
                                      setIsToolboxOpen(false);
                                    } else if (link.urls.length === 1) {
                                      navigate(link.urls[0].url);
                                      setIsToolboxOpen(false);
                                    } else {
                                      setLongPressedLink(link);
                                    }
                                  }}
                                  onMouseDown={() => handleLinkPressStart(link)}
                                  onMouseUp={handleLinkPressEnd}
                                  onMouseLeave={handleLinkPressEnd}
                                  onTouchStart={() => handleLinkPressStart(link)}
                                  onTouchEnd={handleLinkPressEnd}
                                  className="flex flex-row items-center gap-4 p-3 bg-surface-container hover:bg-surface-container-high rounded-full transition-all active:scale-95 text-left relative overflow-hidden w-full shadow-sm"
                                >
                                  <div className="w-12 h-12 bg-primary/10 rounded-full flex items-center justify-center text-primary shrink-0">
                                    <Link size={20} />
                                  </div>
                                  <div className="flex-1 min-w-0">
                                    <p className="text-sm font-bold text-on-surface truncate">{link.title}</p>
                                    <p className="text-[10px] text-on-surface-variant opacity-60">{link.urls.length} {link.urls.length === 1 ? 'URL' : 'URLs'}</p>
                                  </div>
                                  {link.category && (
                                    <span className="text-[10px] font-bold bg-primary/5 text-primary/60 px-3 py-1 rounded-full uppercase tracking-tighter shrink-0 ml-auto">
                                      {link.category}
                                    </span>
                                  )}
                                </button>
                              ))}
                          </div>
                        </div>
                      )}
                    </div>
                  )}

                  {activeTool === 'Network Monitor' && (
                    <div className="md-card p-4 space-y-4">
                      <div className="flex items-center justify-between px-2">
                        <h3 className="text-[10px] font-bold text-outline uppercase tracking-widest">Network Activity</h3>
                        <span className="text-[10px] font-bold text-green-600">LIVE</span>
                      </div>
                      <div className="space-y-2 max-h-[400px] overflow-y-auto">
                        {networkLogs.length === 0 ? (
                          <div className="py-8 text-center text-outline">
                            <p className="text-xs">No activity yet</p>
                          </div>
                        ) : (
                          networkLogs.map((req, i) => (
                            <div key={i} className="flex items-center justify-between p-3 bg-surface-container rounded-xl text-[10px] font-mono">
                              <div className="flex items-center gap-3 min-w-0">
                                <span className={cn(
                                  "px-1.5 py-0.5 rounded text-[8px] font-bold",
                                  req.method === 'GET' ? "bg-blue-100 text-blue-700" : "bg-purple-100 text-purple-700"
                                )}>{req.method}</span>
                                <span className="truncate text-on-surface opacity-80" title={req.url}>{req.url}</span>
                              </div>
                              <div className="flex items-center gap-3 shrink-0 ml-4">
                                <span className={cn(
                                  "font-bold",
                                  req.status >= 200 && req.status < 300 ? "text-green-600" : "text-red-600"
                                )}>{req.status}</span>
                                <span className="text-outline">{req.size}</span>
                              </div>
                            </div>
                          ))
                        )}
                      </div>
                    </div>
                  )}

                  {activeTool === 'Cookie Manager' && (
                    <div className="md-card p-4 space-y-4">
                      <div className="flex items-center justify-between px-2">
                        <h3 className="text-[10px] font-bold text-outline uppercase tracking-widest">Cookies</h3>
                        <button 
                          onClick={() => {
                            cookies.forEach(c => deleteCookie(c.name));
                            refreshCookies();
                          }}
                          className="text-[10px] font-bold text-red-600 uppercase"
                        >
                          Clear All
                        </button>
                      </div>

                      {/* Add Cookie Form */}
                      <div className="p-3 bg-surface-container rounded-2xl space-y-3">
                        <p className="text-[10px] font-bold text-primary uppercase tracking-wider">Add Cookie</p>
                        <div className="grid grid-cols-2 gap-2">
                          <input 
                            type="text" 
                            placeholder="Name" 
                            value={newCookie.name}
                            onChange={(e) => setNewCookie({ ...newCookie, name: e.target.value })}
                            className="md-input py-2 text-xs"
                          />
                          <input 
                            type="text" 
                            placeholder="Value" 
                            value={newCookie.value}
                            onChange={(e) => setNewCookie({ ...newCookie, value: e.target.value })}
                            className="md-input py-2 text-xs"
                          />
                        </div>
                        <button 
                          onClick={() => addCookie(newCookie.name, newCookie.value)}
                          className="md-button-filled w-full py-2 text-xs"
                        >
                          Add Cookie
                        </button>
                      </div>

                      <div className="space-y-2 max-h-[400px] overflow-y-auto pr-1">
                        {cookies.length === 0 ? (
                          <div className="py-8 text-center text-outline">
                            <p className="text-xs">No cookies found for this session</p>
                          </div>
                        ) : (
                          cookies.map((cookie, i) => (
                            <div key={i} className="p-3 bg-surface-container rounded-xl space-y-1 group">
                              <div className="flex items-center justify-between">
                                <span className="text-xs font-bold text-primary truncate mr-2">{cookie.name}</span>
                                <button 
                                  onClick={() => deleteCookie(cookie.name)}
                                  className="text-outline hover:text-red-500 transition-colors"
                                >
                                  <Trash2 size={14} />
                                </button>
                              </div>
                              <div className="relative">
                                <input 
                                  type="text"
                                  value={cookie.value}
                                  onChange={(e) => {
                                    const updatedValue = e.target.value;
                                    document.cookie = `${cookie.name}=${updatedValue}; path=/; max-age=31536000`;
                                    refreshCookies();
                                  }}
                                  className="w-full bg-transparent border-none text-[10px] font-mono text-on-surface-variant focus:ring-0 p-0"
                                />
                                <div className="absolute right-0 top-0 opacity-0 group-hover:opacity-100 transition-opacity">
                                  <Copy 
                                    size={12} 
                                    className="text-outline cursor-pointer hover:text-primary" 
                                    onClick={() => navigator.clipboard.writeText(cookie.value)}
                                  />
                                </div>
                              </div>
                            </div>
                          ))
                        )}
                      </div>
                    </div>
                  )}

                  {activeTool === 'Notes' && (
                    <div className="md-card p-6 h-[400px] flex flex-col">
                      <textarea 
                        value={notes}
                        onChange={(e) => {
                          setNotes(e.target.value);
                          localStorage.setItem('browser_notes', e.target.value);
                        }}
                        placeholder="Write your notes here..."
                        className="flex-1 bg-transparent outline-none resize-none text-sm font-medium leading-relaxed"
                      />
                    </div>
                  )}

                  {activeTool === 'Device Info' && (
                    <div className="md-card p-6 space-y-4">
                      <div className="space-y-2">
                        <p className="text-[10px] font-bold text-outline uppercase tracking-widest">Browser Agent</p>
                        <p className="text-xs font-mono bg-surface-container p-3 rounded-xl break-all">{navigator.userAgent}</p>
                      </div>
                      <div className="grid grid-cols-2 gap-4">
                        <div className="bg-surface-container p-4 rounded-xl">
                          <p className="text-[10px] font-bold text-outline uppercase tracking-widest">Platform</p>
                          <p className="text-sm font-bold">{(navigator as any).platform || 'Unknown'}</p>
                        </div>
                        <div className="bg-surface-container p-4 rounded-xl">
                          <p className="text-[10px] font-bold text-outline uppercase tracking-widest">Language</p>
                          <p className="text-sm font-bold">{navigator.language}</p>
                        </div>
                        <div className="bg-surface-container p-4 rounded-xl">
                          <p className="text-[10px] font-bold text-outline uppercase tracking-widest">Cores</p>
                          <p className="text-sm font-bold">{navigator.hardwareConcurrency || 'N/A'}</p>
                        </div>
                        <div className="bg-surface-container p-4 rounded-xl">
                          <p className="text-[10px] font-bold text-outline uppercase tracking-widest">Memory</p>
                          <p className="text-sm font-bold">{(navigator as any).deviceMemory ? `${(navigator as any).deviceMemory} GB` : 'N/A'}</p>
                        </div>
                      </div>
                    </div>
                  )}

                  {activeTool === 'Security Scan' && (
                    <div className="md-card p-6 space-y-6">
                      <div className="flex flex-col items-center gap-4 py-6">
                        <div className={cn(
                          "w-20 h-20 rounded-full flex items-center justify-center transition-all",
                          isSecurityLoading ? "bg-surface-container animate-pulse" :
                          securityData?.isHttps ? "bg-green-100 text-green-600" : "bg-red-100 text-red-600"
                        )}>
                          {isSecurityLoading ? <RefreshCw className="animate-spin" size={32} /> : <ShieldCheck size={48} />}
                        </div>
                        <div className="text-center">
                          <p className="text-lg font-bold">
                            {isSecurityLoading ? 'Scanning...' : (securityData?.isHttps ? 'Site is Secure' : 'Site is Unsecure')}
                          </p>
                          <p className="text-xs text-outline font-bold">
                            {securityData?.isHttps ? 'HTTPS Encryption Active' : 'Unencrypted Connection'}
                          </p>
                        </div>
                      </div>

                      {!isSecurityLoading && securityData && (
                        <div className="space-y-3 animate-in fade-in slide-in-from-top-4 duration-300">
                          <div className="flex items-center justify-between p-4 bg-surface-container rounded-2xl">
                            <div className="flex items-center gap-3">
                              <Globe size={18} className="text-primary" />
                              <span className="text-xs font-bold">SSL/TLS</span>
                            </div>
                            <span className={cn(
                              "text-[10px] font-bold px-2 py-1 rounded-lg",
                              securityData.isHttps ? "text-green-600 bg-green-50" : "text-red-600 bg-red-50"
                            )}>{securityData.isHttps ? 'VALID' : 'MISSING'}</span>
                          </div>
                          <div className="flex items-center justify-between p-4 bg-surface-container rounded-2xl">
                            <div className="flex items-center gap-3">
                              <ShieldCheck size={18} className="text-primary" />
                              <span className="text-xs font-bold">HSTS</span>
                            </div>
                            <span className={cn(
                              "text-[10px] font-bold px-2 py-1 rounded-lg",
                              securityData.hsts ? "text-green-600 bg-green-50" : "text-outline bg-surface-variant"
                            )}>{securityData.hsts ? 'ENABLED' : 'DISABLED'}</span>
                          </div>
                          <div className="flex items-center justify-between p-4 bg-surface-container rounded-2xl">
                            <div className="flex items-center gap-3">
                              <ShieldCheck size={18} className="text-primary" />
                              <span className="text-xs font-bold">CSP</span>
                            </div>
                            <span className={cn(
                              "text-[10px] font-bold px-2 py-1 rounded-lg",
                              securityData.csp ? "text-green-600 bg-green-50" : "text-outline bg-surface-variant"
                            )}>{securityData.csp ? 'ACTIVE' : 'MISSING'}</span>
                          </div>
                          <div className="p-4 bg-surface-container rounded-2xl space-y-2">
                            <div className="flex justify-between text-[10px] font-bold text-outline uppercase tracking-wider">
                              <span>Server Info</span>
                              <span>Details</span>
                            </div>
                            <div className="flex justify-between text-xs">
                              <span className="text-on-surface-variant">Server</span>
                              <span className="font-bold">{securityData.server}</span>
                            </div>
                            <div className="flex justify-between text-xs">
                              <span className="text-on-surface-variant">X-Powered-By</span>
                              <span className="font-bold">{securityData.poweredBy}</span>
                            </div>
                          </div>
                        </div>
                      )}

                      <button
                        onClick={() => setActiveTool('Security Scan')}
                        className="md-button-tonal w-full py-3 text-xs"
                      >
                        Refresh Scan
                      </button>
                    </div>
                  )}

                  {activeTool === 'Morse Code' && (
                    <div className="md-card p-6 space-y-6">
                      <textarea 
                        value={textInput}
                        onChange={(e) => setTextInput(e.target.value)}
                        className="md-input w-full h-32 resize-none"
                        placeholder="Enter text to convert to Morse..."
                      />
                      <div className="bg-surface-container p-4 rounded-xl min-h-[100px] break-all font-mono text-sm">
                        {textInput.toUpperCase().split('').map(char => {
                          const morse: any = {
                            'A': '.-', 'B': '-...', 'C': '-.-.', 'D': '-..', 'E': '.', 'F': '..-.',
                            'G': '--.', 'H': '....', 'I': '..', 'J': '.---', 'K': '-.-', 'L': '.-..',
                            'M': '--', 'N': '-.', 'O': '---', 'P': '.--.', 'Q': '--.-', 'R': '.-.',
                            'S': '...', 'T': '-', 'U': '..-', 'V': '...-', 'W': '.--', 'X': '-..-',
                            'Y': '-.--', 'Z': '--..', '1': '.----', '2': '..---', '3': '...--',
                            '4': '....-', '5': '.....', '6': '-....', '7': '--...', '8': '---..',
                            '9': '----.', '0': '-----', ' ': '/'
                          };
                          return morse[char] || char;
                        }).join(' ')}
                      </div>
                    </div>
                  )}

                  {activeTool === 'BMI Calc' && (
                    <div className="md-card p-6 space-y-6">
                      <div className="grid grid-cols-2 gap-4">
                        <div>
                          <label className="text-[10px] font-bold text-outline uppercase mb-2 block">Weight (kg)</label>
                          <input type="number" value={unitValue} onChange={(e) => setUnitValue(e.target.value)} className="md-input w-full" />
                        </div>
                        <div>
                          <label className="text-[10px] font-bold text-outline uppercase mb-2 block">Height (cm)</label>
                          <input type="number" value={calcValue} onChange={(e) => setCalcValue(e.target.value)} className="md-input w-full" />
                        </div>
                      </div>
                      <button 
                        onClick={() => {
                          const w = parseFloat(unitValue);
                          const h = parseFloat(calcValue) / 100;
                          if (w && h) setJsonOutput((w / (h * h)).toFixed(1));
                        }}
                        className="md-button-filled w-full"
                      >
                        Calculate BMI
                      </button>
                      {jsonOutput && (
                        <div className="text-center p-6 bg-primary/5 rounded-2xl">
                          <p className="text-xs font-bold text-primary uppercase mb-1">Your BMI</p>
                          <p className="text-4xl font-bold text-primary">{jsonOutput}</p>
                        </div>
                      )}
                    </div>
                  )}

                  {activeTool === 'Age Calc' && (
                    <div className="md-card p-6 space-y-6">
                      <input 
                        type="date" 
                        onChange={(e) => {
                          const birth = new Date(e.target.value);
                          const now = new Date();
                          let age = now.getFullYear() - birth.getFullYear();
                          const m = now.getMonth() - birth.getMonth();
                          if (m < 0 || (m === 0 && now.getDate() < birth.getDate())) age--;
                          setJsonOutput(age.toString());
                        }}
                        className="md-input w-full"
                      />
                      {jsonOutput && (
                        <div className="text-center p-6 bg-primary/5 rounded-2xl">
                          <p className="text-xs font-bold text-primary uppercase mb-1">Your Age</p>
                          <p className="text-4xl font-bold text-primary">{jsonOutput} Years</p>
                        </div>
                      )}
                    </div>
                  )}

                  {activeTool === 'Color Picker' && (
                    <div className="md-card p-6 space-y-6">
                      <input 
                        type="color" 
                        value={settings.themeColor}
                        onChange={(e) => setSettings({ ...settings, themeColor: e.target.value })}
                        className="w-full h-24 rounded-2xl cursor-pointer border-none p-0"
                      />
                      <div className="grid grid-cols-2 gap-4">
                        <div className="bg-surface-container p-4 rounded-xl">
                          <p className="text-[10px] font-bold text-outline uppercase mb-1">HEX</p>
                          <p className="text-sm font-mono font-bold">{settings.themeColor}</p>
                        </div>
                        <button 
                          onClick={() => navigator.clipboard.writeText(settings.themeColor)}
                          className="md-button-tonal"
                        >
                          <Copy size={18} /> Copy
                        </button>
                      </div>
                    </div>
                  )}
                </div>
              )}
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Media Sniffer Overlay */}
      <AnimatePresence>
        {isMediaSnifferOpen && (
          <motion.div 
            initial={{ opacity: 0, y: '100%' }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: '100%' }}
            className="fixed inset-0 bg-surface z-50 flex flex-col"
          >
            <div className="p-4 flex items-center justify-between border-b border-outline-variant/30 bg-surface">
              <h2 className="font-bold text-xl text-on-surface">Media Sniffer</h2>
              <button onClick={() => setIsMediaSnifferOpen(false)} className="p-3 hover:bg-surface-variant rounded-full transition-colors text-on-surface-variant">
                <X size={28} />
              </button>
            </div>

            <div className="flex-1 overflow-y-auto p-6">
              {detectedMedia.length === 0 ? (
                <div className="flex flex-col items-center justify-center py-24 text-outline text-center">
                  <div className="w-24 h-24 bg-surface-container rounded-full flex items-center justify-center mb-6">
                    <Video size={48} strokeWidth={1.5} />
                  </div>
                  <p className="font-bold text-lg text-on-surface">No media detected</p>
                  <p className="text-sm opacity-60 mt-1">Navigate to a page with videos or audio to sniff media</p>
                </div>
              ) : (
                <div className="space-y-6">
                  {/* Search and Filter */}
                  <div className="space-y-4">
                    <div className="relative">
                      <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-outline" size={18} />
                      <input 
                        type="text" 
                        placeholder="Search media..." 
                        value={mediaSearch}
                        onChange={(e) => setMediaSearch(e.target.value)}
                        className="w-full pl-12 pr-4 py-3 bg-surface-container rounded-2xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all"
                      />
                    </div>
                    <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-hide">
                      {(['all', 'video', 'audio', 'image'] as const).map((filter) => (
                        <button
                          key={filter}
                          onClick={() => setMediaFilter(filter)}
                          className={cn(
                            "px-4 py-2 rounded-xl text-xs font-bold whitespace-nowrap transition-all",
                            mediaFilter === filter 
                              ? "bg-primary text-on-primary shadow-lg shadow-primary/20" 
                              : "bg-surface-container-high text-on-surface-variant hover:bg-surface-container-highest"
                          )}
                        >
                          {filter.charAt(0).toUpperCase() + filter.slice(1)}
                        </button>
                      ))}
                    </div>
                  </div>

                  <div className="flex items-center justify-between">
                    <h3 className="text-xs font-bold text-primary uppercase tracking-widest">
                      Detected Media ({filteredMedia.length})
                    </h3>
                    <div className="flex gap-4">
                      <button 
                        onClick={selectAllMedia}
                        className="text-[11px] font-bold text-primary uppercase tracking-wider hover:bg-primary/10 px-3 py-1.5 rounded-xl transition-colors"
                      >
                        {filteredMedia.every(m => m.selected) ? 'Deselect All' : 'Select All'}
                      </button>
                      <button 
                        onClick={downloadSelectedMedia}
                        disabled={!detectedMedia.some(m => m.selected)}
                        className="md-button-filled text-[11px] px-4 py-2"
                      >
                        Download Selected
                      </button>
                    </div>
                  </div>
                  <div className="grid grid-cols-1 gap-4">
                    {filteredMedia.length === 0 ? (
                      <div className="py-12 text-center text-outline">
                        <p className="text-sm">No media matches your search or filter</p>
                      </div>
                    ) : (
                      filteredMedia.map((media) => (
                        <div 
                          key={media.id} 
                          onClick={() => toggleMediaSelection(media.id)}
                          className={cn(
                            "md-card p-4 flex items-center justify-between gap-4 cursor-pointer transition-all active:scale-[0.98]",
                            media.selected ? "ring-2 ring-primary bg-primary/5" : ""
                          )}
                        >
                          <div className="flex items-center gap-4 flex-1 min-w-0">
                            <div className="w-14 h-14 bg-primary-container rounded-2xl flex items-center justify-center text-primary shrink-0 overflow-hidden shadow-inner">
                              {media.type === 'video' ? <Video size={28} /> : media.type === 'audio' ? <Music size={28} /> : <img src={media.src} className="w-full h-full object-cover" referrerPolicy="no-referrer" />}
                            </div>
                            <div className="min-w-0">
                              <p className="text-sm font-bold text-on-surface truncate">{media.title}</p>
                              <p className="text-[10px] text-on-surface-variant truncate font-mono opacity-60">{media.src}</p>
                            </div>
                          </div>
                          <div className={cn(
                            "w-7 h-7 rounded-full border-2 flex items-center justify-center transition-all",
                            media.selected ? "bg-primary border-primary scale-110" : "border-outline-variant"
                          )}>
                            {media.selected && <CheckCircle2 size={16} className="text-on-primary" />}
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              )}
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* History Overlay */}
      <AnimatePresence>
        {isHistoryOpen && (
          <motion.div 
            initial={{ opacity: 0, x: '-100%' }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: '-100%' }}
            className="fixed inset-0 bg-surface z-50 flex flex-col"
          >
            <div className="p-4 flex items-center justify-between border-b border-outline-variant/30 bg-surface">
              <h2 className="font-bold text-xl text-on-surface">History</h2>
              <div className="flex gap-3">
                <button 
                  onClick={() => {
                    if (confirm('Clear all history?')) setHistory([]);
                  }}
                  className="p-3 text-red-500 hover:bg-red-50 rounded-full transition-colors"
                >
                  <Trash2 size={28} />
                </button>
                <button onClick={() => setIsHistoryOpen(false)} className="p-3 hover:bg-surface-variant rounded-full transition-colors text-on-surface-variant">
                  <X size={28} />
                </button>
              </div>
            </div>
            <div className="flex-1 overflow-y-auto p-4">
              {history.length === 0 ? (
                <div className="flex flex-col items-center justify-center py-24 text-outline">
                  <div className="w-24 h-24 bg-surface-container rounded-full flex items-center justify-center mb-6">
                    <History size={48} strokeWidth={1.5} />
                  </div>
                  <p className="font-bold text-lg">No history yet</p>
                </div>
              ) : (
                <div className="space-y-3">
                  {history.map(item => (
                    <div 
                      key={item.id}
                      onClick={() => {
                        navigate(item.url);
                        setIsHistoryOpen(false);
                      }}
                      className="md-card p-4 flex items-center justify-between group cursor-pointer active:scale-[0.98]"
                    >
                      <div className="flex items-center gap-4 min-w-0">
                        <div className="w-12 h-12 bg-surface-container rounded-2xl flex items-center justify-center text-outline shrink-0">
                          <Globe size={24} />
                        </div>
                        <div className="min-w-0">
                          <p className="text-sm font-bold text-on-surface truncate">{item.title}</p>
                          <p className="text-xs text-on-surface-variant truncate opacity-60">{item.url}</p>
                        </div>
                      </div>
                      <div className="flex items-center gap-4">
                        <span className="text-[10px] font-bold text-outline whitespace-nowrap">
                          {new Date(item.timestamp).toLocaleDateString()}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Bookmarks Overlay */}
      <AnimatePresence>
        {isBookmarksOpen && (
          <motion.div 
            initial={{ opacity: 0, x: '100%' }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: '100%' }}
            className="fixed inset-0 bg-surface z-50 flex flex-col"
          >
            <div className="p-4 flex items-center justify-between border-b border-outline-variant/30 bg-surface">
              <h2 className="font-bold text-xl text-on-surface">Bookmarks</h2>
              <button onClick={() => setIsBookmarksOpen(false)} className="p-3 hover:bg-surface-variant rounded-full transition-colors text-on-surface-variant">
                <X size={28} />
              </button>
            </div>
            <div className="flex-1 overflow-y-auto p-4">
              {bookmarks.length === 0 ? (
                <div className="flex flex-col items-center justify-center py-24 text-outline">
                  <div className="w-24 h-24 bg-surface-container rounded-full flex items-center justify-center mb-6">
                    <Bookmark size={48} strokeWidth={1.5} />
                  </div>
                  <p className="font-bold text-lg">No bookmarks yet</p>
                </div>
              ) : (
                <div className="space-y-3">
                  {bookmarks.map(item => (
                    <div 
                      key={item.id}
                      onClick={() => {
                        navigate(item.url);
                        setIsBookmarksOpen(false);
                      }}
                      className="md-card p-4 flex items-center justify-between group cursor-pointer active:scale-[0.98]"
                    >
                      <div className="flex items-center gap-4 min-w-0">
                        <div className="w-12 h-12 bg-surface-container rounded-2xl flex items-center justify-center text-outline shrink-0">
                          <Globe size={24} />
                        </div>
                        <div className="min-w-0">
                          <p className="text-sm font-bold text-on-surface truncate">{item.title}</p>
                          <p className="text-xs text-on-surface-variant truncate opacity-60">{item.url}</p>
                        </div>
                      </div>
                      <button 
                        onClick={(e) => {
                          e.stopPropagation();
                          setBookmarks(prev => prev.filter(b => b.id !== item.id));
                        }}
                        className="p-2 text-outline hover:text-red-500 transition-colors"
                      >
                        <Trash2 size={20} />
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Bottom Context Menu (Merged Menu) */}
      <AnimatePresence>
        {isMenuOpen && (
          <>
            <motion.div 
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setIsMenuOpen(false)}
              className="fixed inset-0 bg-black/40 z-[60] backdrop-blur-[2px]"
            />
            <motion.div 
              initial={{ y: '100%' }}
              animate={{ y: 0 }}
              exit={{ y: '100%' }}
              transition={{ type: 'spring', damping: 25, stiffness: 200 }}
              className="fixed bottom-0 left-0 right-0 bg-surface rounded-t-[32px] shadow-2xl z-[70] max-h-[90vh] flex flex-col overflow-hidden"
            >
              <div className="w-12 h-1.5 bg-outline-variant/30 rounded-full mx-auto my-3" />
              
              <div className="flex-1 overflow-y-auto p-4 space-y-6 pb-12">
                {/* Navigation Actions */}
                <div className="grid grid-cols-4 gap-2">
                  <button onClick={() => { window.history.back(); setIsMenuOpen(false); }} className="flex flex-col items-center gap-2 p-3 hover:bg-surface-variant rounded-2xl transition-colors">
                    <div className="w-12 h-12 bg-primary/10 text-primary rounded-full flex items-center justify-center"><ChevronLeft size={24} /></div>
                    <span className="text-[10px] font-bold text-on-surface">Back</span>
                  </button>
                  <button onClick={() => { window.history.forward(); setIsMenuOpen(false); }} className="flex flex-col items-center gap-2 p-3 hover:bg-surface-variant rounded-2xl transition-colors">
                    <div className="w-12 h-12 bg-primary/10 text-primary rounded-full flex items-center justify-center"><ChevronRight size={24} /></div>
                    <span className="text-[10px] font-bold text-on-surface">Forward</span>
                  </button>
                  <button onClick={() => { navigate(activeTab.url); setIsMenuOpen(false); }} className="flex flex-col items-center gap-2 p-3 hover:bg-surface-variant rounded-2xl transition-colors">
                    <div className="w-12 h-12 bg-primary/10 text-primary rounded-full flex items-center justify-center"><RotateCcw size={24} /></div>
                    <span className="text-[10px] font-bold text-on-surface">Reload</span>
                  </button>
                  <button 
                    onClick={() => { setSettings({ ...settings, desktopMode: !settings.desktopMode }); setIsMenuOpen(false); }} 
                    className={cn(
                      "flex flex-col items-center gap-2 p-3 rounded-2xl transition-colors",
                      settings.desktopMode ? "bg-primary/20" : "hover:bg-surface-variant"
                    )}
                  >
                    <div className={cn(
                      "w-12 h-12 rounded-full flex items-center justify-center",
                      settings.desktopMode ? "bg-primary text-on-primary" : "bg-primary/10 text-primary"
                    )}>
                      {settings.desktopMode ? <Monitor size={24} /> : <Smartphone size={24} />}
                    </div>
                    <span className="text-[10px] font-bold text-on-surface">{settings.desktopMode ? 'Desktop' : 'Mobile'}</span>
                  </button>
                </div>

                {/* Page Tools */}
                <section className="space-y-3">
                  <h3 className="text-[10px] font-bold text-primary uppercase tracking-widest px-2">Page Tools</h3>
                  <div className="grid grid-cols-2 gap-2">
                    <button 
                      onClick={() => { setPageToolView(pageToolView === 'reader' ? 'normal' : 'reader'); setIsMenuOpen(false); }}
                      className={cn(
                        "flex items-center gap-3 p-4 rounded-2xl transition-colors text-sm font-bold",
                        pageToolView === 'reader' ? "bg-primary text-on-primary" : "bg-surface-container hover:bg-surface-variant text-on-surface"
                      )}
                    >
                      <FileText size={20} /> Reader Mode
                    </button>
                    <button 
                      onClick={() => { setPageToolView(pageToolView === 'source' ? 'normal' : 'source'); setIsMenuOpen(false); }}
                      className={cn(
                        "flex items-center gap-3 p-4 rounded-2xl transition-colors text-sm font-bold",
                        pageToolView === 'source' ? "bg-primary text-on-primary" : "bg-surface-container hover:bg-surface-variant text-on-surface"
                      )}
                    >
                      <SearchCode size={20} /> View Source
                    </button>
                    <button 
                      onClick={() => { setIsFindOnPageOpen(true); setIsMenuOpen(false); }}
                      className="flex items-center gap-3 p-4 bg-surface-container hover:bg-surface-variant rounded-2xl transition-colors text-sm font-bold text-on-surface"
                    >
                      <Search size={20} /> Find on Page
                    </button>
                    <button 
                      onClick={() => { translatePage(); setIsMenuOpen(false); }}
                      className="flex items-center gap-3 p-4 bg-surface-container hover:bg-surface-variant rounded-2xl transition-colors text-sm font-bold text-on-surface"
                    >
                      <Languages size={20} /> Translate Page
                    </button>
                    <button 
                      onClick={() => { toggleBookmark(); setIsMenuOpen(false); }}
                      className="flex items-center gap-3 p-4 bg-surface-container hover:bg-surface-variant rounded-2xl transition-colors text-sm font-bold text-on-surface"
                    >
                      <Bookmark size={20} fill={bookmarks.find(b => b.url === activeTab.url) ? "currentColor" : "none"} /> 
                      {bookmarks.find(b => b.url === activeTab.url) ? 'Bookmarked' : 'Bookmark'}
                    </button>
                  </div>
                </section>

                {/* Export Options */}
                <section className="space-y-3">
                  <h3 className="text-[10px] font-bold text-primary uppercase tracking-widest px-2">Export & Save</h3>
                  <div className="grid grid-cols-4 gap-2">
                    <button onClick={() => { saveAsPdf(); setIsMenuOpen(false); }} className="flex flex-col items-center gap-2 p-3 bg-surface-container hover:bg-surface-variant rounded-2xl transition-colors">
                      <div className="w-10 h-10 bg-red-50 text-red-600 rounded-xl flex items-center justify-center font-bold text-[10px]">PDF</div>
                      <span className="text-[9px] font-bold text-on-surface">PDF</span>
                    </button>
                    <button onClick={() => { saveAsHtml(); setIsMenuOpen(false); }} className="flex flex-col items-center gap-2 p-3 bg-surface-container hover:bg-surface-variant rounded-2xl transition-colors">
                      <div className="w-10 h-10 bg-blue-50 text-blue-600 rounded-xl flex items-center justify-center font-bold text-[10px]">HTML</div>
                      <span className="text-[9px] font-bold text-on-surface">HTML</span>
                    </button>
                    <button onClick={() => { saveAsMhtml(); setIsMenuOpen(false); }} className="flex flex-col items-center gap-2 p-3 bg-surface-container hover:bg-surface-variant rounded-2xl transition-colors">
                      <div className="w-10 h-10 bg-amber-50 text-amber-600 rounded-xl flex items-center justify-center font-bold text-[10px]">MHT</div>
                      <span className="text-[9px] font-bold text-on-surface">MHTML</span>
                    </button>
                    <button onClick={() => { saveAsMarkdown(); setIsMenuOpen(false); }} className="flex flex-col items-center gap-2 p-3 bg-surface-container hover:bg-surface-variant rounded-2xl transition-colors">
                      <div className="w-10 h-10 bg-green-50 text-green-600 rounded-xl flex items-center justify-center font-bold text-[10px]">MD</div>
                      <span className="text-[9px] font-bold text-on-surface">Markdown</span>
                    </button>
                  </div>
                </section>

                {/* System Actions */}
                <div className="space-y-2">
                  <button 
                    onClick={() => { setIsSettingsOpen(true); setIsMenuOpen(false); }}
                    className="w-full flex items-center justify-between p-4 bg-surface-container hover:bg-surface-variant rounded-2xl transition-colors text-sm font-bold text-on-surface"
                  >
                    <div className="flex items-center gap-4">
                      <Settings size={20} className="text-primary" /> Settings
                    </div>
                    <ChevronRight size={16} className="text-outline" />
                  </button>
                  <button 
                    onClick={() => { setIsHistoryOpen(true); setIsMenuOpen(false); }}
                    className="w-full flex items-center justify-between p-4 bg-surface-container hover:bg-surface-variant rounded-2xl transition-colors text-sm font-bold text-on-surface"
                  >
                    <div className="flex items-center gap-4">
                      <History size={20} className="text-primary" /> History
                    </div>
                    <ChevronRight size={16} className="text-outline" />
                  </button>
                  <button 
                    onClick={() => { setIsBookmarksOpen(true); setIsMenuOpen(false); }}
                    className="w-full flex items-center justify-between p-4 bg-surface-container hover:bg-surface-variant rounded-2xl transition-colors text-sm font-bold text-on-surface"
                  >
                    <div className="flex items-center gap-4">
                      <Bookmark size={20} className="text-primary" /> Bookmarks
                    </div>
                    <ChevronRight size={16} className="text-outline" />
                  </button>
                </div>
              </div>
            </motion.div>
          </>
        )}
      </AnimatePresence>

      {/* Settings Overlay */}
      <AnimatePresence>
        {isSettingsOpen && (
          <motion.div 
            initial={{ opacity: 0, y: '100%' }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: '100%' }}
            className="fixed inset-0 bg-surface z-50 flex flex-col"
          >
            <div className="p-4 flex items-center justify-between bg-surface border-b border-outline-variant/30">
              <h2 className="font-bold text-xl text-on-surface">Settings</h2>
              <button onClick={() => setIsSettingsOpen(false)} className="p-3 hover:bg-surface-variant rounded-full transition-colors text-on-surface-variant">
                <X size={28} />
              </button>
            </div>
            <div className="flex-1 overflow-y-auto p-4 space-y-6 pb-24">
              <section className="space-y-3">
                <h3 className="text-[10px] font-bold text-primary uppercase tracking-widest">Appearance</h3>
                <div className="md-card overflow-hidden">
                  <div className="p-4 flex items-center justify-between border-b border-outline-variant/30">
                    <div>
                      <p className="text-sm font-bold text-on-surface">Dark Mode</p>
                      <p className="text-[10px] text-outline font-bold">Use dark theme</p>
                    </div>
                    <button
                      onClick={() => setSettings({ ...settings, darkMode: !settings.darkMode })}
                      className={cn(
                        "w-12 h-6 rounded-full transition-all relative",
                        settings.darkMode ? "bg-primary" : "bg-outline-variant"
                      )}
                    >
                      <motion.div
                        animate={{ x: settings.darkMode ? 24 : 4 }}
                        className="absolute top-1 w-4 h-4 bg-white rounded-full shadow-md"
                      />
                    </button>
                  </div>
                  <div className="p-4 flex items-center justify-between border-b border-outline-variant/30">
                    <div>
                      <p className="text-sm font-bold text-on-surface">Compact Mode</p>
                      <p className="text-[10px] text-outline font-bold">Reduce sizes and spacing</p>
                    </div>
                    <button 
                      onClick={() => setSettings({ ...settings, compactMode: !settings.compactMode })}
                      className={cn(
                        "w-12 h-6 rounded-full transition-all relative",
                        settings.compactMode ? "bg-primary" : "bg-outline-variant"
                      )}
                    >
                      <motion.div 
                        animate={{ x: settings.compactMode ? 24 : 4 }}
                        className="absolute top-1 w-4 h-4 bg-white rounded-full shadow-md"
                      />
                    </button>
                  </div>
                  <div className="p-4 flex items-center justify-between border-b border-outline-variant/30">
                    <div>
                      <p className="text-sm font-bold text-on-surface">Font Size</p>
                    </div>
                    <select 
                      value={settings.fontSize}
                      onChange={(e) => setSettings({ ...settings, fontSize: e.target.value as any })}
                      className="text-xs font-bold bg-surface-container rounded-lg px-3 py-1.5 outline-none"
                    >
                      <option value="small">Small</option>
                      <option value="medium">Medium</option>
                      <option value="large">Large</option>
                    </select>
                  </div>
                  <div className="p-4 flex items-center justify-between border-b border-outline-variant/30">
                    <div>
                      <p className="text-sm font-bold text-on-surface">Theme Color</p>
                    </div>
                    <div className="flex gap-2">
                      {['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6'].map(color => (
                        <button 
                          key={color}
                          onClick={() => setSettings({ ...settings, themeColor: color })}
                          className={cn(
                            "w-6 h-6 rounded-full border-2",
                            settings.themeColor === color ? "border-on-surface scale-110" : "border-transparent"
                          )}
                          style={{ backgroundColor: color }}
                        />
                      ))}
                    </div>
                  </div>
                  <div className="p-4 flex items-center justify-between">
                    <div>
                      <p className="text-sm font-bold text-on-surface">User Scripts</p>
                      <p className="text-[10px] text-outline font-bold">Enable custom JS injection</p>
                    </div>
                    <button 
                      onClick={() => setSettings({ ...settings, enableUserScripts: !settings.enableUserScripts })}
                      className={cn(
                        "w-12 h-6 rounded-full transition-all relative",
                        settings.enableUserScripts ? "bg-primary" : "bg-outline-variant"
                      )}
                    >
                      <motion.div 
                        animate={{ x: settings.enableUserScripts ? 24 : 4 }}
                        className="absolute top-1 w-4 h-4 bg-white rounded-full shadow-md"
                      />
                    </button>
                  </div>
                </div>
              </section>

              <section className="space-y-3">
                <h3 className="text-[10px] font-bold text-primary uppercase tracking-widest">Search & Navigation</h3>
                <div className="md-card overflow-hidden">
                  <div className="p-4 border-b border-outline-variant/30">
                    <label className="block text-[10px] font-bold text-outline mb-2 uppercase tracking-wider">Search Engine</label>
                    <div className="grid grid-cols-3 gap-2">
                      {['google', 'bing', 'duckduckgo'].map(engine => (
                        <button 
                          key={engine}
                          onClick={() => setSettings({ ...settings, searchEngine: engine as any })}
                          className={cn(
                            "py-2 text-[10px] font-bold rounded-xl border transition-all",
                            settings.searchEngine === engine ? "bg-primary text-on-primary border-primary" : "bg-surface-container border-outline-variant/30 text-on-surface-variant"
                          )}
                        >
                          {engine.charAt(0).toUpperCase() + engine.slice(1)}
                        </button>
                      ))}
                    </div>
                  </div>
                  <div className="p-4 border-b border-outline-variant/30">
                    <label className="block text-[10px] font-bold text-outline mb-1 uppercase tracking-wider">Homepage</label>
                    <input 
                      type="text" 
                      value={settings.homepageUrl}
                      onChange={(e) => setSettings({ ...settings, homepageUrl: e.target.value })}
                      className="md-input w-full py-2 text-xs"
                    />
                  </div>
                  <div className="p-4">
                    <label className="block text-[10px] font-bold text-outline mb-1 uppercase tracking-wider">Proxy Server URL</label>
                    <input
                      type="text"
                      value={settings.proxyBaseUrl}
                      onChange={(e) => setSettings({ ...settings, proxyBaseUrl: e.target.value })}
                      className="md-input w-full py-2 text-xs"
                      placeholder="https://your-omni-proxy.vercel.app"
                    />
                    <p className="text-[8px] text-outline mt-1 font-bold">Point to your deployed backend for web browsing in APK</p>
                  </div>
                </div>
              </section>

              <section className="space-y-3">
                <h3 className="text-[10px] font-bold text-primary uppercase tracking-widest">Privacy & Security</h3>
                <div className="md-card overflow-hidden">
                  <div className="p-4 flex items-center justify-between border-b border-outline-variant/30">
                    <div className="flex items-center gap-3">
                      <ShieldCheck size={20} className="text-green-600" />
                      <div>
                        <p className="text-sm font-bold text-on-surface">Ad Blocker</p>
                        <p className="text-[10px] text-outline font-bold">Block intrusive ads</p>
                      </div>
                    </div>
                    <button 
                      onClick={() => setSettings({ ...settings, enableAdBlock: !settings.enableAdBlock })}
                      className={cn(
                        "w-12 h-6 rounded-full transition-all relative",
                        settings.enableAdBlock ? "bg-primary" : "bg-outline-variant"
                      )}
                    >
                      <motion.div 
                        animate={{ x: settings.enableAdBlock ? 24 : 4 }}
                        className="absolute top-1 w-4 h-4 bg-white rounded-full shadow-md"
                      />
                    </button>
                  </div>
                  <div className="p-4 flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <EyeOff size={20} className="text-indigo-600" />
                      <div>
                        <p className="text-sm font-bold text-on-surface">Privacy Mode</p>
                        <p className="text-[10px] text-outline font-bold">Don't save history</p>
                      </div>
                    </div>
                    <button 
                      onClick={() => setSettings({ ...settings, privacyMode: !settings.privacyMode })}
                      className={cn(
                        "w-12 h-6 rounded-full transition-all relative",
                        settings.privacyMode ? "bg-primary" : "bg-outline-variant"
                      )}
                    >
                      <motion.div 
                        animate={{ x: settings.privacyMode ? 24 : 4 }}
                        className="absolute top-1 w-4 h-4 bg-white rounded-full shadow-md"
                      />
                    </button>
                  </div>
                </div>
              </section>

              <section className="space-y-3">
                <h3 className="text-[10px] font-bold text-primary uppercase tracking-widest">Downloads</h3>
                <div className="md-card overflow-hidden">
                  <div className="p-4">
                    <label className="block text-[10px] font-bold text-outline mb-1 uppercase tracking-wider">Download Folder</label>
                    <input 
                      type="text" 
                      value={settings.downloadFolder}
                      onChange={(e) => setSettings({ ...settings, downloadFolder: e.target.value })}
                      className="md-input w-full py-2 text-xs"
                    />
                  </div>
                </div>
              </section>

              <section className="space-y-3">
                <h3 className="text-[10px] font-bold text-primary uppercase tracking-widest">Export Tools</h3>
                <div className="grid grid-cols-3 gap-3">
                  <button onClick={saveAsPdf} className="flex flex-col items-center gap-2 p-3 md-card hover:bg-primary/5 transition-all active:scale-95">
                    <div className="w-10 h-10 bg-red-100 text-red-700 rounded-xl flex items-center justify-center shadow-inner">
                      <FileText size={20} />
                    </div>
                    <span className="text-[10px] font-bold text-on-surface">PDF</span>
                  </button>
                  <button onClick={saveAsHtml} className="flex flex-col items-center gap-2 p-3 md-card hover:bg-primary/5 transition-all active:scale-95">
                    <div className="w-10 h-10 bg-blue-100 text-blue-700 rounded-xl flex items-center justify-center shadow-inner">
                      <Globe size={20} />
                    </div>
                    <span className="text-[10px] font-bold text-on-surface">HTML</span>
                  </button>
                  <button onClick={saveAsMarkdown} className="flex flex-col items-center gap-2 p-3 md-card hover:bg-primary/5 transition-all active:scale-95">
                    <div className="w-10 h-10 bg-green-100 text-green-700 rounded-xl flex items-center justify-center shadow-inner">
                      <FileText size={20} />
                    </div>
                    <span className="text-[10px] font-bold text-on-surface">MD</span>
                  </button>
                </div>
              </section>

              <section className="space-y-3">
                <h3 className="text-[10px] font-bold text-primary uppercase tracking-widest">Data</h3>
                <div className="grid grid-cols-2 gap-3 pb-24">
                  <button 
                    onClick={exportData}
                    className="md-button-tonal flex-1 py-3 text-xs"
                  >
                    <Download size={16} /> Export
                  </button>
                  <button 
                    onClick={() => fileInputRef.current?.click()}
                    className="md-button-tonal flex-1 py-3 text-xs"
                  >
                    <Plus size={16} /> Import
                  </button>
                  <input 
                    type="file" 
                    ref={fileInputRef} 
                    onChange={importData} 
                    accept=".json" 
                    className="hidden" 
                  />
                </div>
              </section>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Context Menu */}
      <AnimatePresence>
        {isContextMenuOpen && (
          <motion.div 
            initial={{ opacity: 0, scale: 0.9, y: -10 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.9, y: -10 }}
            style={{ top: contextMenuPos.y, left: contextMenuPos.x }}
            className="fixed z-[100] w-56 bg-surface rounded-[24px] shadow-2xl border border-outline-variant/30 overflow-hidden p-2"
          >
            <div className="grid grid-cols-3 gap-1 mb-2">
              <button onClick={() => { window.history.back(); setIsContextMenuOpen(false); }} className="flex flex-col items-center gap-1 p-2 hover:bg-surface-variant rounded-xl transition-colors text-[10px] font-bold text-on-surface">
                <ChevronLeft size={20} className="text-primary" /> Back
              </button>
              <button onClick={() => { window.history.forward(); setIsContextMenuOpen(false); }} className="flex flex-col items-center gap-1 p-2 hover:bg-surface-variant rounded-xl transition-colors text-[10px] font-bold text-on-surface">
                <ChevronRight size={20} className="text-primary" /> Forward
              </button>
              <button onClick={() => { navigate(activeTab.url); setIsContextMenuOpen(false); }} className="flex flex-col items-center gap-1 p-2 hover:bg-surface-variant rounded-xl transition-colors text-[10px] font-bold text-on-surface">
                <RotateCcw size={20} className="text-primary" /> Reload
              </button>
            </div>
            <div className="h-px bg-outline-variant/30 mb-2 mx-2" />
            
            <div className="space-y-0.5">
              <button onClick={() => { navigator.clipboard.writeText(activeTab.url); setIsContextMenuOpen(false); }} className="w-full flex items-center gap-3 px-4 py-2.5 hover:bg-surface-variant rounded-xl transition-colors text-sm font-bold text-on-surface">
                <Copy size={18} className="text-primary" /> Copy URL
              </button>
              <button onClick={() => { toggleBookmark(); setIsContextMenuOpen(false); }} className="w-full flex items-center gap-3 px-4 py-2.5 hover:bg-surface-variant rounded-xl transition-colors text-sm font-bold text-on-surface">
                <Bookmark size={18} className="text-primary" /> {bookmarks.find(b => b.url === activeTab.url) ? 'Remove Bookmark' : 'Add Bookmark'}
              </button>
              <button onClick={() => { translatePage(); setIsContextMenuOpen(false); }} className="w-full flex items-center gap-3 px-4 py-2.5 hover:bg-surface-variant rounded-xl transition-colors text-sm font-bold text-on-surface">
                <Languages size={18} className="text-primary" /> Translate Page
              </button>
              <button onClick={() => { addTab(); setIsContextMenuOpen(false); }} className="w-full flex items-center gap-3 px-4 py-2.5 hover:bg-surface-variant rounded-xl transition-colors text-sm font-bold text-on-surface">
                <Plus size={18} className="text-primary" /> New Tab
              </button>
              <button onClick={() => { setIsFindOnPageOpen(true); setIsContextMenuOpen(false); }} className="w-full flex items-center gap-3 px-4 py-2.5 hover:bg-surface-variant rounded-xl transition-colors text-sm font-bold text-on-surface">
                <Search size={18} className="text-primary" /> Find on Page
              </button>
              <button onClick={() => { if (navigator.share) navigator.share({ url: activeTab.url }); setIsContextMenuOpen(false); }} className="w-full flex items-center gap-3 px-4 py-2.5 hover:bg-surface-variant rounded-xl transition-colors text-sm font-bold text-on-surface">
                <Share2 size={18} className="text-primary" /> Share Page
              </button>
            </div>

            <div className="h-px bg-outline-variant/30 my-1.5 mx-2" />
            
            <div className="space-y-0.5">
              <button onClick={() => { setPageToolView('reader'); setIsContextMenuOpen(false); }} className="w-full flex items-center gap-3 px-4 py-2.5 hover:bg-surface-variant rounded-xl transition-colors text-sm font-bold text-on-surface">
                <FileText size={18} className="text-primary" /> Reader Mode
              </button>
              <button onClick={() => { setPageToolView('source'); setIsContextMenuOpen(false); }} className="w-full flex items-center gap-3 px-4 py-2.5 hover:bg-surface-variant rounded-xl transition-colors text-sm font-bold text-on-surface">
                <SearchCode size={18} className="text-primary" /> View Source
              </button>
              <button onClick={() => { setIsSettingsOpen(true); setIsContextMenuOpen(false); }} className="w-full flex items-center gap-3 px-4 py-2.5 hover:bg-surface-variant rounded-xl transition-colors text-sm font-bold text-on-surface">
                <Settings size={18} className="text-primary" /> Settings
              </button>
            </div>

            <div className="h-px bg-outline-variant/30 my-1.5 mx-2" />
            
            <div className="grid grid-cols-2 gap-1 mt-1">
              <button onClick={() => { setIsDownloadsOpen(true); setIsContextMenuOpen(false); }} className="flex flex-col items-center gap-1 p-2 hover:bg-surface-variant rounded-xl transition-colors text-[9px] font-bold text-on-surface">
                <Download size={18} className="text-primary" /> Files
              </button>
              <button onClick={() => { setIsHistoryOpen(true); setIsContextMenuOpen(false); }} className="flex flex-col items-center gap-1 p-2 hover:bg-surface-variant rounded-xl transition-colors text-[9px] font-bold text-on-surface">
                <History size={18} className="text-primary" /> History
              </button>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
      {/* Long Press URL List Overlay */}
      <AnimatePresence>
        {longPressedLink && (
          <>
            <motion.div 
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setLongPressedLink(null)}
              className="fixed inset-0 bg-black/40 z-[100] backdrop-blur-[2px]"
            />
            <motion.div 
              initial={{ scale: 0.9, opacity: 0, y: 20 }}
              animate={{ scale: 1, opacity: 1, y: 0 }}
              exit={{ scale: 0.9, opacity: 0, y: 20 }}
              className="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[90%] max-w-md bg-surface rounded-[32px] shadow-2xl z-[101] overflow-hidden"
            >
              <div className="p-6 border-b border-outline-variant/30">
                <h3 className="font-bold text-lg text-on-surface truncate">{longPressedLink.title}</h3>
                <p className="text-xs text-on-surface-variant opacity-60 truncate">Select a URL to open</p>
              </div>
              <div className="p-4 space-y-2 max-h-[300px] overflow-y-auto">
                {longPressedLink.urls.map((u, i) => {
                  const url = typeof u === 'string' ? u : u.url;
                  const label = typeof u === 'string' ? u : u.label;
                  return (
                    <button
                      key={i}
                      onClick={() => { navigate(url); setLongPressedLink(null); setIsToolboxOpen(false); }}
                      className="w-full flex items-center gap-4 p-4 hover:bg-surface-variant rounded-2xl transition-colors text-sm font-bold text-on-surface text-left"
                    >
                      <div className="w-10 h-10 bg-primary/10 text-primary rounded-full flex items-center justify-center shrink-0">
                        <ExternalLink size={20} />
                      </div>
                      <div className="min-w-0">
                        <p className="text-sm font-bold text-on-surface truncate">{label}</p>
                        <p className="text-[10px] text-on-surface-variant opacity-60 truncate">{url}</p>
                      </div>
                    </button>
                  );
                })}
              </div>
              <div className="p-4 bg-surface-container-low">
                <button 
                  onClick={() => setLongPressedLink(null)}
                  className="w-full py-3 text-sm font-bold text-primary uppercase tracking-widest"
                >
                  Close
                </button>
              </div>
            </motion.div>
          </>
        )}
      </AnimatePresence>

      {/* Media Grabber Floating Notification */}
      <AnimatePresence>
        {showMediaGrabber && detectedMedia.length > 0 && !isMediaSnifferOpen && (
          <motion.div
            initial={{ opacity: 0, scale: 0.8, y: 100 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.8, y: 100 }}
            className="fixed bottom-24 right-6 z-[40]"
          >
            <button
              onClick={() => {
                setIsMediaSnifferOpen(true);
                setShowMediaGrabber(false);
              }}
              className="md-fab relative group"
            >
              <Video size={28} />
              <span className="absolute -top-1 -right-1 bg-red-500 text-white text-[10px] font-bold w-5 h-5 rounded-full flex items-center justify-center border-2 border-surface shadow-sm">
                {detectedMedia.length}
              </span>
              <div className="absolute right-full mr-4 top-1/2 -translate-y-1/2 bg-surface-container-highest px-3 py-1.5 rounded-xl shadow-lg border border-outline-variant/30 opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap pointer-events-none">
                <span className="text-[10px] font-bold text-on-surface">Media Found!</span>
              </div>
            </button>
            <button
              onClick={() => setShowMediaGrabber(false)}
              className="absolute -top-2 -right-2 w-6 h-6 bg-surface-container-highest text-on-surface-variant rounded-full flex items-center justify-center shadow-md border border-outline-variant/30"
            >
              <X size={12} />
            </button>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

