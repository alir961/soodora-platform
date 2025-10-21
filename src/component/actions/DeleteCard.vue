<script setup lang="ts">
import { PropType, ref } from 'vue';
import { showToast } from '@/utils';

const props = defineProps({
  itemType: {
    required: true,
    type: String as PropType<'post' | 'product' | 'link hub' | 'link'>,
  },
  itemSlug: {
    required: true,
    type: String,
  },
  deleteFunction: {
    required: true,
    type: Function as PropType<(slug: string) => Promise<void>>,
  },
});

const emit = defineEmits(['deleted']);

const showDeleteModal = ref(false);
const isDeleting = ref(false);

const open = () => {
  showDeleteModal.value = true;
};

const closeModal = () => {
  if (isDeleting.value) return;
  showDeleteModal.value = false;
};

const confirmDelete = async () => {
  isDeleting.value = true;
  try {
    await props.deleteFunction(props.itemSlug);
    emit('deleted', props.itemSlug);
    await showToast(`Succeed to delete ${props.itemType}.`);
    showDeleteModal.value = false;
  } catch (error: unknown) {
    const message = `Failed to delete ${props.itemType}. ` + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  } finally {
    isDeleting.value = false;
  }
};

defineExpose({
  open,
});
</script>

<template>
  <Teleport to="body">
    <transition name="modal-fade">
      <div v-if="showDeleteModal" class="delete-modal-overlay" @click="closeModal">
        <div class="delete-modal" @click.stop>
          <h3>Delete {{ itemType }}?</h3>
          <p>This cannot be undone.</p>

          <div class="modal-actions">
            <button class="cancel-btn" @click="closeModal" :disabled="isDeleting">Cancel</button>
            <button class="delete-btn" @click="confirmDelete" :disabled="isDeleting">
              <span v-if="!isDeleting">Delete</span>
              <div v-else class="loading-spinner"></div>
            </button>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<style scoped>
.delete-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: transparent;
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.delete-modal {
  background: linear-gradient(145deg, rgb(6, 22, 57), rgb(10, 18, 35));
  border-radius: 15.4px;
  width: 66%;
  max-width: 242px;
  padding: 0.99rem 0.88rem;
  border: 1px solid rgba(255, 255, 255, 0.175);
  box-shadow:
    0 20px 40px rgba(0, 0, 0, 0.8),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
  text-align: center;
}

.delete-modal h3 {
  margin: 0 0 0.33rem 0;
  font-size: 1.1rem;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: -0.01em;
}

.delete-modal p {
  margin: 0 0 0.825rem 0;
  font-size: 0.825rem;
  color: rgba(255, 255, 255, 0.6);
  font-weight: 500;
}

.modal-actions {
  display: flex;
  gap: 0.44rem;
  justify-content: center;
}

.cancel-btn,
.delete-btn {
  height: 37.4px;
  border-radius: 7.7px;
  font-size: 0.88rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 1.1rem;
  width: 99px;
}

.cancel-btn {
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.cancel-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.cancel-btn:active:not(:disabled) {
  transform: scale(0.97);
}

.cancel-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.delete-btn {
  background: linear-gradient(135deg, #dc2626, #ef4444, #f87171);
  color: #ffffff;
  box-shadow:
    0 3.3px 11px rgba(239, 68, 68, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
}

.delete-btn:hover:not(:disabled) {
  box-shadow:
    0 4.4px 15.4px rgba(239, 68, 68, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
}

.delete-btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow:
    0 2.2px 6.6px rgba(239, 68, 68, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
}

.delete-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none !important;
}

.loading-spinner {
  width: 17.6px;
  height: 17.6px;
  border: 2.2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.modal-fade-enter-active {
  animation: modalFadeIn 0.15s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.modal-fade-leave-active {
  animation: modalFadeIn 0.12s cubic-bezier(0.8, 0.2, 0.8, 0.2) reverse;
}

.modal-fade-enter-active .delete-modal {
  animation: modalScale 0.2s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.modal-fade-leave-active .delete-modal {
  animation: modalScale 0.15s cubic-bezier(0.8, 0.2, 0.8, 0.2) reverse;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes modalScale {
  from {
    opacity: 0;
    transform: scale(0.92) translateY(8.8px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@media (max-width: 480px) {
  .delete-modal {
    max-width: calc(100% - 2rem);
  }

  .delete-modal h3 {
    font-size: 1.045rem;
  }

  .delete-modal p {
    font-size: 0.77rem;
  }

  .cancel-btn,
  .delete-btn {
    height: 35.2px;
    font-size: 0.825rem;
    min-width: 71.5px;
  }
}
</style>
