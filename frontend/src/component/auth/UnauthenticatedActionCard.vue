<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const emit = defineEmits(['close']);

const showModal = ref(false);

const open = () => {
  showModal.value = true;
};

const close = () => {
  showModal.value = false;
  emit('close');
};

const goToAuth = () => {
  close();
  router.push('/authentication');
};

defineExpose({
  open,
  close,
});
</script>

<template>
  <Teleport to="body">
    <transition name="modal-fade">
      <div v-if="showModal" class="unauth-overlay" @click="close">
        <div class="unauth-card" @click.stop>
          <div class="orb orb-1"></div>
          <div class="orb orb-2"></div>
          <div class="orb orb-3"></div>

          <div class="content">
            <div class="icon-wrapper">
              <div class="lock-icon">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path
                    fill-rule="evenodd"
                    d="M12 1.5a5.25 5.25 0 00-5.25 5.25v3a3 3 0 00-3 3v6.75a3 3 0 003 3h10.5a3 3 0 003-3v-6.75a3 3 0 00-3-3v-3c0-2.9-2.35-5.25-5.25-5.25zm3.75 8.25v-3a3.75 3.75 0 10-7.5 0v3h7.5z"
                    clip-rule="evenodd" />
                </svg>
              </div>
            </div>

            <h2>Oops! You're not logged in</h2>
            <p>Join the groove to like, comment, and connect with the community.</p>

            <div class="actions">
              <button class="auth-btn" @click="goToAuth">
                <span>Sign In / Sign Up</span>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="arrow">
                  <path
                    fill-rule="evenodd"
                    d="M3 10a.75.75 0 01.75-.75h10.638L10.23 5.29a.75.75 0 111.04-1.08l5.5 5.25a.75.75 0 010 1.08l-5.5 5.25a.75.75 0 11-1.04-1.08l4.158-3.96H3.75A.75.75 0 013 10z"
                    clip-rule="evenodd" />
                </svg>
              </button>
              <button class="cancel-btn" @click="close">Maybe Later</button>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<style scoped>
.unauth-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.75);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.unauth-card {
  position: relative;
  background: linear-gradient(145deg, rgba(15, 23, 42, 0.95), rgba(30, 41, 59, 0.95));
  border-radius: 24px;
  width: 100%;
  max-width: 420px;
  padding: 2.5rem 2rem;
  border: 1px solid rgba(59, 130, 246, 0.3);
  box-shadow:
    0 25px 50px rgba(0, 0, 0, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
  overflow: hidden;
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.4;
  pointer-events: none;
  animation: float 8s ease-in-out infinite;
}

.orb-1 {
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.6), transparent 70%);
  top: -100px;
  right: -100px;
  animation-delay: 0s;
}

.orb-2 {
  width: 150px;
  height: 150px;
  background: radial-gradient(circle, rgba(147, 51, 234, 0.5), transparent 70%);
  bottom: -75px;
  left: -75px;
  animation-delay: 2s;
}

.orb-3 {
  width: 120px;
  height: 120px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.4), transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: 4s;
}

@keyframes float {
  0%,
  100% {
    transform: translate(0, 0);
  }
  33% {
    transform: translate(20px, -20px);
  }
  66% {
    transform: translate(-20px, 20px);
  }
}

.content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.icon-wrapper {
  margin-bottom: 1.5rem;
}

.lock-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #1d4ed8, #2563eb, #3b82f6);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow:
    0 10px 30px rgba(37, 99, 235, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  animation: pulse 2s ease-in-out infinite;
}

.lock-icon svg {
  width: 32px;
  height: 32px;
  color: #ffffff;
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
    box-shadow:
      0 10px 30px rgba(37, 99, 235, 0.4),
      inset 0 1px 0 rgba(255, 255, 255, 0.2);
  }
  50% {
    transform: scale(1.05);
    box-shadow:
      0 15px 40px rgba(37, 99, 235, 0.6),
      inset 0 1px 0 rgba(255, 255, 255, 0.3);
  }
}

.unauth-card h2 {
  margin: 0 0 0.75rem 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: -0.02em;
}

.unauth-card p {
  margin: 0 0 2rem 0;
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.6;
  max-width: 320px;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  width: 100%;
}

.auth-btn,
.cancel-btn {
  width: 100%;
  height: 52px;
  border-radius: 14px;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  position: relative;
  overflow: hidden;
}

.auth-btn {
  background: linear-gradient(135deg, #1d4ed8, #2563eb 50%, #3b82f6);
  color: #ffffff;
  box-shadow:
    0 10px 30px rgba(37, 99, 235, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.auth-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.auth-btn:hover::before {
  left: 100%;
}

.auth-btn:hover {
  box-shadow:
    0 15px 40px rgba(37, 99, 235, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
}

.auth-btn:active {
  transform: translateY(0);
  box-shadow:
    0 8px 20px rgba(37, 99, 235, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.auth-btn .arrow {
  width: 20px;
  height: 20px;
  transition: transform 0.2s ease;
}

.auth-btn:hover .arrow {
  transform: translateX(4px);
}

.cancel-btn {
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.cancel-btn:hover {
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.9);
  border-color: rgba(255, 255, 255, 0.15);
}

.cancel-btn:active {
  transform: scale(0.98);
}

.modal-fade-enter-active {
  animation: overlayFadeIn 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.modal-fade-leave-active {
  animation: overlayFadeIn 0.2s cubic-bezier(0.8, 0.2, 0.8, 0.2) reverse;
}

.modal-fade-enter-active .unauth-card {
  animation: cardSlideIn 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.modal-fade-leave-active .unauth-card {
  animation: cardSlideIn 0.25s cubic-bezier(0.8, 0.2, 0.8, 0.2) reverse;
}

@keyframes overlayFadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes cardSlideIn {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@media (max-width: 480px) {
  .unauth-overlay {
    padding: 1.5rem;
  }

  .unauth-card {
    padding: 2rem 1.5rem;
    max-width: 100%;
  }

  .unauth-card h2 {
    font-size: 1.35rem;
  }

  .unauth-card p {
    font-size: 0.95rem;
  }

  .lock-icon {
    width: 56px;
    height: 56px;
  }

  .lock-icon svg {
    width: 28px;
    height: 28px;
  }

  .auth-btn,
  .cancel-btn {
    height: 48px;
    font-size: 0.95rem;
  }
}
</style>
