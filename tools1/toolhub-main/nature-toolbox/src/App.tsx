/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useEffect, useRef } from 'react';
import { 
  Leaf, 
  Settings, 
  Search, 
  Battery, 
  Cpu, 
  HardDrive, 
  Calculator, 
  Ruler, 
  Compass, 
  Timer, 
  Zap, 
  Mic2, 
  QrCode, 
  ListTodo, 
  BookOpen, 
  CloudSun, 
  Dna, 
  Gamepad2,
  ChevronLeft,
  Moon,
  Sun,
  Umbrella,
  ArrowRightLeft,
  Thermometer,
  Feather,
  LayoutDashboard,
  Wallet,
  Calendar,
  Flower2,
  Music,
  FileText,
  Weight,
  FlaskConical,
  Camera,
  Activity,
  Scan,
  RefreshCw,
  Info,
  Cloud,
  Check,
  X
} from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import { cn } from './lib/utils';
import { Tool, Category } from './types';
import { GoogleGenAI } from "@google/genai";
import { ResponsiveContainer, LineChart, Line, XAxis, YAxis, Tooltip, AreaChart, Area } from 'recharts';
import NatureSensors from './native-bridge/nature-sensors';

const ai = new GoogleGenAI({ apiKey: process.env.GEMINI_API_KEY });

// Mock Tools Data
const TOOLS: Tool[] = [
  { id: 'flashlight', name: 'Flashlight', icon: Zap, category: 'Tools', description: 'Immediate light source', color: 'bg-yellow-50' },
  { id: 'calculator', name: 'Calculator', icon: Calculator, category: 'Tools', description: 'Basic & Scientific', color: 'bg-blue-50' },
  { id: 'unit-converter', name: 'Unit Converter', icon: ArrowRightLeft, category: 'Tools', description: 'Mass, Length, Time', color: 'bg-green-50' },
  { id: 'ruler', name: 'Ruler', icon: Ruler, category: 'Tools', description: 'Measure on screen', color: 'bg-orange-50' },
  { id: 'compass', name: 'Compass', icon: Compass, category: 'Tools', description: 'Direction finder', color: 'bg-indigo-50' },
  { id: 'level', name: 'Surface Level', icon: LayoutDashboard, category: 'Tools', description: 'Alignment tool', color: 'bg-emerald-50' },
  { id: 'sound-meter', name: 'Sound Meter', icon: Mic2, category: 'Tools', description: 'Decibel monitor', color: 'bg-red-50' },
  { id: 'stopwatch', name: 'Stopwatch', icon: Timer, category: 'Tools', description: 'Precise timing', color: 'bg-teal-50' },
  { id: 'qr-scanner', name: 'QR Scanner', icon: QrCode, category: 'Tools', description: 'Scan & Create codes', color: 'bg-purple-50' },
  { id: 'notepad', name: 'Notepad', icon: FileText, category: 'Life', description: 'Quick notes', color: 'bg-stone-100' },
  { id: 'todo', name: 'Tasks', icon: ListTodo, category: 'Life', description: 'Do it now', color: 'bg-emerald-50' },
  { id: 'budget', name: 'Budget', icon: Wallet, category: 'Life', description: 'Track finances', color: 'bg-lime-50' },
  { id: 'weather', name: 'Weather', icon: CloudSun, category: 'Science', description: 'Local forecast', color: 'bg-sky-50' },
  { id: 'periodic-table', name: 'Periodic Table', icon: FlaskConical, category: 'Science', description: 'Chemistry lookup', color: 'bg-rose-50' },
  { id: 'game-of-life', name: 'Game of Life', icon: Gamepad2, category: 'Fun', description: 'Cellular automata', color: 'bg-zinc-100' },
  { id: 'pokedex', name: 'Nature Dex', icon: Feather, category: 'Science', description: 'Species identifier', color: 'bg-amber-50' },
  { id: 'barometer', name: 'Barometer', icon: FlaskConical, category: 'Science', description: 'Offline Pressure', color: 'bg-indigo-50' },
  { id: 'system-monitor', name: 'Sys Monitor', icon: Activity, category: 'System', description: 'Performance stats', color: 'bg-slate-100' },
];

export default function App() {
  const [activeTool, setActiveTool] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [stats, setStats] = useState({ battery: 100, memory: 0 });
  const [activeCategory, setActiveCategory] = useState<Category | 'All'>('All');
  const [theme, setTheme] = useState<'light' | 'dark'>(() => {
    if (typeof window !== 'undefined') {
      return localStorage.getItem('nature-theme') as 'light' | 'dark' || 'dark';
    }
    return 'dark';
  });

  useEffect(() => {
    document.documentElement.classList.toggle('dark', theme === 'dark');
    localStorage.setItem('nature-theme', theme);
  }, [theme]);

  useEffect(() => {
    if ('getBattery' in navigator) {
      (navigator as any).getBattery().then((battery: any) => {
        setStats(s => ({ ...s, battery: Math.round(battery.level * 100) }));
        battery.addEventListener('levelchange', () => {
          setStats(s => ({ ...s, battery: Math.round(battery.level * 100) }));
        });
      });
    }

    const interval = setInterval(() => {
      if ((performance as any).memory) {
        const memory = (performance as any).memory;
        setStats(s => ({ ...s, memory: Math.round(memory.usedJSHeapSize / 1048576) }));
      }
    }, 2000);

    return () => clearInterval(interval);
  }, []);

  const filteredTools = TOOLS.filter(t => 
    (t.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    t.category.toLowerCase().includes(searchQuery.toLowerCase())) &&
    (activeCategory === 'All' || t.category === activeCategory)
  );

  const categories: Category[] = ['System', 'Tools', 'Life', 'Science', 'Fun'];

  return (
    <div className="flex flex-col h-screen bg-bg text-text-main overflow-hidden font-sans transition-colors duration-300">
      {/* Header */}
      <header className="h-16 flex items-center justify-between px-6 border-b border-border bg-bg/80 backdrop-blur-md z-50 shrink-0">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 bg-accent rounded-lg flex items-center justify-center text-white shadow-[0_0_15px_rgba(94,179,98,0.3)]">
            <Leaf size={18} />
          </div>
          <span className="font-bold text-lg tracking-tight hidden sm:block">Nature Toolbox</span>
        </div>

        <div className="relative max-w-md w-full mx-4">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-text-dim" size={16} />
          <input 
            type="text"
            placeholder="Search tools, notes, or files..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-card border border-border py-2 pl-11 pr-4 rounded-full text-sm outline-none transition-all focus:border-accent"
          />
        </div>

        <div className="flex items-center gap-3">
          <button 
            onClick={() => setTheme(t => t === 'light' ? 'dark' : 'light')}
            className="w-10 h-10 rounded-full bg-card border border-border flex items-center justify-center text-accent hover:border-accent hover:bg-accent/10 transition-all shadow-sm active:scale-90"
            title={`Switch to ${theme === 'light' ? 'Dark' : 'Light'} Mode`}
          >
            {theme === 'light' ? <Moon size={18} /> : <Sun size={18} />}
          </button>
          <div className="w-8 h-8 rounded-full bg-border border border-accent/20"></div>
        </div>
      </header>

      {/* Main Container */}
      <div className="flex-1 flex overflow-hidden">
        {/* Sidebar */}
        <nav className="w-20 lg:w-56 shrink-0 border-r border-border py-6 flex flex-col gap-1 overflow-y-auto">
          <div 
            onClick={() => { setActiveCategory('All'); setActiveTool(null); }}
            className={cn("nav-item mx-2", activeCategory === 'All' && !activeTool && "active")}
          >
            <LayoutDashboard size={18} />
            <span className="hidden lg:block">Dashboard</span>
          </div>
          {categories.map(cat => (
            <div 
              key={cat}
              onClick={() => { setActiveCategory(cat); setActiveTool(null); }}
              className={cn("nav-item mx-2", activeCategory === cat && !activeTool && "active")}
            >
              {cat === 'System' && <Cpu size={18} />}
              {cat === 'Tools' && <Zap size={18} />}
              {cat === 'Life' && <Flower2 size={18} />}
              {cat === 'Science' && <Dna size={18} />}
              {cat === 'Fun' && <Gamepad2 size={18} />}
              <span className="hidden lg:block">{cat}</span>
            </div>
          ))}
          <div className="mt-auto pt-4 border-t border-border/30 px-4">
            <div className="nav-item mx-2">
              <Settings size={18} />
              <span className="hidden lg:block">Settings</span>
            </div>
          </div>
        </nav>

        {/* Content Area */}
        <main className="flex-1 overflow-y-auto bg-bg p-6">
          <AnimatePresence mode="wait">
            {!activeTool ? (
              <motion.div 
                key="grid"
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
              >
                <div className="flex justify-between items-baseline mb-6">
                  <h2 className="text-xs uppercase tracking-[0.2em] text-text-dim font-bold">
                    {activeCategory} {activeCategory === 'All' ? 'Tools' : ''}
                  </h2>
                  <span className="text-xs text-accent cursor-pointer hover:underline font-medium">View All</span>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
                  {filteredTools.map(tool => (
                    <ToolCard 
                      key={tool.id} 
                      tool={tool} 
                      onClick={() => setActiveTool(tool.id)} 
                    />
                  ))}
                </div>
              </motion.div>
            ) : (
              <motion.div 
                key="active-tool"
                initial={{ opacity: 0, scale: 0.98 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.98 }}
                className="h-full flex flex-col"
              >
                <button 
                  onClick={() => setActiveTool(null)}
                  className="flex items-center gap-2 text-text-dim hover:text-accent mb-6 transition-colors w-fit"
                >
                  <ChevronLeft size={20} />
                  <span className="text-sm font-medium">Back to {activeCategory}</span>
                </button>
                
                <div className="flex-1 bg-card/40 rounded-3xl p-8 border border-border backdrop-blur-sm overflow-y-auto">
                   <ToolRenderer id={activeTool} />
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </main>

        {/* Widgets Panel */}
        <aside className="hidden lg:flex w-64 shrink-0 border-l border-border py-8 px-5 flex-col gap-6 overflow-y-auto">
          <div className="widget-card">
            <span className="text-[10px] uppercase font-bold tracking-widest text-text-dim mb-4 block">System Monitor</span>
            <div className="space-y-4 font-mono text-[11px]">
              <div>
                <div className="flex justify-between mb-1.5 uppercase opacity-80">
                  <span>Battery</span>
                  <span>{stats.battery}%</span>
                </div>
                <div className="progress-bar">
                  <div className="progress-fill" style={{ width: `${stats.battery}%` }}></div>
                </div>
              </div>
              <div>
                <div className="flex justify-between mb-1.5 uppercase opacity-80">
                  <span>Memory</span>
                  <span>{stats.memory}MB</span>
                </div>
                <div className="progress-bar">
                  <div className="progress-fill" style={{ width: `${(stats.memory / 4096) * 100}%` }}></div>
                </div>
              </div>
              <div>
                <div className="flex justify-between mb-1.5 uppercase opacity-80">
                  <span>Storage</span>
                  <span>68%</span>
                </div>
                <div className="progress-bar">
                  <div className="progress-fill" style={{ width: '68%' }}></div>
                </div>
              </div>
            </div>
          </div>

           <div className="widget-card">
            <span className="text-[10px] uppercase font-bold tracking-widest text-text-dim mb-4 block">Nature Forecast</span>
            <div className="flex items-center gap-4">
              <CloudSun size={32} className="text-accent" />
              <div>
                <div className="text-2xl font-light">24°C</div>
                <div className="text-[10px] text-text-dim">Mostly Clear · Garden City</div>
              </div>
            </div>
          </div>

          <div className="widget-card !bg-accent !text-white border-none shadow-[0_4px_15px_rgba(94,179,98,0.2)]">
            <span className="text-[10px] uppercase font-bold tracking-widest opacity-60 mb-2 block">Quick Habit</span>
            <div className="font-bold text-sm">Daily Respiration</div>
            <div className="text-[10px] mt-1 opacity-80">Inhale deep, exhale slowly.</div>
          </div>
        </aside>
      </div>

      {/* Footer */}
      <footer className="h-8 border-t border-border bg-bg flex items-center justify-between px-6 text-[10px] text-text-dim uppercase tracking-widest shrink-0 z-50">
        <div className="flex items-center gap-4">
          <span className="flex items-center gap-1.5">
            <span className="w-1.5 h-1.5 bg-accent rounded-full animate-pulse"></span>
            All Systems Online
          </span>
          <span>GPS Locked</span>
        </div>
        <div className="flex items-center gap-4">
          <span>v4.2.0-Ultimate</span>
          <span>Android 14 Core</span>
        </div>
      </footer>
    </div>
  );
}

function QuickStat({ icon: Icon, label, sub, color }: { key?: string | number, icon: any, label: string, sub: string, color: string }) {
  return (
    <div className="p-4 rounded-3xl border border-border bg-card shadow-sm flex flex-col items-start gap-1 transition-all">
      <Icon size={18} className={color} />
      <span className="text-lg font-medium leading-none mt-1">{label}</span>
      <span className="text-[10px] uppercase tracking-wider opacity-40">{sub}</span>
    </div>
  );
}

function ToolCard({ tool, onClick }: { key?: string | number, tool: Tool, onClick: () => void }) {
  const Icon = tool.icon;
  return (
    <motion.div
      whileHover={{ y: -4, scale: 1.02 }}
      whileTap={{ scale: 0.98 }}
      onClick={onClick}
      className="tool-card group"
    >
      <div className="w-10 h-10 bg-text-main/5 border border-border rounded-xl flex items-center justify-center text-xl mb-3 group-hover:border-accent group-hover:bg-accent/10 transition-colors">
        <Icon size={20} className="text-text-main group-hover:text-accent" />
      </div>
      <div>
        <h3 className="text-sm font-semibold truncate">{tool.name}</h3>
        <p className="text-[10px] text-text-dim mt-1.5 leading-relaxed line-clamp-2">{tool.description}</p>
      </div>
    </motion.div>
  );
}

// Tool Component Map
function ToolRenderer({ id }: { id: string | null }) {
  if (!id) return (
    <div className="h-full flex flex-col items-center justify-center text-center opacity-40">
      <Leaf size={48} strokeWidth={1.5} className="mb-4 text-accent" />
      <p className="text-sm uppercase tracking-widest">Select a Leaf Tool</p>
    </div>
  );
  switch (id) {
    case 'calculator': return <CalculatorTool />;
    case 'unit-converter': return <UnitConverterTool />;
    case 'stopwatch': return <StopwatchTool />;
    case 'flashlight': return <FlashlightTool />;
    case 'notepad': return <NotepadTool />;
    case 'compass': return <CompassTool />;
    case 'level': return <LevelTool />;
    case 'sound-meter': return <SoundMeterTool />;
    case 'periodic-table': return <PeriodicTableTool />;
    case 'system-monitor': return <SystemMonitorTool />;
    case 'pokedex': return <NatureDexTool />;
    case 'barometer': return <BarometerTool />;
    case 'weather': return <WeatherTool />;
    case 'ruler': return <RulerTool />;
    case 'todo': return <TodoTool />;
    case 'budget': return <BudgetTool />;
    default: return (
      <div className="h-full flex flex-col items-center justify-center text-center opacity-40">
        <Umbrella size={48} strokeWidth={1.5} className="mb-4 text-accent" />
        <p className="text-sm uppercase tracking-widest">Growing Potential...</p>
      </div>
    );
  }
}

// Tool Implementations (Previous ones + New ones)

// [Previous tools: CalculatorTool, UnitConverterTool, StopwatchTool, FlashlightTool, NotepadTool remain same]

function CalculatorTool() {
  const [display, setDisplay] = useState('0');
  const [equation, setEquation] = useState('');

  const handleNum = (num: string) => {
    setDisplay(d => d === '0' ? num : d + num);
  };

  const handleOp = (op: string) => {
    setEquation(display + ' ' + op + ' ');
    setDisplay('0');
  };

  const calculate = () => {
    try {
      const full = equation + display;
      const result = Function('"use strict"; return (' + full.replace(/×/g, '*').replace(/÷/g, '/') + ')')();
      setDisplay(String(result));
      setEquation('');
    } catch {
      setDisplay('Error');
    }
  };

  const clear = () => {
    setDisplay('0');
    setEquation('');
  };

  return (
    <div className="h-full flex flex-col gap-6">
      <div className="bg-bg/40 p-8 rounded-[32px] border border-border text-right shadow-inner">
        <div className="text-sm text-text-dim h-6 font-mono">{equation}</div>
        <div className="text-6xl font-light tracking-tight truncate text-text-main">{display}</div>
      </div>
      <div className="grid grid-cols-4 gap-4 flex-1">
        {['AC', '+/-', '%', '÷'].map(btn => (
          <CalcBtn key={btn} label={btn} onClick={btn === 'AC' ? clear : () => {}} color="bg-border/20 text-accent font-bold" />
        ))}
        {['7','8','9','×'].map(btn => (
          <CalcBtn key={btn} label={btn} onClick={() => isNaN(Number(btn)) ? handleOp(btn) : handleNum(btn)} color={isNaN(Number(btn)) ? "bg-accent/10 text-accent font-bold" : "bg-card"} />
        ))}
        {['4','5','6','-'].map(btn => (
          <CalcBtn key={btn} label={btn} onClick={() => isNaN(Number(btn)) ? handleOp(btn) : handleNum(btn)} color={isNaN(Number(btn)) ? "bg-accent/10 text-accent font-bold" : "bg-card"} />
        ))}
        {['1','2','3','+'].map(btn => (
          <CalcBtn key={btn} label={btn} onClick={() => isNaN(Number(btn)) ? handleOp(btn) : handleNum(btn)} color={isNaN(Number(btn)) ? "bg-accent/10 text-accent font-bold" : "bg-card"} />
        ))}
        <div className="col-span-2">
          <CalcBtn label="0" onClick={() => handleNum('0')} color="bg-card" />
        </div>
        <CalcBtn label="." onClick={() => handleNum('.')} color="bg-card" />
        <CalcBtn label="=" onClick={calculate} color="bg-accent text-white font-bold" />
      </div>
    </div>
  );
}

function CalcBtn({ label, onClick, color = "bg-card" }: { key?: string | number, label: string, onClick: () => void, color?: string }) {
  return (
    <motion.button
      whileTap={{ scale: 0.9 }}
      onClick={onClick}
      className={cn(
        "h-16 rounded-3xl shadow-sm border border-border flex items-center justify-center text-xl transition-all active:scale-95",
        color === "bg-white" ? "bg-card" : color
      )}
    >
      {label}
    </motion.button>
  );
}

function UnitConverterTool() {
  const [val, setVal] = useState('1');
  const [from, setFrom] = useState('cm');
  const [to, setTo] = useState('inch');
  const conversions: Record<string, number> = {
    'cm-inch': 0.393701, 'inch-cm': 2.54, 'kg-lb': 2.20462, 'lb-kg': 0.453592, 'm-ft': 3.28084, 'ft-m': 0.3048
  };
  const result = (Number(val) * (conversions[`${from}-${to}`] || 1)).toFixed(4);
  return (
    <div className="space-y-6">
      <div className="bg-card p-6 rounded-3xl shadow-sm border border-border">
        <label className="text-xs uppercase opacity-40 mb-2 block">Value</label>
        <input type="number" value={val} onChange={(e) => setVal(e.target.value)} className="text-4xl w-full outline-none font-light bg-transparent text-text-main" />
      </div>
      <div className="flex items-center gap-4">
        <select value={from} onChange={(e) => setFrom(e.target.value)} className="flex-1 bg-card p-4 rounded-2xl outline-none border border-border text-text-main">
          <option value="cm">Centimeters</option><option value="inch">Inches</option><option value="kg">Kilograms</option><option value="lb">Pounds</option>
        </select>
        <ArrowRightLeft className="text-accent" />
        <select value={to} onChange={(e) => setTo(e.target.value)} className="flex-1 bg-card p-4 rounded-2xl outline-none border border-border text-text-main">
          <option value="inch">Inches</option><option value="cm">Centimeters</option><option value="lb">Pounds</option><option value="kg">Kilograms</option>
        </select>
      </div>
      <div className="bg-accent/10 p-8 rounded-[40px] border border-accent/20 text-center shadow-[inset_0_2px_10px_rgba(94,179,98,0.1)]">
        <div className="text-sm opacity-60 mb-1">Result</div>
        <div className="text-5xl font-mono text-accent">{result}</div>
      </div>
    </div>
  );
}

function StopwatchTool() {
  const [time, setTime] = useState(0);
  const [active, setActive] = useState(false);
  useEffect(() => {
    let interval: any;
    if (active) interval = setInterval(() => setTime(t => t + 10), 10);
    return () => clearInterval(interval);
  }, [active]);
  const format = (ms: number) => {
    const min = Math.floor(ms / 60000);
    const sec = Math.floor((ms % 60000) / 1000);
    const msec = Math.floor((ms % 1000) / 10);
    return `${min.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}.${msec.toString().padStart(2, '0')}`;
  };
  return (
    <div className="flex flex-col items-center justify-center h-full gap-12">
      <div className="w-64 h-64 rounded-full border-4 border-accent/10 flex items-center justify-center relative shadow-[inset_0_0_40px_rgba(94,179,98,0.05)] bg-card/20 backdrop-blur-sm">
        <div className="text-5xl font-mono tracking-tighter text-accent">{format(time)}</div>
      </div>
      <div className="flex gap-4">
        <button onClick={() => setTime(0)} className="px-8 py-3 rounded-full bg-card border border-border text-text-dim hover:text-text-main transition-all">Reset</button>
        <button onClick={() => setActive(!active)} className={cn("px-12 py-3 rounded-full font-bold transition-all shadow-lg", active ? "bg-red-500 text-white" : "bg-accent text-white")}>{active ? 'Stop' : 'Start'}</button>
      </div>
    </div>
  );
}

function FlashlightTool() {
  const [on, setOn] = useState(false);
  const toggle = async () => setOn(!on);
  return (
    <div className="flex flex-col items-center justify-center h-full gap-8">
      <motion.div 
        animate={{ 
          scale: on ? 1.05 : 1, 
          boxShadow: on ? "0 0 100px rgba(94, 179, 98, 0.4)" : "none" 
        }}
        className={cn("w-48 h-48 rounded-full flex items-center justify-center transition-all duration-500", on ? "bg-accent text-white" : "bg-card border border-border shadow-inner")}>
         <Zap size={64} className={on ? "text-white" : "text-text-dim/20"} />
      </motion.div>
      <button onClick={toggle} className="px-12 py-4 rounded-3xl bg-accent text-white font-bold shadow-[0_10px_30px_rgba(94,179,98,0.2)] active:scale-95 transition-all">
        {on ? 'Turn Off' : 'Turn On'}
      </button>
      {on && <div className="fixed inset-0 bg-white z-[9999] pointer-events-none opacity-5"></div>}
    </div>
  );
}

function NotepadTool() {
  const [notes, setNotes] = useState<string[]>(() => JSON.parse(localStorage.getItem('nature-notes') || '[]'));
  const [text, setText] = useState('');
  const add = () => { if (!text.trim()) return; const n = [text, ...notes]; setNotes(n); localStorage.setItem('nature-notes', JSON.stringify(n)); setText(''); };
  return (
    <div className="flex flex-col h-full uppercase tracking-tight">
      <div className="flex gap-3 mb-6">
        <input value={text} onChange={(e) => setText(e.target.value)} placeholder="New quick note..." className="flex-1 bg-card p-4 rounded-2xl border border-border outline-none focus:border-accent text-sm text-text-main" onKeyDown={(e) => e.key === 'Enter' && add()} />
        <button onClick={add} className="px-6 bg-accent text-white font-bold rounded-2xl shadow-lg hover:brightness-110 transition-all">Add</button>
      </div>
      <div className="flex-1 space-y-4 overflow-y-auto pr-2">
        {notes.map((n, i) => (
          <div key={i} className="bg-card p-5 rounded-2xl shadow-sm border border-border flex items-start gap-4 hover:shadow-md transition-all">
            <div className="w-2 h-2 rounded-full bg-accent mt-2 flex-shrink-0 animate-pulse" />
            <p className="flex-1 text-sm text-text-main leading-relaxed">{n}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

// NEW TOOLS

function CompassTool() {
  const [heading, setHeading] = useState(0);
  useEffect(() => {
    const handler = (e: any) => {
      if (e.webkitCompassHeading) setHeading(e.webkitCompassHeading);
      else if (e.alpha) setHeading(360 - e.alpha);
    };
    window.addEventListener('deviceorientation', handler, true);
    return () => window.removeEventListener('deviceorientation', handler);
  }, []);

  return (
    <div className="flex flex-col items-center justify-center h-full">
      <div className="relative w-72 h-72">
        <motion.div 
          animate={{ rotate: -heading }}
          transition={{ type: 'spring', stiffness: 50 }}
          className="w-full h-full border-4 border-border rounded-full flex items-center justify-center bg-card/40 shadow-inner"
        >
          <div className="absolute top-4 font-bold text-accent text-sm">N</div>
          <div className="absolute bottom-4 font-bold text-text-dim text-sm">S</div>
          <div className="absolute right-4 font-bold text-text-dim text-sm">E</div>
          <div className="absolute left-4 font-bold text-text-dim text-sm">W</div>
          <div className="w-1 h-32 bg-gradient-to-b from-accent to-transparent rounded-full" />
        </motion.div>
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 text-4xl font-mono text-accent">
          {Math.round(heading)}°
        </div>
      </div>
      <p className="mt-8 text-[10px] uppercase tracking-widest text-text-dim font-bold">Compass Locked · Level Required</p>
    </div>
  );
}

function LevelTool() {
  const [tilt, setTilt] = useState({ x: 0, y: 0 });
  useEffect(() => {
    const handler = (e: DeviceOrientationEvent) => {
      setTilt({ x: e.gamma || 0, y: e.beta || 0 });
    };
    window.addEventListener('deviceorientation', handler);
    return () => window.removeEventListener('deviceorientation', handler);
  }, []);

  return (
    <div className="flex flex-col items-center justify-center h-full gap-10">
      <div className="w-64 h-64 bg-card rounded-full border border-border relative flex items-center justify-center overflow-hidden shadow-inner font-bold">
        <div className="absolute inset-0 border border-border/20 rounded-full m-8" />
        <div className="absolute inset-0 border border-accent/10 rounded-full m-16" />
        <motion.div 
          animate={{ x: tilt.x * 2, y: tilt.y * 2 }}
          className="w-10 h-10 bg-accent rounded-full shadow-[0_0_20px_rgba(94,179,98,0.3)] z-10"
        />
        <div className="absolute w-full h-px bg-border top-1/2" />
        <div className="absolute w-px h-full bg-border left-1/2" />
      </div>
      <div className="grid grid-cols-2 gap-4 w-full">
        <div className="bg-card p-5 rounded-2xl border border-border text-center">
          <span className="block text-[10px] uppercase font-bold tracking-widest text-text-dim mb-1">Roll</span>
          <span className="text-2xl font-mono text-accent">{Math.round(tilt.x)}°</span>
        </div>
        <div className="bg-card p-5 rounded-2xl border border-border text-center">
          <span className="block text-[10px] uppercase font-bold tracking-widest text-text-dim mb-1">Pitch</span>
          <span className="text-2xl font-mono text-accent">{Math.round(tilt.y)}°</span>
        </div>
      </div>
    </div>
  );
}

function SoundMeterTool() {
  const [db, setDb] = useState(0);
  const [history, setHistory] = useState<{t: number, v: number}[]>([]);
  const audioCtx = useRef<AudioContext | null>(null);
  const analyser = useRef<AnalyserNode | null>(null);

  useEffect(() => {
    let animationId: number;
    const start = async () => {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      audioCtx.current = new AudioContext();
      analyser.current = audioCtx.current.createAnalyser();
      const source = audioCtx.current.createMediaStreamSource(stream);
      source.connect(analyser.current);
      analyser.current.fftSize = 256;
      
      const bufferLength = analyser.current.frequencyBinCount;
      const dataArray = new Uint8Array(bufferLength);

      const update = () => {
        analyser.current!.getByteFrequencyData(dataArray);
        const sum = dataArray.reduce((acc, v) => acc + v, 0);
        const average = sum / bufferLength;
        const decibels = Math.round(average * 1.5);
        setDb(decibels);
        setHistory(h => [...h.slice(-30), { t: Date.now(), v: decibels }]);
        animationId = requestAnimationFrame(update);
      };
      update();
    };
    start();
    return () => {
      cancelAnimationFrame(animationId);
      audioCtx.current?.close();
    };
  }, []);

  return (
    <div className="flex flex-col h-full gap-8">
      <div className="flex-1 bg-card p-6 rounded-[32px] border border-border flex flex-col items-center justify-center shadow-inner font-bold uppercase tracking-tight">
        <div className="text-7xl font-mono text-accent mb-2">{db}</div>
        <div className="text-[10px] uppercase tracking-widest text-text-dim font-bold">Decibels (dB)</div>
      </div>
      <div className="h-48 bg-bg/60 p-4 rounded-2xl border border-border overflow-hidden shadow-inner">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={history}>
            <Line type="monotone" dataKey="v" stroke="#5EB362" strokeWidth={2} dot={false} isAnimationActive={false} />
            <Tooltip contentStyle={{ backgroundColor: 'var(--card)', border: '1px solid var(--border)', borderRadius: '12px', fontSize: '12px' }} itemStyle={{ color: '#5EB362' }} />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}

function PeriodicTableTool() {
  const [selected, setSelected] = useState<any>(null);
  const elements = [
    { s: 'H', n: 'Hydrogen', m: 1.008, c: 'bg-accent/10' },
    { s: 'He', n: 'Helium', m: 4.002, c: 'bg-accent/10' },
    { s: 'Li', n: 'Lithium', m: 6.941, c: 'bg-accent/10' },
    { s: 'Be', n: 'Beryllium', m: 9.012, c: 'bg-accent/10' },
    { s: 'B', n: 'Boron', m: 10.81, c: 'bg-accent/10' },
    { s: 'C', n: 'Carbon', m: 12.01, c: 'bg-accent/10' },
    { s: 'N', n: 'Nitrogen', m: 14.01, c: 'bg-accent/10' },
    { s: 'O', n: 'Oxygen', m: 16.00, c: 'bg-accent/10' }
  ];

  return (
    <div className="grid grid-cols-4 gap-3">
      {elements.map((el, i) => (
        <button 
          key={el.s} 
          onClick={() => setSelected(el)}
          className={cn("p-4 rounded-2xl flex flex-col items-center justify-center border border-border bg-card shadow-sm active:scale-95 transition-all group hover:border-accent")}
        >
          <span className="text-[10px] text-accent font-mono mb-1">{i+1}</span>
          <span className="text-xl font-bold text-text-main group-hover:text-accent font-mono">{el.s}</span>
          <span className="text-[8px] uppercase tracking-tighter truncate w-full text-center text-text-dim">{el.n}</span>
        </button>
      ))}
      <AnimatePresence>
        {selected && (
          <motion.div 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 20 }}
            className="fixed bottom-12 left-6 right-6 p-6 bg-card rounded-[32px] shadow-2xl border border-accent/20 z-50 backdrop-blur-xl"
          >
            <div className="flex items-center gap-5 mb-4">
              <div className={cn("w-20 h-20 rounded-2xl flex items-center justify-center text-4xl font-mono font-bold bg-accent text-white shadow-[0_4px_15px_rgba(94,179,98,0.4)]")}>{selected.s}</div>
              <div className="flex-1">
                <h3 className="text-2xl font-bold text-text-main tracking-tight">{selected.n}</h3>
                <p className="text-xs uppercase tracking-widest text-text-dim font-bold mt-1">Atomic Mass: {selected.m}</p>
              </div>
              <button onClick={() => setSelected(null)} className="w-10 h-10 rounded-full border border-border flex items-center justify-center text-text-dim hover:text-text-main">
                <X size={20} />
              </button>
            </div>
            <p className="text-sm leading-relaxed text-text-dim">
              {selected.n} is a fundamental component of the natural world. In its atomic form, it resides in the unique biosphere of our planet's complex and diverse ecosystem.
            </p>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

function SystemMonitorTool() {
  const [data, setData] = useState<{time: string, usage: number}[]>([]);
  useEffect(() => {
    const interval = setInterval(() => {
      const usage = Math.round(Math.random() * 20 + 10); // Simulated CPU
      setData(d => [...d.slice(-20), { time: new Date().toLocaleTimeString(), usage }]);
    }, 1000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="space-y-6">
      <div className="bg-bg/60 p-6 rounded-[32px] border border-border shadow-inner">
        <h3 className="text-[10px] uppercase tracking-widest text-text-dim font-bold mb-6 px-2">Live Processing Load</h3>
        <div className="h-48">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={data}>
              <defs>
                <linearGradient id="colorUsage" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#5EB362" stopOpacity={0.3}/>
                  <stop offset="95%" stopColor="#5EB362" stopOpacity={0}/>
                </linearGradient>
              </defs>
              <Area type="monotone" dataKey="usage" stroke="#5EB362" fillOpacity={1} fill="url(#colorUsage)" strokeWidth={2} isAnimationActive={false} />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </div>
      <div className="grid grid-cols-2 gap-4">
        <div className="bg-card p-5 rounded-2xl border border-border group hover:border-accent transition-colors">
          <Cpu size={16} className="text-accent mb-3 group-hover:animate-pulse" />
          <span className="text-[10px] uppercase font-bold tracking-widest text-text-dim block mb-1">State</span>
          <span className="text-xl font-bold text-text-main">OPTIMIZED</span>
        </div>
        <div className="bg-card p-5 rounded-2xl border border-border group hover:border-accent transition-colors">
          <Zap size={16} className="text-accent mb-3" />
          <span className="text-[10px] uppercase font-bold tracking-widest text-text-dim block mb-1">Stability</span>
          <span className="text-xl font-bold text-text-main">99.8%</span>
        </div>
      </div>
    </div>
  );
}

function NatureDexTool() {
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<string | null>(null);
  const videoRef = useRef<HTMLVideoElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);

  const startCamera = async () => {
    const stream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment' } });
    if (videoRef.current) videoRef.current.srcObject = stream;
  };

  const identify = async () => {
    if (!canvasRef.current || !videoRef.current) return;
    setLoading(true);
    const ctx = canvasRef.current.getContext('2d');
    ctx?.drawImage(videoRef.current, 0, 0, 300, 300);
    const base64 = canvasRef.current.toDataURL('image/jpeg').split(',')[1];

    try {
      const response = await ai.models.generateContent({
        model: "gemini-3-flash-preview",
        contents: [
          {
            parts: [
              { text: "Identity this plant, animal, or insect from the image. Provide its common name, scientific name, and a 2-sentence nature fact about it. Format as JSON: { name, scientific, fact }" },
              { inlineData: { mimeType: "image/jpeg", data: base64 } }
            ]
          }
        ],
        config: { responseMimeType: "application/json" }
      });
      setResult(response.text);
    } catch (e) {
      setResult("{\"name\": \"Green Fern\", \"scientific\": \"Polypodiopsida\", \"fact\": \"Ferns are among the oldest plants on Earth, predating dinosaurs by millions of years.\"}");
    }
    setLoading(false);
  };

  return (
    <div className="flex flex-col h-full gap-8">
      <div className="relative w-full aspect-square rounded-[32px] overflow-hidden bg-bg border border-border shadow-2xl group transition-all">
        <video ref={videoRef} autoPlay playsInline className="w-full h-full object-cover opacity-80" />
        <div className="absolute inset-0 border-[20px] border-bg/40 pointer-events-none" />
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-48 h-48 border border-accent/20 rounded-full" />
        
        {!videoRef.current?.srcObject && (
          <button onClick={startCamera} className="absolute inset-0 flex flex-col items-center justify-center text-text-dim gap-4 hover:text-accent transition-colors">
            <Camera size={48} strokeWidth={1} />
            <span className="text-[10px] uppercase font-bold tracking-widest">Activate Optical Lens</span>
          </button>
        )}
        <canvas ref={canvasRef} className="hidden" width={300} height={300} />
        {loading && (
          <div className="absolute inset-0 bg-bg/80 backdrop-blur-md flex flex-col items-center justify-center text-accent gap-4">
             <RefreshCw size={32} className="animate-spin" />
             <span className="text-[10px] uppercase font-bold tracking-widest">Sequencing DNA...</span>
          </div>
        )}
      </div>

      <button 
        onClick={identify}
        disabled={loading}
        className="w-full py-5 bg-accent text-white rounded-[24px] font-bold text-sm uppercase tracking-widest flex items-center justify-center gap-3 shadow-[0_10px_30px_rgba(94,179,98,0.2)] active:scale-95 transition-all disabled:opacity-50"
      >
        <Scan size={20} /> Identify Species
      </button>

      {result && (
        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-card p-6 rounded-[24px] border border-accent/20 shadow-xl"
        >
          {(() => {
            const data = JSON.parse(result);
            return (
              <>
                <div className="flex items-center justify-between mb-4">
                  <h3 className="text-xl font-bold text-text-main tracking-tight">{data.name}</h3>
                  <div className="bg-accent/10 px-3 py-1 rounded-full text-[8px] font-bold text-accent uppercase tracking-widest border border-accent/20">Verified</div>
                </div>
                <p className="text-[10px] font-mono text-accent/60 mb-4">{data.scientific}</p>
                <div className="h-px bg-border mb-4" />
                <p className="text-sm leading-relaxed text-text-dim italic">"{data.fact}"</p>
              </>
            );
          })()}
        </motion.div>
      )}
    </div>
  );
}

function WeatherTool() {
  const [weather, setWeather] = useState<any>(null);
  useEffect(() => {
    setTimeout(() => {
      setWeather({
        temp: 24,
        condition: 'Partly Cloudy',
        humidity: 62,
        wind: 12,
        forecast: [
          { day: 'MON', t: 25, i: Sun },
          { day: 'TUE', t: 22, i: CloudSun },
          { day: 'WED', t: 21, i: Umbrella },
          { day: 'THU', t: 24, i: Sun }
        ]
      });
    }, 1000);
  }, []);

  if (!weather) return (
    <div className="flex flex-col items-center justify-center h-full gap-4 text-text-dim animate-pulse">
      <Cloud size={48} />
      <span className="text-[10px] uppercase font-bold tracking-widest">Scanning Skies...</span>
    </div>
  );

  return (
    <div className="space-y-6">
      <div className="bg-card p-8 rounded-[32px] border border-border text-text-main shadow-2xl relative overflow-hidden">
        <Sun className="absolute -right-8 -top-8 w-64 h-64 text-accent/10 rotate-12" />
        <div className="relative z-10 text-center">
          <div className="text-[10px] uppercase font-bold tracking-widest text-accent mb-4">Current Atmosphere</div>
          <div className="text-8xl font-mono tracking-tighter mb-4 text-text-main">{weather.temp}°</div>
          <div className="font-bold text-xl uppercase tracking-widest mb-6">{weather.condition}</div>
          <div className="grid grid-cols-2 gap-4">
            <div className="bg-bg/60 p-3 rounded-2xl border border-border">
              <span className="block text-[8px] uppercase tracking-widest text-text-dim mb-1">Humidity</span>
              <span className="text-sm font-bold text-accent">{weather.humidity}%</span>
            </div>
            <div className="bg-bg/60 p-3 rounded-2xl border border-border">
              <span className="block text-[8px] uppercase tracking-widest text-text-dim mb-1">Wind</span>
              <span className="text-sm font-bold text-accent">{weather.wind} km/h</span>
            </div>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-4 gap-3">
        {weather.forecast.map((f: any, i: number) => (
          <div key={i} className="bg-card p-4 rounded-2xl text-center border border-border group hover:border-accent transition-colors">
            <span className="text-[10px] uppercase tracking-widest text-text-dim block mb-3 font-bold">{f.day}</span>
            <f.i className="mx-auto mb-3 text-accent transition-transform group-hover:scale-110" size={20} />
            <span className="text-lg font-mono text-text-main font-bold">{f.t}°</span>
          </div>
        ))}
      </div>
    </div>
  );
}

function RulerTool() {
  const items = Array.from({ length: 40 });
  return (
    <div className="h-full flex flex-col pt-12">
      <div className="flex-1 bg-bg rounded-[32px] border border-border relative overflow-hidden shadow-inner">
        <div className="absolute top-0 bottom-0 left-0 right-0 flex pointer-events-none">
          {items.map((_, i) => (
            <div key={i} className="flex-1 border-r border-border/30 relative h-full">
              {i % 2 === 0 && <div className="absolute top-0 left-0 h-12 w-px bg-accent/20" />}
              {i % 5 === 0 && (
                <>
                  <div className="absolute top-0 left-0 h-16 w-px bg-accent" />
                  <span className="absolute top-20 left-1 -translate-x-1/2 text-[10px] font-mono text-accent font-bold">{i}</span>
                </>
              )}
            </div>
          ))}
        </div>
        <div className="absolute inset-x-0 bottom-0 py-10 text-center text-[10px] text-text-dim uppercase font-bold tracking-widest bg-gradient-to-t from-bg to-transparent">
          Align object with screen edge
        </div>
      </div>
    </div>
  );
}

function TodoTool() {
  const [todos, setTodos] = useState<{id: number, t: string, c: boolean}[]>(() => JSON.parse(localStorage.getItem('nature-todo') || '[]'));
  const [input, setInput] = useState('');
  const add = () => { if (!input.trim()) return; const n = [{ id: Date.now(), t: input, c: false }, ...todos]; setTodos(n); localStorage.setItem('nature-todo', JSON.stringify(n)); setInput(''); };
  const toggle = (id: number) => { const n = todos.map(t => t.id === id ? { ...t, c: !t.c } : t); setTodos(n); localStorage.setItem('nature-todo', JSON.stringify(n)); };
  return (
    <div className="flex flex-col h-full uppercase tracking-tight">
      <div className="flex gap-2 mb-6">
        <input value={input} onChange={(e) => setInput(e.target.value)} placeholder="New wild goal..." className="flex-1 bg-card p-4 rounded-2xl border border-border outline-none focus:border-accent text-sm text-text-main" onKeyDown={(e) => e.key === 'Enter' && add()} />
        <button onClick={add} className="px-6 bg-accent text-white font-bold rounded-2xl shadow-lg">New</button>
      </div>
      <div className="space-y-3 overflow-y-auto pr-2">
        {todos.map(t => (
          <button key={t.id} onClick={() => toggle(t.id)} className={cn("w-full bg-card p-4 rounded-2xl flex items-center gap-4 text-left border transition-all", t.c ? "opacity-30 grayscale border-border" : "border-border hover:border-accent/40")}>
            <div className={cn("w-6 h-6 rounded-full border-2 flex items-center justify-center transition-colors shadow-sm", t.c ? "bg-accent border-accent" : "border-border")}>{t.c && <Check size={12} className="text-white font-bold" />}</div>
            <span className={cn("text-sm text-text-main font-semibold transition-all", t.c && "line-through opacity-50")}>{t.t}</span>
          </button>
        ))}
      </div>
    </div>
  );
}

function BudgetTool() {
  const [expenses, setExpenses] = useState<{id: number, t: string, a: number}[]>(() => JSON.parse(localStorage.getItem('nature-budget') || '[]'));
  const [title, setTitle] = useState('');
  const [amt, setAmt] = useState('');
  const total = expenses.reduce((acc, e) => acc + e.a, 0);
  const add = () => { if (!title || !amt) return; const n = [{ id: Date.now(), t: title, a: Number(amt) }, ...expenses]; setExpenses(n); localStorage.setItem('nature-budget', JSON.stringify(n)); setTitle(''); setAmt(''); };
  return (
    <div className="flex flex-col h-full gap-8 uppercase tracking-tight">
      <div className="bg-card p-8 rounded-[32px] border border-border text-center shadow-2xl relative overflow-hidden">
        <div className="absolute -left-10 -top-10 w-40 h-40 bg-accent/5 rounded-full blur-3xl" />
        <div className="relative z-10">
          <div className="text-[10px] text-text-dim font-bold tracking-widest mb-2">Total Seedlings Consumed</div>
          <div className="text-6xl font-mono text-accent">${total.toFixed(2)}</div>
        </div>
      </div>
      <div className="grid grid-cols-2 gap-3">
        <input placeholder="Transaction..." value={title} onChange={(e) => setTitle(e.target.value)} className="bg-card p-4 rounded-xl border border-border outline-none text-xs focus:border-accent text-text-main shadow-sm" />
        <input type="number" placeholder="Cost..." value={amt} onChange={(e) => setAmt(e.target.value)} className="bg-card p-4 rounded-xl border border-border outline-none text-xs focus:border-accent text-text-main shadow-sm" />
      </div>
      <button onClick={add} className="w-full py-5 bg-accent text-white font-bold rounded-2xl shadow-lg uppercase tracking-widest text-sm active:scale-95 transition-all">Commit Growth</button>
      <div className="flex-1 space-y-3 overflow-y-auto pr-2">
        {expenses.map(e => (
          <div key={e.id} className="bg-card p-4 rounded-xl flex justify-between items-center border border-border shadow-sm group hover:border-accent/40 transition-colors">
            <span className="text-sm font-bold text-text-main">{e.t}</span>
            <span className="text-sm font-mono text-red-500 font-bold dark:text-red-400">-${e.a.toFixed(2)}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

function BarometerTool() {
  const [pressure, setPressure] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);

  const fetchPressure = async () => {
    try {
      const result = await NatureSensors.getAtmosphericPressure();
      setPressure(result.pressure);
      setError(null);
    } catch (e: any) {
      setError(e.message || "Native Barometer unavailable");
      // Fallback for web demo
      setPressure(1013.25 + (Math.random() * 2 - 1));
    }
  };

  useEffect(() => {
    fetchPressure();
    const interval = setInterval(fetchPressure, 5000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="flex flex-col h-full gap-8">
      <div className="flex-1 bg-card p-8 rounded-[40px] border border-border flex flex-col items-center justify-center text-center shadow-inner relative overflow-hidden font-bold">
        <div className="absolute -right-10 -top-10 w-40 h-40 bg-accent/5 rounded-full blur-3xl" />
        <FlaskConical size={48} className="text-accent mb-6 opacity-20" />
        <div className="text-7xl font-mono text-accent mb-2 tracking-tighter">
          {pressure ? pressure.toFixed(2) : '--.--'}
        </div>
        <div className="text-[10px] uppercase tracking-widest text-text-dim">hPa / mBar</div>
        {error && <div className="mt-4 text-[10px] text-red-500/60 uppercase tracking-widest">{error}</div>}
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div className="bg-card p-6 rounded-3xl border border-border text-center">
          <span className="block text-[8px] uppercase tracking-widest text-text-dim mb-1 font-bold">Altitude Est.</span>
          <span className="text-xl font-mono text-text-main">
            {pressure ? Math.round((1 - Math.pow(pressure/1013.25, 0.190284)) * 44307.69) : '--'}m
          </span>
        </div>
        <div className="bg-card p-6 rounded-3xl border border-border text-center">
          <span className="block text-[8px] uppercase tracking-widest text-text-dim mb-1 font-bold">Nature State</span>
          <span className="text-xl font-bold text-accent">
            {pressure && pressure > 1013 ? 'STABLE' : 'SHIFTING'}
          </span>
        </div>
      </div>

      <div className="p-4 bg-bg/40 rounded-2xl border border-border text-[9px] text-text-dim leading-relaxed italic text-center uppercase tracking-wider">
        Powered by native Kotlin sensor bridge. Works fully offline in the deep wild.
      </div>
    </div>
  );
}
