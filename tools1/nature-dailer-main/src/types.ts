export type ThemeType = 'forest' | 'ocean' | 'desert' | 'frost' | 'night' | 'artistic';
export type CallerScreenStyle = 'nature' | 'minimalist';
export type CallControlButton = 'mute' | 'keypad' | 'speaker' | 'add-call' | 'video' | 'record';

export interface Theme {
  id: ThemeType;
  name: string;
  colors: {
    bg: string;
    text: string;
    accent: string;
    surface: string;
    dial: string;
  };
  image: string;
}

export interface Contact {
  id: string;
  name: string;
  number: string;
  image: string;
  category: string;
}

export interface CallLog {
  id: string;
  name: string;
  number: string;
  time: string;
  type: 'incoming' | 'outgoing' | 'missed';
  duration?: string;
}

export interface CallNote {
  id: string;
  callLogId: string;
  text: string;
  timestamp: string;
}

export interface AppSettings {
  theme: ThemeType;
  stealthRecording: boolean;
  callNameAnnouncement: boolean;
  inCallControls: CallControlButton[];
  callerScreenStyle: CallerScreenStyle;
  googleDriveLinked: boolean;
}
