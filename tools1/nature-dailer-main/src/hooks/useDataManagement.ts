import { Contact, CallLog, ThemeType } from '../types';

export const exportAppData = (theme: ThemeType, calls: CallLog[]) => {
  const data = JSON.stringify({ theme, calls });
  const blob = new Blob([data], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `natura_dialer_backup_${new Date().toISOString().split('T')[0]}.json`;
  a.click();
  URL.revokeObjectURL(url);
};

export const importAppData = (file: File): Promise<any> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      try {
        const data = JSON.parse(e.target?.result as string);
        resolve(data);
      } catch (error) {
        reject(error);
      }
    };
    reader.onerror = reject;
    reader.readAsText(file);
  });
};

export const linkGoogleDrive = async (): Promise<boolean> => {
  // Simulate Google OAuth flow
  return new Promise((resolve) => {
    setTimeout(() => {
      console.log('Google Drive linked successfully (mock)');
      resolve(true);
    }, 2000);
  });
};

export const syncToGoogleDrive = async (theme: ThemeType, calls: CallLog[]) => {
  // Simulate data upload
  return new Promise((resolve) => {
    console.log('Syncing data to Google Drive...', { theme, calls });
    setTimeout(() => {
      console.log('Sync complete');
      resolve(true);
    }, 1500);
  });
};
