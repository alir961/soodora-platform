import { Preferences } from '@capacitor/preferences';
import { reject } from '@/utils';

const BEARER_TOKEN_KEY: string = 'BearerToken';
const USER_DATA_KEY: string = 'UserData';

async function saveBearerToken(token: string): Promise<void> {
  await Preferences.set({
    key: BEARER_TOKEN_KEY,
    value: token,
  });
}

async function loadBearerToken(): Promise<string> {
  return Preferences.get({ key: BEARER_TOKEN_KEY })
    .then((storeEntry) => {
      if (!storeEntry.value) return reject('Unable to retrieve user token');
      return storeEntry.value;
    })
    .catch(reject);
}

async function deleteBearerToken(): Promise<void> {
  await Preferences.remove({ key: BEARER_TOKEN_KEY });
}

async function deleteUserData(): Promise<void> {
  return Preferences.remove({ key: USER_DATA_KEY });
}

export { saveBearerToken, deleteBearerToken, loadBearerToken, deleteUserData };
