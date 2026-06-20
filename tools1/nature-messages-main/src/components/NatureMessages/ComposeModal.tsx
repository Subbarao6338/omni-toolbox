import React, { useState } from 'react';
import { X, Send, Tag } from 'lucide-react';
import { Message } from '../../lib/dataService';

const CATEGORIES = ['Personal', 'Work', 'Promotions', 'OTP', 'Transactions'] as const;

interface ComposeModalProps {
  onClose: () => void;
  onSend: (sender: string, text: string, category: Message['category']) => void;
}

export const ComposeModal: React.FC<ComposeModalProps> = ({ onClose, onSend }) => {
  const [sender, setSender] = useState('');
  const [text, setText] = useState('');
  const [category, setCategory] = useState<Message['category']>('Personal');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (sender && text) {
      onSend(sender, text, category);
      onClose();
    }
  };

  return (
    <div className="fixed inset-0 bg-black/40 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-in fade-in duration-200">
      <div className="bg-white w-full max-w-md rounded-[32px] shadow-2xl overflow-hidden dark:bg-[#1a2e20] border border-[#eef2ef] dark:border-[#2d5a27]">
        <div className="p-6 flex justify-between items-center border-b border-[#f5f8f6] dark:border-[#2d5a27]">
          <h2 className="text-xl font-bold text-[#1a2e20] dark:text-white">Compose Message</h2>
          <button onClick={onClose} className="p-2 hover:bg-gray-100 rounded-full dark:hover:bg-white/10">
            <X className="w-5 h-5 text-[#94a89a]" />
          </button>
        </div>
        
        <form onSubmit={handleSubmit} className="p-6 flex flex-col gap-4">
          <div>
            <label className="text-[10px] font-bold text-[#94a89a] uppercase tracking-wider block mb-1">Sender Name/Number</label>
            <input 
              type="text" 
              placeholder="e.g. Alice or 12345"
              className="w-full p-3 rounded-2xl bg-[#f5f8f6] border border-transparent focus:border-[#2d5a27] outline-none transition-all dark:bg-[#0d1a12] dark:text-white"
              value={sender}
              onChange={(e) => setSender(e.target.value)}
              required
            />
          </div>

          <div>
            <label className="text-[10px] font-bold text-[#94a89a] uppercase tracking-wider block mb-1">Message Content</label>
            <textarea 
              placeholder="Type your message..."
              rows={4}
              className="w-full p-3 rounded-2xl bg-[#f5f8f6] border border-transparent focus:border-[#2d5a27] outline-none transition-all resize-none dark:bg-[#0d1a12] dark:text-white"
              value={text}
              onChange={(e) => setText(e.target.value)}
              required
            />
          </div>

          <div>
            <label className="text-[10px] font-bold text-[#94a89a] uppercase tracking-wider block mb-2">Assign Category</label>
            <div className="flex flex-wrap gap-2">
              {CATEGORIES.map(cat => (
                <button
                  key={cat}
                  type="button"
                  onClick={() => setCategory(cat as any)}
                  className={`px-3 py-1.5 rounded-full text-[10px] font-bold transition-all border ${
                    category === cat 
                    ? 'bg-[#2d5a27] text-white border-[#2d5a27]' 
                    : 'bg-white text-[#4a6b54] border-[#e2e8e4] hover:bg-[#f5f8f6] dark:bg-[#1a2e20] dark:border-[#2d5a27] dark:text-[#94a89a]'
                  }`}
                >
                  {cat}
                </button>
              ))}
            </div>
          </div>

          <button 
            type="submit"
            className="mt-4 w-full bg-[#2d5a27] text-white py-4 rounded-2xl font-bold flex items-center justify-center gap-2 hover:bg-[#1e3d1a] transition-all shadow-lg shadow-green-900/20"
          >
            <Send className="w-4 h-4" />
            Post Message
          </button>
        </form>
      </div>
    </div>
  );
};
