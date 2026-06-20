import { DocItem, Account } from '@/types';
import { DocumentProvider } from './base';
import { storage } from '../storage';

export class LocalProvider extends DocumentProvider {
  async listDocuments(account: Account): Promise<DocItem[]> {
    return await storage.getLocalDocuments();
  }

  async getDocumentContent(doc: DocItem, account: Account): Promise<string | ArrayBuffer> {
    if (doc.content) return doc.content;
    throw new Error('Local document content missing');
  }
}
