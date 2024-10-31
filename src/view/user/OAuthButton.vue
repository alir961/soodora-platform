<script setup lang="ts">
import {PropType} from 'vue';
import {IonIcon, IonButton} from '@ionic/vue';
import {logoGoogle} from 'ionicons/icons';
import {useRouter} from 'vue-router';
import {redirectToUrl} from '@/axiosConfiguration';
import {loadBearerToken} from '@/store/localStorage';
import {capitalize, redirectLogin} from '@/utils';
import {OAuthProvider} from '@/client/profileClient';

interface ProviderData {
  provider: OAuthProvider;
  icon: string;
}

const router = useRouter();

const props = defineProps({
  provider: {
    required: true,
    type: String as PropType<OAuthProvider>,
  },
  linked: Boolean,
});

const providers: ProviderData[] = [
  {provider: 'google', icon: logoGoogle},
];
const provider: ProviderData | undefined = providers.find(provider => provider.provider === props.provider);

const startOAuthFlow = () => {
  if (!provider) return;
  loadBearerToken()
    .then(token => redirectToUrl(`http://localhost:8080/auth/${provider.provider}?token=${token}`))
    .catch(() => redirectLogin(router));
};
</script>

<template>
  <ion-button v-if="provider" @click="startOAuthFlow" expand="block" :class="provider.provider" :disabled="props.linked">
    <ion-icon :icon="provider.icon"/>
    {{ (props.linked ? 'Connected to ' : 'Connect with ') + capitalize(provider.provider) }}
  </ion-button>
</template>

<style scoped>
ion-button {
  --box-shadow: 0 2px 6px 0 rgb(0, 0, 0, 0.25);

  --padding-top: 10px;
  --padding-bottom: 10px;

  border-radius: 20px;
  margin: 0 20px 20px 20px;

  &.google {
    --background: #ea493b;
    --background-hover: #d42617;
    --background-activated: #9f1c11é;
    --background-focused: #b92114;

    --color: white;
  }

  & ion-icon {
    margin-right: 10px;
  }
}
</style>
