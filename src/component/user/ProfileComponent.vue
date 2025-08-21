<script setup lang="ts">
import { User } from '@/entity/User';
import { PropType, computed } from 'vue';
import RoleTagComponent from '@/component/user/RoleTagComponent.vue';
import FollowCounter from '@/component/follow/FollowCounter.vue';

const props = defineProps({
  profile: {
    required: true,
    type: Object as PropType<User>,
  },
});

const formattedDescription = computed(() => props.profile.description?.replace(/\n/g, '<br>') ?? '');
</script>

<template>
  <div id="profile">
    <header>
      <img v-if="profile.avatarUrl" :src="profile.avatarUrl" :alt="`${profile.username} avatar`" />
      <img v-else src="/assets/images/svg/avatar.svg" :alt="`${profile.username} avatar`" />
      <div>
        <h1>{{ profile.username }}</h1>
        <div class="roles">
          <RoleTagComponent v-for="role in profile.roles" :key="role" :role="role" class="role-tag" />
        </div>
      </div>
    </header>

    <div class="content">
      <p v-html="formattedDescription" class="description" />
      <FollowCounter :user="profile" class="followers" />
    </div>
  </div>
</template>

<style scoped>
#profile {
  h1 {
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

  header {
    padding-bottom: 1em;
    display: flex;
    align-items: center;
    justify-content: flex-start;

    p {
      font-size: 1.1em;
      line-height: 1.6em;
      color: #c0c0e0;
      user-select: text;
    }

    .description {
      color: var(--ion-color-medium);
      font-size: 1em;
    }

    .followers {
      width: 100%;
      margin-top: 0.5em;
      border-top: 1px solid rgba(255, 255, 255, 0.15);
      padding-top: 1em;
      margin-bottom: 0;
    }
  }

  .roles {
    display: flex;
    align-items: center;
    gap: 0.5em;

    .role-tag {
      font-size: 0.8em;
    }
  }
}
</style>
