import { SourceType } from '@/types';
import { DocumentProvider } from './base';
import { GDriveProvider } from './gdrive';
import { NotionProvider } from './notion';
import { MegaProvider } from './mega';
import { LocalProvider } from './local';

class ProviderFactory {
  private providers: Partial<Record<SourceType, DocumentProvider>> = {};

  getProvider(source: SourceType): DocumentProvider {
    if (!this.providers[source]) {
      switch (source) {
        case 'gdrive':
          this.providers[source] = new GDriveProvider();
          break;
        case 'notion':
          this.providers[source] = new NotionProvider();
          break;
        case 'mega':
          this.providers[source] = new MegaProvider();
          break;
        case 'local':
          this.providers[source] = new LocalProvider();
          break;
        default:
          throw new Error(`Unsupported source type: ${source}`);
      }
    }
    return this.providers[source]!;
  }
}

export const providerFactory = new ProviderFactory();
