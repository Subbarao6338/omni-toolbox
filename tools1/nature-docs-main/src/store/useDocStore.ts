import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { DocItem, Account, SourceType } from '@/types';

export type Theme = 'light' | 'dark' | 'system';
export type ColorScheme = 'blue' | 'green' | 'purple' | 'orange';

interface DocState {
  accounts: Account[];
  selectedSource: SourceType;
  selectedAccount: string | null;
  documents: DocItem[];
  selectedDoc: DocItem | null;
  isLoading: boolean;
  refreshTrigger: number;

  // Theme state
  theme: Theme;
  colorScheme: ColorScheme;

  // Actions
  addAccount: (account: Account) => void;
  removeAccount: (accountId: string) => void;
  setSelectedSource: (source: SourceType) => void;
  setSelectedAccount: (accountId: string | null) => void;
  setDocuments: (docs: DocItem[]) => void;
  addDocument: (doc: DocItem) => void;
  updateDocument: (docId: string, updates: Partial<DocItem>) => void;
  setSelectedDoc: (doc: DocItem | null) => void;
  setLoading: (loading: boolean) => void;
  removeDocument: (docId: string) => void;
  triggerRefresh: () => void;

  // Theme actions
  setTheme: (theme: Theme) => void;
  setColorScheme: (scheme: ColorScheme) => void;
}

export const useDocStore = create<DocState>()(
  persist(
    (set) => ({
      accounts: [],
      selectedSource: 'local',
      selectedAccount: null,
      documents: [],
      selectedDoc: null,
      isLoading: false,
      refreshTrigger: 0,

      theme: 'system',
      colorScheme: 'blue',

      addAccount: (account) => set((state) => {
        const existingIndex = state.accounts.findIndex(a => a.id === account.id);
        if (existingIndex > -1) {
          const newAccounts = [...state.accounts];
          newAccounts[existingIndex] = account;
          return { accounts: newAccounts };
        }
        return { accounts: [...state.accounts, account] };
      }),

      removeAccount: (accountId) => set((state) => ({
        accounts: state.accounts.filter(a => a.id !== accountId)
      })),

      setSelectedSource: (source) => set((state) => ({
        selectedSource: source,
        selectedAccount: null,
        documents: [],
        selectedDoc: state.selectedDoc?.source === source ? state.selectedDoc : null
      })),

      setSelectedAccount: (accountId) => set((state) => {
        const account = state.accounts.find(a => a.id === accountId);
        const shouldClearDoc = state.selectedDoc &&
          state.selectedDoc.source !== 'local' &&
          state.selectedDoc.accountId !== accountId;

        return {
          selectedAccount: accountId,
          selectedDoc: shouldClearDoc ? null : state.selectedDoc
        };
      }),

      setDocuments: (docs) => set({
        documents: docs
      }),

      addDocument: (doc) => set((state) => ({
        documents: [...state.documents, doc]
      })),

      updateDocument: (docId, updates) => set((state) => {
        const newDocs = state.documents.map(d => d.id === docId ? { ...d, ...updates } : d);
        const shouldUpdateSelected = state.selectedDoc?.id === docId;
        return {
          documents: newDocs,
          selectedDoc: shouldUpdateSelected ? { ...state.selectedDoc!, ...updates } : state.selectedDoc
        };
      }),

      setSelectedDoc: (doc) => set({
        selectedDoc: doc
      }),

      setLoading: (loading) => set({
        isLoading: loading
      }),

      removeDocument: (docId) => set((state) => ({
        documents: state.documents.filter(d => d.id !== docId),
        selectedDoc: state.selectedDoc?.id === docId ? null : state.selectedDoc
      })),

      triggerRefresh: () => set((state) => ({
        refreshTrigger: state.refreshTrigger + 1
      })),

      setTheme: (theme) => set({ theme }),
      setColorScheme: (scheme) => set({ colorScheme: scheme }),
    }),
    {
      name: 'nature-docs-storage',
      partialize: (state) => ({
        accounts: state.accounts,
        theme: state.theme,
        colorScheme: state.colorScheme,
        selectedSource: state.selectedSource,
        selectedAccount: state.selectedAccount,
      }),
    }
  )
);
