import { CallNote } from '../types';
import { useLocalStorage } from './useLocalStorage';

export function useCallNotes(callLogId: string) {
  const [notes, setNotes] = useLocalStorage<CallNote[]>('callNotes', []);

  const addNote = async (text: string) => {
    const newNote: CallNote = {
      id: Date.now().toString(),
      callLogId,
      text,
      timestamp: new Date().toISOString(),
    };
    setNotes(prev => [...prev, newNote]);
  };

  return { notes: notes.filter(n => n.callLogId === callLogId), addNote };
}
