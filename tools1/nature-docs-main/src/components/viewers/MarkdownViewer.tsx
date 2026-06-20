'use client';

import React from 'react';
import { DocItem } from '@/types';
import MarkdownIt from 'markdown-it';
import DOMPurify from 'dompurify';
import hljs from 'highlight.js';
import 'highlight.js/styles/github-dark.css';
import { useDocStore } from '@/store/useDocStore';
import { ViewerHeader } from './ViewerHeader';

const md: MarkdownIt = new MarkdownIt({
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return '<pre><code class="hljs">' +
               hljs.highlight(str, { language: lang, ignoreIllegals: true }).value +
               '</code></pre>';
      } catch (__) {}
    }

    return '<pre><code class="hljs">' + md.utils.escapeHtml(str) + '</code></pre>';
  }
});

export const MarkdownViewer = ({ doc }: { doc: DocItem }) => {
  const { setSelectedDoc } = useDocStore();
  const content = typeof doc.content === 'string'
    ? doc.content
    : doc.content instanceof ArrayBuffer
      ? new TextDecoder().decode(doc.content)
      : '# No content available';

  const htmlContent = md.render(content);
  const sanitizedHtml = typeof window !== 'undefined' ? DOMPurify.sanitize(htmlContent) : htmlContent;

  return (
    <div className="flex flex-col h-full bg-surface">
      <ViewerHeader doc={doc} onClose={() => setSelectedDoc(null)} />

      <div className="flex-1 overflow-y-auto p-6 md:p-12">
        <article
          className="prose dark:prose-invert max-w-3xl mx-auto prose-headings:text-primary prose-a:text-primary prose-img:rounded-2xl"
          dangerouslySetInnerHTML={{ __html: sanitizedHtml }}
        />
      </div>
    </div>
  );
};
