import { retrieveCallbackURL } from '@/axiosConfiguration';
import { Router } from 'vue-router';
import { toastController } from '@ionic/vue';

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

const showToast = async (message: string, color: 'success' | 'danger' | 'warning' = 'success') => {
  const toast = await toastController.create({
    message,
    duration: 2000,
    color,
    position: 'top',
  });
  await toast.present();
};

export { reject, redirectLogin, capitalize, redirectExplore, showToast };
