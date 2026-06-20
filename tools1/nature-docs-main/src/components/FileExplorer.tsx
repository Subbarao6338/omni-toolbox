'use client';

import React, { useState, useCallback } from 'react';
import { useDocStore } from '@/store/useDocStore';
import {
  FileText,
  Search,
  Grid,
  List as ListIcon,
  MoreVertical,
  Upload,
  Calendar,
  Layers,
  ArrowUpDown,
  ArrowUp,
  ArrowDown,
  RefreshCw
} from 'lucide-react';
import { clsx } from 'clsx';
import { useDropzone } from 'react-dropzone';
import { motion } from 'framer-motion';
import { storage } from '@/lib/storage';
import { DocItem } from '@/types';
import { Trash2 } from 'lucide-react';
import { formatBytes } from '@/lib/utils/format';
import { getFileIconInfo } from '@/lib/utils/icons';

export const FileExplorer = () => {
  const { documents, setSelectedDoc, isLoading, selectedSource, addDocument, setDocuments, removeDocument, triggerRefresh } = useDocStore();
  const [searchQuery, setSearchQuery] = useState('');
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');
  const [sortBy, setSortBy] = useState<'name' | 'date'>('date');
  const [sortOrder, setSortOrder] = useState<'asc' | 'desc'>('desc');

  const filteredDocs = documents
    .filter(doc => doc.name.toLowerCase().includes(searchQuery.toLowerCase()))
    .sort((a, b) => {
      if (sortBy === 'name') {
        return sortOrder === 'asc'
          ? a.name.localeCompare(b.name)
          : b.name.localeCompare(a.name);
      } else {
        const dateA = new Date(a.updatedAt).getTime();
        const dateB = new Date(b.updatedAt).getTime();
        return sortOrder === 'asc' ? dateA - dateB : dateB - dateA;
      }
    });

  const onDrop = useCallback((acceptedFiles: File[]) => {
    acceptedFiles.forEach((file) => {
      const reader = new FileReader();

      reader.onload = async () => {
        const content = reader.result as ArrayBuffer;
        const newDoc: DocItem = {
          id: Math.random().toString(36).substring(7),
          name: file.name,
          type: file.type,
          size: file.size,
          updatedAt: new Date().toISOString(),
          source: 'local' as const,
          accountId: 'local',
          content: content
        };
        await storage.saveDocument(newDoc);
        addDocument(newDoc);
      };

      reader.readAsArrayBuffer(file);
    });
  }, [addDocument]);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    noClick: true,
  });

  const handleDelete = async (e: React.MouseEvent, docId: string) => {
    e.stopPropagation();
    if (confirm('Are you sure you want to delete this document?')) {
      if (selectedSource === 'local') {
        await storage.removeDocument(docId);
      }
      removeDocument(docId);
    }
  };

  return (
    <div className="flex-1 flex flex-col bg-surface h-full min-w-0" {...getRootProps()}>
      <input {...getInputProps()} />

      <header className="p-4 md:p-6 space-y-4">
        <div className="flex items-center justify-between gap-4">
          <div className="relative flex-1 max-w-2xl">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-on-surface-variant" size={20} />
            <input
              type="text"
              placeholder="Search your library..."
              className="w-full pl-12 pr-4 py-3 bg-surface-variant/30 text-on-surface rounded-2xl border-none focus:ring-2 focus:ring-primary transition-all outline-none"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>

          <div className="flex items-center gap-2">
            <div className="flex items-center bg-surface-variant/20 p-1 rounded-xl">
              <button
                onClick={() => {
                  if (sortBy === 'name') setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
                  else setSortBy('name');
                }}
                className={clsx(
                  "p-2 rounded-lg transition-all flex items-center gap-1 text-xs font-medium",
                  sortBy === 'name' ? "bg-surface shadow-sm text-primary" : "text-on-surface-variant hover:text-on-surface"
                )}
                title="Sort by Name"
              >
                Name
                {sortBy === 'name' && (sortOrder === 'asc' ? <ArrowUp size={12} /> : <ArrowDown size={12} />)}
              </button>
              <button
                onClick={() => {
                  if (sortBy === 'date') setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
                  else setSortBy('date');
                }}
                className={clsx(
                  "p-2 rounded-lg transition-all flex items-center gap-1 text-xs font-medium",
                  sortBy === 'date' ? "bg-surface shadow-sm text-primary" : "text-on-surface-variant hover:text-on-surface"
                )}
                title="Sort by Date"
              >
                Date
                {sortBy === 'date' && (sortOrder === 'asc' ? <ArrowUp size={12} /> : <ArrowDown size={12} />)}
              </button>
            </div>

            <div className="flex items-center bg-surface-variant/20 p-1 rounded-xl">
              {selectedSource !== 'local' && (
                <button
                  onClick={() => {
                    triggerRefresh();
                  }}
                  disabled={isLoading}
                  className={clsx(
                    "p-2 rounded-lg transition-all text-on-surface-variant hover:text-on-surface hover:bg-surface disabled:opacity-50",
                    isLoading && "text-primary"
                  )}
                  title="Refresh documents"
                >
                  <motion.div
                    animate={isLoading ? { rotate: 360 } : { rotate: 0 }}
                    transition={isLoading ? { repeat: Infinity, duration: 1, ease: "linear" } : { duration: 0.2 }}
                  >
                    <RefreshCw size={20} />
                  </motion.div>
                </button>
              )}
              <button
                onClick={() => setViewMode('grid')}
              className={clsx(
                "p-2 rounded-lg transition-all",
                viewMode === 'grid' ? "bg-surface shadow-sm text-primary" : "text-on-surface-variant hover:text-on-surface"
              )}
            >
              <Grid size={20} />
            </button>
            <button
              onClick={() => setViewMode('list')}
              className={clsx(
                "p-2 rounded-lg transition-all",
                viewMode === 'list' ? "bg-surface shadow-sm text-primary" : "text-on-surface-variant hover:text-on-surface"
              )}
            >
                <ListIcon size={20} />
              </button>
            </div>
          </div>
        </div>
      </header>

      <main className="flex-1 overflow-y-auto px-4 md:px-6 pb-24 md:pb-6">
        {selectedSource === 'local' && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            onClick={() => {
              const input = document.querySelector('input[type="file"]') as HTMLInputElement;
              input?.click();
            }}
            className={clsx(
              "mb-8 border-2 border-dashed rounded-3xl p-10 flex flex-col items-center justify-center transition-all cursor-pointer group",
              isDragActive
                ? "border-primary bg-primary-container/30"
                : "border-outline/20 hover:border-primary/50 hover:bg-surface-variant/20"
            )}
          >
            <div className="p-4 bg-primary-container text-on-primary-container rounded-2xl mb-4 group-hover:scale-110 transition-transform">
              <Upload size={32} />
            </div>
            <p className="text-lg font-medium text-on-surface text-center">
              {isDragActive ? "Drop to add files" : "Click or drag files to upload"}
            </p>
            <p className="text-sm text-on-surface-variant mt-2">Support for PDF, MD, TXT, HTML, and DOCX</p>
          </motion.div>
        )}

        {isLoading ? (
          <div className="flex flex-col items-center justify-center h-64 gap-4">
            <div className="relative w-12 h-12">
              <div className="absolute inset-0 rounded-full border-4 border-primary/20" />
              <div className="absolute inset-0 rounded-full border-4 border-primary border-t-transparent animate-spin" />
            </div>
            <p className="text-on-surface-variant font-medium">Fetching documents...</p>
          </div>
        ) : filteredDocs.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-64 text-on-surface-variant">
            <div className="p-6 bg-surface-variant/20 rounded-full mb-6">
              <FileText size={64} className="opacity-20" />
            </div>
            <h3 className="text-xl font-semibold text-on-surface">No documents found</h3>
            <p className="mt-2 text-center max-w-xs">Try adjusting your search or select a different source.</p>
          </div>
        ) : viewMode === 'list' ? (
          <div className="bg-surface border border-outline/10 rounded-3xl overflow-hidden shadow-sm">
            <div className="overflow-x-auto">
              <table className="w-full text-left">
                <thead>
                  <tr className="border-b border-outline/10 bg-surface-variant/10">
                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-wider text-on-surface-variant">Name</th>
                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-wider text-on-surface-variant hidden md:table-cell">Size</th>
                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-wider text-on-surface-variant hidden md:table-cell">Updated</th>
                    <th className="px-6 py-4 text-xs font-bold uppercase tracking-wider text-on-surface-variant hidden sm:table-cell">Source</th>
                    <th className="px-6 py-4 w-10"></th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-outline/5">
                  {filteredDocs.map((doc) => (
                    <tr
                      key={doc.id}
                      className="hover:bg-primary-container/20 cursor-pointer transition-colors group"
                      onClick={() => setSelectedDoc(doc)}
                    >
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-4">
                          {(() => {
                            const iconInfo = getFileIconInfo(doc.type, doc.name);
                            const Icon = iconInfo.icon;
                            return (
                              <div className={clsx("p-2 rounded-xl", iconInfo.bgColor, iconInfo.color)}>
                                <Icon size={20} />
                              </div>
                            );
                          })()}
                          <div>
                            <p className="font-semibold text-on-surface truncate max-w-[150px] md:max-w-md">{doc.name}</p>
                            <p className="text-xs text-on-surface-variant md:hidden">
                              {doc.size ? formatBytes(doc.size) : 'Unknown size'} • {new Date(doc.updatedAt).toLocaleDateString()} • {doc.source}
                            </p>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-sm text-on-surface-variant hidden md:table-cell">
                        {doc.size ? formatBytes(doc.size) : '--'}
                      </td>
                      <td className="px-6 py-4 text-sm text-on-surface-variant hidden md:table-cell">
                        <div className="flex items-center gap-2">
                          <Calendar size={14} />
                          {new Date(doc.updatedAt).toLocaleDateString()}
                        </div>
                      </td>
                      <td className="px-6 py-4 hidden sm:table-cell">
                        <span className="px-3 py-1 bg-surface-variant text-on-surface-variant rounded-full text-xs font-medium capitalize">
                          {doc.source}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-right">
                        <div className="flex justify-end gap-2">
                          {doc.source === 'local' && (
                            <button
                              onClick={(e) => handleDelete(e, doc.id)}
                              className="p-2 hover:bg-red-500/10 text-red-500 rounded-full opacity-0 group-hover:opacity-100 transition-opacity"
                              title="Delete local file"
                            >
                              <Trash2 size={18} />
                            </button>
                          )}
                          <button className="p-2 hover:bg-surface-variant rounded-full text-on-surface-variant opacity-0 group-hover:opacity-100 transition-opacity">
                            <MoreVertical size={18} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-4 md:gap-6">
            {filteredDocs.map((doc) => (
              <motion.div
                key={doc.id}
                layout
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                whileHover={{ y: -4 }}
                className="group relative bg-surface border border-outline/10 p-3 rounded-3xl hover:border-primary/50 hover:shadow-xl transition-all cursor-pointer"
                onClick={() => setSelectedDoc(doc)}
              >
                {(() => {
                  const iconInfo = getFileIconInfo(doc.type, doc.name);
                  const Icon = iconInfo.icon;
                  return (
                    <div className="aspect-[4/5] bg-surface-variant/20 rounded-2xl mb-4 flex items-center justify-center relative overflow-hidden">
                      <div className="absolute inset-0 bg-gradient-to-br from-primary/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity" />
                      <div className={clsx("transition-colors group-hover:opacity-80", iconInfo.color)}>
                        <Icon size={48} />
                      </div>
                      <div className="absolute bottom-2 right-2 p-1.5 bg-surface/80 backdrop-blur-sm rounded-lg shadow-sm">
                        <span className="text-[10px] font-bold uppercase text-on-surface-variant">{iconInfo.label}</span>
                      </div>
                    </div>
                  );
                })()}
                <div className="px-1">
                  <div className="flex items-center justify-between gap-2">
                    <p className="text-sm font-bold text-on-surface truncate flex-1">{doc.name}</p>
                    {doc.source === 'local' && (
                      <button
                        onClick={(e) => handleDelete(e, doc.id)}
                        className="p-1 hover:bg-red-500/10 text-red-500 rounded-lg opacity-0 group-hover:opacity-100 transition-opacity"
                      >
                        <Trash2 size={14} />
                      </button>
                    )}
                  </div>
                  <div className="flex items-center justify-between gap-2 mt-1">
                    <span className="text-[10px] font-medium text-on-surface-variant uppercase flex items-center gap-1">
                      <Layers size={10} />
                      {doc.source}
                    </span>
                    {doc.size && (
                      <span className="text-[10px] font-medium text-on-surface-variant">
                        {formatBytes(doc.size)}
                      </span>
                    )}
                  </div>
                </div>
              </motion.div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
};
