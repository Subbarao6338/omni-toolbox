import React from 'react';
import { BarChart2 } from 'lucide-react';

const CATEGORIES = ['Personal', 'Work', 'Promotions', 'OTP', 'Transactions', 'Blocked'] as const;

interface SidebarProps {
  currentCategory: string;
  onSelectCategory: (cat: string) => void;
  counts: Record<string, number>;
  spamCount: number;
  totalThreads: number;
}

export const Sidebar: React.FC<SidebarProps> = ({
  currentCategory,
  onSelectCategory,
  counts,
  spamCount,
  totalThreads
}) => {
  return (
    <div className="bg-white rounded-[28px] p-6 shadow-sm border border-[#eef2ef] flex flex-col gap-2 overflow-y-auto dark:bg-[#1a2e20] dark:border-[#2d5a27] dark:text-[#f1f5f2]">
        <div className="text-[11px] font-bold uppercase tracking-wider text-[#94a89a] mb-2 px-2 flex items-center gap-2">
          <BarChart2 className="w-3 h-3" /> Filters
        </div>
        {['All Messages', ...CATEGORIES, 'Spam'].map(c => (
            <button 
              key={c} 
              onClick={() => onSelectCategory(c)}
              className={`p-4 rounded-xl text-left font-medium transition-all flex justify-between items-center group ${
                currentCategory === c 
                ? 'bg-[#2d5a27] text-white shadow-md shadow-green-900/20 translate-x-1' 
                : 'hover:bg-[#f5f8f6] text-[#1a2e20] dark:text-[#f1f5f2] dark:hover:bg-[#2d5a27]/30'
              }`}
            >
              <span>{c}</span>
              {(c === 'Spam' && spamCount > 0) || (CATEGORIES.includes(c as any) && counts[c] > 0) ? (
                <span className={`text-[10px] px-1.5 py-0.5 rounded-full ${currentCategory === c ? 'bg-white/20 text-white' : 'bg-[#eef7ee] text-[#2d5a27] dark:bg-[#2d5a27] dark:text-white'}`}>
                  {c === 'Spam' ? spamCount : counts[c]}
                </span>
              ) : null}
            </button>
        ))}
        <div className="mt-auto p-4 bg-[#f9fafa] border border-[#eef2ef] rounded-xl text-[#7a8b7f] text-sm text-center dark:bg-[#1a2e20]/50 dark:border-[#2d5a27] dark:text-[#94a89a]">
          Storage: {totalThreads} Threads
        </div>
    </div>
  );
};
