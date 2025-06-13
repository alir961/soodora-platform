import axios from 'axios';
import { reject } from '@/utils';
import { getAuthorizedHeader } from '@/client/authenticationClient';
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

async function callRetrieveFollowers(user: User): Promise<User[]> {
  return axios
    .get(`/users/${user.slug}/followers`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data) return response.data as User[];
      return reject('Unable to call API');
    })
    .catch(reject);
}

async function callRetrieveFollowing(user: User): Promise<User[]> {
  return axios
    .get(`/users/${user.slug}/following`, { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data) return response.data as User[];
      return reject('Unable to call API');
    })
    .catch(reject);
}

export { callRetrieveUser, callRetrieveFollowers, callRetrieveFollowing };
