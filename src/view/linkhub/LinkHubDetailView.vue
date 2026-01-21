<script setup lang="ts">
import { showToast } from '@/utils';
import { IonContent, IonIcon, onIonViewDidEnter } from '@ionic/vue';
import { getLinkHub } from '@/client/linkHubClient';
import { useRoute, useRouter } from 'vue-router';
import { ref, computed } from 'vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import Header from '@/component/AppHeader.vue';
import { linkOutline, chevronForwardOutline, shareOutline, arrowBackOutline } from 'ionicons/icons';
import { Link, platformIcons } from '@/entity/Link';
import UsernameTag from '@/component/user/UsernameTag.vue';
import { Capacitor } from '@capacitor/core';
import { Share } from '@capacitor/share';

const route = useRoute();
const router = useRouter();
const pageSlug = computed(() => route.params.slug as string | undefined);

const pageData = ref({
  title: '',
  description: '',
  owner: {
    slug: '',
    username: '',
    description: '',
    avatarUrl: null as string | null,
  },
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
    const page = await getLinkHub(pageSlug.value);
    pageData.value.title = page.title;
    pageData.value.description = page.description;
    pageData.value.owner = {
      slug: page.owner.slug,
      username: page.owner.username,
      description: page.owner.description,
      avatarUrl: page.owner.avatarUrl,
    };
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

async function shareHub() {
  // Build the proper share URL
  const baseUrl = 'https://app.soodora.com';
  const url = `${baseUrl}/linkhub/${pageSlug.value}`;

  try {
    // Use Capacitor Share on native platforms for proper share sheet
    if (Capacitor.isNativePlatform()) {
      await Share.share({
        url: url,
        dialogTitle: 'Share Link Hub',
      });
    } else if (navigator.share) {
      // Web Share API - pass just the URL to avoid "title text url" format
      await navigator.share({
        url: url,
      });
    } else {
      // Fallback: copy just the URL to clipboard
      await navigator.clipboard.writeText(url);
      await showToast('Link Hub URL copied to clipboard!', 'success');
    }
  } catch (err) {
    console.error('Error sharing:', err);
  }
}

function handleBack() {
  router.back();
}

onIonViewDidEnter(loadPage);
</script>

<template>
  <ApplicationViewport>
    <ion-content class="form-content-wrapper">
      <Header
        :title="pageData.title"
        :subtitle="`${links.length} ${links.length === 1 ? 'link' : 'links'}`"
        :icon="arrowBackOutline"
        :on-icon-click="handleBack">
        <template #actions>
          <button class="share-btn" @click="shareHub" title="Share">
            <ion-icon :icon="shareOutline" />
          </button>
        </template>
      </Header>

      <div class="public-view">
        <div class="ambient-glow">
          <div class="glow-orb orb-1"></div>
          <div class="glow-orb orb-2"></div>
          <div class="glow-orb orb-3"></div>
        </div>

        <div class="hero-section">
          <div class="hero-content">
            <div class="hero-avatar-wrapper">
              <img
                v-if="pageData.owner.avatarUrl"
                :src="pageData.owner.avatarUrl"
                :alt="pageData.owner.username"
                class="hero-avatar" />
              <div v-else class="hero-avatar-placeholder">
                <ion-icon :icon="linkOutline" />
              </div>
            </div>
            <div class="hero-info">
              <UsernameTag :user="pageData.owner" class="hero-username" />
              <p class="hero-description" v-if="pageData.description">{{ pageData.description }}</p>
            </div>
          </div>
        </div>

        <div class="public-links" v-if="links.length > 0">
          <a
            v-for="(link, idx) in links"
            :key="idx"
            :href="link.url.startsWith('http') ? link.url : 'https://' + link.url"
            target="_blank"
            rel="noopener noreferrer"
            class="public-link-card">
            <div class="link-left">
              <div class="public-link-icon">
                <ion-icon v-if="platformIcons[link.iconUrl]" :src="platformIcons[link.iconUrl]" />
                <ion-icon v-else :icon="linkOutline" />
              </div>
              <div class="public-link-info">
                <h3>{{ link.title }}</h3>
                <p v-if="link.description">{{ link.description }}</p>
              </div>
            </div>
            <div class="link-arrow-wrapper">
              <ion-icon :icon="chevronForwardOutline" class="public-link-arrow" />
            </div>
          </a>
        </div>

        <div v-else class="empty-links">
          <div class="empty-icon">
            <ion-icon :icon="linkOutline" />
          </div>
          <p>No links available yet</p>
        </div>
      </div>
    </ion-content>
  </ApplicationViewport>
</template>

<style scoped>
.form-content-wrapper {
  --background: #000000;
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
  padding: 4rem 1.5rem;
  margin: 2rem 1.5rem;
  background: linear-gradient(135deg, rgba(30, 41, 59, 0.3) 0%, rgba(15, 23, 42, 0.3) 100%);
  border: 2px dashed rgba(59, 130, 246, 0.2);
  border-radius: 20px;
}

.empty-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 1.5rem;
  background: rgba(59, 130, 246, 0.1);
  border: 2px solid rgba(59, 130, 246, 0.2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon ion-icon {
  font-size: 32px;
  color: #60a5fa;
  opacity: 0.6;
}

.empty-links p {
  margin: 0;
  font-size: 1rem;
  font-weight: 500;
  color: #b4bcd0;
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
  max-width: 680px;
  margin: 0 auto;
  padding: 0;
  position: relative;
}

.ambient-glow {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 1;
  animation: glowFloat 10s ease-in-out infinite;
}

.orb-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.25) 0%, rgba(59, 130, 246, 0.08) 50%, transparent 100%);
  top: -150px;
  left: -150px;
}

.orb-2 {
  width: 450px;
  height: 450px;
  background: radial-gradient(circle, rgba(96, 165, 250, 0.2) 0%, rgba(96, 165, 250, 0.06) 50%, transparent 100%);
  bottom: -100px;
  right: -100px;
}

.orb-3 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.18) 0%, rgba(37, 99, 235, 0.05) 50%, transparent 100%);
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
}

@keyframes glowFloat {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(-20px, -25px) scale(1.05);
  }
  66% {
    transform: translate(20px, 20px) scale(0.95);
  }
}

.hero-section {
  padding: 3rem 1.5rem 2rem;
  position: relative;
  z-index: 1;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(59, 130, 246, 0.025) 10%,
    rgba(148, 163, 184, 0.063) 85%,
    rgba(59, 131, 246, 0.051) 180%,
    transparent 100%
  );
}

.hero-section::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 5%;
  right: 5%;
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.05) 10%,
    rgba(255, 255, 255, 0.1) 50%,
    rgba(255, 255, 255, 0.05) 90%,
    transparent 100%
  );
}

.hero-content {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 1.5rem;
  text-align: center;
}

.hero-avatar-wrapper {
  position: relative;
}

.hero-avatar,
.hero-avatar-placeholder {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.1);
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.3),
    0 0 0 1px rgba(255, 255, 255, 0.05);
}

.hero-avatar-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(37, 99, 235, 0.1));
}

.hero-avatar-placeholder ion-icon {
  font-size: 42px;
  color: #60a5fa;
}

.hero-info {
  display: flex;
  flex-direction: column;
  align-items: start;
  gap: 0.5rem;
  max-width: 500px;
}

.hero-title {
  font-size: 2rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
  letter-spacing: -0.02em;
  line-height: 1.1;
}

:deep(.hero-username .username) {
  display: block;
  font-size: 0.9375rem;
  color: #60a5fa;
  margin: 0;
  font-weight: 500;
  cursor: pointer;
  transition: color 0.2s ease;
}

:deep(.hero-username .username:hover) {
  color: #93bbfd;
}

.hero-description {
  font-size: 0.9375rem;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
  line-height: 1.6;
  max-width: 400px;
}

.public-links {
  display: flex;
  flex-direction: column;
  gap: 0.875rem;
  padding: 2rem 1.5rem;
}

.public-link-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 1.125rem 1.25rem;
  background: linear-gradient(135deg, rgba(30, 41, 59, 0.4) 0%, rgba(15, 23, 42, 0.4) 100%);
  backdrop-filter: blur(10px);
  border: 1.5px solid rgba(59, 130, 246, 0.2);
  border-radius: 18px;
  text-decoration: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.link-left {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex: 1;
  min-width: 0;
}

.public-link-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1) 0%, rgba(37, 99, 235, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.public-link-card:hover {
  background: linear-gradient(135deg, rgba(30, 41, 59, 0.6) 0%, rgba(15, 23, 42, 0.6) 100%);
  border-color: rgba(59, 130, 246, 0.4);
  transform: translateY(-4px) scale(1.01);
  box-shadow:
    0 12px 40px rgba(0, 0, 0, 0.4),
    0 0 0 1px rgba(59, 130, 246, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
}

.public-link-card:hover::before {
  opacity: 1;
}

.public-link-card:active {
  transform: translateY(-2px) scale(1);
}

.public-link-icon {
  width: 60px;
  height: 60px;
  flex-shrink: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(37, 99, 235, 0.15));
  border: 1.5px solid rgba(59, 130, 246, 0.3);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
  box-shadow:
    0 4px 16px rgba(59, 130, 246, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
}

.public-link-card:hover .public-link-icon {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.3), rgba(37, 99, 235, 0.2));
  border-color: rgba(59, 130, 246, 0.5);
  box-shadow:
    0 6px 24px rgba(59, 130, 246, 0.25),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
  transform: scale(1.05);
}

.public-link-icon ion-icon {
  font-size: 30px;
  color: #60a5fa;
  transition: all 0.3s ease;
}

.public-link-card:hover .public-link-icon ion-icon {
  color: #93bbfd;
}

.public-link-info {
  flex: 1;
  min-width: 0;
  position: relative;
  z-index: 1;
}

.public-link-info h3 {
  margin: 0 0 6px 0;
  font-size: 1.125rem;
  font-weight: 700;
  color: #ffffff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  letter-spacing: -0.01em;
  transition: color 0.3s ease;
}

.public-link-card:hover .public-link-info h3 {
  color: #93bbfd;
}

.public-link-info p {
  margin: 0;
  font-size: 0.9rem;
  color: #9aa3ad;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.link-arrow-wrapper {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(59, 130, 246, 0.1);
  border-radius: 10px;
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
}

.public-link-card:hover .link-arrow-wrapper {
  background: rgba(59, 130, 246, 0.2);
  transform: translateX(4px);
}

.public-link-arrow {
  font-size: 20px;
  color: #60a5fa;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.public-link-card:hover .public-link-arrow {
  color: #93bbfd;
}

@media (max-width: 768px) {
  .form-header {
    padding: 0.625rem 1rem;
  }

  .header-title {
    font-size: 1rem;
  }

  .header-subtitle {
    font-size: 0.8rem;
  }

  .hero-section {
    padding: 1.5rem 1rem;
  }

  .hero-title {
    font-size: 1.5rem;
  }

  .hero-description {
    font-size: 1rem;
  }

  .public-links {
    padding: 1.5rem 1rem;
    gap: 0.75rem;
  }

  .public-link-card {
    padding: 1rem 1.125rem;
  }

  .public-link-icon {
    width: 52px;
    height: 52px;
  }

  .public-link-icon ion-icon {
    font-size: 26px;
  }

  .public-link-info h3 {
    font-size: 1.05rem;
  }

  .public-link-info p {
    font-size: 0.85rem;
  }

  .link-arrow-wrapper {
    width: 28px;
    height: 28px;
  }

  .public-link-arrow {
    font-size: 18px;
  }

  .empty-links {
    padding: 3rem 1.25rem;
    margin: 1.5rem 1rem;
  }

  .empty-icon {
    width: 56px;
    height: 56px;
  }

  .empty-icon ion-icon {
    font-size: 28px;
  }
}
</style>
