import { AppSettings } from '../types';
import { useLocalStorage } from './useLocalStorage';

const DEFAULT_SETTINGS: AppSettings = {
  theme: 'artistic',
  stealthRecording: false,
  callNameAnnouncement: false,
  inCallControls: ['mute', 'keypad', 'speaker', 'add-call', 'video', 'record'],
  callerScreenStyle: 'nature',
  googleDriveLinked: false,
};

export function useSettings() {
  const [settings, setSettings] = useLocalStorage<AppSettings>('settings', DEFAULT_SETTINGS);

  const updateSettings = async (newSettings: Partial<AppSettings>) => {
    setSettings(prev => ({ ...prev, ...newSettings }));
  };

  return { settings, updateSettings, loading: false };
}
