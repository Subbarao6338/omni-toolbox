'use client';

import React from 'react';
import { DocItem } from '@/types';
import DOMPurify from 'dompurify';
import { useDocStore } from '@/store/useDocStore';
import { ViewerHeader } from './ViewerHeader';

export const HtmlViewer = ({ doc }: { doc: DocItem }) => {
  const { setSelectedDoc } = useDocStore();
  const content = typeof doc.content === 'string'
    ? doc.content
    : doc.content instanceof ArrayBuffer
      ? new TextDecoder().decode(doc.content)
      : '<html><body>No content available</body></html>';

  const sanitizedHtml = typeof window !== 'undefined' ? DOMPurify.sanitize(content, { WHOLE_DOCUMENT: true }) : content;

  return (
    <div className="flex flex-col h-full bg-white">
      <ViewerHeader doc={doc} onClose={() => setSelectedDoc(null)} />

      <div className="flex-1 bg-white">
        <iframe
          srcDoc={sanitizedHtml}
          className="w-full h-full border-none"
          title={doc.name}
        />
      </div>
    </div>
  );
};
