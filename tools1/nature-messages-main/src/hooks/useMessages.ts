import { useState, useEffect, useCallback } from 'react';
import { dataService, Message } from '../lib/dataService';

export function useMessages() {
  const [messages, setMessages] = useState<Message[]>([]);

  const refresh = useCallback(() => {
    setMessages(dataService.getMessages());
  }, []);

  useEffect(() => {
    refresh();
  }, [refresh]);

  const togglePin = (id: string) => {
    const msg = messages.find(m => m.id === id);
    if (msg) {
      dataService.updateMessage(id, { isPinned: !msg.isPinned });
      refresh();
    }
  };

  const markAsRead = (id: string) => {
    dataService.updateMessage(id, { isRead: true });
    refresh();
  };

  const blockSender = (sender: string) => {
    dataService.blockSender(sender);
    refresh();
  };

  const updateCategory = (id: string, category: Message['category']) => {
    dataService.updateMessage(id, { category, isBlocked: category === 'Blocked' });
    refresh();
  };

  const deleteMsg = (id: string) => {
    dataService.deleteMessage(id);
    refresh();
  };

  const bulkAction = (ids: string[], updates: Partial<Message>) => {
    dataService.bulkUpdate(ids, updates);
    refresh();
  };

  const createMsg = (sender: string, text: string, category: Message['category']) => {
    dataService.addMessage({ sender, text, category });
    refresh();
  };

  const markNotSpam = (id: string) => {
    dataService.updateMessage(id, { isSpam: false, spamReason: undefined });
    refresh();
  };

  return {
    messages,
    refresh,
    togglePin,
    markAsRead,
    blockSender,
    updateCategory,
    deleteMsg,
    bulkAction,
    createMsg,
    markNotSpam
  };
}
