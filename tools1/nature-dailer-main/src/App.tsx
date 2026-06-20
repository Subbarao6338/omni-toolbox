/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useMemo, useRef, ReactNode } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Phone, Users, History, Settings, Leaf, Download, Upload, BarChart2, Volume2, Layout, Sliders, Cloud } from 'lucide-react';
import { THEMES, MOCK_CONTACTS } from './constants';
import { Theme, Contact, CallLog, ThemeType, CallControlButton } from './types';
import { exportAppData, importAppData, linkGoogleDrive, syncToGoogleDrive } from './hooks/useDataManagement';
import { calculateCallStats } from './hooks/useCallStats';
import { useCallManager } from './hooks/useCallManager';
import { useContacts } from './hooks/useContacts';
import { useSettings } from './hooks/useSettings';
import { trackEvent } from './services/analytics';
import Dialpad from './components/Dialpad';
import ContactsList from './components/ContactsList';
import CallHistory from './components/CallHistory';
import CallStatsView from './components/CallStatsView';
import InCallView from './components/InCallView';
import ThemePicker from './components/ThemePicker';

type ViewMode = 'dialer' | 'contacts' | 'history' | 'settings' | 'stats';

export default function App() {
  const { ongoingCall, callLogs, setCallLogs, blockedNumbers, startCall, endCall, blockNumber } = useCallManager();
  const { contacts } = useContacts();
  const { settings, updateSettings } = useSettings();

  const activeThemeId = settings.theme as ThemeType;
  const setActiveThemeId = (id: ThemeType) => updateSettings({ theme: id });
  
  const [view, setView] = useState<ViewMode>('dialer');
  const [isThemePickerOpen, setIsThemePickerOpen] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleStartCall = (number: string) => startCall(number, contacts);
  const handleBlock = (number: string) => blockNumber(number);
  const handleEndCall = () => endCall();

  const handleImport = async (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files?.[0]) {
      const data = await importAppData(e.target.files[0]);
      setActiveThemeId(data.theme);
      setCallLogs(data.calls);
    }
  };

  const handleExport = () => {
    exportAppData(activeThemeId, callLogs);
  };

  const stats = useMemo(() => calculateCallStats(callLogs), [callLogs]);

  const activeTheme = useMemo(() => 
    THEMES.find(t => t.id === activeThemeId) || THEMES[0], 
  [activeThemeId]);
  
  const isArtistic = activeThemeId === 'artistic';

  return (
    <div 
      className="relative w-full h-full flex flex-col transition-colors duration-1000 overflow-hidden"
      style={{ backgroundColor: activeTheme.colors.bg }}
    >
      <div 
        className="absolute inset-0 z-0 bg-cover bg-center transition-opacity duration-1000"
        style={{ 
          backgroundImage: `url(${activeTheme.image})`, 
          opacity: isArtistic ? 0.05 : 0.4 
        }}
      />
      {!isArtistic && <div className="absolute inset-0 z-0 bg-gradient-to-t from-black via-transparent to-black/20" />}

      <main className="relative z-10 flex-1 flex flex-col overflow-hidden">
        <div className={`flex-1 flex flex-col overflow-hidden mx-auto w-full transition-all duration-500 ${
          isArtistic ? 'max-w-md bg-white my-4 rounded-[48px] shadow-[0_40px_100px_rgba(45,64,49,0.15)] overflow-hidden border-8 border-white' : ''
        }`}>
          <AnimatePresence mode="wait">
            {ongoingCall ? (
              <motion.div
                key="incall"
                initial={{ y: '100%' }}
                animate={{ y: 0 }}
                exit={{ y: '100%' }}
                transition={{ type: 'spring', damping: 25, stiffness: 200 }}
                className="absolute inset-0 z-50"
              >
                <InCallView 
                  call={ongoingCall} 
                  theme={activeTheme} 
                  onEnd={handleEndCall}
                  settings={settings}
                />
              </motion.div>
            ) : (
              <motion.div
                key={view}
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                className="flex-1 flex flex-col overflow-hidden"
              >
                {view === 'dialer' && <Dialpad theme={activeTheme} onCall={handleStartCall} />}
                {view === 'contacts' && <ContactsList theme={activeTheme} contacts={contacts} onCall={handleStartCall} />}
                {view === 'history' && <CallHistory theme={activeTheme} calls={callLogs.filter(log => !blockedNumbers.includes(log.number))} onCall={handleStartCall} onBlock={handleBlock} />}
                {view === 'stats' && <CallStatsView theme={activeTheme} stats={stats} />}
                {view === 'settings' && (
                  <div className="p-6">
                    <h2 className={`text-3xl font-bold mb-8 ${isArtistic ? 'font-serif text-[#2D4031]' : 'font-display'}`} style={{ color: !isArtistic ? activeTheme.colors.text : '' }}>
                      {isArtistic ? 'Nature Settings' : 'Settings'}
                    </h2>
                    <div className="space-y-4">
                      <div className="grid grid-cols-2 gap-4">
                        <button onClick={handleExport} className={`w-full p-4 rounded-2xl flex items-center justify-center gap-2 ${isArtistic ? 'bg-[#F2F4F0]' : 'glass-panel'}`}>
                          <Download size={18} /> Export
                        </button>
                        <button onClick={() => fileInputRef.current?.click()} className={`w-full p-4 rounded-2xl flex items-center justify-center gap-2 ${isArtistic ? 'bg-[#F2F4F0]' : 'glass-panel'}`}>
                          <Upload size={18} /> Import
                        </button>
                        <input type="file" ref={fileInputRef} onChange={handleImport} accept=".json" className="hidden" />
                      </div>

                      <button 
                        onClick={() => setIsThemePickerOpen(true)}
                        className={`w-full p-4 rounded-2xl flex items-center justify-between ${
                          isArtistic ? 'bg-[#F2F4F0]' : 'glass-panel'
                        }`}
                      >
                        <div className="flex items-center gap-3">
                          <Leaf className="w-5 h-5" style={{ color: activeTheme.colors.accent }} />
                          <span className={`font-medium ${isArtistic ? 'text-[#1A1C19]' : ''}`}>Inspiration Palette</span>
                        </div>
                        <span className="text-sm opacity-60">{activeTheme.name}</span>
                      </button>
                      
                      <div className={`p-4 rounded-2xl ${isArtistic ? 'bg-[#F2F4F0]' : 'glass-panel'}`}>
                        <div className="flex items-center gap-3 mb-2">
                          <Phone className={`w-5 h-5 ${isArtistic ? 'text-[#2D4031]' : 'text-red-400'}`} />
                          <span className={`font-medium ${isArtistic ? 'text-[#1A1C19]' : ''}`}>Stealth Recording</span>
                        </div>
                        <p className={`text-xs opacity-60 mb-3 ${isArtistic ? 'text-[#7A8D6E]' : ''}`}>Record without announcement.</p>
                        <div className="flex items-center justify-between">
                          <span className={`text-sm ${isArtistic ? 'font-medium' : ''}`}>Enabled</span>
                          <button
                              onClick={() => updateSettings({ stealthRecording: !settings.stealthRecording })}
                              className={`w-11 h-6 rounded-full relative p-1 cursor-pointer transition-colors ${
                                  isArtistic ? (settings.stealthRecording ? 'bg-[#7A8D6E]' : 'bg-gray-300') : (settings.stealthRecording ? 'bg-green-500/40' : 'bg-gray-500/40')
                              }`}>
                              <div className={`w-4 h-4 bg-white rounded-full transition-transform ${settings.stealthRecording ? 'ml-auto' : 'ml-0'}`} />
                          </button>
                        </div>
                      </div>

                      <div className={`p-4 rounded-2xl ${isArtistic ? 'bg-[#F2F4F0]' : 'glass-panel'}`}>
                        <div className="flex items-center gap-3 mb-2">
                          <Volume2 className={`w-5 h-5 ${isArtistic ? 'text-[#2D4031]' : 'text-blue-400'}`} />
                          <span className={`font-medium ${isArtistic ? 'text-[#1A1C19]' : ''}`}>Name Announcement</span>
                        </div>
                        <div className="flex items-center justify-between">
                          <span className={`text-sm ${isArtistic ? 'font-medium' : ''}`}>Speak caller name</span>
                          <button
                              onClick={() => updateSettings({ callNameAnnouncement: !settings.callNameAnnouncement })}
                              className={`w-11 h-6 rounded-full relative p-1 cursor-pointer transition-colors ${
                                  isArtistic ? (settings.callNameAnnouncement ? 'bg-[#7A8D6E]' : 'bg-gray-300') : (settings.callNameAnnouncement ? 'bg-blue-500/40' : 'bg-gray-500/40')
                              }`}>
                              <div className={`w-4 h-4 bg-white rounded-full transition-transform ${settings.callNameAnnouncement ? 'ml-auto' : 'ml-0'}`} />
                          </button>
                        </div>
                      </div>

                      <div className={`p-4 rounded-2xl ${isArtistic ? 'bg-[#F2F4F0]' : 'glass-panel'}`}>
                        <div className="flex items-center gap-3 mb-4">
                          <Layout className={`w-5 h-5 ${isArtistic ? 'text-[#2D4031]' : 'text-purple-400'}`} />
                          <span className={`font-medium ${isArtistic ? 'text-[#1A1C19]' : ''}`}>In-Call Style</span>
                        </div>
                        <div className="grid grid-cols-2 gap-2">
                          {(['nature', 'minimalist'] as const).map(style => (
                            <button
                              key={style}
                              onClick={() => updateSettings({ callerScreenStyle: style })}
                              className={`py-2 rounded-xl text-xs font-bold uppercase tracking-wider transition-all ${
                                settings.callerScreenStyle === style
                                  ? (isArtistic ? 'bg-[#2D4031] text-white' : 'bg-white/20 text-white')
                                  : (isArtistic ? 'bg-white/50 text-[#2D4031]/40' : 'bg-white/5 text-white/40')
                              }`}
                            >
                              {style}
                            </button>
                          ))}
                        </div>
                      </div>

                      <div className={`p-4 rounded-2xl ${isArtistic ? 'bg-[#F2F4F0]' : 'glass-panel'}`}>
                        <div className="flex items-center gap-3 mb-4">
                          <Sliders className={`w-5 h-5 ${isArtistic ? 'text-[#2D4031]' : 'text-orange-400'}`} />
                          <span className={`font-medium ${isArtistic ? 'text-[#1A1C19]' : ''}`}>In-Call Controls</span>
                        </div>
                        <div className="grid grid-cols-3 gap-2">
                          {(['mute', 'keypad', 'speaker', 'add-call', 'video', 'record'] as CallControlButton[]).map(control => {
                            const isActive = settings.inCallControls.includes(control);
                            return (
                              <button
                                key={control}
                                onClick={() => {
                                  const newControls = isActive
                                    ? settings.inCallControls.filter(c => c !== control)
                                    : [...settings.inCallControls, control];
                                  updateSettings({ inCallControls: newControls });
                                }}
                                className={`py-2 px-1 rounded-xl text-[10px] font-bold uppercase tracking-tight transition-all ${
                                  isActive
                                    ? (isArtistic ? 'bg-[#7A8D6E] text-white' : 'bg-green-500/20 text-green-400')
                                    : (isArtistic ? 'bg-white/50 text-[#2D4031]/40' : 'bg-white/5 text-white/40')
                                }`}
                              >
                                {control.replace('-', ' ')}
                              </button>
                            );
                          })}
                        </div>
                      </div>

                      <div className={`p-4 rounded-2xl ${isArtistic ? 'bg-[#F2F4F0]' : 'glass-panel'}`}>
                        <div className="flex items-center gap-3 mb-4">
                          <Cloud className={`w-5 h-5 ${isArtistic ? 'text-[#2D4031]' : 'text-cyan-400'}`} />
                          <span className={`font-medium ${isArtistic ? 'text-[#1A1C19]' : ''}`}>Cloud Backup</span>
                        </div>
                        {!settings.googleDriveLinked ? (
                          <button
                            onClick={async () => {
                              const success = await linkGoogleDrive();
                              if (success) updateSettings({ googleDriveLinked: true });
                            }}
                            className={`w-full py-3 rounded-xl text-sm font-bold flex items-center justify-center gap-2 ${
                              isArtistic ? 'bg-[#2D4031] text-white' : 'bg-blue-600 text-white'
                            }`}
                          >
                            Link Google Drive
                          </button>
                        ) : (
                          <div className="space-y-2">
                            <div className="text-xs text-green-500 font-medium mb-2 flex items-center gap-1">
                              <div className="w-2 h-2 rounded-full bg-green-500 animate-pulse" /> Connected
                            </div>
                            <button
                              onClick={() => syncToGoogleDrive(activeThemeId, callLogs)}
                              className={`w-full py-3 rounded-xl text-sm font-bold ${
                                isArtistic ? 'bg-[#F2F4F0] border border-[#2D4031]' : 'bg-white/10'
                              }`}
                            >
                              Sync Now
                            </button>
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                )}
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </main>

      {!ongoingCall && (
        <nav className={`relative z-10 px-6 pb-8 pt-4 flex items-center justify-around transition-all duration-500 ${
          isArtistic ? 'bg-white border-t border-[#F2F4F0] h-[88px] max-w-md mx-auto w-full rounded-t-[32px]' : 'glass-panel border-t-0 bg-black/20 backdrop-blur-3xl'
        }`}>
          <NavItem active={view === 'history'} onClick={() => setView('history')} icon={<History size={24} />} label="Recent" theme={activeTheme} />
          <NavItem active={view === 'stats'} onClick={() => setView('stats')} icon={<BarChart2 size={24} />} label="Stats" theme={activeTheme} />
          <NavItem active={view === 'contacts'} onClick={() => setView('contacts')} icon={<Users size={24} />} label="Contacts" theme={activeTheme} />
          <NavItem active={view === 'dialer'} onClick={() => setView('dialer')} icon={<Phone size={24} />} label="Keypad" theme={activeTheme} />
          <NavItem active={view === 'settings'} onClick={() => setView('settings')} icon={<Settings size={24} />} label="Stars" theme={activeTheme} />
        </nav>
      )}

      <AnimatePresence>
        {isThemePickerOpen && (
          <ThemePicker 
            currentTheme={activeThemeId}
            onSelect={(id) => {
              setActiveThemeId(id);
              setIsThemePickerOpen(false);
            }}
            onClose={() => setIsThemePickerOpen(false)}
          />
        )}
      </AnimatePresence>
    </div>
  );
}

function NavItem({ active, onClick, icon, label, theme }: { active: boolean, onClick: () => void, icon: ReactNode, label: string, theme: Theme }) {
  const isArtistic = theme.id === 'artistic';
  return (
    <button onClick={onClick} className="flex flex-col items-center gap-1 transition-all duration-300 relative" style={{ color: active ? (isArtistic ? '#4F6D52' : theme.colors.accent) : (isArtistic ? 'rgba(45, 64, 49, 0.4)' : 'rgba(255,255,255,0.4)'), transform: active ? 'scale(1.1)' : 'scale(1)' }}>
      <div className={`p-2 rounded-2xl transition-colors ${active && !isArtistic ? 'bg-white/10' : ''}`}>{icon}</div>
      <span className="text-[10px] font-bold tracking-wider uppercase opacity-80">{label}</span>
      {active && isArtistic && <motion.div layoutId="nav-dot" className="absolute -bottom-4 w-5 h-1 bg-[#4F6D52] rounded-full" />}
    </button>
  );
}
