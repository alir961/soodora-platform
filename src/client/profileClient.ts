import axios from 'axios';
import { reject } from '@/utils';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { User } from '@/entity/User';
import { UserUpdatePayload } from '@/entity/UserUpdatePayload';
import { ProfilePageResponse } from '@/entity/ProfileSearchResponse';

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

async function callUpdateProfile(payload: UserUpdatePayload): Promise<void> {
  return axios
    .put('/profile', payload, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Unable to call API');
    })
    .catch(reject);
}

async function callDeleteProfile(): Promise<void> {
  return axios
    .delete('/profile', { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Unable to call API');
    })
    .catch(reject);
}

async function callDeleteUserAvatar(): Promise<void> {
  return axios
    .delete('/profile/:avatar', { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Unable to call API');
    })
    .catch(reject);
}

async function callRetrieveLinkedOAuthProviders(): Promise<OAuthProvider[]> {
  return axios
    .get('/oauth/providers', { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data)
        return response.data.map((provider: OAuthProvider) => provider.toLowerCase()) as OAuthProvider[];
      return reject('Unable to call API');
    })
    .catch(reject);
}

async function callFollowUser(slug: string): Promise<void> {
  return axios
    .post(`/profile/followers/${slug}`, {}, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200) return Promise.resolve();
      return reject('Unable to call API');
    })
    .catch(reject);
}

async function callUnfollowUser(slug: string): Promise<void> {
  return axios
    .delete(`/profile/followers/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200) return Promise.resolve();
      return reject('Unable to call API');
    })
    .catch(reject);
}

async function searchProfile(
  username: string,
  onlyArtists: boolean,
  pageNumber: number,
  pageSize: number,
  sortValue: string
): Promise<ProfilePageResponse> {
  try {
    const response = await axios.get<ProfilePageResponse>(
      `/profile/search?username=${encodeURIComponent(username)}&onlyArtists=${onlyArtists}&page=${pageNumber}&size=${pageSize}&sort=${sortValue}`,
      {
        headers: await getAuthorizedHeader(),
      }
    );

    return response.data;
  } catch (error) {
    let message: string = 'Profile searching Failed!';

    if (error instanceof Error) message = error.message;
    return reject(message);
  }
}

export type { OAuthProvider };
export {
  retrieveAuthenticatedProfile,
  callUpdateProfile,
  callDeleteProfile,
  callDeleteUserAvatar,
  callRetrieveLinkedOAuthProviders,
  callFollowUser,
  callUnfollowUser,
  searchProfile,
};
