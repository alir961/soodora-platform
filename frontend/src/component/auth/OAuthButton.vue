<script setup lang="ts">
import { PropType } from 'vue';
import { alertController, IonIcon } from '@ionic/vue';
import { logoGoogle } from 'ionicons/icons';
import { useRouter } from 'vue-router';
import { redirectToUrl } from '@/axiosConfiguration';
import { loadBearerToken } from '@/store/localStorage';
import { redirectLogin, showToast } from '@/utils';
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
});
const emit = defineEmits(['unlink']);

const providers: ProviderData[] = [{ provider: 'google', icon: logoGoogle, name: 'Google' }];
const provider: ProviderData = providers.find((provider) => provider.provider === props.provider) || providers[0];

const notifyUnlink = async () => {
  await showToast(`You have unlinked ${provider.name}!`, 'danger');
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
  <div style="display: flex; justify-content: center">
    <div @click="clickButton()" :class="['oauth-button', provider.provider]">
      <span class="wrapper">
        <ion-icon :icon="provider.icon" />
        {{ (props.linked ? 'Disconnect from ' : 'Connect with ') + provider.name }}
      </span>
    </div>
  </div>
</template>

<style scoped>
.oauth-button {
  width: 70%;
  margin: 0 auto 0.8rem;
  padding: 0.4rem 1rem;
  background-color: var(--ion-color-primary);
  border-radius: 15px;
  box-shadow: 0 2px 6px 0 rgba(0, 0, 0, 0.25);
  cursor: pointer;
  user-select: none;
  outline: none;
  transition: opacity 0.2s ease;

  & .wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    color: white;
    font-weight: bold;
    font-size: 1.2em;

    & ion-icon {
      margin-right: 0.4em;
    }
  }
}
</style>
