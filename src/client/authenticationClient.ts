import axios from 'axios';
import { loadBearerToken } from '@/store/localStorage';
import { reject } from '@/utils';

async function getAuthorizedHeader(): Promise<object> {
  try {
    const token: string | null = loadBearerToken();

    if (token === null) return {};
    return { Authorization: `Bearer ${token}` };
  } catch (error) {
    console.log(error);
    return {};
  }
}

async function callStatus(): Promise<void> {
  return axios.get('/status').then((response) => {
    if (response && response.status === 200) return Promise.resolve();
    return reject('Application servers seems inaccessible');
  });
}

export { getAuthorizedHeader, callStatus };
