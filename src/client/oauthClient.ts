import {OAuthProvider} from '@/client/profileClient';
import axios from 'axios';
import {getAuthorizedHeader} from '@/client/authenticationClient';

async function callDeleteProvider(provider: OAuthProvider) {
    return axios.delete(`/auth/${provider}`, {headers: await getAuthorizedHeader()});
}

export {callDeleteProvider};