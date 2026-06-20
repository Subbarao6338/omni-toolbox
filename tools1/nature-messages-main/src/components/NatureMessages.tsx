/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { useState, useMemo, useEffect } from 'react';
import { useMessages } from '../hooks/useMessages';
import { dataService } from '../lib/dataService';
import LocalFileSync from './LocalFileSync';
import { geminiService } from '../services/geminiService';
import { MessageItem } from './NatureMessages/MessageItem';
import { Sidebar } from './NatureMessages/Sidebar';
import { ComposeModal } from './NatureMessages/ComposeModal';
import { PieChart, Activity, Shield, Sparkles, RefreshCcw, Search, BookOpen, Sun, Moon, Plus, Menu, LayoutGrid, X } from 'lucide-react';

const CATEGORIES = ['Personal', 'Work', 'Promotions', 'OTP', 'Transactions', 'Blocked'] as const;

export default function NatureMessages() {
  const { messages, refresh, togglePin, markAsRead, blockSender, updateCategory, createMsg, markNotSpam } = useMessages();
  const [category, setCategory] = useState<string>('All Messages');
  const [search, setSearch] = useState('');
  const [isScanning, setIsScanning] = useState(false);
  const [openSelectors, setOpenSelectors] = useState<Record<string, boolean>>({});
  const [summary, setSummary] = useState<string | null>(null);
  const [isSummarizing, setIsSummarizing] = useState(false);
  const [isDarkMode, setIsDarkMode] = useState(false);
  const [showCompose, setShowCompose] = useState(false);
  const [showMobileMenu, setShowMobileMenu] = useState(false);
  const [showMobileIntel, setShowMobileIntel] = useState(false);

  useEffect(() => {
    if (isDarkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [isDarkMode]);

  const filteredMessages = useMemo(() => {
    return messages.filter(m => {
      if (category === 'Blocked') return m.category === 'Blocked';
      if (category === 'Spam' && !m.isSpam) return false;
      if (m.category === 'Blocked' && category !== 'All Messages' && category !== 'Blocked') return false;
      if (m.isBlocked && category !== 'All Messages' && category !== 'Blocked') return false;
      if (category !== 'All Messages' && category !== 'Spam' && m.category !== category) return false;
      if (search && !m.sender.toLowerCase().includes(search.toLowerCase()) && !m.text.toLowerCase().includes(search.toLowerCase())) return false;
      return true;
    }).sort((a, b) => (b.isPinned ? 1 : 0) - (a.isPinned ? 1 : 0));
  }, [messages, category, search]);

  const handleSummarize = async () => {
    setIsSummarizing(true);
    try {
      const recent = messages.filter(m => !m.isSpam && !m.isBlocked).slice(0, 8);
      const summaryText = await geminiService.summarizeMessages(recent);
      setSummary(summaryText);
    } catch (err) {
      console.error(err);
    } finally {
      setIsSummarizing(false);
    }
  };

  const runSpamDetection = async () => {
    setIsScanning(true);
    try {
      const unscanned = messages.filter(m => m.isSpam === undefined);
      if (unscanned.length === 0) { alert("All messages are up to date!"); return; }
      const results = await geminiService.detectSpam(unscanned);
      results.forEach(res => {
        dataService.updateMessage(res.id, { isSpam: res.isSpam, spamReason: res.reason });
      });
      refresh();
    } catch (err) {
      console.error(err);
    } finally {
      setIsScanning(false);
    }
  };

  // Analytics Helpers
  const totalMsgs = messages.length;
  const blockedCount = messages.filter(m => m.category === 'Blocked' || m.isBlocked).length;
  const spamCount = messages.filter(m => m.isSpam).length;
  const unreadCount = messages.filter(m => !m.isRead && !m.isSpam && !m.isBlocked).length;
  
  const categoryCounts = CATEGORIES.reduce((acc, cat) => {
    acc[cat] = messages.filter(m => m.category === cat).length;
    return acc;
  }, {} as Record<string, number>);

  return (
    <div className="bg-[#f1f5f2] min-h-screen text-[#2d3e33] p-4 md:p-6 font-sans transition-all duration-300 dark:bg-[#0d1a12] dark:text-[#f1f5f2]">
      <header className="flex justify-between items-center mb-6 px-1 md:px-3">
        <div className="flex items-center gap-3">
          <button 
            onClick={() => setShowMobileMenu(!showMobileMenu)}
            className="md:hidden p-2 rounded-xl bg-white border border-[#e2e8e4] dark:bg-[#1a2e20] dark:border-[#2d5a27]"
          >
            <Menu className="w-5 h-5 text-[#2d5a27]" />
          </button>
          <div className="w-10 h-10 bg-[#2d5a27] rounded-xl flex items-center justify-center text-white font-bold text-xl shadow-lg shadow-green-900/10 hidden sm:flex">M</div>
          <div>
            <h1 className="text-xl md:text-2xl font-bold text-[#1a2e20] dark:text-white">Nature Messages</h1>
            <p className="text-[9px] text-[#94a89a] font-bold uppercase tracking-[0.2em] hidden xs:block">Secure Local Shield</p>
          </div>
        </div>
        
        <div className="flex items-center gap-2 md:gap-4">
          <button 
            onClick={() => setIsDarkMode(!isDarkMode)}
            className="p-2.5 rounded-full bg-white border border-[#e2e8e4] text-[#2d5a27] shadow-sm hover:bg-[#f5f8f6] transition-all dark:bg-[#1a2e20] dark:border-[#2d5a27] dark:text-[#4caf50]"
          >
            {isDarkMode ? <Sun className="w-4 h-4" /> : <Moon className="w-4 h-4" />}
          </button>
          <button 
            onClick={() => setShowCompose(true)}
            className="flex items-center gap-2 bg-[#2d5a27] text-white px-4 md:px-5 py-2.5 rounded-2xl font-bold text-xs md:text-sm shadow-lg shadow-green-900/20 hover:scale-105 transition-all active:scale-95"
          >
            <Plus className="w-4 h-4" />
            <span className="hidden sm:inline">Compose</span>
          </button>
          <button 
            onClick={() => setShowMobileIntel(!showMobileIntel)}
            className="md:hidden p-2.5 rounded-full bg-white border border-[#e2e8e4] text-[#2d5a27] dark:bg-[#1a2e20] dark:border-[#2d5a27]"
          >
            <LayoutGrid className="w-4 h-4" />
          </button>
        </div>
      </header>

      <main className="flex flex-col md:grid md:grid-cols-[240px_1fr_300px] gap-5 h-auto md:h-[calc(100vh-140px)]">
        {/* Sidebar - Mobile Responsive */}
        <div className={`${showMobileMenu ? 'flex' : 'hidden'} md:flex fixed md:relative inset-0 md:inset-auto z-40 bg-black/20 md:bg-transparent backdrop-blur-sm md:backdrop-blur-none p-4 md:p-0 transition-all`}>
           <div className="w-full max-w-[280px] md:max-w-none h-full md:h-auto bg-white dark:bg-[#1a2e20] rounded-[28px] overflow-hidden flex flex-col relative">
             <button onClick={() => setShowMobileMenu(false)} className="md:hidden absolute top-4 right-4 p-2 text-[#94a89a]"><X className="w-5 h-5"/></button>
             <Sidebar 
                currentCategory={category} 
                onSelectCategory={(c) => { setCategory(c); setShowMobileMenu(false); }} 
                counts={categoryCounts} 
                spamCount={spamCount} 
                totalThreads={totalMsgs} 
              />
           </div>
        </div>

        <div className="bg-white rounded-[28px] p-4 md:p-6 shadow-sm border border-[#eef2ef] flex flex-col gap-4 overflow-hidden dark:bg-[#1a2e20] dark:border-[#2d5a27]">
           <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-2">
               <div className="flex items-center gap-2">
                  <div className="text-[20px] md:text-[22px] font-semibold text-[#1a2e20] dark:text-white">{category}</div>
                  {unreadCount > 0 && <span className="bg-[#2d5a27] text-white px-2 py-0.5 rounded-full text-[9px] font-bold">{unreadCount} NEW</span>}
               </div>
               <div className="relative group w-full sm:w-auto">
                 <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-[#94a89a] group-focus-within:text-[#2d5a27] transition-colors" />
                 <input 
                   type="text" 
                   placeholder="Search..."
                   className="w-full text-[14px] p-2.5 pl-11 pr-5 bg-[#f5f8f6] rounded-2xl outline-none border border-transparent focus:border-[#2d5a27] sm:min-w-[260px] transition-all dark:bg-[#0d1a12] dark:border-[#2d5a27]"
                   value={search}
                   onChange={(e) => setSearch(e.target.value)}
                 />
               </div>
           </div>
           
           <div className="flex-grow overflow-y-auto pr-1 flex flex-col gap-3 custom-scrollbar min-h-[400px]">
             {filteredMessages.length > 0 ? filteredMessages.map(msg => (
               <MessageItem 
                 key={msg.id}
                 msg={msg}
                 onTogglePin={togglePin}
                 onMarkRead={markAsRead}
                 onCategoryChange={(id, cat) => updateCategory(id, cat as any)}
                 onBlock={blockSender}
                 onReminder={() => refresh()}
                 onNotSpam={markNotSpam}
                 isSelectorOpen={!!openSelectors[msg.id]}
                 onToggleSelector={(id) => setOpenSelectors(prev => ({ ...prev, [id]: !prev[id] }))}
               />
             )) : (
               <div className="h-full flex flex-col items-center justify-center text-[#94a89a] py-20">
                  <div className="text-6xl mb-4 opacity-30 animate-pulse">🍃</div>
                  <p className="font-medium text-center italic">Your dashboard is as quiet as a forest path...</p>
               </div>
             )}
           </div>
        </div>

        {/* Intelligence Sidebar - Mobile Responsive */}
        <div className={`${showMobileIntel ? 'flex' : 'hidden'} md:flex fixed md:relative bottom-0 left-0 right-0 md:bottom-auto md:left-auto md:right-auto z-40 md:z-auto bg-black/20 md:bg-transparent backdrop-blur-sm md:backdrop-blur-none p-4 md:p-0 h-[60vh] md:h-auto overflow-y-auto`}>
          <div className="w-full bg-white dark:bg-[#1a2e20] rounded-[28px] p-6 shadow-2xl md:shadow-sm border border-[#eef2ef] md:border-[#eef2ef] dark:border-[#2d5a27] flex flex-col gap-4 relative">
            <button onClick={() => setShowMobileIntel(false)} className="md:hidden absolute top-4 right-4 p-2 text-[#94a89a]"><X className="w-5 h-5"/></button>
            
            <div className="flex justify-between items-center border-b pb-4 border-[#f5f8f6] dark:border-[#2d5a27]">
              <div className="text-[11px] font-bold tracking-widest text-[#94a89a] flex items-center gap-2">
                <PieChart className="w-3 h-3" /> INTELLIGENCE
              </div>
              <Activity className="w-4 h-4 text-[#2d5a27]" />
            </div>

            <div className="flex flex-col gap-2">
              <button 
                onClick={runSpamDetection}
                disabled={isScanning}
                className="w-full py-3.5 rounded-2xl flex items-center justify-center gap-2 font-bold text-xs uppercase tracking-widest bg-[#2d5a27] text-white hover:bg-[#1a3a16] transition-all disabled:opacity-50"
              >
                {isScanning ? <RefreshCcw className="w-4 h-4 animate-spin" /> : <Sparkles className="w-4 h-4" />}
                {isScanning ? 'Scrubbing...' : 'AI Spam Shield'}
              </button>
              
              <button 
                onClick={handleSummarize}
                disabled={isSummarizing}
                className="w-full py-3.5 rounded-2xl flex items-center justify-center gap-2 font-bold text-xs uppercase tracking-widest border border-[#2d5a27] text-[#2d5a27] dark:border-[#4caf50] dark:text-[#4caf50]"
              >
                {isSummarizing ? <RefreshCcw className="w-4 h-4 animate-spin" /> : <BookOpen className="w-4 h-4" />}
                {isSummarizing ? 'Analyzing...' : 'Nature Summary'}
              </button>
            </div>

            {summary && (
              <div className="p-4 bg-[#f9fbf9] border border-[#eef2ef] rounded-2xl text-[12px] text-[#4a6b54] italic leading-relaxed animate-in slide-in-from-top-2 dark:bg-[#0d1a12] dark:border-[#2d5a27] dark:text-[#94a89a]">
                "{summary}"
              </div>
            )}

            <div className="grid grid-cols-2 gap-3">
              <div className="bg-[#f9fbf9] p-4 rounded-2xl border border-[#eef2ef] dark:bg-[#0d1a12] dark:border-[#2d5a27]">
                <div className="text-[20px] font-bold text-[#2d5a27] dark:text-[#4caf50]">{unreadCount}</div>
                <div className="text-[9px] text-[#94a89a] font-bold uppercase tracking-tighter">Current Unread</div>
              </div>
              <div className="bg-[#fff4f4] p-4 rounded-2xl border border-red-50 dark:bg-[#1a1a1a] dark:border-red-900/30">
                <div className="text-[20px] font-bold text-[#d32f2f]">{spamCount}</div>
                <div className="text-[9px] text-[#94a89a] font-bold uppercase tracking-tighter">AI Shielded</div>
              </div>
            </div>

            <div className="mt-4">
               <LocalFileSync onDataRestored={refresh} getCurrentData={() => messages} />
            </div>

            <div className="mt-2 p-4 bg-[#eef7ee] rounded-2xl flex items-center gap-3 border border-[#d8edd8] dark:bg-[#1a2e20] dark:border-[#2d5a27]">
                <Shield className="w-10 h-10 text-[#2d5a27] opacity-20 dark:text-[#4caf50]" />
                <div className="flex flex-col">
                  <div className="text-[12px] font-bold text-[#2d5a27] dark:text-[#4caf50]">Bio-Metric Sync</div>
                  <div className="text-[10px] text-[#4a6b54]">{totalMsgs} threads active</div>
                </div>
            </div>
          </div>
        </div>
      </main>

      {showCompose && <ComposeModal onClose={() => setShowCompose(false)} onSend={createMsg} />}
    </div>
  );
}
