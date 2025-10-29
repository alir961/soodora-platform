<script setup lang="ts">
import { showToast } from '@/utils';
import { IonContent, IonIcon, onIonViewDidEnter } from '@ionic/vue';
import { getLinkPage } from '@/client/linkPageClient';
import { useRoute } from 'vue-router';
import { ref, computed } from 'vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { linkOutline, chevronForwardOutline, arrowBack } from 'ionicons/icons';
import { Link, platformIcons } from '@/entity/Link';

const route = useRoute();
const pageSlug = computed(() => route.params.slug as string | undefined);

const pageData = ref({
  title: '',
  description: '',
});

type PublicLink = {
  title: string;
  description: string;
  url: string;
  iconUrl: string;
  slug?: string;
};
const links = ref<PublicLink[]>([]);

async function loadPage() {
  if (!pageSlug.value) return;
  try {
    const page = await getLinkPage(pageSlug.value);
    pageData.value.title = page.title;
    pageData.value.description = page.description;
    links.value = (page.links || []).map((link: Link) => ({
      slug: link.slug,
      title: link.title,
      description: link.description,
      url: link.url,
      iconUrl: link.iconUrl,
    }));
  } catch (error: unknown) {
    const message = 'Unable to load Link Hub. ' + (error instanceof Error ? error.message : '');
    console.error(error);
    await showToast(message, 'danger');
  }
}

onIonViewDidEnter(loadPage);
</script>

<template>
  <ApplicationViewport :title="pageData.title || 'Link Hub'">
    <ion-content class="form-content-wrapper">
      <div class="public-view">
        <button class="copy-url-btn" @click="$router.back()" title="Go Back">
          <ion-icon :icon="arrowBack" />
        </button>
        <div class="public-header">
          <h1>{{ pageData.title }}</h1>
          <p class="public-description">{{ pageData.description }}</p>
        </div>

        <div class="public-links" v-if="links.length > 0">
          <a
            v-for="(link, idx) in links"
            :key="idx"
            :href="link.url.startsWith('http') ? link.url : 'https://' + link.url"
            target="_blank"
            rel="noopener noreferrer"
            class="public-link-card">
            <div class="public-link-icon">
              <ion-icon v-if="platformIcons[link.iconUrl]" :src="platformIcons[link.iconUrl]" />
              <ion-icon v-else :icon="linkOutline" />
            </div>
            <div class="public-link-info">
              <h3>{{ link.title }}</h3>
              <p v-if="link.description">{{ link.description }}</p>
            </div>
            <ion-icon :icon="chevronForwardOutline" class="public-link-arrow" />
          </a>
        </div>

        <div v-else class="empty-links">
          <p>This Link Hub has no links yet.</p>
        </div>
      </div>
    </ion-content>
  </ApplicationViewport>
</template>

<style scoped>
.form-content-wrapper {
  --background: linear-gradient(to bottom, rgba(13, 27, 42, 0.95), rgba(9, 18, 30, 0.92));
}

.copy-url-btn {
  appearance: none;
  border: 1px solid rgba(59, 130, 246, 0.3);
  background: rgba(59, 130, 246, 0.15);
  color: #60a5fa;
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.copy-url-btn ion-icon {
  font-size: 18px;
}

.copy-url-btn:hover {
  background: rgba(59, 130, 246, 0.25);
  border-color: rgba(59, 130, 246, 0.4);
  transform: scale(1.05);
}

.copy-url-btn:active {
  transform: scale(0.95);
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-state p {
  margin: 0;
  font-size: 0.9rem;
  color: #9aa3ad;
}

.empty-links {
  text-align: center;
  padding: 3rem 1.5rem;
  background: rgba(255, 255, 255, 0.02);
  border: 2px dashed rgba(255, 255, 255, 0.1);
  border-radius: 12px;
}

.empty-links p {
  margin: 0;
  font-size: 0.95rem;
  color: #9aa3ad;
}

@keyframes expandCard {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.cancel-link-btn ion-icon {
  font-size: 15px;
  margin-right: 6px;
}

.platform-option.active ion-icon,
.platform-option.active span {
  color: #60a5fa;
}

.url-input-wrapper input {
  flex: 1;
  appearance: none;
  outline: none;
  border: 0;
  background: transparent;
  color: #fff;
  padding: 12px 14px;
  font-size: 15px;
  font-family: inherit;
}

.url-input-wrapper input::placeholder {
  color: #7f7f7f;
}

.public-view {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem 1.5rem;
}

.public-header {
  text-align: center;
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.public-header h1 {
  font-size: 2rem;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 0.75rem 0;
  text-shadow: 0 0 12px rgba(59, 130, 246, 0.3);
}

.public-description {
  font-size: 1.1rem;
  color: #9aa3ad;
  margin: 0;
  line-height: 1.6;
}

.public-links {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.public-link-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.25rem;
  background: linear-gradient(135deg, rgba(13, 27, 42, 0.6), rgba(9, 18, 30, 0.5));
  border: 1px solid rgba(59, 130, 246, 0.15);
  border-radius: 16px;
  text-decoration: none;
  transition: all 0.25s ease;
  cursor: pointer;
}

.public-link-card:hover {
  background: linear-gradient(135deg, rgba(13, 27, 42, 0.8), rgba(9, 18, 30, 0.7));
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.public-link-card:active {
  transform: translateY(0);
}

.public-link-icon {
  width: 56px;
  height: 56px;
  flex-shrink: 0;
  background: rgba(59, 130, 246, 0.15);
  border: 1px solid rgba(59, 130, 246, 0.3);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.public-link-icon ion-icon {
  font-size: 28px;
  color: #60a5fa;
}

.public-link-info {
  flex: 1;
  min-width: 0;
}

.public-link-info h3 {
  margin: 0 0 4px 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #ffffff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.public-link-info p {
  margin: 0;
  font-size: 0.9rem;
  color: #9aa3ad;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.public-link-arrow {
  font-size: 20px;
  color: #60a5fa;
  flex-shrink: 0;
  transition: transform 0.2s ease;
}

.public-link-card:hover .public-link-arrow {
  transform: translateX(4px);
}

@media (max-width: 768px) {
  .add-link-btn span {
    display: inline;
  }

  .public-view {
    padding: 1.5rem 1rem;
  }

  .public-header h1 {
    font-size: 1.75rem;
  }

  .public-description {
    font-size: 1rem;
  }

  .public-link-icon {
    width: 48px;
    height: 48px;
  }

  .public-link-icon ion-icon {
    font-size: 24px;
  }

  .collapsed-icon ion-icon {
    font-size: 18px;
  }
}
</style>
