import { useState, useEffect, ReactNode } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { PhoneOff, Mic, MicOff, Volume2, Grid, UserPlus, Video, Disc, Radio } from 'lucide-react';
import { Theme, Contact, AppSettings, CallControlButton } from '../types';

interface InCallViewProps {
  call: Contact | { name: string; number: string };
  theme: Theme;
  onEnd: () => void;
  settings: AppSettings;
}

export default function InCallView({ call, theme, onEnd, settings }: InCallViewProps) {
  const [timer, setTimer] = useState(0);
  const [isMuted, setIsMuted] = useState(false);
  const [isSpeaker, setIsSpeaker] = useState(false);
  const [isRecording, setIsRecording] = useState(false);
  const [identifiedName, setIdentifiedName] = useState<string | null>(null);
  const [isIdentifying, setIsIdentifying] = useState(false);

  useEffect(() => {
    const interval = setInterval(() => {
      setTimer(t => t + 1);
    }, 1000);

    if (settings.callNameAnnouncement && 'speechSynthesis' in window) {
      const utterance = new SpeechSynthesisUtterance(`Incoming call from ${call.name}`);
      window.speechSynthesis.speak(utterance);
    }

    if (call.name === 'Unknown') {
      setIsIdentifying(true);
      const timer = setTimeout(() => {
        setIsIdentifying(false);
        setIdentifiedName('Nature Enthusiast'); // Mock identification
      }, 3000);
      return () => {
        clearTimeout(timer);
        clearInterval(interval);
        if ('speechSynthesis' in window) {
          window.speechSynthesis.cancel();
        }
      };
    }

    return () => {
      clearInterval(interval);
      if ('speechSynthesis' in window) {
        window.speechSynthesis.cancel();
      }
    };
  }, []);

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  const renderControl = (control: CallControlButton) => {
    switch (control) {
      case 'mute':
        return <ControlButton active={isMuted} onClick={() => setIsMuted(!isMuted)} icon={isMuted ? <MicOff /> : <Mic />} label="Mute" />;
      case 'keypad':
        return <ControlButton icon={<Grid />} label="Keypad" />;
      case 'speaker':
        return <ControlButton active={isSpeaker} onClick={() => setIsSpeaker(!isSpeaker)} icon={<Volume2 />} label="Speaker" />;
      case 'add-call':
        return <ControlButton icon={<UserPlus />} label="Add Call" />;
      case 'video':
        return <ControlButton icon={<Video />} label="Video" />;
      case 'record':
        return <ControlButton active={isRecording} onClick={() => setIsRecording(!isRecording)} icon={<Disc className={isRecording ? 'animate-pulse text-red-500' : ''} />} label={isRecording ? "Stop Rec" : "Record"} />;
      default: return null;
    }
  };

  const isMinimalist = settings.callerScreenStyle === 'minimalist';

  return (
    <div className={`w-full h-full relative flex flex-col items-center justify-between p-12 overflow-hidden ${
      isMinimalist ? 'bg-zinc-950' : ''
    }`}>
      {/* Background with Theme-specific animation */}
      {!isMinimalist && (
        <>
          <div
            className="absolute inset-0 z-0 bg-cover bg-center"
            style={{ backgroundImage: `url(${theme.image})` }}
          />
          <div className="absolute inset-0 z-0 bg-black/60 backdrop-blur-md" />

          {/* Ambient Pulsing Glow */}
          <motion.div
            animate={{
              scale: [1, 1.2, 1],
              opacity: [0.1, 0.2, 0.1]
            }}
            transition={{ duration: 4, repeat: Infinity }}
            className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[150%] aspect-square rounded-full blur-[100px]"
            style={{ backgroundColor: theme.colors.accent }}
          />
        </>
      )}

      <div className="relative z-10 flex flex-col items-center w-full">
        <span className={`text-xs font-bold tracking-[0.3em] uppercase mb-8 ${isMinimalist ? 'text-zinc-500' : 'opacity-60'}`} style={{ color: !isMinimalist ? theme.colors.accent : '' }}>
          {isRecording ? '• Recording Ongoing' : 'Voice Call'}
        </span>
        
        <div className="relative mb-8">
          <motion.div 
            animate={{ scale: [1, 1.05, 1] }}
            transition={{ duration: 3, repeat: Infinity }}
            className={`rounded-full p-1 relative z-10 ${
              isMinimalist ? 'w-24 h-24 border-2 border-zinc-800' : 'w-32 h-32 border-4 border-white/20'
            }`}
          >
            {'image' in call ? (
              <img src={call.image} alt={call.name} className="w-full h-full object-cover rounded-full" referrerPolicy="no-referrer" />
            ) : (
              <div className={`w-full h-full rounded-full flex items-center justify-center ${isMinimalist ? 'bg-zinc-900' : 'bg-white/10'}`}>
                <Radio size={isMinimalist ? 32 : 48} className={isMinimalist ? 'text-zinc-700' : 'opacity-40'} />
              </div>
            )}
          </motion.div>
          
          {/* Audio Visualization Waves */}
          {!isMinimalist && (
            <div className="absolute inset-0 flex items-center justify-center gap-1 opacity-40">
              {[1, 2, 3, 4, 5].map(i => (
                <motion.div
                  key={i}
                  animate={{ height: [20, 60, 20] }}
                  transition={{ duration: 0.8, repeat: Infinity, delay: i * 0.1 }}
                  className="w-1 bg-white rounded-full"
                />
              ))}
            </div>
          )}
        </div>

        <h2 className={`text-4xl font-display font-bold text-center mb-2 ${isMinimalist ? 'text-white' : ''}`} style={{ color: !isMinimalist ? theme.colors.text : '' }}>
          {identifiedName || call.name}
        </h2>
        {isIdentifying && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="text-sm font-medium text-green-400 mb-2 animate-pulse"
          >
            Identifying caller...
          </motion.div>
        )}
        {identifiedName && (
          <div className="text-sm font-medium text-green-400 mb-2">
            Identified via NatureID
          </div>
        )}
        <p className={`text-lg font-medium mb-4 ${isMinimalist ? 'text-zinc-500' : 'opacity-60'}`}>{call.number}</p>
        <span className={`text-xl font-mono font-medium tracking-widest ${isMinimalist ? 'text-zinc-400' : 'opacity-80'}`}>{formatTime(timer)}</span>
      </div>

      <div className="relative z-10 w-full max-w-sm">
        <div className={`grid gap-6 mb-12 ${settings.inCallControls.length > 3 ? 'grid-cols-3' : 'grid-cols-2'}`}>
          {settings.inCallControls.map(c => <div key={c}>{renderControl(c)}</div>)}
        </div>

        <div className="flex justify-center">
          <button
            onClick={onEnd}
            className={`rounded-full flex items-center justify-center active:scale-90 transition-transform ${
              isMinimalist ? 'w-16 h-16 bg-red-600' : 'w-20 h-20 bg-red-500 shadow-2xl shadow-red-900/40'
            }`}
          >
            <PhoneOff size={isMinimalist ? 28 : 32} className="text-white" />
          </button>
        </div>
      </div>
    </div>
  );
}

function ControlButton({ 
  icon, 
  label, 
  onClick, 
  active = false 
}: { 
  icon: ReactNode; 
  label: string; 
  onClick?: () => void;
  active?: boolean;
}) {
  return (
    <button 
      onClick={onClick}
      className="flex flex-col items-center gap-2 group"
    >
      <div className={`w-16 h-16 rounded-full glass-panel flex items-center justify-center transition-all ${
        active ? 'bg-white text-black border-none' : 'bg-white/5 hover:bg-white/10'
      }`}>
        {icon}
      </div>
      <span className="text-[10px] font-bold tracking-widest uppercase opacity-40 group-hover:opacity-100 transition-opacity">
        {label}
      </span>
    </button>
  );
}
