'use client';

import React, { useState } from 'react';
import { X, Lock, Mail, AlertCircle } from 'lucide-react';
import { useDocStore } from '@/store/useDocStore';
import { Storage } from 'megajs';

export const MegaLoginModal = ({ isOpen, onClose }: { isOpen: boolean; onClose: () => void }) => {
  const { addAccount, setSelectedAccount } = useDocStore();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);

    try {
      // Validate credentials by attempting to initialize storage
      await new Promise((resolve, reject) => {
        new Storage({ email, password }, (err) => {
          if (err) return reject(err);
          resolve(true);
        });
      });

      const newAccount = {
        id: Math.random().toString(36).substring(7),
        name: email,
        source: 'mega' as const,
        connected: true,
        email,
        password
      };

      addAccount(newAccount);
      setSelectedAccount(newAccount.id);
      onClose();
    } catch (err: any) {
      console.error('Mega login error:', err);
      setError(err.message || 'Failed to connect to Mega. Please check your credentials.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-[110] flex items-center justify-center p-4">
      <div className="bg-surface w-full max-w-md rounded-3xl shadow-2xl overflow-hidden border border-outline/10">
        <header className="px-6 py-4 border-b border-outline/10 flex items-center justify-between">
          <h3 className="font-semibold text-on-surface">Connect Mega Account</h3>
          <button onClick={onClose} className="p-2 hover:bg-surface-variant rounded-full text-on-surface-variant transition-colors">
            <X size={20} />
          </button>
        </header>

        <form onSubmit={handleSubmit} className="p-6 space-y-6">
          {error && (
            <div className="p-4 bg-red-500/10 border border-red-500/20 rounded-2xl flex items-start gap-3 text-red-500 text-sm">
              <AlertCircle size={18} className="shrink-0 mt-0.5" />
              <p>{error}</p>
            </div>
          )}
          <div className="space-y-4">
            <div className="relative">
              <Mail className="absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant" size={18} />
              <input
                type="email"
                required
                placeholder="Email Address"
                className="w-full pl-10 pr-4 py-3 bg-surface-variant/30 text-on-surface rounded-2xl border-none focus:ring-2 focus:ring-primary transition-all outline-none"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant" size={18} />
              <input
                type="password"
                required
                placeholder="Password"
                className="w-full pl-10 pr-4 py-3 bg-surface-variant/30 text-on-surface rounded-2xl border-none focus:ring-2 focus:ring-primary transition-all outline-none"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          </div>
          <button
            type="submit"
            disabled={isLoading}
            className="w-full bg-primary text-on-primary py-3 rounded-2xl font-bold hover:shadow-lg hover:bg-primary/90 transition-all disabled:opacity-50"
          >
            {isLoading ? 'Connecting...' : 'Connect Account'}
          </button>
          <p className="text-[10px] text-center text-on-surface-variant">
            By connecting, you agree to Nature Docs accessing your Mega.nz files.
          </p>
        </form>
      </div>
    </div>
  );
};
