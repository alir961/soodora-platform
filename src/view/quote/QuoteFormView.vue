<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { IonIcon, IonContent } from '@ionic/vue';
import { closeOutline, documentTextOutline } from 'ionicons/icons';
import { showToast } from '@/utils';
import { createQuote, getQuote, updateQuote } from '@/client/quoteClient';
import type { CreateQuoteRequest, UpdateQuoteRequest } from '@/entity/Quote';
import Header from '@/component/AppHeader.vue';

const router = useRouter();
const route = useRoute();

const isEditMode = computed(() => !!route.params.slug);
const quoteSlug = computed(() => route.params.slug as string);

const newQuote = ref<CreateQuoteRequest>({
  name: '',
  clientName: '',
  clientEmail: '',
  clientAddress: '',
  clientPhone: '',
  description: '',
});

const isLoading = ref(false);

const closeForm = () => {
  // Smart back navigation - avoid going back to form pages
  const canGoBack = window.history.state?.back;
  const isFromForm =
    canGoBack && (canGoBack.includes('/edit') || canGoBack.includes('/create') || canGoBack.includes('/items/'));

  if (canGoBack && !isFromForm) {
    router.back();
  } else {
    // Default to quotes list if no valid history or coming from form
    router.push('/quotes/me');
  }
};

const loadQuote = async () => {
  if (!isEditMode.value) return;

  try {
    isLoading.value = true;
    const quote = await getQuote(quoteSlug.value);
    newQuote.value = {
      name: quote.name,
      clientName: quote.clientName,
      clientEmail: quote.clientEmail,
      clientAddress: quote.clientAddress,
      clientPhone: quote.clientPhone,
      description: quote.description || '',
    };
  } catch (error: unknown) {
    const message = 'Failed to load quote. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
    router.back();
  } finally {
    isLoading.value = false;
  }
};

const saveNewQuote = async () => {
  try {
    isLoading.value = true;

    if (isEditMode.value) {
      await updateQuote(quoteSlug.value, newQuote.value as UpdateQuoteRequest);
      await router.push(`/quotes/${quoteSlug.value}?t=${Date.now()}`);
    } else {
      const created = await createQuote(newQuote.value);
      await router.replace(`/quotes/${created.slug}?t=${Date.now()}`);
    }
  } catch (error: unknown) {
    const message =
      `Failed to ${isEditMode.value ? 'update' : 'create'} quote. ` + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  } finally {
    isLoading.value = false;
  }
};

const isSubmitDisabled = computed(() => {
  if (isLoading.value) return true;
  if (!newQuote.value.name.trim() || !newQuote.value.clientName.trim() || !newQuote.value.clientEmail.trim()) {
    return true;
  }
  return false;
});

onMounted(() => {
  loadQuote();
});
</script>

<template>
  <ApplicationViewport :title="isEditMode ? 'Edit Quote' : 'Create Quote'">
    <IonContent class="form-content-wrapper">
      <Header
        :title="isEditMode ? 'Edit Quote' : 'Create Quote'"
        :subtitle="newQuote.name || 'New quote'"
        :icon="documentTextOutline">
        <template #actions>
          <button class="close-btn" @click="closeForm">
            <IonIcon :icon="closeOutline" />
          </button>
        </template>
      </Header>
      <div class="form-content">
        <div class="form-section">
          <div class="section-header">
            <IonIcon :icon="documentTextOutline" class="section-icon" />
            <h3>Quote Details</h3>
          </div>

          <div class="form-field">
            <label>Quote Name <span class="required">*</span></label>
            <input type="text" v-model="newQuote.name" placeholder="Enter quote name" required />
          </div>

          <div class="form-field">
            <label>Customer Name <span class="required">*</span></label>
            <input type="text" v-model="newQuote.clientName" placeholder="Enter customer name" required />
          </div>

          <div class="form-field">
            <label>Customer Email <span class="required">*</span></label>
            <input type="email" v-model="newQuote.clientEmail" placeholder="Enter customer email" required />
          </div>

          <div class="form-field">
            <label>Customer Address <span class="optional">(Optional)</span></label>
            <input type="text" v-model="newQuote.clientAddress" placeholder="Enter customer address" />
          </div>

          <div class="form-field">
            <label>Customer Phone <span class="optional">(Optional)</span></label>
            <input type="tel" v-model="newQuote.clientPhone" placeholder="Enter customer phone" />
          </div>

          <div class="form-field">
            <label>Quote Description <span class="optional">(Optional)</span></label>
            <textarea v-model="newQuote.description" placeholder="Describe the quote" rows="4"></textarea>
          </div>
        </div>

        <button class="submit-btn" @click="saveNewQuote" :disabled="isSubmitDisabled">
          {{ isLoading ? (isEditMode ? 'Updating...' : 'Creating...') : isEditMode ? 'Update Quote' : 'Create Quote' }}
        </button>
      </div>
    </IonContent>
  </ApplicationViewport>
</template>

<style scoped>
.form-content-wrapper {
  --background: linear-gradient(to bottom, rgba(13, 27, 42, 0.95), rgba(9, 18, 30, 0.92));
  background: linear-gradient(to bottom, rgba(13, 27, 42, 0.95), rgba(9, 18, 30, 0.92));
  overflow-y: auto;
  max-height: calc(100vh - 64px);
}

.close-btn {
  appearance: none;
  border: 1px solid rgba(239, 68, 68, 0.3);
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
  font-size: 0.9rem;
  font-weight: 600;
  padding: 8px 10px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
}

.close-btn:hover {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.4);
  box-shadow: 0 0 12px rgba(239, 68, 68, 0.2);
}

.close-btn:active {
  transform: scale(0.95);
}

.close-btn ion-icon {
  font-size: 22px;
}

.form-content {
  padding: 1.5rem;
  max-width: 600px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  background: linear-gradient(to bottom, rgba(13, 27, 42, 0.6), rgba(9, 18, 30, 0.5));
  border-radius: 16px;
  margin-top: 1rem;
  margin-bottom: 1rem;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.section-icon {
  font-size: 24px;
  color: #60a5fa;
}

.section-header h3 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
  letter-spacing: 0.01em;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-field label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #cfcfcf;
  letter-spacing: 0.01em;
  display: flex;
  align-items: center;
  gap: 6px;
}

.required {
  color: #f87171;
  font-size: 1rem;
}

.optional {
  color: #9aa3ad;
  font-size: 0.8rem;
  font-weight: 500;
}

.form-field input,
.form-field textarea {
  appearance: none;
  outline: none;
  border: 1px solid rgba(59, 130, 246, 0.2);
  background: rgba(13, 27, 42, 0.4);
  color: #fff;
  padding: 12px 14px;
  border-radius: 12px;
  font-size: 15px;
  font-family: inherit;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    background 0.2s ease;
}

.form-field textarea {
  resize: vertical;
  min-height: 100px;
}

.form-field input::placeholder,
.form-field textarea::placeholder {
  color: #7f7f7f;
}

.form-field input:hover,
.form-field textarea:hover {
  border-color: rgba(59, 130, 246, 0.35);
  background: rgba(13, 27, 42, 0.6);
}

.form-field input:focus,
.form-field textarea:focus {
  border-color: rgba(59, 130, 246, 0.55);
  box-shadow:
    0 0 0 6px rgba(59, 130, 246, 0.1),
    0 8px 24px rgba(0, 0, 0, 0.25);
  background: rgba(13, 27, 42, 0.7);
}

.submit-btn {
  width: 100%;
  height: 48px;
  border: 0;
  border-radius: 12px;
  cursor: pointer;
  color: #fff;
  font-weight: 700;
  font-size: 1rem;
  letter-spacing: 0.01em;
  background: linear-gradient(135deg, #1d4ed8, #2563eb 55%, #60a5fa);
  box-shadow:
    0 10px 30px rgba(37, 99, 235, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
  transition: all 0.2s ease;
  margin-top: 0.5rem;
}

.submit-btn:hover:not(:disabled) {
  box-shadow:
    0 12px 32px rgba(37, 99, 235, 0.45),
    inset 0 1px 0 rgba(255, 255, 255, 0.16);
  transform: translateY(-1px);
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow:
    0 8px 24px rgba(37, 99, 235, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  background: linear-gradient(135deg, #374151, #4b5563);
  box-shadow: none;
}

@media (max-width: 768px) {
  .form-header {
    padding: 1rem 1.25rem;
  }

  .form-header h2 {
    font-size: 1.2rem;
  }

  .form-content {
    padding: 1.25rem;
    margin-top: 0.75rem;
    margin-bottom: 0.75rem;
  }

  .form-field input,
  .form-field textarea {
    font-size: 16px;
  }
}
</style>
