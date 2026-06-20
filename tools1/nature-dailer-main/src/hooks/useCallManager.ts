import { useState } from 'react';
import { Contact, CallLog } from '../types';
import { useLocalStorage } from './useLocalStorage';

export function useCallManager() {
  const [ongoingCall, setOngoingCall] = useState<Contact | { name: string; number: string } | null>(null);
  const [callLogs, setCallLogs] = useLocalStorage<CallLog[]>('callLogs', []);
  const [blockedNumbers, setBlockedNumbers] = useLocalStorage<string[]>('blockedNumbers', []);

  const startCall = async (number: string, contacts: Contact[]) => {
    const contact = contacts.find(c => c.number === number);
    const callData = contact || { name: 'Unknown', number };
    setOngoingCall(callData);
  };

  const endCall = async () => {
    if (ongoingCall) {
      const newLog: CallLog = {
        ...ongoingCall,
        id: Date.now().toString(),
        time: new Date().toISOString(),
        type: 'outgoing',
      };
      setCallLogs(prev => [newLog, ...prev]);
    }
    setOngoingCall(null);
  };

  const blockNumber = async (number: string) => {
    setBlockedNumbers(prev => [...prev, number]);
  };

  return {
    ongoingCall,
    callLogs,
    setCallLogs,
    blockedNumbers,
    startCall,
    endCall,
    blockNumber,
    loading: false,
  };
}
