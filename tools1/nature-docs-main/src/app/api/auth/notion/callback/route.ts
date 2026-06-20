import { NextResponse } from 'next/server';
import axios from 'axios';
import { getBaseUrl, getRedirectUri } from '@/lib/utils/url';

export async function GET(request: Request) {
  const { searchParams } = new URL(request.url);
  const code = searchParams.get('code');
  const baseUrl = getBaseUrl();

  if (!code) {
    return NextResponse.redirect(`${baseUrl}?error=missing_code`);
  }

  const clientId = process.env.NOTION_CLIENT_ID;
  const clientSecret = process.env.NOTION_CLIENT_SECRET;
  const redirectUri = getRedirectUri('/api/auth/notion/callback');

  const auth = Buffer.from(`${clientId}:${clientSecret}`).toString('base64');

  try {
    const response = await axios.post('https://api.notion.com/v1/oauth/token', {
      grant_type: 'authorization_code',
      code,
      redirect_uri: redirectUri
    }, {
      headers: {
        'Authorization': `Basic ${auth}`,
        'Content-Type': 'application/json'
      }
    });

    const { access_token, workspace_name, workspace_id } = response.data;

    const response_redirect = NextResponse.redirect(
      `${baseUrl}?source=notion&workspaceName=${encodeURIComponent(workspace_name)}&workspaceId=${workspace_id}`
    );

    response_redirect.cookies.set('notion_access_token', access_token, {
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'lax',
      path: '/',
      maxAge: 3600 * 24 * 365 // Notion tokens usually don't expire soon
    });

    return response_redirect;
  } catch (error) {
    console.error('Notion auth failed:', error);
    return NextResponse.redirect(`${baseUrl}?error=notion_auth_failed`);
  }
}
