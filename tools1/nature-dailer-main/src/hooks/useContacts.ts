import { Contact } from '../types';
import { useLocalStorage } from './useLocalStorage';

export function useContacts() {
  const [contacts, setContacts] = useLocalStorage<Contact[]>('contacts', []);

  return { contacts, loading: false };
}
