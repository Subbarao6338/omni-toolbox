/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

export interface Message {
  id: string;
  sender: string;
  text: string;
  timestamp: string;
  category: 'Personal' | 'Work' | 'Promotions' | 'OTP' | 'Transactions' | 'Blocked';
  isBlocked: boolean;
  reminderAt?: string;
  isSpam?: boolean;
  spamReason?: string;
  isPinned?: boolean;
  isRead?: boolean;
}

const STORAGE_KEY = 'nature_messages_data';

const initialMessages: Message[] = [
  { id: '1', sender: 'Alice', text: 'Hey, did you see the forest photos?', timestamp: new Date().toISOString(), category: 'Personal', isBlocked: false, isPinned: true, isRead: true },
  { id: '2', sender: 'Work System', text: 'Server maintenance scheduled for midnight.', timestamp: new Date().toISOString(), category: 'Work', isBlocked: false, isPinned: false, isRead: false },
  { id: '3', sender: 'Eco Bank', text: 'Your monthly statement is ready.', timestamp: new Date().toISOString(), category: 'Promotions', isBlocked: false, isPinned: false, isRead: true },
  { id: '4', sender: '998877', text: 'Your OTP is 4432', timestamp: new Date().toISOString(), category: 'OTP', isBlocked: false, isPinned: false, isRead: false },
  { id: '5', sender: 'Nature Weekly', text: 'Sustainable gardening tips for the spring.', timestamp: new Date().toISOString(), category: 'Personal', isBlocked: false, isPinned: false, isRead: false },
  { id: '6', sender: 'Amazon', text: 'Your organic soil order has been shipped.', timestamp: new Date().toISOString(), category: 'Transactions', isBlocked: false, isPinned: false, isRead: true },
  { id: '7', sender: 'Unknown', text: 'WIN A FREE VACATION! Call now 1-800-SPAM', timestamp: new Date().toISOString(), category: 'Promotions', isBlocked: false, isPinned: false, isRead: false, isSpam: true, spamReason: 'Generic promotional offer with suspicious call-to-action.' },
  { id: '8', sender: 'Project Green', text: 'Budget approval pending for the new nursery.', timestamp: new Date().toISOString(), category: 'Work', isBlocked: false, isPinned: false, isRead: false },
];

export const dataService = {
  getMessages: (): Message[] => {
    const data = localStorage.getItem(STORAGE_KEY);
    if (!data) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(initialMessages));
      return initialMessages;
    }
    return JSON.parse(data);
  },

  saveMessages: (messages: Message[]) => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(messages));
  },

  addMessage: (message: Omit<Message, 'id' | 'timestamp' | 'isBlocked'>) => {
    const messages = dataService.getMessages();
    const newMessage: Message = {
      ...message,
      id: Math.random().toString(36).substr(2, 9),
      timestamp: new Date().toISOString(),
      isBlocked: false
    };
    messages.push(newMessage);
    dataService.saveMessages(messages);
    return newMessage;
  },

  blockSender: (sender: string) => {
    const messages = dataService.getMessages();
    const updated = messages.map(m => m.sender === sender ? { ...m, isBlocked: true } : m);
    dataService.saveMessages(updated);
  },

  setReminder: (id: string, time: string) => {
    const messages = dataService.getMessages();
    const updated = messages.map(m => m.id === id ? { ...m, reminderAt: time } : m);
    dataService.saveMessages(updated);
  },

  updateMessage: (id: string, updates: Partial<Message>) => {
    const messages = dataService.getMessages();
    const updated = messages.map(m => m.id === id ? { ...m, ...updates } : m);
    dataService.saveMessages(updated);
  },

  updateFullData: (data: Message[]) => {
    dataService.saveMessages(data);
  },

  deleteMessage: (id: string) => {
    const messages = dataService.getMessages();
    const updated = messages.filter(m => m.id !== id);
    dataService.saveMessages(updated);
  },

  bulkUpdate: (ids: string[], updates: Partial<Message>) => {
    const messages = dataService.getMessages();
    const updated = messages.map(m => ids.includes(m.id) ? { ...m, ...updates } : m);
    dataService.saveMessages(updated);
  },

  clearAll: () => {
    dataService.saveMessages([]);
  }
};
