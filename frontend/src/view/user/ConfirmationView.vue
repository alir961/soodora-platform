<script setup lang="ts">
import { IonSpinner, onIonViewWillEnter } from '@ionic/vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { useRoute, useRouter } from 'vue-router';
import { computed, ref } from 'vue';
import { callConfirmEmail } from '@/client/authenticationClient';

const route = useRoute();
const router = useRouter();

const secret = computed(() => {
  return route.query.secret as string | undefined;
});

const isLoading = ref<boolean>(true);
const isConfirmed = ref<boolean>(false);

const redirectAuthentication = async () => {
  await router.push('/authentication');
};

onIonViewWillEnter(() => {
  if (secret.value)
    callConfirmEmail(secret.value)
      .then(() => (isConfirmed.value = true))
      .catch(() => (isConfirmed.value = false))
      .finally(() => {
        isLoading.value = false;
        setTimeout(redirectAuthentication, 3000);
      });
});
</script>

<template>
  <ApplicationViewport title="Email Confirmation" fullscreen>
    <section v-if="secret">
      <div v-if="isLoading">
        <ion-spinner name="lines" />
        <h1>Confirming your email...</h1>
        <p>Please wait a moment while we verify your account.</p>
      </div>
      <div v-else>
        <h1>{{ isConfirmed ? 'Welcome!' : 'Oops...' }}</h1>
        <p>
          {{
            isConfirmed
              ? 'Your email has been successfully confirmed. You’ll be redirected to the login page shortly.'
              : 'We couldn’t confirm your email. Please try registering again.'
          }}
        </p>
        <ion-spinner name="dots" />
      </div>
    </section>
    <div v-else>
      <h1>Confirm your email</h1>

      <p>A confirmation email has been sent to your address.</p>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
section {
  height: 100%;
}

div {
  height: 100%;
  width: 90%;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  & > h1 {
    text-align: center;
  }

  & > p {
    text-align: center;
  }
}
</style>
