import axios, {AxiosResponse} from 'axios';
import {deleteBearerToken, deleteUserData} from '@/store/localStorage';

declare let window: any;

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
        response => response,
        error => handleError(error.response),
    );
}

async function handleError(response: AxiosResponse): Promise<void> {
    const hasResponseStatus: boolean = (!!response?.status);

    if (!hasResponseStatus || response.status === 502) {
        redirectToUrl('/maintenance');
    } else if (response.status === 401 || response.status === 403) {
        await deleteBearerToken();
        await deleteUserData();
        redirectToUrl('/auth' + retrieveCallbackURL());
    }
}