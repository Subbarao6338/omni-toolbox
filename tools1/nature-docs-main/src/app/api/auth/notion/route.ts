import { NextResponse } from 'next/server';
import { getBaseUrl, getRedirectUri } from '@/lib/utils/url';

export async function GET() {
  const clientId = process.env.NOTION_CLIENT_ID;
  const clientSecret = process.env.NOTION_CLIENT_SECRET;
  const baseUrl = getBaseUrl();

  if (!clientId || !clientSecret) {
    return NextResponse.redirect(`${baseUrl}/?error=env_not_configured&provider=notion`);
  }

  const redirectUri = getRedirectUri('/api/auth/notion/callback');

  const url = `https://api.notion.com/v1/oauth/authorize?client_id=${clientId}&response_type=code&owner=user&redirect_uri=${encodeURIComponent(redirectUri)}`;

  return NextResponse.redirect(url);
}
