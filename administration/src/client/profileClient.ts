import axios from 'axios';
import { reject } from '@/utils';
import { getAuthorizedHeader } from '@/client/authenticationClient';
import { User } from '@/entity/User';

async function callRetrieveProfile(): Promise<User> {
  return axios
    .get('/profile', { headers: await getAuthorizedHeader() })
    .then((response) => {
      if (response && response.status === 200 && response.data) {
        const user = response.data as User;

        if (!user.roles || user.roles.length === 0) user.roles = ['USER'];
        return user;
      }
      return reject('Unable to call API');
    })
    .catch(reject);
}

export { callRetrieveProfile };
