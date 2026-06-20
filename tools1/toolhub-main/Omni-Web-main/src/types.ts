export interface Tab {
  id: string;
  url: string;
  title: string;
  loading: boolean;
}

export interface DownloadItem {
  id: string;
  url: string;
  filename: string;
  fileType: 'video' | 'audio' | 'image' | 'other';
  progress: number;
  status: 'downloading' | 'completed' | 'failed' | 'paused' | 'queued';
  size?: string;
  totalSize?: number;
  downloadedSize?: number;
  timestamp: number;
  error?: string;
  speed?: number;
  eta?: number;
}

export interface MediaItem {
  id: string;
  src: string;
  type: 'video' | 'audio' | 'image';
  title: string;
  selected?: boolean;
}

export interface HistoryItem {
  id: string;
  url: string;
  title: string;
  timestamp: number;
}

export interface BookmarkItem {
  id: string;
  url: string;
  title: string;
  timestamp: number;
}

export interface UserScript {
  id: string;
  name: string;
  code: string;
  enabled: boolean;
}

export interface Bookmarklet {
  id: string;
  name: string;
  code: string;
}

export interface AppSettings {
  homepageUrl: string;
  downloadFolder: string;
  themeColor: string;
  enableUserScripts: boolean;
  searchEngine: 'google' | 'bing' | 'duckduckgo' | 'custom';
  customSearchUrl?: string;
  fontSize: 'small' | 'medium' | 'large';
  showBottomNav: boolean;
  enableAdBlock: boolean;
  privacyMode: boolean;
  compactMode: boolean;
  desktopMode: boolean;
  darkMode?: boolean;
  proxyBaseUrl?: string;
}
