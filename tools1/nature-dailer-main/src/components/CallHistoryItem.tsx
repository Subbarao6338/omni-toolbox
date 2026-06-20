import React, { useState } from 'react';
import { PhoneIncoming, PhoneOutgoing, PhoneMissed, Info, Ban, StickyNote } from 'lucide-react';
import { CallLog, Theme } from '../types';
import { useCallNotes } from '../hooks/useCallNotes';

interface CallHistoryItemProps {
  key?: React.Key;
  theme: Theme;
  call: CallLog;
  onCall: (number: string) => void;
  onBlock: (number: string) => void;
}

export default function CallHistoryItem({ theme, call, onCall, onBlock }: CallHistoryItemProps) {
  const isArtistic = theme.id === 'artistic';
  const { notes, addNote } = useCallNotes(call.id);
  const [noteText, setNoteText] = useState("");
  const [showNotes, setShowNotes] = useState(false);

  return (
    <div
      className={`w-full flex flex-col gap-4 py-4 px-4 rounded-2xl transition-all ${
        isArtistic ? 'hover:bg-[#F2F4F0] active:bg-[#D9C9B4]/20' : 'hover:bg-white/5 active:bg-white/10'
      }`}
      onClick={() => onCall(call.number)}
    >
      <div className="flex items-center gap-4">
        <div className={`w-12 h-12 rounded-full flex items-center justify-center ${
          call.type === 'missed' 
            ? 'bg-red-500/10 text-red-500' 
            : (isArtistic ? 'bg-[#F2F4F0] text-[#7A8D6E]' : 'bg-white/5 text-white/40')
        }`}>
          {call.type === 'incoming' && <PhoneIncoming size={20} />}
          {call.type === 'outgoing' && <PhoneOutgoing size={20} />}
          {call.type === 'missed' && <PhoneMissed size={20} />}
        </div>
        
        <div className="flex-1 text-left">
          <div className="font-semibold text-lg">{call.name}</div>
          <div className={`text-xs ${isArtistic ? 'text-[#7A8D6E]' : 'opacity-40'}`}>
            {call.number} • {call.time}
          </div>
        </div>

        <div className="flex items-center gap-2">
            <button onClick={(e) => { e.stopPropagation(); setShowNotes(!showNotes); }} className={`p-2 rounded-full ${isArtistic ? 'text-[#7A8D6E]' : 'opacity-40'}`}>
                <StickyNote size={16} />
            </button>
            <button onClick={(e) => { e.stopPropagation(); onBlock(call.number); }} className="p-2 text-red-500">
              <Ban size={16} />
            </button>
        </div>
      </div>
      
      {showNotes && (
        <div className="pl-16 pr-4 space-y-2">
            {notes.map(n => <div key={n.id} className="text-xs opacity-70 italic">{n.text}</div>)}
            <input 
                value={noteText} 
                onChange={(e) => setNoteText(e.target.value)}
                placeholder="Add note..."
                className="w-full text-xs p-2 bg-white/10 rounded"
                onClick={(e) => e.stopPropagation()}
            />
            <button onClick={(e) => { e.stopPropagation(); addNote(noteText); setNoteText(""); }} className="text-[10px] bg-green-500 text-white px-2 py-1 rounded">Save</button>
        </div>
      )}
    </div>
  );
}
