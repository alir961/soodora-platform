<script setup lang="ts">
import { warningOutline } from 'ionicons/icons';
import { ref } from 'vue';
import { callUpdateConfiguration } from '@/client/profileClient';

const emit = defineEmits(['configured']);

const isMigrating = ref<boolean>(false);

const confirmConfiguration = async (migrate: boolean) => {
  isMigrating.value = true;
  await callUpdateConfiguration(migrate);
  emit('configured');
  isMigrating.value = false;
};
</script>

<template>
  <div class="modal-overlay">
    <div class="modal-card">
      <div class="modal-icon-wrapper">
        <ion-icon :icon="warningOutline" class="warning-icon" />
      </div>
      <h3>Become an Artist</h3>
      <p>
        Ready to share your talent? Upgrading unlocks exclusive tools like the <strong>Soodora Kit</strong> and
        <strong>Link Hub</strong>. <br /><br />
        <strong>Note:</strong> This transition is <strong>permanent</strong> and cannot be undone. If you aren't ready
        yet, you can always upgrade later from your profile.
      </p>

      <div class="modal-actions">
        <button class="modal-btn cancel" @click="confirmConfiguration(false)" :disabled="isMigrating">
          Maybe Later
        </button>

        <button class="modal-btn confirm" @click="confirmConfiguration(true)" :disabled="isMigrating">
          <span v-if="!isMigrating">Upgrade Now</span>
          <span v-else>Processing...</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.65);
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 1rem;
}

.modal-card {
  background: rgba(22, 22, 24, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 24px;
  width: 100%;
  max-width: 340px;
  text-align: center;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
  animation: modalPop 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

@keyframes modalPop {
  0% {
    transform: scale(0.9);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.modal-icon-wrapper {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(255, 200, 0, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px auto;
  border: 1px solid rgba(255, 200, 0, 0.3);
}

.warning-icon {
  font-size: 32px;
  color: #ffc800;
}

.modal-card h3 {
  color: #fff;
  margin: 0 0 12px 0;
  font-size: 1.4rem;
  font-weight: 700;
}

.modal-card p {
  color: #a0a0a0;
  font-size: 0.95rem;
  line-height: 1.5;
  margin-bottom: 24px;
}

.modal-card p strong {
  color: #fff;
}

.modal-actions {
  display: flex;
  gap: 12px;
}

.modal-btn {
  flex: 1;
  padding: 12px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: transform 0.1s;
  border: none;
}

.modal-btn:active {
  transform: scale(0.96);
}

.modal-btn.cancel {
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
}

.modal-btn.confirm {
  background: #3b82f6;
  color: #fff;
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3);
}

.modal-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
