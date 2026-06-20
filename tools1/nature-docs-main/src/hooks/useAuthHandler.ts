import { useEffect } from 'react';
import { useDocStore } from '@/store/useDocStore';

export const useAuthHandler = (showToast: (msg: string, type: 'error' | 'success') => void) => {
  const { addAccount, setSelectedAccount } = useDocStore();

  useEffect(() => {
    const fetchToken = async () => {
      const url = new URL(window.location.href);
      const source = url.searchParams.get('source');
      const error = url.searchParams.get('error');

      if (error === 'env_not_configured') {
        const provider = url.searchParams.get('provider');
        const providerName = provider === 'gdrive' ? 'Google Drive' : provider === 'notion' ? 'Notion' : 'the provider';
        showToast(`Configuration Error: ${providerName} is not properly configured. Please check your environment variables.`, 'error');
        window.history.replaceState({}, document.title, "/");
        return;
      }

      if (source && (source === 'gdrive' || source === 'notion')) {
        try {
          const res = await fetch(`/api/auth/token?source=${source}`);
          if (res.ok) {
            const { accessToken } = await res.json();
            const email = url.searchParams.get('email');
            const workspaceName = url.searchParams.get('workspaceName');
            const workspaceId = url.searchParams.get('workspaceId');

            const accountId = workspaceId || email || Math.random().toString(36).substring(7);
            const name = workspaceName || email || (source === 'gdrive' ? 'Google Drive' : 'Notion');

            addAccount({
              id: accountId,
              name,
              source: source as any,
              connected: true,
              accessToken
            });
            setSelectedAccount(accountId);
          }
        } catch (error) {
          console.error('Failed to fetch token from cookie:', error);
        } finally {
          // Clean up URL
          window.history.replaceState({}, document.title, "/");
        }
      }
    };

    fetchToken();
  }, [addAccount, setSelectedAccount, showToast]);
};
