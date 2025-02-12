import axios from 'axios';
import { loadBearerToken } from '@/store/localStorage';
import { reject } from '@/utils';

interface LoginRequest {
  email: string;
  password: string;
}

interface RegisterRequest {
  username: string;
  firstname: string;
  lastname: string;
  email: string;
  password: string;
}

async function getAuthorizedHeader(): Promise<object> {
  try {
    const token: string = await loadBearerToken();

    return { Authorization: `Bearer ${token}` };
  } catch (e) {
    return {};
  }
}

async function checkStatus(): Promise<void> {
  return axios.get('/status').then((response) => {
    if (response && response.status === 200) return Promise.resolve();
    return reject('Application servers seems inaccessible');
  });
}

async function callLogin(credentials: LoginRequest): Promise<string> {
  return axios
    .post('/auth/login', credentials)
    .then((response) => {
      if (response.status === 200 && response.data?.token) {
        return response.data.token;
      }
      return reject('Invalid credentials');
    })
    .catch(reject);
}

async function callRegister(request: RegisterRequest): Promise<void> {
  return axios.post('/auth/register', request);
}

export type { LoginRequest, RegisterRequest };
export { getAuthorizedHeader, checkStatus, callLogin, callRegister };
