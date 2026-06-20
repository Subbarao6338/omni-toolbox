export type SourceType = 'local' | 'gdrive' | 'notion' | 'mega';

export interface DocItem {
  id: string;
  name: string;
  type: string; // mime type or extension
  size?: number;
  updatedAt: string;
  source: SourceType;
  accountId: string;
  url?: string;
  content?: string | ArrayBuffer;
  path?: string;
}

export interface Account {
  id: string;
  name: string;
  source: SourceType;
  accessToken?: string;
  refreshToken?: string;
  apiKey?: string;
  email?: string;
  password?: string;
  connected: boolean;
}

export interface Source {
  type: SourceType;
  accounts: Account[];
}
