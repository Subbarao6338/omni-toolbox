'use client';

import React, { useState } from 'react';
import { useDocStore } from '@/store/useDocStore';
import { SourceType } from '@/types';
import { MegaLoginModal } from './MegaLoginModal';
import { SettingsModal } from './SettingsModal';
import {
  HardDrive,
  Cloud,
  BookOpen,
  Plus,
  Settings,
  FolderOpen,
  ChevronRight,
  Trash2
} from 'lucide-react';
import { clsx } from 'clsx';
import { motion } from 'framer-motion';

const sources: { type: SourceType; icon: React.ReactNode; label: string }[] = [
  { type: 'local', icon: <HardDrive size={20} />, label: 'Local Files' },
  { type: 'gdrive', icon: <Cloud size={20} />, label: 'Google Drive' },
  { type: 'notion', icon: <BookOpen size={20} />, label: 'Notion' },
  { type: 'mega', icon: <Cloud size={20} />, label: 'Mega' },
];

export const Sidebar = () => {
  const [isMegaModalOpen, setIsMegaModalOpen] = useState(false);
  const [isSettingsOpen, setIsSettingsOpen] = useState(false);

  const {
    selectedSource,
    setSelectedSource,
    accounts,
    selectedAccount,
    setSelectedAccount,
    removeAccount,
  } = useDocStore();

  const filteredAccounts = accounts.filter(a => a.source === selectedSource);

  const handleAddAccount = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (selectedSource === 'local') return;

    if (selectedSource === 'mega') {
      setIsMegaModalOpen(true);
      return;
    }

    // Redirect to OAuth routes
    window.location.href = `/api/auth/${selectedSource}`;
  };

  const handleRemoveAccount = (e: React.MouseEvent, accountId: string) => {
    e.stopPropagation();
    if (confirm('Are you sure you want to disconnect this account?')) {
      removeAccount(accountId);
      if (selectedAccount === accountId) {
        setSelectedAccount(null);
      }
    }
  };

  return (
    <div className="w-full md:w-72 bg-surface text-on-surface h-full flex flex-col border-r border-outline/10">
      <div className="p-6">
        <h1 className="text-2xl font-semibold flex items-center gap-3 text-primary">
          <div className="p-2 bg-primary-container text-on-primary-container rounded-xl">
            <FolderOpen size={24} />
          </div>
          Nature Docs
        </h1>
      </div>

      <div className="flex-1 overflow-y-auto px-4 py-2 space-y-6">
        <div>
          <h2 className="px-4 text-sm font-medium text-on-surface-variant mb-2">Sources</h2>
          <div className="space-y-1">
            {sources.map((source) => (
              <button
                key={source.type}
                onClick={() => setSelectedSource(source.type)}
                className={clsx(
                  "w-full flex items-center justify-between px-4 py-3 rounded-2xl transition-all duration-200 group",
                  selectedSource === source.type
                    ? "bg-secondary-container text-on-secondary-container"
                    : "hover:bg-surface-variant/50 text-on-surface-variant"
                )}
              >
                <div className="flex items-center gap-3">
                  <div className={clsx(
                    "p-1.5 rounded-lg transition-colors",
                    selectedSource === source.type ? "text-primary" : "group-hover:text-primary"
                  )}>
                    {source.icon}
                  </div>
                  <span className="font-medium">{source.label}</span>
                </div>
                {selectedSource === source.type && (
                  <motion.div layoutId="active-indicator">
                    <div className="w-1.5 h-1.5 rounded-full bg-primary" />
                  </motion.div>
                )}
              </button>
            ))}
          </div>
        </div>

        <div>
          <div className="px-4 flex items-center justify-between mb-2">
            <h2 className="text-sm font-medium text-on-surface-variant">Accounts</h2>
            {selectedSource !== 'local' && (
              <button
                onClick={handleAddAccount}
                className="p-1.5 hover:bg-primary-container text-primary rounded-full transition-colors"
                title="Add account"
              >
                <Plus size={18} />
              </button>
            )}
          </div>
          <div className="space-y-1 px-1">
            {selectedSource === 'local' ? (
              <div className="px-4 py-3 bg-surface-variant/30 rounded-2xl text-sm text-on-surface-variant italic">
                No account needed for local files
              </div>
            ) : filteredAccounts.length === 0 ? (
              <button
                onClick={handleAddAccount}
                className="w-full flex flex-col items-center justify-center p-6 border-2 border-dashed border-outline/20 rounded-2xl hover:border-primary/50 transition-colors group"
              >
                <Plus className="text-outline group-hover:text-primary mb-1" size={24} />
                <span className="text-xs text-on-surface-variant group-hover:text-primary font-medium">Connect Account</span>
              </button>
            ) : (
              filteredAccounts.map((account) => (
                <button
                  key={account.id}
                  onClick={() => setSelectedAccount(account.id)}
                  className={clsx(
                    "w-full flex items-center gap-3 px-4 py-3 rounded-2xl text-sm transition-all group",
                    selectedAccount === account.id
                      ? "bg-primary text-on-primary shadow-md"
                      : "hover:bg-surface-variant/50 text-on-surface-variant"
                  )}
                >
                  <div className={clsx(
                    "w-2 h-2 rounded-full",
                    account.connected ? "bg-green-400" : "bg-red-400"
                  )} />
                  <span className="flex-1 text-left font-medium truncate">{account.name}</span>
                  <div className="flex items-center gap-1">
                    <button
                      onClick={(e) => handleRemoveAccount(e, account.id)}
                      className={clsx(
                        "p-1 hover:bg-black/10 rounded transition-opacity",
                        selectedAccount === account.id ? "text-on-primary hover:bg-white/20" : "text-on-surface-variant hover:text-red-500",
                        "opacity-0 group-hover:opacity-100"
                      )}
                      title="Remove account"
                    >
                      <Trash2 size={14} />
                    </button>
                    <ChevronRight size={14} className={clsx(
                      "opacity-0 group-hover:opacity-100 transition-opacity",
                      selectedAccount === account.id ? "text-on-primary" : "text-outline"
                    )} />
                  </div>
                </button>
              ))
            )}
          </div>
        </div>
      </div>

      <div className="p-4 border-t border-outline/10">
        <button
          onClick={() => setIsSettingsOpen(true)}
          className="w-full flex items-center gap-3 px-4 py-3 rounded-2xl text-on-surface-variant hover:bg-surface-variant transition-colors group"
        >
          <div className="p-1.5 group-hover:rotate-45 transition-transform duration-500">
            <Settings size={22} />
          </div>
          <span className="font-medium">Settings</span>
        </button>
      </div>

      <MegaLoginModal isOpen={isMegaModalOpen} onClose={() => setIsMegaModalOpen(false)} />
      <SettingsModal isOpen={isSettingsOpen} onClose={() => setIsSettingsOpen(false)} />
    </div>
  );
};
