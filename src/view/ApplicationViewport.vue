<script setup lang="ts">
import { IonContent, IonHeader, IonPage, IonToolbar, IonButtons } from '@ionic/vue';
import { checkStatus } from '@/client/authenticationClient';
import { onMounted, onUnmounted, ref, nextTick } from 'vue';
import { retrieveAuthenticatedProfile } from '@/client/profileClient';
import { useAuthStore } from '@/store/reactiveStorage';
import { loadBearerToken } from '@/store/localStorage';

const props = defineProps({
  title: {
    required: true,
    type: String,
  },
  authenticated: Boolean,
  offline: Boolean,
  fullscreen: Boolean,
  backButton: Boolean,
});
const authStore = useAuthStore();
// In Ionic Vue, template refs on components point to the Vue wrapper; use $el to access the Web Component
const contentRef = ref<InstanceType<typeof IonContent> | null>(null);
const bottomPadding = ref('0px'); // used only when styling the outer content
let scrollEl: HTMLElement | null = null;
let ro: ResizeObserver | null = null;
let mo: MutationObserver | null = null;
let rafId: number | null = null;
let nowPlayingOffset = 0;
let styleTargetEl: HTMLElement | null = null; // the IonContent element to style (inner if present)
let hostEl: HTMLElement | null = null; // the outer wrapper ion-content element

// Compute and apply padding only when content scrolls and the player is visible
const updatePadding = () => {
  if (!scrollEl) return;
  const isScrollable = scrollEl.scrollHeight > scrollEl.clientHeight + 1;
  const extra = 8; // small visual gap so last item never kisses the bar
  const pxVal = nowPlayingOffset > 0 && isScrollable ? Math.ceil(nowPlayingOffset + extra) : 0;
  // If we're styling the inner content, don't also pad the outer
  if (styleTargetEl && hostEl && styleTargetEl !== hostEl) {
    bottomPadding.value = '0px';
    styleTargetEl.style.setProperty('--padding-bottom', `${pxVal}px`);
  } else {
    // Style outer wrapper
    bottomPadding.value = `${pxVal}px`;
    if (hostEl) hostEl.style.setProperty('--padding-bottom', `${pxVal}px`);
  }
};

const handleOffset = (e: Event) => {
  const px = (e as CustomEvent).detail?.px as number | undefined;
  nowPlayingOffset = px && px > 0 ? px : 0;
  updatePadding();
};

onMounted(async () => {
  if (props.offline) return;

  await checkStatus().catch(console.error);
  try {
    await loadBearerToken();

    authStore.authenticate(await retrieveAuthenticatedProfile());
  } catch (error) {
    authStore.logout();
  }

  // Setup scroll element and observers for conditional bottom padding
  await nextTick();
  try {
    const hostCandidate = (contentRef.value?.$el ?? contentRef.value) as unknown;
    let host: HTMLElement | null = null;
    if (hostCandidate instanceof HTMLElement) {
      host = hostCandidate as HTMLElement;
    }
    let targetEl: HTMLElement | null = null;
    // Prefer a direct child ion-content from the slot (page-level content), not modal content
    if (host && host.children) {
      for (const child of Array.from(host.children)) {
        if ((child as HTMLElement).tagName === 'ION-CONTENT') {
          targetEl = child as HTMLElement;
          break;
        }
      }
    }
    hostEl = host ?? null;
    if (!targetEl) targetEl = host;
    styleTargetEl = targetEl ?? null;
    type IonContentEl = HTMLElement & { getScrollElement?: () => Promise<HTMLElement> };
    const ionEl = (targetEl as IonContentEl) ?? null;
    const el: HTMLElement | null =
      ionEl && typeof ionEl.getScrollElement === 'function' ? await ionEl.getScrollElement() : null;
    scrollEl = el ?? null;
  } catch {
    scrollEl = null;
  }

  document.addEventListener('now-playing-offset', handleOffset as EventListener);
  window.addEventListener('resize', updatePadding);

  if (typeof ResizeObserver !== 'undefined' && scrollEl) {
    ro = new ResizeObserver(updatePadding);
    ro.observe(scrollEl);
  }
  // Observe DOM mutations (children added/removed) to react when content changes scrollability
  if (typeof MutationObserver !== 'undefined' && (styleTargetEl || hostEl)) {
    const observeEl = styleTargetEl ?? hostEl!;
    mo = new MutationObserver(() => {
      if (rafId) cancelAnimationFrame(rafId);
      rafId = requestAnimationFrame(() => {
        rafId = null;
        updatePadding();
      });
    });
    mo.observe(observeEl, { childList: true, subtree: true });
  }
  // Initial calculation: if a NowPlaying bar already exists, measure it and apply immediately
  const bar = document.querySelector('.now-playing') as HTMLElement | null;
  nowPlayingOffset = bar ? bar.getBoundingClientRect().height : 0;
  updatePadding();
});

onUnmounted(() => {
  document.removeEventListener('now-playing-offset', handleOffset as EventListener);
  window.removeEventListener('resize', updatePadding);
  if (ro && scrollEl) ro.unobserve(scrollEl);
  ro = null;
  if (mo) {
    mo.disconnect();
    mo = null;
  }
  if (rafId) {
    cancelAnimationFrame(rafId);
    rafId = null;
  }
  // Clear style on target
  if (styleTargetEl) styleTargetEl.style.setProperty('--padding-bottom', '0px');
  if (hostEl) hostEl.style.setProperty('--padding-bottom', '0px');
});
</script>

<template>
  <ion-page>
    <ion-header v-if="!fullscreen" collapse="condense" class="safe-area">
      <ion-buttons slot="start">
        <slot name="buttons"></slot>
      </ion-buttons>
      <ion-toolbar style="--background: #0d0d0d">
        <div class="header">
          <div class="header-image-container">
            <img src="/assets/images/png/banner-light.png" alt="Soodora banner" />
          </div>
          <slot name="toolbar"></slot>
        </div>
      </ion-toolbar>
    </ion-header>
    <ion-content :fullscreen="true" ref="contentRef" :style="{ '--padding-bottom': bottomPadding }">
      <slot></slot>
    </ion-content>
  </ion-page>
</template>

<style scoped>
ion-header {
  background-color: #0d0d0d !important;
  --background: #0d0d0d !important;
  color: white;
}

.header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-top: 2%;
  margin-bottom: 2%;
  height: 50px;
}

.header-image-container {
  padding-left: 1em;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  width: 50%;
}

.header-image-container img {
  width: auto;
  align-items: center;
}

.safe-area {
  padding: calc(env(safe-area-inset-top) - 10px) 0px 0px;
}
</style>
