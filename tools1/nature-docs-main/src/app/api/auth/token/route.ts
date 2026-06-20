import { cookies } from 'next/headers';
import { NextResponse } from 'next/server';

export async function GET(request: Request) {
  const { searchParams } = new URL(request.url);
  const source = searchParams.get('source');

  if (!source) {
    return NextResponse.json({ error: 'Missing source' }, { status: 400 });
  }

  const cookieStore = await cookies();
  const tokenName = `${source}_access_token`;
  let token = cookieStore.get(tokenName)?.value;

  // Handle token refresh for GDrive
  if (!token && source === 'gdrive') {
    const refreshToken = cookieStore.get('gdrive_refresh_token')?.value;
    if (refreshToken) {
      try {
        const { google } = await import('googleapis');
        const { getRedirectUri } = await import('@/lib/utils/url');
        const oauth2Client = new google.auth.OAuth2(
          process.env.GOOGLE_CLIENT_ID,
          process.env.GOOGLE_CLIENT_SECRET,
          getRedirectUri('/api/auth/gdrive/callback')
        );
        oauth2Client.setCredentials({ refresh_token: refreshToken });
        const { credentials } = await oauth2Client.refreshAccessToken();
        token = credentials.access_token || undefined;

        if (token) {
          const response = NextResponse.json({ accessToken: token });
          response.cookies.set('gdrive_access_token', token, {
            httpOnly: true,
            secure: process.env.NODE_ENV === 'production',
            sameSite: 'lax',
            path: '/',
            maxAge: 3600
          });
          return response;
        }
      } catch (error) {
        console.error('Failed to refresh GDrive token:', error);
      }
    }
  }

  if (!token) {
    return NextResponse.json({ error: 'Token not found' }, { status: 404 });
  }

  const response = NextResponse.json({ accessToken: token });

  // Optional: clear cookie after retrieval if you want to ensure it's only used once
  // response.cookies.delete(tokenName);

  return response;
}
