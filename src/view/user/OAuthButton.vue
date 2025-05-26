<script setup lang="ts">
import { PropType } from 'vue';
import { alertController, IonButton, IonIcon, toastController } from '@ionic/vue';
import { logoGoogle } from 'ionicons/icons';
import { useRouter } from 'vue-router';
import { redirectToUrl } from '@/axiosConfiguration';
import { loadBearerToken } from '@/store/localStorage';
import { redirectLogin } from '@/utils';
import { OAuthProvider } from '@/client/profileClient';
import { callDeleteProvider } from '@/client/oauthClient';

interface ProviderData {
  provider: OAuthProvider;
  icon: string;
  name: string;
}

const router = useRouter();

const props = defineProps({
  provider: {
    required: true,
    type: String as PropType<OAuthProvider>,
  },
  linked: Boolean,
  disabled: Boolean,
});
const emit = defineEmits(['unlink']);

const providers: ProviderData[] = [{ provider: 'google', icon: logoGoogle, name: 'Google' }];
const provider: ProviderData = providers.find((provider) => provider.provider === props.provider) || providers[0];

const notifyUnlink = async () => {
  const toast = await toastController.create({
    message: `You have unlinked ${provider.name}!`,
    duration: 1500,
    position: 'top',
  });

  await toast.present();
  emit('unlink');
};

const alertButtons = [
  {
    text: 'Cancel',
    role: 'cancel',
  },
  {
    text: 'OK',
    role: 'confirm',
    handler: () => {
      callDeleteProvider(props.provider)
        .then(notifyUnlink)
        .catch((reason) => console.error('Unable to unlink OAuth provider', reason));
    },
  },
];

const clickButton = async () => {
  if (!provider) return;

  if (props.linked) {
    const alert = await alertController.create({
      header: 'Confirmation',
      subHeader: 'Cannot be reversed!',
      message: `You will unlink ${provider.name} from your account. You will need to reauthorize again before using service. Are you sure?`,
      buttons: alertButtons,
    });

    await alert.present();
  } else {
    loadBearerToken()
      .then((token) => redirectToUrl(`${import.meta.env.VITE_API_URL}/auth/${provider.provider}?token=${token}`))
      .catch(() => redirectLogin(router));
  }
};
</script>

<template>
  <ion-button @click="clickButton" expand="block" :class="provider.provider" :disabled="disabled">
    <span class="wrapper">
      <ion-icon :icon="provider.icon" />
      {{ (props.linked ? 'Disconnect from ' : 'Connect with ') + provider.name }}
    </span>
  </ion-button>
</template>

<style scoped>
ion-button {
  --color: white;
  --box-shadow: 0 2px 6px 0 rgb(0, 0, 0, 0.25);

  --padding-top: 10px;
  --padding-bottom: 10px;

  border-radius: 20px;
  margin: 0 20px 20px 20px;

  & .wrapper {
    min-width: 80%;
    font-weight: bold;
    display: flex;
    justify-content: start;
    align-items: center;

    & ion-icon {
      margin-right: 10px;
      font-size: 24px;
    }
  }
}
</style>
