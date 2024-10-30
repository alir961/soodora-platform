import axios from "axios";
import {reject} from "@/utils";
import {getAuthorizedHeader} from "@/client/authenticationClient";
import {User} from "@/entity/User";

async function retrieveAuthenticatedProfile(): Promise<User> {
    return axios.get('/profile', {headers: await getAuthorizedHeader()})
        .then((response) => {
            if (response && response.status === 200 && response.data)
                return response.data as User;
            return reject('Unable to call API');
        })
        .catch(reject);
}

export {retrieveAuthenticatedProfile};