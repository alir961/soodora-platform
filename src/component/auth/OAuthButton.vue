<script setup lang="ts">
import { PropType, computed } from 'vue';
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
  mode: {
    type: String as PropType<'auth' | 'link'>,
    default: 'link',
  },
  fluid: {
    type: Boolean,
    default: false,
  },
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

const buttonText = computed(() => {
  if (props.mode === 'auth') return `Sign in with ${provider.name}`;
  return (props.linked ? 'Disconnect from ' : 'Connect with ') + provider.name;
});

const clickButton = async () => {
  if (!provider) return;

  if (props.mode === 'auth') {
    redirectToUrl(`${import.meta.env.VITE_API_URL}/auth/${provider.provider}`);
    return;
  }

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
  <div @click="clickButton()" :class="['oauth-button', provider.provider, { fluid: props.fluid }]">
    <span class="wrapper">
      <ion-icon :icon="provider.icon" />
      {{ buttonText }}
    </span>
  </div>
</template>

<style scoped>
.oauth-button {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 46px;
  background-color: var(--ion-color-step-150, #1a1a1a);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.35);
  cursor: pointer;
  user-select: none;
  outline: none;
  transition:
    transform 0.08s ease,
    filter 0.2s ease,
    box-shadow 0.2s ease;

  & .wrapper {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    width: auto;
    color: white;
    font-weight: 700;
    font-size: 1.1rem;
    line-height: 1;

    & ion-icon {
      margin: 0;
      font-size: 1.1em;
      line-height: 1;
      vertical-align: middle;
    }
  }
}

.oauth-button.fluid {
  width: 100%;
}

.oauth-button:hover {
  filter: saturate(110%);
  transform: translateY(-1px);
}
</style>
