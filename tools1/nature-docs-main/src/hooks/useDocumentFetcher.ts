import { useEffect } from 'react';
import { useDocStore } from '@/store/useDocStore';
import { providerFactory } from '@/lib/providers/factory';
import { ToastType } from '@/components/Toast';

export const useDocumentFetcher = (showToast?: (message: string, type: ToastType) => void) => {
  const {
    selectedSource,
    selectedAccount,
    accounts,
    setDocuments,
    setLoading,
    refreshTrigger,
    selectedDoc,
    updateDocument,
  } = useDocStore();

  // Fetch list of documents
  useEffect(() => {
    const fetchDocs = async () => {
      setLoading(true);
      try {
        const provider = providerFactory.getProvider(selectedSource);
        const account = accounts.find(a => a.id === selectedAccount) || accounts.find(a => a.source === selectedSource);

        if (account || selectedSource === 'local') {
          const docs = await provider.listDocuments(account || { id: 'local', name: 'Local', source: 'local', connected: true });
          setDocuments(docs);
        } else {
          setDocuments([]);
        }
      } catch (error: any) {
        console.error('Failed to fetch docs:', error);
        let message = 'Failed to fetch documents from ' + selectedSource;
        if (error.message?.includes('401') || error.message?.includes('403') || error.message?.includes('token')) {
          message = `Authentication expired for ${selectedSource}. Please reconnect.`;
        } else if (error.message?.includes('network') || error.message?.includes('fetch')) {
          message = `Network error: Unable to reach ${selectedSource}.`;
        }
        showToast?.(message, 'error');
      } finally {
        setLoading(false);
      }
    };

    fetchDocs();
  }, [selectedSource, selectedAccount, accounts, setDocuments, setLoading, refreshTrigger, showToast]);

  // Fetch content when a document is selected
  useEffect(() => {
    const fetchContent = async () => {
      if (!selectedDoc || selectedDoc.content) return;

      try {
        const provider = providerFactory.getProvider(selectedDoc.source);
        const account = accounts.find(a => a.id === selectedDoc.accountId);
        const content = await provider.getDocumentContent(selectedDoc, account || { id: 'local', name: 'Local', source: 'local', connected: true });

        // Update the document in the store with its content
        updateDocument(selectedDoc.id, { content });
      } catch (error) {
        console.error('Failed to fetch document content:', error);
        showToast?.('Failed to load document content', 'error');
      }
    };

    fetchContent();
  }, [selectedDoc, accounts, updateDocument, showToast]);
};
