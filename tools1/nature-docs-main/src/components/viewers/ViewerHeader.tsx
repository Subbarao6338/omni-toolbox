import React from 'react';
import { DocItem } from '@/types';
import { X, ExternalLink, Download } from 'lucide-react';
import { getFileIconInfo } from '@/lib/utils/icons';
import { downloadFile } from '@/lib/utils/download';
import { clsx } from 'clsx';

interface ViewerHeaderProps {
  doc: DocItem;
  onClose: () => void;
  children?: React.ReactNode;
}

export const ViewerHeader = ({ doc, onClose, children }: ViewerHeaderProps) => {
  const iconInfo = getFileIconInfo(doc.type, doc.name);
  const Icon = iconInfo.icon;

  const getSourceUrl = () => {
    if (doc.source === 'gdrive') return `https://drive.google.com/file/d/${doc.id}/view`;
    if (doc.source === 'notion') return `https://notion.so/${doc.id.replace(/-/g, '')}`;
    return null;
  };

  const sourceUrl = getSourceUrl();

  return (
    <div className="bg-surface px-4 py-2 border-b border-outline/10 flex items-center justify-between z-10">
      <div className="flex items-center gap-3 flex-1 min-w-0 pr-2 md:pr-4">
        <div className="flex items-center gap-2">
          <div className={clsx("p-1.5 rounded-lg shrink-0", iconInfo.bgColor, iconInfo.color)}>
            <Icon size={16} />
          </div>
          <h2 className="text-sm md:text-base font-medium truncate max-w-[120px] md:max-w-md" title={doc.name}>
            {doc.name}
          </h2>
        </div>

        {sourceUrl && (
          <a
            href={sourceUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="p-1.5 text-primary hover:bg-primary-container rounded-lg transition-colors shrink-0"
            title="Open in Source"
          >
            <ExternalLink size={18} />
          </a>
        )}

        <div className="hidden sm:flex items-center gap-2 ml-2">
          {children}
        </div>
      </div>

      <div className="flex items-center gap-1 md:gap-2">
        <div className="flex sm:hidden">
          {children}
        </div>
        <button
          onClick={() => doc.content && downloadFile(doc.name, doc.content, doc.type)}
          className="p-2 hover:bg-primary-container text-primary rounded-xl transition-colors shrink-0"
          title="Download"
        >
          <Download size={20} />
        </button>
        <button
          onClick={onClose}
          className="p-2 hover:bg-surface-variant rounded-full shrink-0"
          title="Close"
        >
          <X size={20} />
        </button>
      </div>
    </div>
  );
};
