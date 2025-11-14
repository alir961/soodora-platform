<script setup lang="ts">
import { computed, PropType } from 'vue';
import { useRouter } from 'vue-router';
import { IonIcon } from '@ionic/vue';
import { musicalNotesOutline, personOutline } from 'ionicons/icons';
import { Playlist } from '@/entity/Playlist';

const props = defineProps({
  playlist: {
    required: true,
    type: Object as PropType<Playlist>,
  },
});

const router = useRouter();

const goToPlaylist = () => {
  router.push(`/playlists/${props.playlist.slug}`);
};

const trackCountText = computed(() => {
  const count = props.playlist.numberOfTracks || 0;
  return `${count} track${count !== 1 ? 's' : ''}`;
});
</script>

<template>
  <div class="playlist-card" @click="goToPlaylist">
    <div class="playlist-cover-wrapper">
      <img v-if="playlist.imageUrl" :src="playlist.imageUrl" :alt="playlist.name" class="playlist-cover" />
      <div v-else class="playlist-cover-placeholder">
        <ion-icon :icon="musicalNotesOutline" />
      </div>
    </div>

    <div class="playlist-info">
      <h3 class="playlist-name">{{ playlist.name }}</h3>
      <p class="playlist-description" v-if="playlist.description">
        {{ playlist.description }}
      </p>
      <div class="playlist-meta">
        <span class="meta-item">
          <ion-icon :icon="personOutline" />
          {{ playlist.owner.username }}
        </span>
        <span class="meta-divider">•</span>
        <span class="meta-item">
          <ion-icon :icon="musicalNotesOutline" />
          {{ trackCountText }}
        </span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.playlist-card {
  position: relative;
  display: flex;
  gap: 1rem;
  padding: 1rem;
  background: linear-gradient(135deg, rgba(13, 27, 42, 0.6), rgba(9, 18, 30, 0.5));
  border: 1px solid rgba(59, 130, 246, 0.15);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.playlist-card::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.05), transparent 50%);
  opacity: 0;
  transition: opacity 0.25s ease;
}

.playlist-card:hover {
  transform: translateY(-2px);
  border-color: rgba(59, 130, 246, 0.3);
  box-shadow:
    0 8px 24px rgba(0, 0, 0, 0.3),
    0 0 0 1px rgba(59, 130, 246, 0.1);
}

.playlist-card:hover::before {
  opacity: 1;
}

.playlist-card:active {
  transform: translateY(0);
}

.playlist-cover-wrapper {
  position: relative;
  width: 80px;
  height: 80px;
  flex-shrink: 0;
  border-radius: 12px;
  overflow: hidden;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.playlist-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.playlist-card:hover .playlist-cover {
  transform: scale(1.05);
}

.playlist-cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(37, 99, 235, 0.1));
  color: #60a5fa;
}

.playlist-cover-placeholder ion-icon {
  font-size: 32px;
}

.playlist-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
  justify-content: center;
}

.playlist-name {
  font-size: 1.125rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.playlist-description {
  font-size: 0.875rem;
  color: #9aa3ad;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.playlist-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.8125rem;
  color: #9aa3ad;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-item ion-icon {
  font-size: 14px;
  color: #60a5fa;
}

.meta-divider {
  color: rgba(255, 255, 255, 0.3);
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .playlist-card {
    padding: 0.875rem;
    gap: 0.875rem;
  }

  .playlist-cover-wrapper {
    width: 70px;
    height: 70px;
  }

  .playlist-cover-placeholder ion-icon {
    font-size: 28px;
  }

  .playlist-name {
    font-size: 1rem;
  }

  .playlist-description {
    font-size: 0.8125rem;
  }

  .playlist-meta {
    font-size: 0.75rem;
  }

  .meta-item ion-icon {
    font-size: 12px;
  }
}
</style>
