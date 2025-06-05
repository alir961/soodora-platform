import { retrieveCallbackURL } from '@/axiosConfiguration';
import { Router } from 'vue-router';
import { User } from '@/entity/User';
import { retrieveAuthenticatedProfile } from '@/client/profileClient';
import { toastController } from '@ionic/vue';
import { saveUserData } from '@/store/localStorage';

function reject(reason: string | Error) {
  if (reason instanceof Error) return Promise.reject(reason);
  return Promise.reject(new Error(reason));
}

async function redirectLogin(router: Router) {
  await router.push('/auth' + retrieveCallbackURL());
}

async function redirectExplore(router: Router) {
  await router.push('/explore' + retrieveCallbackURL());
}

function capitalize(value: string): string {
  if (value.length < 1) return value;
  return value[0].toUpperCase() + value.slice(1).toLowerCase();
}

async function userIdentifierNotFoundToast() {
  return await toastController.create({
    message: 'User identifier not found.',
    duration: 1500,
    position: 'top',
  });
}

async function getAuthenticatedUser(): Promise<User> {
  try {
    if (!(await retrieveAuthenticatedProfile())) {
      throw new Error('User not found');
    }
    return retrieveAuthenticatedProfile();
  } catch (error) {
    await saveUserData(await retrieveAuthenticatedProfile());
    return retrieveAuthenticatedProfile();
  }
}

export { reject, redirectLogin, capitalize, getAuthenticatedUser, redirectExplore, userIdentifierNotFoundToast };
