import { DocItem, Account } from '@/types';

export abstract class DocumentProvider {
  abstract listDocuments(account: Account): Promise<DocItem[]>;
  abstract getDocumentContent(doc: DocItem, account: Account): Promise<string | ArrayBuffer>;
}
