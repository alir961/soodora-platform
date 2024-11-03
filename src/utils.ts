import { retrieveCallbackURL } from '@/axiosConfiguration';
import { Router } from 'vue-router';
import { User } from '@/entity/User';
import { loadUserData, saveUserData } from '@/store/localStorage';
import { retrieveAuthenticatedProfile } from '@/client/profileClient';

function reject(reason: string | Error) {
  if (reason instanceof Error) return Promise.reject(reason);
  return Promise.reject(new Error(reason));
}

async function redirectLogin(router: Router) {
  await router.push('/auth' + retrieveCallbackURL());
}

function capitalize(value: string): string {
  if (value.length < 1) return value;
  return value[0].toUpperCase() + value.slice(1).toLowerCase();
}

async function getAuthenticatedUser(): Promise<User> {
  try {
    return await loadUserData();
  } catch (error) {
    const user: User = await retrieveAuthenticatedProfile();

    await saveUserData(user);
    return user;
  }
}

export { reject, redirectLogin, capitalize, getAuthenticatedUser };
