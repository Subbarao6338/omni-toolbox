import { DocItem, Account } from '@/types';
import { DocumentProvider } from './base';
import { Client } from '@notionhq/client';
import { ListBlockChildrenResponse, SearchResponse, PageObjectResponse } from '@notionhq/client/build/src/api-endpoints';

type NotionBlock = ListBlockChildrenResponse['results'][number] & {
  type: string;
  [key: string]: any;
  has_children: boolean;
};

export class NotionProvider extends DocumentProvider {
  async listDocuments(account: Account): Promise<DocItem[]> {
    const token = account.accessToken || account.apiKey;
    if (!token) return [];

    const notion = new Client({ auth: token });

    try {
      const response = await notion.search({
        filter: { property: 'object', value: 'page' },
        sort: { direction: 'descending', timestamp: 'last_edited_time' }
      }) as SearchResponse;

      return response.results.map((page) => {
        if (page.object !== 'page') return null;
        const p = page as PageObjectResponse;
        const properties = p.properties;
        let title = 'Untitled';

        // Notion title can be in different properties depending on the page setup
        const titleProp = properties?.title || properties?.Name || properties?.Page;
        if (titleProp?.type === 'title' && titleProp.title?.[0]?.plain_text) {
          title = titleProp.title[0].plain_text;
        }

        return {
          id: p.id,
          name: title,
          type: 'text/markdown',
          updatedAt: p.last_edited_time || new Date().toISOString(),
          source: 'notion',
          accountId: account.id
        } as DocItem;
      }).filter((doc): doc is DocItem => doc !== null);
    } catch (error) {
      console.error('Notion fetch error:', error);
      return [];
    }
  }

  async getDocumentContent(doc: DocItem, account: Account): Promise<string | ArrayBuffer> {
    const token = account.accessToken || account.apiKey;
    if (!token) throw new Error('Missing Notion token');

    const notion = new Client({ auth: token });

    try {
      const markdown = await this.getBlocksMarkdown(notion, doc.id);
      return markdown || '# No content in Notion page';
    } catch (error) {
      console.error('Failed to fetch Notion page content:', error);
      throw error;
    }
  }

  private async getBlocksMarkdown(notion: Client, blockId: string, depth = 0): Promise<string> {
    const blocks = await notion.blocks.children.list({ block_id: blockId });
    let markdown = '';

    const processRichText = (richText: any[]) => {
      return richText.map((t) => {
        let text = t.plain_text;
        if (t.annotations.bold) text = `**${text}**`;
        if (t.annotations.italic) text = `*${text}*`;
        if (t.annotations.strikethrough) text = `~~${text}~~`;
        if (t.annotations.code) text = `\`${text}\``;
        if (t.href) text = `[${text}](${t.href})`;
        return text;
      }).join('');
    };

    for (const block of blocks.results as NotionBlock[]) {
      const type = block.type;
      const richText = block[type]?.rich_text || [];
      const text = processRichText(richText);
      const indent = '  '.repeat(depth);

      switch (type) {
        case 'paragraph':
          markdown += indent + text + '\n\n';
          break;
        case 'heading_1':
          markdown += indent + '# ' + text + '\n\n';
          break;
        case 'heading_2':
          markdown += indent + '## ' + text + '\n\n';
          break;
        case 'heading_3':
          markdown += indent + '### ' + text + '\n\n';
          break;
        case 'bulleted_list_item':
          markdown += indent + '* ' + text + '\n';
          break;
        case 'numbered_list_item':
          markdown += indent + '1. ' + text + '\n';
          break;
        case 'code':
          markdown += indent + '```' + (block.code.language || '') + '\n' + block.code.rich_text.map((t: any) => t.plain_text).join('') + '\n' + indent + '```\n\n';
          break;
        case 'quote':
          markdown += indent + '> ' + text + '\n\n';
          break;
        case 'callout':
          const icon = block.callout.icon?.emoji || 'ℹ️';
          markdown += indent + `> ${icon} ${text}\n\n`;
          break;
        case 'divider':
          markdown += indent + '---\n\n';
          break;
        case 'to_do':
          const checked = block.to_do.checked ? '[x]' : '[ ]';
          markdown += indent + checked + ' ' + text + '\n';
          break;
        case 'toggle':
          markdown += indent + `<details><summary>${text}</summary>\n\n`;
          if (block.has_children) {
            markdown += await this.getBlocksMarkdown(notion, block.id, depth + 1);
          }
          markdown += indent + `</details>\n\n`;
          break;
        case 'image':
          const imageUrl = block.image.type === 'external' ? block.image.external.url : block.image.file.url;
          const caption = block.image.caption?.map((t: any) => t.plain_text).join('') || 'Image';
          markdown += indent + `![${caption}](${imageUrl})\n\n`;
          break;
        case 'video':
          const videoUrl = block.video.type === 'external' ? block.video.external.url : block.video.file.url;
          markdown += indent + `[Video: ${videoUrl}](${videoUrl})\n\n`;
          break;
        case 'table':
          if (block.has_children) {
            const rows = await notion.blocks.children.list({ block_id: block.id });
            markdown += indent + this.processTable(rows.results as any[]) + '\n\n';
          }
          break;
      }

      // Recursively fetch children for lists if they have them (other than toggle which is handled above)
      if (type !== 'toggle' && type !== 'table' && block.has_children) {
        markdown += await this.getBlocksMarkdown(notion, block.id, depth + 1);
      }
    }

    return markdown;
  }

  private processTable(rows: any[]): string {
    let tableMarkdown = '';
    rows.forEach((row, index) => {
      const cells = row.table_row.cells.map((cell: any[]) => {
        return cell.map((t: any) => t.plain_text).join('');
      });
      tableMarkdown += '| ' + cells.join(' | ') + ' |\n';
      if (index === 0) {
        tableMarkdown += '| ' + cells.map(() => '---').join(' | ') + ' |\n';
      }
    });
    return tableMarkdown;
  }
}
