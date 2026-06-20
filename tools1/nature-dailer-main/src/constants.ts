import { Theme, Contact, CallLog } from './types';

export const THEMES: Theme[] = [
  {
    id: 'forest',
    name: 'Deep Forest',
    colors: {
      bg: '#1a2e1a',
      text: '#e6ede6',
      accent: '#4ade80',
      surface: 'rgba(20, 40, 20, 0.6)',
      dial: '#2d4c2d',
    },
    image: 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=1920&q=80',
  },
  {
    id: 'ocean',
    name: 'Ocean Breeze',
    colors: {
      bg: '#0c2431',
      text: '#e0f2fe',
      accent: '#38bdf8',
      surface: 'rgba(12, 36, 49, 0.6)',
      dial: '#1a3a4a',
    },
    image: 'https://images.unsplash.com/photo-1439405326854-014607f694d7?auto=format&fit=crop&w=1920&q=80',
  },
  {
    id: 'desert',
    name: 'Golden Desert',
    colors: {
      bg: '#2d1a0c',
      text: '#fef3c7',
      accent: '#fbbf24',
      surface: 'rgba(45, 26, 12, 0.6)',
      dial: '#4a2d1a',
    },
    image: 'https://images.unsplash.com/photo-1473580044384-7ba9967e16a0?auto=format&fit=crop&w=1920&q=80',
  },
  {
    id: 'frost',
    name: 'Winter Frost',
    colors: {
      bg: '#1a1d2e',
      text: '#eef2ff',
      accent: '#818cf8',
      surface: 'rgba(26, 29, 46, 0.6)',
      dial: '#2d324c',
    },
    image: 'https://images.unsplash.com/photo-1483921020237-2ff51e8e4b22?auto=format&fit=crop&w=1920&q=80',
  },
  {
    id: 'night',
    name: 'Starry Night',
    colors: {
      bg: '#050505',
      text: '#f5f5f5',
      accent: '#a855f7',
      surface: 'rgba(15, 15, 15, 0.6)',
      dial: '#1f1f1f',
    },
    image: 'https://images.unsplash.com/photo-1519681393784-d120267933ba?auto=format&fit=crop&w=1920&q=80',
  },
  {
    id: 'artistic',
    name: 'Artistic Flair',
    colors: {
      bg: '#F2F4F0',
      text: '#1A1C19',
      accent: '#2D4031',
      surface: '#FFFFFF',
      dial: 'rgba(122, 141, 110, 0.1)',
    },
    image: 'https://images.unsplash.com/photo-1541701494587-cb58502866ab?auto=format&fit=crop&w=1920&q=80',
  },
];

export const MOCK_CONTACTS: Contact[] = [
  {
    id: '1',
    name: 'Arjuna Smith',
    number: '+1 (555) 123-4567',
    image: 'https://i.pravatar.cc/150?u=arjuna',
    category: 'Family',
  },
  {
    id: '2',
    name: 'Leila Moss',
    number: '+1 (555) 987-6543',
    image: 'https://i.pravatar.cc/150?u=leila',
    category: 'Work',
  },
  {
    id: '3',
    name: 'Cedar Greene',
    number: '+1 (555) 456-7890',
    image: 'https://i.pravatar.cc/150?u=cedar',
    category: 'Friends',
  },
  {
    id: '4',
    name: 'River Stone',
    number: '+1 (555) 111-2222',
    image: 'https://i.pravatar.cc/150?u=river',
    category: 'Work',
  },
  {
    id: '5',
    name: 'Sky Blue',
    number: '+1 (555) 333-4444',
    image: 'https://i.pravatar.cc/150?u=sky',
    category: 'Family',
  },
];

export const MOCK_CALLS: CallLog[] = [
  {
    id: '1',
    name: 'Arjuna Smith',
    number: '+1 (555) 123-4567',
    time: '2 mins ago',
    type: 'incoming',
    duration: '15:20',
  },
  {
    id: '2',
    name: 'Leila Moss',
    number: '+1 (555) 987-6543',
    time: '45 mins ago',
    type: 'missed',
  },
  {
    id: '3',
    name: 'Unknown Number',
    number: '+1 (555) 000-0000',
    time: '2 hours ago',
    type: 'outgoing',
    duration: '0:45',
  },
];
