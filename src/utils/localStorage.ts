import {Preferences} from "@capacitor/preferences";

const saveToken = async (token: string) => {
    await Preferences.set({
        key: 'BearerToken',
        value: token,
    });
};

const getToken = async () => {
    return (await Preferences.get({key: 'BearerToken'})).value;
};

const removeToken = async () => {
    await Preferences.remove({ key: 'BearerToken' });
};

const logout = async () => {
    await removeToken();
    window.location.reload()
    console.log('Logged out successfully, token removed.');
};

export { saveToken, removeToken, logout, getToken };
