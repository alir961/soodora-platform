<script setup lang="ts">
import {PropType} from 'vue';
import {IonButton, IonIcon} from '@ionic/vue';
import {logoSoundcloud, logoYoutube} from 'ionicons/icons';
import {useRouter} from 'vue-router';
import {redirectToUrl} from '@/axiosConfiguration';
import {loadBearerToken} from '@/store/localStorage';
import {redirectLogin} from '@/utils';
import {OAuthProvider} from '@/client/profileClient';
import {callDeleteProvider} from '@/client/oauthClient';

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

const providers: ProviderData[] = [
  {provider: 'google', icon: logoYoutube, name: 'YouTube'},
  {provider: 'spotify', icon: 'logo-spotify', name: 'Spotify'},
  {provider: 'soundcloud', icon: logoSoundcloud, name: 'Soundcloud'},
];
const provider: ProviderData = providers.find(provider => provider.provider === props.provider) || providers[0];

const startOAuthFlow = async () => {
  if (!provider) return;

  if (props.linked) {
    await callDeleteProvider(props.provider)
      .then(() => emit('unlink'))
      .catch((reason) => console.log('Unable to unlink OAuth provider: ' + reason));
  } else {
    loadBearerToken()
      .then(token => redirectToUrl(`http://localhost:8080/auth/${provider.provider}?token=${token}`))
      .catch(() => redirectLogin(router));
  }
};
</script>

<template>
  <ion-button @click="startOAuthFlow" expand="block" :class="provider.provider" :disabled="disabled">
      <span class="wrapper">
        <ion-icon :icon="provider.icon"/>
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

  &.google {
    --background: #ff0808;
    --background-hover: #d42617;
    --background-activated: #9f1c11é;
    --background-focused: #b92114;
  }

  &.spotify {
    --background: #25d865;
    --background-hover: #d42617;
    --background-activated: #9f1c11é;
    --background-focused: #b92114;
  }

  &.soundcloud {
    --background: #ff5a08;
    --background-hover: #d42617;
    --background-activated: #9f1c11é;
    --background-focused: #b92114;
  }

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
