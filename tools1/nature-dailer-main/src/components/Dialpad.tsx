import { useState } from 'react';
import { motion } from 'motion/react';
import { Delete, Phone, UserPlus } from 'lucide-react';
import { Theme } from '../types';

interface DialpadProps {
  theme: Theme;
  onCall: (number: string) => void;
}

export default function Dialpad({ theme, onCall }: DialpadProps) {
  const [number, setNumber] = useState('');
  const isArtistic = theme.id === 'artistic';

  const keys = [
    { main: '1', sub: ' ' },
    { main: '2', sub: 'ABC' },
    { main: '3', sub: 'DEF' },
    { main: '4', sub: 'GHI' },
    { main: '5', sub: 'JKL' },
    { main: '6', sub: 'MNO' },
    { main: '7', sub: 'PQRS' },
    { main: '8', sub: 'TUV' },
    { main: '9', sub: 'WXYZ' },
    { main: '*', sub: '' },
    { main: '0', sub: '+' },
    { main: '#', sub: '' },
  ];

  const handleKeyPress = (val: string) => {
    if (number.length < 15) setNumber(prev => prev + val);
  };

  const handleDelete = () => {
    setNumber(prev => prev.slice(0, -1));
  };

  return (
    <div className="flex-1 flex flex-col justify-end p-6 pb-12">
      {/* Number Display */}
      <div className="flex-1 flex flex-col items-center justify-center mb-8">
        <motion.div 
          key={number}
          initial={{ scale: 0.95, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          className={`text-5xl font-bold tracking-tight mb-2 h-16 flex items-center ${isArtistic ? 'font-serif text-[#1A1C19]' : 'font-display'}`}
          style={{ color: !isArtistic ? theme.colors.text : '' }}
        >
          {number}
        </motion.div>
        
        {number && (
          <button className={`flex items-center gap-2 text-sm transition-opacity ${isArtistic ? 'text-[#7A8D6E]' : 'opacity-60 hover:opacity-100'}`}>
            <UserPlus size={16} />
            <span>Add to contacts</span>
          </button>
        )}
      </div>

      {/* Dial Keys Grid */}
      <div className="grid grid-cols-3 gap-6 max-w-sm mx-auto w-full">
        {keys.map((key) => (
          <button
            key={key.main}
            onClick={() => handleKeyPress(key.main)}
            className={`group relative flex flex-col items-center justify-center aspect-square rounded-full transition-all active:scale-90 ${
              isArtistic ? 'hover:bg-[#F2F4F0]' : ''
            }`}
            style={{ backgroundColor: !isArtistic ? theme.colors.dial + '44' : '' }}
          >
            <span className={`text-3xl font-medium ${isArtistic ? 'text-[#1A1C19]' : ''}`} style={{ color: !isArtistic ? theme.colors.text : '' }}>
              {key.main}
            </span>
            <span className={`text-[10px] font-bold tracking-widest uppercase ${isArtistic ? 'text-[#7A8D6E]' : 'opacity-40'}`}>
              {key.sub}
            </span>
            <div 
              className={`absolute inset-0 rounded-full opacity-0 group-active:opacity-100 transition-opacity ${isArtistic ? 'bg-[#7A8D6E]/10' : 'bg-white/10'}`}
            />
          </button>
        ))}
      </div>

      {/* Call & Delete Actions */}
      <div className="grid grid-cols-3 gap-6 max-w-sm mx-auto w-full mt-10 items-center">
        <div /> {/* Spacer */}
        <button
          onClick={() => number && onCall(number)}
          className={`flex items-center justify-center aspect-square shadow-lg overflow-hidden transition-transform active:scale-90 ${
            isArtistic ? 'rounded-[22px] bg-[#2D4031] shadow-[#2D4031]/30' : 'rounded-full'
          }`}
          style={{ backgroundColor: !isArtistic ? theme.colors.accent : '' }}
        >
          <Phone size={32} className={isArtistic ? 'text-white' : 'text-black'} />
          {!isArtistic && (
            <motion.div
              animate={{ scale: [1, 1.2, 1], opacity: [0.1, 0.3, 0.1] }}
              transition={{ repeat: Infinity, duration: 2 }}
              className="absolute inset-0 bg-white"
            />
          )}
        </button>
        <button
          onClick={handleDelete}
          onContextMenu={(e) => { e.preventDefault(); setNumber(''); }}
          className={`flex items-center justify-center aspect-square rounded-full transition-all active:scale-90 ${
            isArtistic ? 'hover:bg-[#F2F4F0] text-[#7A8D6E]' : 'hover:bg-white/10'
          }`}
          style={{ color: !isArtistic ? theme.colors.text : '' }}
        >
          {number && <Delete size={28} className={isArtistic ? '' : 'opacity-80'} />}
        </button>
      </div>
    </div>
  );
}
