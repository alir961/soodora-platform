import {Preferences} from "@capacitor/preferences";
import {Ref} from "vue";

const saveToken = async (token: string) => {
    await Preferences.set({
        key: 'BearerToken',
        value: token,
    });
};

const getToken = async () => {
    const result = await Preferences.get({ key: 'BearerToken' });
    return result.value;
};

const removeToken = async () => {
    await Preferences.remove({ key: 'BearerToken' });
};

const saveUserInfo = async (userInfo: Ref) => {
    await Preferences.set({
        key: 'userInfo',
        value: JSON.stringify(userInfo.value),
    });
};

const removeUserInfo = async () => {
    await Preferences.remove({ key: 'userInfo' });
};

const getUserInfoFromPreferences = async () => {
    const storedUserInfo = await Preferences.get({ key: 'userInfo' });
    if (storedUserInfo.value) {
        try {
            return JSON.parse(storedUserInfo.value);
        } catch (error) {
            console.error('Error parsing userInfo:', error);
            return null;
        }
    }
    return null;
};

export { saveToken, removeToken, getToken, saveUserInfo, removeUserInfo, getUserInfoFromPreferences };
