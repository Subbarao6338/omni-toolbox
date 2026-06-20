import { LucideIcon } from 'lucide-react';

export type Category = 'System' | 'Tools' | 'Life' | 'Science' | 'Fun' | 'Media';

export interface Tool {
  id: string;
  name: string;
  icon: LucideIcon;
  category: Category;
  description: string;
  color: string;
}

export type Theme = 'light' | 'dark' | 'nature';
