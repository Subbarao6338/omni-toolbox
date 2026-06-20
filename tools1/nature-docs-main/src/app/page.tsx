'use client';

import { useEffect, useState } from 'react';
import dynamic from 'next/dynamic';
import { Sidebar } from '@/components/Sidebar';
import { FileExplorer } from '@/components/FileExplorer';
import { Menu, X } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

const PdfViewer = dynamic(() => import('@/components/viewers/PdfViewer').then(mod => mod.PdfViewer), { ssr: false });
const MarkdownViewer = dynamic(() => import('@/components/viewers/MarkdownViewer').then(mod => mod.MarkdownViewer), { ssr: false });
const DocxViewer = dynamic(() => import('@/components/viewers/DocxViewer').then(mod => mod.DocxViewer), { ssr: false });
const HtmlViewer = dynamic(() => import('@/components/viewers/HtmlViewer').then(mod => mod.HtmlViewer), { ssr: false });
import { useDocStore } from '@/store/useDocStore';
import { useAuthHandler } from '@/hooks/useAuthHandler';
import { useDocumentFetcher } from '@/hooks/useDocumentFetcher';
import { Toast, ToastType } from '@/components/Toast';
import { ViewerHeader } from '@/components/viewers/ViewerHeader';

export default function Home() {
  const {
    selectedDoc,
    setSelectedDoc,
    theme,
    colorScheme,
  } = useDocStore();

  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [toast, setToast] = useState<{ message: string; type: ToastType; isVisible: boolean }>({
    message: '',
    type: 'info',
    isVisible: false
  });

  const showToast = (message: string, type: ToastType) => {
    setToast({ message, type, isVisible: true });
  };

  // Custom hooks for auth and document fetching
  useAuthHandler(showToast);
  useDocumentFetcher(showToast);

  // Apply theme attributes to document element
  useEffect(() => {
    const root = window.document.documentElement;

    const applyTheme = () => {
      let actualTheme = theme;
      if (theme === 'system') {
        actualTheme = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
      }
      root.setAttribute('data-theme', actualTheme);
    };

    applyTheme();
    root.setAttribute('data-color-scheme', colorScheme);

    if (theme === 'system') {
      const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
      const handleChange = () => applyTheme();
      mediaQuery.addEventListener('change', handleChange);
      return () => mediaQuery.removeEventListener('change', handleChange);
    }
  }, [theme, colorScheme]);

  const renderViewer = () => {
    if (!selectedDoc) return null;

    const type = selectedDoc.type.toLowerCase();
    const name = selectedDoc.name.toLowerCase();
    const isPdf = type.includes('pdf');
    const isMarkdown = type.includes('markdown') || type.includes('plain') || name.endsWith('.md') || name.endsWith('.txt');
    const isHtml = type.includes('html') || name.endsWith('.html');
    const isWord = type.includes('word') || name.endsWith('.docx');

    const hasViewer = isPdf || isMarkdown || isHtml || isWord;

    return (
      <div className="fixed inset-0 z-50 bg-surface flex flex-col md:relative md:inset-auto md:flex-1 h-full">
        {!hasViewer && <ViewerHeader doc={selectedDoc} onClose={() => setSelectedDoc(null)} />}
        <div className="flex-1 overflow-hidden">
          {isPdf && <PdfViewer doc={selectedDoc} />}
          {isMarkdown && <MarkdownViewer doc={selectedDoc} />}
          {isHtml && <HtmlViewer doc={selectedDoc} />}
          {isWord && <DocxViewer doc={selectedDoc} />}

          {!hasViewer && (
            <div className="flex flex-col items-center justify-center h-full text-on-surface-variant p-6 text-center">
              <div className="p-6 bg-surface-variant/20 rounded-full mb-6">
                <X size={64} className="opacity-20" />
              </div>
              <h3 className="text-xl font-semibold text-on-surface">No viewer available</h3>
              <p className="mt-2 max-w-xs">We don&apos;t support previewing this file type yet. You can still download it using the button in the header.</p>
            </div>
          )}
        </div>
      </div>
    );
  };

  return (
    <div className="flex h-screen overflow-hidden bg-surface text-on-surface">
      {/* Mobile Menu Button */}
      <button
        onClick={() => setIsSidebarOpen(true)}
        className="md:hidden fixed bottom-6 right-6 z-40 bg-primary text-on-primary p-4 rounded-2xl shadow-lg"
      >
        <Menu size={24} />
      </button>

      {/* Sidebar - Desktop */}
      <div className="hidden md:block">
        <Sidebar />
      </div>

      {/* Sidebar - Mobile */}
      <AnimatePresence>
        {isSidebarOpen && (
          <>
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setIsSidebarOpen(false)}
              className="fixed inset-0 bg-black/50 z-40 md:hidden"
            />
            <motion.div
              initial={{ x: '-100%' }}
              animate={{ x: 0 }}
              exit={{ x: '-100%' }}
              transition={{ type: 'spring', damping: 25, stiffness: 200 }}
              className="fixed inset-y-0 left-0 w-72 z-50 md:hidden"
            >
              <Sidebar />
            </motion.div>
          </>
        )}
      </AnimatePresence>

      <div className="flex-1 flex flex-col min-w-0">
        <FileExplorer />
      </div>

      {selectedDoc && renderViewer()}

      <Toast
        message={toast.message}
        type={toast.type}
        isVisible={toast.isVisible}
        onClose={() => setToast(prev => ({ ...prev, isVisible: false }))}
      />
    </div>
  );
}
