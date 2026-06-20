import React, { useRef } from 'react';
import { Upload, Download, FileJson } from 'lucide-react';

interface LocalFileSyncProps {
  onDataRestored: (data: any) => void;
  getCurrentData: () => any;
}

export default function LocalFileSync({ onDataRestored, getCurrentData }: LocalFileSyncProps) {
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleExport = () => {
    const data = getCurrentData();
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `nature_messages_backup_${new Date().toISOString().split('T')[0]}.json`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  };

  const handleImportClick = () => {
    fileInputRef.current?.click();
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (e) => {
      try {
        const json = JSON.parse(e.target?.result as string);
        if (Array.isArray(json)) {
          onDataRestored(json);
        } else {
          console.error("Invalid format");
        }
      } catch (err) {
        console.error(err);
      }
    };
    reader.readAsText(file);
    event.target.value = '';
  };

  const handleClear = () => {
    if (window.confirm("Are you sure you want to clear all data? This cannot be undone unless you have a backup.")) {
      onDataRestored([]);
    }
  };

  return (
    <div className="bg-white rounded-[28px] p-4 md:p-6 shadow-sm border border-[#eef2ef] flex flex-col gap-4 dark:bg-[#1a2e20] dark:border-[#2d5a27]">
      <div className="flex justify-between items-center">
        <div className="text-[10px] md:text-[11px] font-bold uppercase tracking-wider text-[#94a89a]">Local Ecology Management</div>
        <FileJson className="w-5 h-5 text-[#2d5a27]" />
      </div>

      <div className="grid grid-cols-2 gap-3">
        <button
          onClick={handleExport}
          className="p-3 md:p-4 bg-[#eef7ee] text-[#2d5a27] rounded-xl font-semibold flex flex-col items-center justify-center gap-1 md:gap-2 hover:bg-[#d8edd8] transition-all dark:bg-[#0d1a12] dark:text-[#4caf50]"
        >
          <Download className="w-5 h-5" />
          <span className="text-[10px]">Export</span>
        </button>
        <button
          onClick={handleImportClick}
          className="p-3 md:p-4 bg-[#f5f8f6] text-[#2d5a27] rounded-xl font-semibold flex flex-col items-center justify-center gap-1 md:gap-2 hover:bg-[#e8ecea] transition-all dark:bg-[#0d1a12] dark:text-[#4caf50]"
        >
          <Upload className="w-5 h-5" />
          <span className="text-[10px]">Import</span>
        </button>
      </div>

      <button 
        onClick={handleClear}
        className="w-full text-[10px] font-bold text-red-400 hover:text-red-600 transition-colors uppercase tracking-widest pt-2 border-t border-[#f5f8f6] dark:border-[#2d5a27]"
      >
        Clear All Local Memory
      </button>

      <input
        type="file"
        ref={fileInputRef}
        onChange={handleFileChange}
        accept=".json"
        className="hidden"
      />
    </div>
  );
}
