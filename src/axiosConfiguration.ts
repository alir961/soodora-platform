import axios, { AxiosResponse } from 'axios';
import { deleteBearerToken, deleteUserData } from '@/store/localStorage';
import {alertController} from "@ionic/vue";

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
  axios.defaults.headers.common['Access-Control-Allow-Origin'] = '*';
  axios.defaults.headers.common['Access-Control-Allow-Methods'] = 'POST, GET, OPTIONS, PUT, DELETE';

  axios.interceptors.response.use(
    (response) => response,
    (error) => handleError(error.response)
  );
}

const alertButtons = [
  {
    text: 'OK',
    role: 'confirm',
  },
];

async function handleError(response: AxiosResponse): Promise<void> {
  const hasResponseStatus: boolean = !!response?.status;

  if (!hasResponseStatus || response.status === 502) {
    redirectToUrl('/maintenance');
  } else if (response.status === 401) {
    await deleteBearerToken();
    await deleteUserData();
    redirectToUrl('/auth' + retrieveCallbackURL());
  } else if (response.status === 403) {
    const alert = await alertController.create({
      header: 'Error',
      subHeader: 'Cannot process',
      message: `Access to this resource is unprovided`,
      buttons: alertButtons,
    });
    await alert.present();
  }
}
