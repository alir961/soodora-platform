import axios, { AxiosResponse } from 'axios';
import { deleteBearerToken, deleteUserData } from '@/store/localStorage';
import { reject } from '@/utils';

declare let window: Window;

export function retrieveCallbackURL() {
  return `?callback=${window.location}`;
}

export function redirectToUrl(url: string) {
  window.location = url;
}

export function configureAxios(): void {
  axios.defaults.baseURL = import.meta.env.VITE_API_URL as string;
  axios.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

  axios.interceptors.response.use(
    (response) => response,
    async (error) => {
      await handleError(error.response);
      return reject(error);
    }
  );
}

async function handleError(response: AxiosResponse): Promise<void> {
  const hasResponseStatus: boolean = !!response?.status;

  if (!hasResponseStatus || response.status === 502) {
    redirectToUrl('/maintenance');
  } else if (response.status === 401) {
    await deleteBearerToken();
    await deleteUserData();
    redirectToUrl('/auth' + retrieveCallbackURL());
  }
}
