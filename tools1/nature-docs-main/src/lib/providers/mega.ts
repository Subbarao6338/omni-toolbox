import { DocItem, Account } from '@/types';
import { DocumentProvider } from './base';
import { Storage, File, MutableFile } from 'megajs';

export class MegaProvider extends DocumentProvider {
  private storageCache: Map<string, Storage> = new Map();

  private async getStorage(account: Account): Promise<Storage> {
    const { email, password } = account;

    if (!email || !password) throw new Error('Mega credentials missing');

    const cacheKey = email;
    if (this.storageCache.has(cacheKey)) {
      return this.storageCache.get(cacheKey)!;
    }

    return new Promise((resolve, reject) => {
      const storage = new Storage({
        email,
        password,
      }, (err) => {
        if (err) return reject(err);
        this.storageCache.set(cacheKey, storage);
        resolve(storage);
      });
    });
  }

  async listDocuments(account: Account): Promise<DocItem[]> {
    try {
      const storage = await this.getStorage(account);

      const files: DocItem[] = [];
      const traverse = (node: MutableFile) => {
        if (node.children) {
          node.children.forEach(traverse);
        } else {
          const mimeType = this.getMimeType(node.name || '');
          if (mimeType) {
            // Use handle from node if available, otherwise nodeId
            const id = (node as any).handle || node.nodeId || '';
            files.push({
              id,
              name: node.name || 'Untitled',
              type: mimeType,
              updatedAt: (node as any).mtime ? new Date((node as any).mtime * 1000).toISOString() : new Date().toISOString(),
              size: node.size,
              source: 'mega',
              accountId: account.id
            });
          }
        }
      };

      if (storage.root) {
        traverse(storage.root);
      }
      return files;
    } catch (error) {
      console.error('Mega listDocuments error:', error);
      return [];
    }
  }

  private getMimeType(filename: string): string | null {
    const name = filename.toLowerCase();
    if (name.endsWith('.pdf')) return 'application/pdf';
    if (name.endsWith('.md')) return 'text/markdown';
    if (name.endsWith('.txt')) return 'text/plain';
    if (name.endsWith('.html')) return 'text/html';
    if (name.endsWith('.docx')) return 'application/vnd.openxmlformats-officedocument.wordprocessingml.document';
    return null;
  }

  async getDocumentContent(doc: DocItem, account: Account): Promise<string | ArrayBuffer> {
    try {
      const storage = await this.getStorage(account);
      const file = storage.find(doc.id) as File;

      if (!file) throw new Error('File not found in Mega');

      return new Promise((resolve, reject) => {
        file.download({}, (err, data) => {
          if (err) return reject(err);
          if (!data) return reject(new Error('Mega download returned no data'));
          resolve(data.buffer as ArrayBuffer);
        });
      });
    } catch (error) {
      console.error('Mega getDocumentContent error:', error);
      throw error;
    }
  }
}
