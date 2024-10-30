import {Preferences} from "@capacitor/preferences";
import {reject} from "@/utils";
import {User} from "@/entity/User";

const BEARER_TOKEN_KEY: string = "BearerToken";
const USER_DATA_KEY: string = "UserData";

async function saveBearerToken(token: string): Promise<void> {
    return Preferences.set({
        key: BEARER_TOKEN_KEY,
        value: token,
    });
}

async function loadBearerToken(): Promise<string> {
    return Preferences.get({key: BEARER_TOKEN_KEY})
        .then((storeEntry) => {
            if (!storeEntry.value)
                return reject("Unable to retrieve user token");
            return storeEntry.value;
        })
        .catch(reject);
}

async function deleteBearerToken(): Promise<void> {
    return Preferences.remove({key: BEARER_TOKEN_KEY});
}

async function saveUserData(userData: User): Promise<void> {
    return Preferences.set({
        key: USER_DATA_KEY,
        value: JSON.stringify(userData),
    });
}

async function loadUserData(): Promise<User> {
    return Preferences.get({key: USER_DATA_KEY})
        .then((storeEntry) => {
            if (!storeEntry.value)
                return reject("Unable to retrieve user data");
            return JSON.parse(storeEntry.value);
        })
        .catch(reject);
}

async function deleteUserData(): Promise<void> {
    return Preferences.remove({key: USER_DATA_KEY});
}

export {saveBearerToken, deleteBearerToken, loadBearerToken, saveUserData, loadUserData, deleteUserData};
