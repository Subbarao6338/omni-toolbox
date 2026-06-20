'use client';

import React from 'react';
import { useDocStore, Theme, ColorScheme } from '@/store/useDocStore';
import { X, Moon, Sun, Monitor, Palette, User, Trash2 } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { clsx } from 'clsx';

interface SettingsModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const SettingsModal = ({ isOpen, onClose }: SettingsModalProps) => {
  const {
    theme,
    setTheme,
    colorScheme,
    setColorScheme,
    accounts,
    removeAccount
  } = useDocStore();

  const themeOptions: { value: Theme; icon: React.ReactNode; label: string }[] = [
    { value: 'light', icon: <Sun size={18} />, label: 'Light' },
    { value: 'dark', icon: <Moon size={18} />, label: 'Dark' },
    { value: 'system', icon: <Monitor size={18} />, label: 'System' },
  ];

  const colorOptions: { value: ColorScheme; color: string; label: string }[] = [
    { value: 'blue', color: 'bg-blue-500', label: 'Blue' },
    { value: 'green', color: 'bg-green-500', label: 'Green' },
    { value: 'purple', color: 'bg-purple-500', label: 'Purple' },
    { value: 'orange', color: 'bg-orange-500', label: 'Orange' },
  ];

  return (
    <AnimatePresence>
      {isOpen && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
            className="fixed inset-0 bg-black/50 backdrop-blur-sm"
          />
          <motion.div
            initial={{ opacity: 0, scale: 0.95, y: 20 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.95, y: 20 }}
            className="relative bg-surface w-full max-w-lg rounded-3xl shadow-2xl overflow-hidden flex flex-col max-h-[80vh]"
          >
            <header className="px-6 py-4 border-b border-outline/10 flex items-center justify-between">
              <h2 className="text-xl font-semibold flex items-center gap-2">
                <Palette className="text-primary" size={24} />
                Settings
              </h2>
              <button
                onClick={onClose}
                className="p-2 hover:bg-surface-variant rounded-full transition-colors"
              >
                <X size={20} />
              </button>
            </header>

            <div className="flex-1 overflow-y-auto p-6 space-y-8">
              {/* Theme Selection */}
              <section>
                <h3 className="text-sm font-bold text-on-surface-variant uppercase tracking-wider mb-4">Appearance</h3>
                <div className="grid grid-cols-3 gap-3">
                  {themeOptions.map((opt) => (
                    <button
                      key={opt.value}
                      onClick={() => setTheme(opt.value)}
                      className={clsx(
                        "flex flex-col items-center gap-2 p-4 rounded-2xl border-2 transition-all",
                        theme === opt.value
                          ? "border-primary bg-primary-container text-on-primary-container shadow-md"
                          : "border-outline/10 hover:border-primary/30 text-on-surface-variant"
                      )}
                    >
                      {opt.icon}
                      <span className="text-xs font-bold">{opt.label}</span>
                    </button>
                  ))}
                </div>
              </section>

              {/* Color Scheme Selection */}
              <section>
                <h3 className="text-sm font-bold text-on-surface-variant uppercase tracking-wider mb-4">Color Scheme</h3>
                <div className="grid grid-cols-4 gap-3">
                  {colorOptions.map((opt) => (
                    <button
                      key={opt.value}
                      onClick={() => setColorScheme(opt.value)}
                      className={clsx(
                        "flex flex-col items-center gap-2 p-3 rounded-2xl border-2 transition-all",
                        colorScheme === opt.value
                          ? "border-primary bg-primary-container shadow-md"
                          : "border-outline/10 hover:border-primary/30"
                      )}
                    >
                      <div className={clsx("w-8 h-8 rounded-full shadow-inner", opt.color)} />
                      <span className="text-[10px] font-bold text-on-surface">{opt.label}</span>
                    </button>
                  ))}
                </div>
              </section>

              {/* Account Management */}
              <section>
                <h3 className="text-sm font-bold text-on-surface-variant uppercase tracking-wider mb-4">Connected Accounts</h3>
                <div className="space-y-3">
                  {accounts.length === 0 ? (
                    <div className="p-8 text-center bg-surface-variant/20 rounded-2xl border-2 border-dashed border-outline/10">
                      <p className="text-sm text-on-surface-variant">No cloud accounts connected yet.</p>
                    </div>
                  ) : (
                    accounts.map((account) => (
                      <div
                        key={account.id}
                        className="flex items-center justify-between p-4 bg-surface-variant/20 rounded-2xl border border-outline/5"
                      >
                        <div className="flex items-center gap-3">
                          <div className="p-2 bg-primary/10 text-primary rounded-xl">
                            <User size={20} />
                          </div>
                          <div>
                            <p className="font-bold text-on-surface">{account.name}</p>
                            <p className="text-xs text-on-surface-variant capitalize">{account.source}</p>
                          </div>
                        </div>
                        <button
                          onClick={() => removeAccount(account.id)}
                          className="p-2 hover:bg-red-500/10 text-red-500 rounded-lg transition-colors"
                          title="Remove Account"
                        >
                          <Trash2 size={18} />
                        </button>
                      </div>
                    ))
                  )}
                </div>
              </section>
            </div>

            <footer className="p-6 bg-surface-variant/10 border-t border-outline/10 text-center">
              <p className="text-xs text-on-surface-variant">Nature Docs v1.0.0</p>
            </footer>
          </motion.div>
        </div>
      )}
    </AnimatePresence>
  );
};
