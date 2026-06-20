import React from 'react';
import { Pin, Bell, AlertTriangle, ChevronDown } from 'lucide-react';
import { Message } from '../../lib/dataService';

const CATEGORIES = ['Personal', 'Work', 'Promotions', 'OTP', 'Transactions', 'Blocked'] as const;

interface MessageItemProps {
  msg: Message;
  onTogglePin: (id: string) => void;
  onMarkRead: (id: string) => void;
  onCategoryChange: (id: string, cat: Message['category']) => void;
  onBlock: (sender: string) => void;
  onReminder: (id: string) => void;
  onNotSpam: (id: string) => void;
  isSelectorOpen: boolean;
  onToggleSelector: (id: string) => void;
}

export const MessageItem: React.FC<MessageItemProps> = ({
  msg,
  onTogglePin,
  onMarkRead,
  onCategoryChange,
  onBlock,
  onReminder,
  onNotSpam,
  isSelectorOpen,
  onToggleSelector
}) => {
  return (
    <div 
      onClick={() => !msg.isRead && onMarkRead(msg.id)}
      className={`p-5 rounded-3xl border transition-all group flex justify-between items-start hover:shadow-md cursor-pointer ${
        msg.isSpam ? 'bg-red-50/30 border-red-100' : 
        msg.isRead ? 'bg-white border-[#eef2ef] hover:bg-[#f9fbf9]' : 
        'bg-[#f0f7f0] border-[#d8edd8] shadow-sm'
      }`}
    >
      <div className="flex flex-col gap-1 flex-grow">
        <div className="flex items-center gap-2 flex-wrap mb-1">
          {!msg.isRead && <div className="w-2 h-2 bg-[#2d5a27] rounded-full mr-1"></div>}
          <span className={`font-bold ${msg.isRead ? 'text-[#1a2e20]' : 'text-black'}`}>{msg.sender}</span>
          
          <div className="relative">
            <button 
              onClick={(e) => { e.stopPropagation(); onToggleSelector(msg.id); }}
              className="flex items-center gap-1.5 text-[10px] bg-white/80 text-[#2d5a27] px-2 py-0.5 rounded-full font-bold uppercase transition-all hover:bg-white border border-[#eef2ef]"
            >
              {msg.category}
              <ChevronDown className={`w-2.5 h-2.5 transition-transform ${isSelectorOpen ? 'rotate-180' : ''}`} />
            </button>
            
            {isSelectorOpen && (
              <div className="absolute top-full left-0 mt-1 bg-white border border-[#eef2ef] rounded-xl shadow-xl z-20 py-2 min-w-[120px]">
                {CATEGORIES.map(cat => (
                  <button
                    key={cat}
                    onClick={(e) => { e.stopPropagation(); onCategoryChange(msg.id, cat as any); }}
                    className={`w-full text-left px-4 py-1.5 text-[11px] font-semibold transition-colors hover:bg-[#f5f8f6] ${msg.category === cat ? 'text-[#2d5a27] bg-[#f0f7f0]' : 'text-[#4a6b54]'}`}
                  >
                    {cat}
                  </button>
                ))}
              </div>
            )}
          </div>

          {msg.isPinned && <Pin className="w-3 h-3 text-[#2d5a27] fill-[#2d5a27]" />}
          {msg.reminderAt && <span className="text-[10px] bg-[#fff8e1] text-[#f57c00] px-2 py-1 rounded-full font-bold flex items-center gap-1"><Bell className="w-2 h-2" /> Reminder</span>}
          {msg.isSpam && (
            <span className="text-[10px] bg-red-100 text-red-600 px-2 py-1 rounded-full font-bold flex items-center gap-1 group/spam relative cursor-help">
              <AlertTriangle className="w-2 h-2" /> POTENTIAL SPAM
              <div className="absolute bottom-full left-0 mb-2 invisible group-hover/spam:visible bg-gray-800 text-white text-[10px] p-2 rounded-lg w-48 shadow-xl z-10 font-normal">
                 {msg.spamReason || "AI identified this as potential spam."}
              </div>
            </span>
          )}
        </div>
        <div className={`text-[14px] leading-relaxed max-w-lg truncate ${msg.isRead ? 'text-[#4a6b54]' : 'text-[#1a2e20] font-medium'}`}>{msg.text}</div>
        <div className="text-[11px] text-[#94a89a] mt-1 opacity-70 italic">{new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</div>
      </div>
      <div className="flex gap-2">
          <button 
            onClick={(e) => { e.stopPropagation(); onTogglePin(msg.id); }}
            className={`p-2 px-3 rounded-xl transition-all ${msg.isPinned ? 'bg-[#2d5a27] text-white' : 'bg-[#f5f8f6] text-[#2d5a27] hover:bg-[#eef7ee]'}`}
          >
            <Pin className="w-3 h-3" />
          </button>
          <button 
            onClick={(e) => { e.stopPropagation(); onBlock(msg.sender); }}
            className="p-2 px-3 bg-[#fff4f4] hover:bg-[#d32f2f] hover:text-white text-[#d32f2f] rounded-xl text-[10px] font-bold transition-all"
          >
            Block
          </button>
          {msg.isSpam && (
            <button 
              onClick={(e) => { e.stopPropagation(); onNotSpam(msg.id); }}
              className="p-2 px-3 bg-[#e8f5e9] hover:bg-[#2d5a27] hover:text-white text-[#2d5a27] rounded-xl text-[10px] font-bold transition-all"
            >
              Safe
            </button>
          )}
      </div>
    </div>
  );
};
