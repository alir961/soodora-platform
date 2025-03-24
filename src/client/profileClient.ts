import axios from 'axios';
import { reject } from '@/utils';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { User } from '@/entity/User';

type OAuthProvider = 'google';

async function retrieveAuthenticatedProfile(): Promise<User> {
  return axios
    .get('/profile', { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data) return response.data as User;
      return reject('Unable to call API');
    })
    .catch(reject);
}

async function retrieveLinkedOAuthProviders(): Promise<OAuthProvider[]> {
  return axios
    .get('/oauth/providers', { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data)
        return response.data.map((provider: OAuthProvider) => provider.toLowerCase()) as OAuthProvider[];
      return reject('Unable to call API');
    })
    .catch(reject);
}

async function deleteUserAccount(slug: string): Promise<void> {
  return axios
    .delete('/users/' + slug, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Unable to call API');
    })
    .catch(reject);
}

export type { OAuthProvider };
export { retrieveAuthenticatedProfile, retrieveLinkedOAuthProviders, deleteUserAccount };
