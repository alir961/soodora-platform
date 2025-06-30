<script setup lang="ts">
import { IonCardContent, IonCardHeader, IonCardTitle } from '@ionic/vue';
import { User } from '@/entity/User';
import { PropType, computed } from 'vue';
import { useAuthStore } from '@/store/reactiveStorage';
import RoleTagComponent from '@/component/user/RoleTagComponent.vue';
import FollowCounter from '@/component/follow/FollowCounter.vue';
import FollowButton from '@/component/follow/FollowButton.vue';

const props = defineProps({
  profile: {
    required: true,
    type: Object as PropType<User>,
  },
});

const authStore = useAuthStore();

const canFollow = computed(() => {
  return authStore.getAuthenticatedUserSlug !== props.profile.slug;
});

const formattedDescription = computed(() => props.profile.description?.replace(/\n/g, '<br>') ?? '');
</script>

<template>
  <div id="profile-container">
    <ion-card-header>
      <div class="header-content">
        <ion-card-title>
          <img v-if="profile.avatarUrl" :src="profile.avatarUrl" :alt="`${profile.username} avatar`" />
          <img v-else src="/assets/images/svg/avatar.svg" :alt="`${profile.username} avatar`" />
        </ion-card-title>
        <div>
          <ion-card-title>{{ profile.username }}</ion-card-title>
          <div class="tag-and-follow">
            <RoleTagComponent v-for="role in profile.roles" :key="role" :role="role" class="role-tag" />
            <FollowButton v-if="canFollow" :user="profile" @click.stop />
          </div>
        </div>
      </div>
    </ion-card-header>

    <ion-card-content>
      <p v-html="formattedDescription" class="description" />
      <FollowCounter :user="profile" class="followers" />
    </ion-card-content>
  </div>
</template>

<style scoped>
#profile-container {
  ion-card-title {
    display: flex;
    align-items: center;
    font-weight: 700;
    font-size: 1.8em;
    letter-spacing: 0.05em;
    text-shadow: 0 0 2px rgba(0, 116, 228, 0.5);

    img {
      border-radius: 50%;
      height: 2.5em;
      width: 2.5em;
      margin-right: 0.75em;
      border: 1px solid #0074e4;
      box-shadow: 0 0 4px #0074e4;
      transition: transform 0.3s ease;

      &:hover {
        transform: scale(1.1);
      }
    }
  }

  ion-card-subtitle {
    color: #b0b0d0;
    font-weight: 500;
    font-size: 1.05em;
    margin-bottom: 1.2em;
  }

  ion-card-content {
    padding-bottom: 1em;
    p {
      font-size: 1.1em;
      line-height: 1.6em;
      color: #c0c0e0;
      user-select: text;
    }

    .description {
      color: var(--ion-color-medium);
    }

    .followers {
      width: 100%;
      margin-top: 0.5em;
      border-top: 1px solid rgba(255, 255, 255, 0.15);
      padding-top: 1em;
      margin-bottom: 0;
    }
  }
  .tag-and-follow {
    display: flex;
    align-items: center;
    gap: 0.5em;
  }

  .header-content {
    display: flex;
    align-items: center;
    justify-content: flex-start;
  }
}
</style>
