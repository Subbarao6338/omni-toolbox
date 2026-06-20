'use client';

import React, { useState, useEffect, useRef } from 'react';
import { DocItem } from '@/types';
import { ChevronLeft, ChevronRight, ZoomIn, ZoomOut } from 'lucide-react';
import { Document, Page, pdfjs } from 'react-pdf';
import { useDocStore } from '@/store/useDocStore';
import { ViewerHeader } from './ViewerHeader';

// Set up the worker for react-pdf
pdfjs.GlobalWorkerOptions.workerSrc = `//unpkg.com/pdfjs-dist@${pdfjs.version}/build/pdf.worker.min.mjs`;

export const PdfViewer = ({ doc }: { doc: DocItem }) => {
  const { setSelectedDoc } = useDocStore();
  const [numPages, setNumPages] = useState<number | null>(null);
  const [pageNumber, setPageNumber] = useState(1);
  const [scale, setScale] = useState(1.0);
  const [error, setError] = useState<Error | null>(null);
  const [containerWidth, setContainerWidth] = useState<number>(0);
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const updateWidth = () => {
      if (containerRef.current) {
        setContainerWidth(containerRef.current.clientWidth - 48); // minus padding
      }
    };

    updateWidth();
    window.addEventListener('resize', updateWidth);
    return () => window.removeEventListener('resize', updateWidth);
  }, []);

  function onDocumentLoadSuccess({ numPages }: { numPages: number }) {
    setNumPages(numPages);
    setError(null);
  }

  function onDocumentLoadError(err: Error) {
    console.error('PDF Load Error:', err);
    setError(err);
  }

  return (
    <div className="flex flex-col h-full bg-surface-variant/10">
      <ViewerHeader doc={doc} onClose={() => setSelectedDoc(null)}>
        <div className="flex items-center bg-surface-variant/30 rounded-xl p-1">
          <button
            disabled={pageNumber <= 1}
            onClick={() => setPageNumber(prev => prev - 1)}
            className="p-1.5 hover:bg-surface disabled:opacity-30 rounded-lg transition-colors"
          >
            <ChevronLeft size={18} />
          </button>
          <span className="px-3 text-sm font-medium">
            {pageNumber} / {numPages || '--'}
          </span>
          <button
            disabled={numPages ? pageNumber >= numPages : true}
            onClick={() => setPageNumber(prev => prev + 1)}
            className="p-1.5 hover:bg-surface disabled:opacity-30 rounded-lg transition-colors"
          >
            <ChevronRight size={18} />
          </button>
        </div>

        <div className="flex items-center bg-surface-variant/30 rounded-xl p-1">
          <button onClick={() => setScale(s => Math.max(0.5, s - 0.1))} className="p-1.5 hover:bg-surface rounded-lg">
            <ZoomOut size={18} />
          </button>
          <span className="px-2 text-xs font-bold w-12 text-center">{Math.round(scale * 100)}%</span>
          <button onClick={() => setScale(s => Math.min(2.0, s + 0.1))} className="p-1.5 hover:bg-surface rounded-lg">
            <ZoomIn size={18} />
          </button>
        </div>
      </ViewerHeader>

      <div
        ref={containerRef}
        className="flex-1 overflow-auto p-4 md:p-8 flex justify-center"
      >
        <div className="bg-white shadow-2xl rounded-sm">
          <Document
            file={doc.url || doc.content}
            onLoadSuccess={onDocumentLoadSuccess}
            onLoadError={onDocumentLoadError}
            loading={
              <div className="flex flex-col items-center justify-center p-20 gap-4">
                <div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin" />
                <p className="text-on-surface-variant animate-pulse font-medium">Preparing document...</p>
              </div>
            }
            error={
              <div className="flex flex-col items-center justify-center p-20 gap-4 text-red-500">
                <p className="font-bold">Failed to load PDF</p>
                <p className="text-sm text-center max-w-xs">{error?.message || 'Unknown error occurred'}</p>
              </div>
            }
          >
            <Page
              pageNumber={pageNumber}
              scale={scale}
              width={containerWidth > 0 ? Math.min(containerWidth, 800 * scale) : undefined}
              renderTextLayer={true}
              renderAnnotationLayer={true}
              className="max-w-full"
            />
          </Document>
        </div>
      </div>
    </div>
  );
};
