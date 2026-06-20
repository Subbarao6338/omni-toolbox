import React from 'react';
import {
  FileText,
  FileCode,
  FileDigit,
  File as FileIcon,
  LucideIcon
} from 'lucide-react';

export interface IconInfo {
  icon: LucideIcon;
  color: string;
  bgColor: string;
  label: string;
}

export const getFileIconInfo = (type: string, name: string): IconInfo => {
  const lowercaseType = type.toLowerCase();
  const lowercaseName = name.toLowerCase();

  // PDF
  if (lowercaseType.includes('pdf') || lowercaseName.endsWith('.pdf')) {
    return {
      icon: FileDigit,
      color: 'text-red-600',
      bgColor: 'bg-red-500/10',
      label: 'PDF'
    };
  }

  // Markdown
  if (lowercaseType.includes('markdown') || lowercaseName.endsWith('.md')) {
    return {
      icon: FileCode,
      color: 'text-primary',
      bgColor: 'bg-primary/10',
      label: 'Markdown'
    };
  }

  // HTML
  if (lowercaseType.includes('html') || lowercaseName.endsWith('.html')) {
    return {
      icon: FileCode,
      color: 'text-orange-600',
      bgColor: 'bg-orange-500/10',
      label: 'HTML'
    };
  }

  // Word/DOCX
  if (lowercaseType.includes('word') || lowercaseType.includes('officedocument.wordprocessingml') || lowercaseName.endsWith('.docx')) {
    return {
      icon: FileText,
      color: 'text-blue-600',
      bgColor: 'bg-blue-500/10',
      label: 'Word'
    };
  }

  // Default
  return {
    icon: FileIcon,
    color: 'text-on-surface-variant',
    bgColor: 'bg-surface-variant/20',
    label: 'File'
  };
};
