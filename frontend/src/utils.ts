import { retrieveCallbackURL } from '@/axiosConfiguration';
import { Router } from 'vue-router';
import { toastController } from '@ionic/vue';

function reject(reason: string | Error) {
  if (reason instanceof Error) return Promise.reject(reason);
  return Promise.reject(new Error(reason));
}

async function redirectLogin(router: Router) {
  await router.push('/authentication' + retrieveCallbackURL());
}

function capitalize(value: string): string {
  if (value.length < 1) return value;
  return value[0].toUpperCase() + value.slice(1).toLowerCase();
}

export type ToastStatus = 'success' | 'danger' | 'warning';

const showToast = async (message: string, color: ToastStatus = 'success') => {
  const toast = await toastController.create({
    message,
    duration: 2000,
    color,
    position: 'top',
  });

  await toast.present();
};

const formatDate = (isoDate: string): string => {
  const date = new Date(isoDate);
  const day = date.getDate().toString().padStart(2, '0');
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const year = date.getFullYear();
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');

  return `${day}/${month}/${year} ${hours}:${minutes}`;
};

export { reject, redirectLogin, capitalize, showToast, formatDate };
