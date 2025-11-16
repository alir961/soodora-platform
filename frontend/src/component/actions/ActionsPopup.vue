<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import { IonIcon } from '@ionic/vue';

export interface ActionItem {
  icon: string;
  label: string;
  action: () => void;
  destructive?: boolean;
}

interface Props {
  isOpen: boolean;
  x: number;
  y: number;
  actions: ActionItem[];
}

const props = defineProps<Props>();
const emit = defineEmits<{
  close: [];
}>();

const popupStyle = ref({});

const updatePosition = () => {
  if (!props.isOpen) return;

  const padding = 16;
  const popupWidth = 220;
  const actionHeight = 47; // 14px padding top + 14px padding bottom + 19px content
  const popupHeight = props.actions.length * actionHeight;

  let x = props.x;
  let y = props.y;

  // Adjust if too close to right edge
  if (x + popupWidth > window.innerWidth - padding) {
    x = window.innerWidth - popupWidth - padding;
  }

  // Adjust if too close to bottom edge
  if (y + popupHeight > window.innerHeight - padding) {
    y = props.y - popupHeight - 16; // Position above the button instead
  }

  // Adjust if too close to left edge
  if (x < padding) {
    x = padding;
  }

  // Adjust if too close to top edge (after moving above)
  if (y < padding) {
    y = padding;
  }

  popupStyle.value = {
    left: `${x}px`,
    top: `${y}px`,
  };
};

watch(
  () => [props.isOpen, props.x, props.y],
  () => {
    if (props.isOpen) {
      updatePosition();
    }
  }
);

const handleBackdropClick = () => {
  emit('close');
};

const handleAction = (action: () => void) => {
  action();
  emit('close');
};

onMounted(() => {
  updatePosition();
});
</script>

<template>
  <Teleport to="body">
    <Transition name="fade">
      <div v-if="isOpen" class="popup-backdrop" @click="handleBackdropClick">
        <div class="popup-card" :style="popupStyle" @click.stop>
          <button
            v-for="(item, index) in actions"
            :key="index"
            class="popup-action"
            :class="{ destructive: item.destructive }"
            @click="handleAction(item.action)">
            <IonIcon :icon="item.icon" />
            <span>{{ item.label }}</span>
          </button>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.popup-backdrop {
  position: fixed;
  inset: 0;
  z-index: 10000;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
}

.popup-card {
  position: fixed;
  min-width: 220px;
  background: linear-gradient(135deg, rgba(20, 25, 35, 0.98), rgba(15, 20, 30, 0.98));
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: 12px;
  box-shadow:
    0 12px 40px rgba(0, 0, 0, 0.6),
    0 0 0 1px rgba(59, 130, 246, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(24px) saturate(180%);
  -webkit-backdrop-filter: blur(24px) saturate(180%);
  overflow: hidden;
  animation: popup-appear 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes popup-appear {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.popup-action {
  width: 100%;
  padding: 14px 16px;
  background: transparent;
  border: none;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #ffffff;
  font-size: 0.95rem;
  font-weight: 500;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.popup-action:last-child {
  border-bottom: none;
}

.popup-action ion-icon {
  font-size: 1.25rem;
  color: #60a5fa;
  flex-shrink: 0;
}

.popup-action:hover {
  background: rgba(59, 130, 246, 0.1);
}

.popup-action:active {
  background: rgba(59, 130, 246, 0.15);
  transform: scale(0.98);
}

.popup-action.destructive ion-icon {
  color: #ef4444;
}

.popup-action.destructive:hover {
  background: rgba(239, 68, 68, 0.1);
}

.popup-action.destructive:active {
  background: rgba(239, 68, 68, 0.15);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
