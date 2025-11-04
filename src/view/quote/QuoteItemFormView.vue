<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { IonIcon } from '@ionic/vue';
import { closeOutline, cubeOutline } from 'ionicons/icons';
import { showToast } from '@/utils';
import { createQuoteArticle, updateQuoteArticle, getQuote } from '@/client/quoteClient';
import type { CreateQuoteArticleRequest, UpdateQuoteArticleRequest } from '@/entity/Quote';
import CurrencySelector from '@/component/deviseSelector/CurrencySelector.vue';
import { Currency } from '@/entity/Money';

const router = useRouter();
const route = useRoute();

const quoteSlug = computed(() => route.params.quoteSlug as string);
const itemSlug = computed(() => route.params.itemSlug as string);
const isEditMode = computed(() => !!itemSlug.value);

const itemForm = ref({
  description: '',
  quantity: 1,
  unitPrice: 0,
  currency: 'EUR' as Currency,
  vatPercent: 20,
});

const isLoading = ref(false);

const closeForm = () => {
  router.back();
};

const loadItem = async () => {
  if (!isEditMode.value) return;

  try {
    isLoading.value = true;
    const quote = await getQuote(quoteSlug.value);
    const item = quote.quoteArticles.find((a) => a.slug === itemSlug.value);

    if (item) {
      itemForm.value = {
        description: item.description,
        quantity: item.quantity,
        unitPrice: item.unitPrice.amount,
        currency: item.unitPrice.currency,
        vatPercent: item.vatPercent,
      };
    } else {
      await showToast('Item not found', 'danger');
      router.back();
    }
  } catch (error: unknown) {
    const message = 'Failed to load item. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
    router.back();
  } finally {
    isLoading.value = false;
  }
};

const saveItem = async () => {
  try {
    isLoading.value = true;

    if (isEditMode.value) {
      await updateQuoteArticle(itemSlug.value, {
        description: itemForm.value.description,
        quantity: itemForm.value.quantity,
        price: itemForm.value.unitPrice,
        currency: itemForm.value.currency,
        vatPercent: itemForm.value.vatPercent,
      } as UpdateQuoteArticleRequest);
    } else {
      await createQuoteArticle({
        quoteSlug: quoteSlug.value,
        description: itemForm.value.description,
        quantity: itemForm.value.quantity,
        price: itemForm.value.unitPrice,
        currency: itemForm.value.currency,
        vatPercent: itemForm.value.vatPercent,
      } as CreateQuoteArticleRequest);
    }

    await router.push(`/quotes/${quoteSlug.value}?t=${Date.now()}`);
  } catch (error: unknown) {
    const message =
      `Failed to ${isEditMode.value ? 'update' : 'add'} item. ` + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  } finally {
    isLoading.value = false;
  }
};

const itemTotal = computed(() => {
  return (itemForm.value.quantity * itemForm.value.unitPrice).toFixed(2);
});

const isSubmitDisabled = computed(() => {
  if (isLoading.value) return true;
  if (!itemForm.value.description.trim() || itemForm.value.quantity <= 0 || itemForm.value.unitPrice <= 0) {
    return true;
  }
  return false;
});

onMounted(() => {
  loadItem();
});
</script>

<template>
  <ApplicationViewport :title="isEditMode ? 'Edit Item' : 'Add Item'">
    <div class="form-header">
      <h2>{{ isEditMode ? 'Edit Item' : 'Add Item' }}</h2>
      <button class="close-btn" @click="closeForm">
        <IonIcon :icon="closeOutline" />
        Close
      </button>
    </div>

    <div class="form-content-wrapper">
      <div class="form-content">
        <div class="form-section">
          <div class="section-header">
            <IonIcon :icon="cubeOutline" class="section-icon" />
            <h3>Item Details</h3>
          </div>

          <div class="form-field">
            <label>Description <span class="required">*</span></label>
            <textarea v-model="itemForm.description" placeholder="Enter item description" rows="3" required></textarea>
          </div>

          <div class="form-row">
            <div class="form-field">
              <label>Quantity <span class="required">*</span></label>
              <input type="number" v-model.number="itemForm.quantity" placeholder="1" min="1" required />
            </div>

            <div class="form-field">
              <label>Unit Price <span class="required">*</span></label>
              <input
                type="number"
                v-model.number="itemForm.unitPrice"
                placeholder="0.00"
                min="0"
                step="0.01"
                required />
            </div>
          </div>

          <div class="form-row">
            <div class="form-field">
              <label>Currency <span class="required">*</span></label>
              <CurrencySelector v-model="itemForm.currency" />
            </div>

            <div class="form-field">
              <label>VAT % <span class="required">*</span></label>
              <input
                type="number"
                v-model.number="itemForm.vatPercent"
                placeholder="20"
                min="0"
                max="100"
                step="0.1"
                required />
            </div>
          </div>

          <div class="total-preview">
            <span class="total-label">Item Total:</span>
            <span class="total-value">{{ itemTotal }} {{ itemForm.currency }}</span>
          </div>
        </div>

        <button class="submit-btn" @click="saveItem" :disabled="isSubmitDisabled">
          {{ isLoading ? (isEditMode ? 'Updating...' : 'Adding...') : isEditMode ? 'Update Item' : 'Add Item' }}
        </button>
      </div>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.form-content-wrapper {
  --background: linear-gradient(to bottom, rgba(13, 27, 42, 0.95), rgba(9, 18, 30, 0.92));
  background: linear-gradient(to bottom, rgba(13, 27, 42, 0.95), rgba(9, 18, 30, 0.92));
  overflow-y: auto;
  max-height: calc(100vh - 64px);
}

.form-header {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid rgba(59, 130, 246, 0.15);
  background: linear-gradient(to bottom, rgba(13, 27, 42, 0.98), rgba(13, 27, 42, 0.85));
  backdrop-filter: blur(16px) saturate(180%);
  -webkit-backdrop-filter: blur(16px) saturate(180%);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.3);
}

.form-header h2 {
  font-size: 1.4rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
  text-shadow: 0 0 8px rgba(59, 130, 246, 0.3);
}

.close-btn {
  appearance: none;
  border: 1px solid rgba(59, 130, 246, 0.2);
  background: rgba(59, 130, 246, 0.08);
  color: #60a5fa;
  font-size: 0.9rem;
  font-weight: 600;
  padding: 8px 14px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 6px;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

.close-btn:hover {
  background: rgba(59, 130, 246, 0.15);
  border-color: rgba(59, 130, 246, 0.35);
  box-shadow: 0 0 12px rgba(59, 130, 246, 0.2);
}

.close-btn:active {
  transform: scale(0.98);
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
  flex: 1;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
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
  min-height: 80px;
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

.total-preview {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.25rem;
  background: rgba(59, 130, 246, 0.08);
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: 12px;
  margin-top: 0.5rem;
}

.total-label {
  font-size: 0.95rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.7);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.total-value {
  font-size: 1.3rem;
  font-weight: 700;
  color: #60a5fa;
  font-variant-numeric: tabular-nums;
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

  .form-row {
    grid-template-columns: 1fr;
  }

  .form-field input,
  .form-field textarea {
    font-size: 16px;
  }

  .total-preview {
    padding: 0.875rem 1rem;
  }

  .total-value {
    font-size: 1.15rem;
  }
}
</style>
