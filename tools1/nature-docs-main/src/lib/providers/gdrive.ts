import { DocItem, Account } from '@/types';
import { DocumentProvider } from './base';

interface GDriveFile {
  id: string;
  name: string;
  mimeType: string;
  modifiedTime: string;
  size?: string;
}

interface GDriveListResponse {
  files: GDriveFile[];
}

export class GDriveProvider extends DocumentProvider {
  async listDocuments(account: Account): Promise<DocItem[]> {
    if (!account.accessToken) return [];

    try {
      // Use standard fetch instead of googleapis library in client-side code
      const response = await fetch(
        `https://www.googleapis.com/drive/v3/files?pageSize=100&fields=files(id,name,mimeType,modifiedTime,size)&q=${encodeURIComponent(
          "(mimeType = 'application/pdf' or mimeType = 'text/markdown' or mimeType = 'text/plain' or mimeType = 'text/html' or mimeType = 'application/vnd.openxmlformats-officedocument.wordprocessingml.document') and trashed = false"
        )}`,
        {
          headers: {
            Authorization: `Bearer ${account.accessToken}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error(`GDrive API error: ${response.statusText}`);
      }

      const data = (await response.json()) as GDriveListResponse;
      const files = data.files || [];
      return files.map((file) => ({
        id: file.id,
        name: file.name,
        type: file.mimeType,
        updatedAt: file.modifiedTime,
        size: file.size ? parseInt(file.size) : undefined,
        source: 'gdrive',
        accountId: account.id
      }));
    } catch (error) {
      console.error('GDrive listDocuments error:', error);
      return [];
    }
  }

  async getDocumentContent(doc: DocItem, account: Account): Promise<string | ArrayBuffer> {
    if (!account.accessToken) throw new Error('Missing access token');

    try {
      const response = await fetch(
        `https://www.googleapis.com/drive/v3/files/${doc.id}?alt=media`,
        {
          headers: {
            Authorization: `Bearer ${account.accessToken}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error(`GDrive API error: ${response.statusText}`);
      }

      return await response.arrayBuffer();
    } catch (error) {
      console.error('GDrive getDocumentContent error:', error);
      throw error;
    }
  }
}
