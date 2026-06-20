import { NextResponse } from 'next/server';
import { google } from 'googleapis';
import { getBaseUrl, getRedirectUri } from '@/lib/utils/url';

export async function GET() {
  const clientId = process.env.GOOGLE_CLIENT_ID;
  const clientSecret = process.env.GOOGLE_CLIENT_SECRET;
  const baseUrl = getBaseUrl();

  if (!clientId || !clientSecret) {
    return NextResponse.redirect(`${baseUrl}/?error=env_not_configured&provider=gdrive`);
  }

  const oauth2Client = new google.auth.OAuth2(
    clientId,
    clientSecret,
    getRedirectUri('/api/auth/gdrive/callback')
  );

  const scopes = [
    'https://www.googleapis.com/auth/drive.readonly',
    'https://www.googleapis.com/auth/drive.metadata.readonly'
  ];

  const url = oauth2Client.generateAuthUrl({
    access_type: 'offline',
    scope: scopes,
    prompt: 'consent'
  });

  return NextResponse.redirect(url);
}
