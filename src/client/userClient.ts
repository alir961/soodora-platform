import axios from 'axios';
import { reject } from '@/utils';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { UserUpdatePayload } from '@/entity/UserUpdatePayload';
import { User } from '@/entity/User';

async function callRetrieveUser(slug: string): Promise<User> {
  return axios
    .get(`/users/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Unable to call API');
      return response.data as User;
    })
    .catch(reject);
}

async function callUpdateUser(slug: string, payload: UserUpdatePayload): Promise<void> {
  return axios
    .put(`/users/${slug}`, payload, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 202) return reject('Unable to call API');
    })
    .catch(reject);
}

async function callDeleteUser(slug: string): Promise<void> {
  return axios
    .delete(`/users/${slug}`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (!response || response.status !== 200) return reject('Unable to call API');
    })
    .catch(reject);
}

export { callRetrieveUser, callUpdateUser, callDeleteUser };
