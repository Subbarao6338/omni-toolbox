import { motion } from 'motion/react';
import { X, Check } from 'lucide-react';
import { THEMES } from '../constants';
import { ThemeType } from '../types';

interface ThemePickerProps {
  currentTheme: ThemeType;
  onSelect: (id: ThemeType) => void;
  onClose: () => void;
}

export default function ThemePicker({ currentTheme, onSelect, onClose }: ThemePickerProps) {
  const isArtisticActive = currentTheme === 'artistic';

  return (
    <motion.div 
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="fixed inset-0 z-[100] bg-black/80 backdrop-blur-md flex items-end sm:items-center justify-center p-4"
    >
      <div className="absolute inset-0" onClick={onClose} />
      
      <motion.div 
        initial={{ y: 100 }}
        animate={{ y: 0 }}
        className={`relative w-full max-w-md rounded-[32px] overflow-hidden shadow-2xl border ${
           isArtisticActive ? 'bg-white border-none' : 'bg-zinc-900 border-white/10'
        }`}
      >
        <div className={`p-6 flex items-center justify-between ${isArtisticActive ? 'border-b border-[#F2F4F0]' : 'border-b border-white/5'}`}>
          <div>
            <h3 className={`text-xl font-bold ${isArtisticActive ? 'font-serif text-[#2D4031]' : 'font-display'}`}>Nature Themes</h3>
            <p className={`text-[10px] uppercase tracking-widest font-bold ${isArtisticActive ? 'text-[#7A8D6E]' : 'opacity-50'}`}>Inspiration Palette</p>
          </div>
          <button onClick={onClose} className={`p-2 rounded-full transition-colors ${isArtisticActive ? 'bg-[#F2F4F0] text-[#2D4031]' : 'bg-white/5 hover:bg-white/10'}`}>
            <X size={20} />
          </button>
        </div>

        <div className="p-4 grid grid-cols-2 gap-4">
          {THEMES.map((theme) => (
            <button
              key={theme.id}
              onClick={() => onSelect(theme.id)}
              className="relative aspect-square rounded-2xl overflow-hidden group border-2 transition-all"
              style={{ borderColor: currentTheme === theme.id ? theme.colors.accent : 'transparent' }}
            >
              <img src={theme.image} alt={theme.name} className="w-full h-full object-cover transition-transform group-hover:scale-110" referrerPolicy="no-referrer" />
              <div className="absolute inset-0 bg-gradient-to-t from-black/40 via-transparent to-black/20" />
              <div className="absolute bottom-3 left-3 right-3 flex items-center justify-between">
                <span className="text-[10px] font-bold tracking-wider uppercase text-white drop-shadow-md">{theme.name}</span>
                {currentTheme === theme.id && (
                  <div className="w-6 h-6 rounded-full flex items-center justify-center" style={{ backgroundColor: theme.colors.accent }}>
                    <Check size={14} className={theme.id === 'artistic' ? 'text-white' : 'text-black'} />
                  </div>
                )}
              </div>
            </button>
          ))}
        </div>

        <div className="p-6 pt-2 pb-8">
          <button 
            onClick={onClose}
            className={`w-full py-4 rounded-2xl font-bold uppercase tracking-[0.2em] text-sm transition-colors ${
              isArtisticActive ? 'bg-[#2D4031] text-white hover:bg-[#1A1C19]' : 'bg-white text-black hover:bg-zinc-200'
            }`}
          >
            Apply Theme
          </button>
        </div>
      </motion.div>
    </motion.div>
  );
}
