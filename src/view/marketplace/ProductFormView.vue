<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { IonContent, IonIcon } from '@ionic/vue';
import { useRoute, useRouter } from 'vue-router';
import { cartOutline, closeOutline } from 'ionicons/icons';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import ImageUploadPreview from '@/component/marketplace/ImageUploadPreview.vue';
import { ProductCreationData } from '@/entity/Product';
import { callSearchProducts, createProduct, updateProduct } from '@/client/productClient';
import { showToast } from '@/utils';

const route = useRoute();
const router = useRouter();

const isEditMode = computed(() => !!route.params.slug);
const headerTitle = computed(() => (isEditMode.value ? 'Edit Product' : 'Create Product'));
const submitButtonText = computed(() => (isEditMode.value ? 'Save Changes' : 'Create Product'));

const productData = ref<ProductCreationData>({
  name: '',
  description: '',
  category: '',
  price: undefined as unknown as number,
  currency: '',
  quantity: undefined as unknown as number,
});

const currentImageUrl = ref<string>('');
const selectedFile = ref<File | null>(null);
const isLoading = ref(false);

onMounted(async () => {
  if (isEditMode.value) {
    isLoading.value = true;
    const productSlug = route.params.slug as string;

    try {
      const response = await callSearchProducts({
        page: 0,
        size: 100,
        sort: 'createdAt,desc',
      });

      const product = response.content.find((p) => p.slug === productSlug);

      if (product) {
        productData.value = {
          name: product.name,
          description: product.description,
          category: product.category,
          price: product.price.amount,
          currency: product.price.currency,
          quantity: product.quantity ?? 0,
        };
        currentImageUrl.value = product.imageUrl;
      } else {
        throw new Error('Product not found');
      }
    } catch (error) {
      console.error('Failed to load product:', error);
      await showToast('Failed to load product', 'danger');
      router.back();
    } finally {
      isLoading.value = false;
    }
  }
});

const onFileSelected = (file: File | null) => {
  selectedFile.value = file;
};

const closeForm = () => {
  router.back();
};

const isSubmitDisabled = computed(() => {
  if (isLoading.value) return true;
  if (
    !productData.value.name.trim() ||
    !productData.value.description.trim() ||
    !productData.value.category.trim() ||
    !productData.value.price ||
    !productData.value.currency.trim() ||
    productData.value.quantity === null ||
    productData.value.quantity === undefined
  ) {
    return true;
  }
  return false;
});

const submitForm = async () => {
  if (isSubmitDisabled.value) return;

  const formData = new FormData();
  formData.append(
    'data',
    new Blob(
      [
        JSON.stringify({
          name: productData.value.name,
          description: productData.value.description,
          category: productData.value.category,
          price: productData.value.price,
          currency: productData.value.currency,
          quantity: productData.value.quantity,
        }),
      ],
      { type: 'application/json' }
    )
  );

  if (selectedFile.value) {
    formData.append('file', selectedFile.value);
  }

  try {
    if (isEditMode.value) {
      const productSlug = route.params.slug as string;
      await updateProduct(productSlug, formData);
    } else {
      await createProduct(formData);
    }

    await router.push('/marketplace');
  } catch (error: unknown) {
    const action = isEditMode.value ? 'update' : 'create';
    const message = `Failed to ${action} product. ` + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
};
</script>

<template>
  <ApplicationViewport :title="headerTitle">
    <ion-content class="form-content-wrapper">
      <div class="form-header">
        <h2>{{ headerTitle }}</h2>
        <button class="close-btn" @click="closeForm">
          <ion-icon :icon="closeOutline" />
          Close
        </button>
      </div>

      <div v-if="isLoading" class="loading">
        <p>Loading product...</p>
      </div>

      <div v-else class="form-content">
        <div class="section-header">
          <IonIcon :icon="cartOutline" class="section-icon" />
          <h3>Product Details</h3>
        </div>
        <div class="form-field">
          <label>Product Name <span class="required">*</span></label>
          <input type="text" v-model="productData.name" placeholder="Enter product name" required />
        </div>

        <div class="form-field">
          <label>Description <span class="required">*</span></label>
          <textarea v-model="productData.description" placeholder="Describe your product" rows="4" required></textarea>
        </div>

        <div class="form-field">
          <label>Category <span class="required">*</span></label>
          <select v-model="productData.category" required :class="{ placeholder: !productData.category }">
            <option value="" disabled selected hidden>Select a category</option>
            <option value="Vinyl">Vinyl</option>
            <option value="CD">CD</option>
            <option value="Clothing">Clothing</option>
            <option value="Concert Tickets">Concert Tickets</option>
            <option value="Digital Downloads">Digital Downloads</option>
            <option value="Accessories">Accessories</option>
            <option value="Posters">Posters</option>
          </select>
        </div>

        <div class="form-row">
          <div class="form-field">
            <label>Price <span class="required">*</span></label>
            <input
              type="number"
              v-model.number="productData.price"
              placeholder="Enter price"
              step="0.01"
              min="0"
              required />
          </div>
          <div class="form-field">
            <label>Currency <span class="required">*</span></label>
            <select v-model="productData.currency" required :class="{ placeholder: !productData.currency }">
              <option value="" disabled selected hidden>Select a currency</option>
              <option value="EUR">€ - EUR</option>
              <option value="USD">$ - USD</option>
              <option value="GBP">£ - GBP</option>
              <option value="CAD">C$ - CAD</option>
            </select>
          </div>
        </div>

        <div class="form-field">
          <label>Quantity <span class="required">*</span></label>
          <input type="number" v-model.number="productData.quantity" placeholder="Enter quantity" min="0" required />
        </div>

        <div class="form-field">
          <label>Product Image <span class="optional">(Optional)</span></label>
          <ImageUploadPreview :current-image-url="currentImageUrl" @file-selected="onFileSelected" />
        </div>

        <button class="submit-btn" @click="submitForm" :disabled="isSubmitDisabled">
          {{ submitButtonText }}
        </button>
      </div>
    </ion-content>
  </ApplicationViewport>
</template>

<style scoped>
.form-content-wrapper {
  --background: linear-gradient(to bottom, rgba(13, 27, 42, 0.95), rgba(9, 18, 30, 0.92));
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

.loading {
  text-align: center;
  padding: 3rem 1.5rem;
  color: #9aa3ad;
}

.form-content {
  padding: 1.5rem;
  max-width: 600px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  background: linear-gradient(to bottom, rgba(13, 27, 42, 0.6), rgba(9, 18, 30, 0.5));
  border-radius: 16px;
  margin-top: 1rem;
  margin-bottom: 1rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
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
}

.form-field input,
.form-field textarea,
.form-field select {
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
.form-field textarea:hover,
.form-field select:hover {
  border-color: rgba(59, 130, 246, 0.35);
  background: rgba(13, 27, 42, 0.6);
}

.form-field input:focus,
.form-field textarea:focus,
.form-field select:focus {
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

.submit-btn:hover {
  box-shadow:
    0 12px 32px rgba(37, 99, 235, 0.45),
    inset 0 1px 0 rgba(255, 255, 255, 0.16);
  transform: translateY(-1px);
}

.submit-btn:active {
  transform: translateY(0);
  box-shadow:
    0 8px 24px rgba(37, 99, 235, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
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

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  background: linear-gradient(135deg, #374151, #4b5563);
  box-shadow: none;
}

.form-field select.placeholder,
.form-row .form-field .placeholder {
  color: #7f7f7f !important;
}
.form-field select:invalid {
  color: #7f7f7f;
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }

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
  .form-field textarea,
  .form-field select {
    font-size: 16px;
  }
}
</style>
