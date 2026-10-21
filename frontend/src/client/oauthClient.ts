import { OAuthProvider } from '@/client/profileClient';
import axios from 'axios';
import { getAuthorizedHeader } from '@/client/authenticationClient';

async function callDeleteProvider(provider: OAuthProvider) {
  return axios.delete(`/auth/${provider}`, { headers: await getAuthorizedHeader() });
}

// Linking: the session token goes in the Authorization header, never in a URL.
async function callLinkProvider(provider: OAuthProvider): Promise<string> {
  const response = await axios.post(`/auth/${provider}/link`, null, { headers: await getAuthorizedHeader() });
  return response.data.url as string;
}

// After a sign-in, the redirect carries a one-time code that is traded for the session token.
async function callExchangeLoginCode(provider: OAuthProvider, code: string): Promise<string> {
  const response = await axios.post(`/auth/${provider}/token`, { code });
  return response.data.token as string;
}

export { callDeleteProvider, callLinkProvider, callExchangeLoginCode };
