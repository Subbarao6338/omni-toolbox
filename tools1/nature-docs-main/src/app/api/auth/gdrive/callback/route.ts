import { NextResponse } from 'next/server';
import { google } from 'googleapis';
import { getBaseUrl, getRedirectUri } from '@/lib/utils/url';

export async function GET(request: Request) {
  const { searchParams } = new URL(request.url);
  const code = searchParams.get('code');
  const baseUrl = getBaseUrl();

  if (!code) {
    return NextResponse.redirect(`${baseUrl}?error=missing_code`);
  }

  const oauth2Client = new google.auth.OAuth2(
    process.env.GOOGLE_CLIENT_ID,
    process.env.GOOGLE_CLIENT_SECRET,
    getRedirectUri('/api/auth/gdrive/callback')
  );

  try {
    const { tokens } = await oauth2Client.getToken(code);
    oauth2Client.setCredentials(tokens);

    // Fetch user info to get email
    const drive = google.drive({ version: 'v3', auth: oauth2Client });
    const about = await drive.about.get({ fields: 'user(emailAddress)' });
    const email = about.data.user?.emailAddress || 'Google Drive';

    const response = NextResponse.redirect(`${baseUrl}?source=gdrive&email=${encodeURIComponent(email)}`);

    // Securely store tokens in cookies
    response.cookies.set('gdrive_access_token', tokens.access_token || '', {
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'lax',
      path: '/',
      maxAge: 3600 // 1 hour
    });

    if (tokens.refresh_token) {
      response.cookies.set('gdrive_refresh_token', tokens.refresh_token, {
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        sameSite: 'lax',
        path: '/',
        maxAge: 30 * 24 * 3600 // 30 days
      });
    }

    return response;
  } catch (error) {
    console.error('Failed to exchange code for tokens (GDrive):', error);
    return NextResponse.redirect(`${baseUrl}?error=gdrive_auth_failed`);
  }
}
