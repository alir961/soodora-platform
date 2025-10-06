<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import type { CreateQuoteArticleRequest, QuoteArticleDTO, QuoteDTO, UpdateQuoteArticleRequest } from '@/entity/Quote';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import {
  addCircleOutline,
  arrowBackOutline,
  createOutline,
  documentTextOutline,
  downloadOutline,
  trashOutline,
} from 'ionicons/icons';
import {
  IonAlert,
  IonButton,
  IonButtons,
  IonContent,
  IonHeader,
  IonIcon,
  IonInput,
  IonItem,
  IonLabel,
  IonList,
  IonListHeader,
  IonModal,
  IonTextarea,
  IonTitle,
  IonToolbar,
} from '@ionic/vue';
import { showToast } from '@/utils';
import { useAuthStore } from '@/store/reactiveStorage';
import {
  createQuoteArticle,
  deleteQuote as deleteQuoteApi,
  deleteQuoteArticle,
  exportQuotePdf as downloadQuotePdf,
  getQuote,
  updateQuote,
  updateQuoteArticle,
} from '@/client/quoteClient';
import CurrencySelector from '@/component/deviseSelector/CurrencySelector.vue';
import { Currency } from '@/entity/Money';

const route = useRoute();
const router = useRouter();

const quote = ref<QuoteDTO | null>(null);
const showDeleteConfirm = ref(false);
const showEditModal = ref(false);
const showArticleModal = ref(false);
const editingArticle = ref<QuoteArticleDTO | null>(null);

const editCustomerName = ref('');
const editCustomerEmail = ref('');
const editCustomerAddress = ref('');
const editCustomerPhone = ref('');
const editNotes = ref('');

const articleDescription = ref('');
const articleQuantity = ref(1);
const articlePrice = ref(0);
const articleCurrency = ref<Currency>('EUR');
const articleVatPercent = ref(20);

const authStore = useAuthStore();
const isOwner = computed(() => {
  return quote.value?.artist.slug === authStore.getAuthenticatedUserSlug;
});

const loadQuote = async (slug: string) => {
  try {
    const data = await getQuote(slug);
    quote.value = data;

    editCustomerName.value = data.clientName;
    editCustomerEmail.value = data.clientEmail;
    editCustomerAddress.value = data.clientAddress;
    editCustomerPhone.value = data.clientPhone;
    editNotes.value = data.description ?? '';

    if (!quote.value.quoteArticles) quote.value.quoteArticles = [];
  } catch (e) {
    await showToast('Failed to load quote', 'danger');
  }
};

const openEditModal = () => {
  showEditModal.value = true;
};

const submitEdit = async () => {
  if (!quote.value) return;
  try {
    quote.value = await updateQuote(quote.value.slug, {
      clientName: editCustomerName.value,
      clientEmail: editCustomerEmail.value,
      clientAddress: editCustomerAddress.value,
      clientPhone: editCustomerPhone.value,
      description: editNotes.value,
    });
    showEditModal.value = false;
    await showToast('Quote updated successfully');
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : 'Failed to update quote';
    await showToast(message, 'danger');
  }
};

const confirmDeleteQuote = () => {
  showDeleteConfirm.value = true;
};

const handleDeleteQuote = async () => {
  if (!quote.value) return;
  try {
    await deleteQuoteApi(quote.value.slug);
    await router.push('/quotes/me');
    await showToast('Quote deleted successfully');
  } catch {
    await showToast('Failed to delete quote', 'danger');
  }
};

const openArticleModal = (article?: QuoteArticleDTO) => {
  if (article) {
    editingArticle.value = article;
    articleDescription.value = article.description;
    articleQuantity.value = article.quantity;
    articlePrice.value = article.unitPrice.amount;
    articleCurrency.value = article.unitPrice.currency;
    articleVatPercent.value = article.vatPercent;
  } else {
    editingArticle.value = null;
    articleDescription.value = '';
    articleQuantity.value = 1;
    articlePrice.value = 0;
    articleCurrency.value = 'EUR';
    articleVatPercent.value = 20;
  }
  showArticleModal.value = true;
};

const submitArticle = async () => {
  if (!quote.value) return;

  try {
    if (editingArticle.value) {
      const updated = await updateQuoteArticle(editingArticle.value.slug, {
        description: articleDescription.value,
        quantity: articleQuantity.value,
        price: articlePrice.value,
        currency: articleCurrency.value,
        vatPercent: articleVatPercent.value,
      } as UpdateQuoteArticleRequest);

      const index = quote.value.quoteArticles.findIndex((a) => a.slug === updated.slug);
      if (index !== -1) quote.value.quoteArticles[index] = updated;

      await showToast('Article updated');
    } else {
      const created = await createQuoteArticle({
        quoteSlug: quote.value.slug,
        description: articleDescription.value,
        quantity: articleQuantity.value,
        price: articlePrice.value,
        currency: articleCurrency.value,
        vatPercent: articleVatPercent.value,
      } as CreateQuoteArticleRequest);

      quote.value.quoteArticles.push(created);
      await showToast('Article added');
    }

    showArticleModal.value = false;
  } catch {
    await showToast('Failed to save article', 'danger');
  }
};

const handleDeleteArticle = async (articleSlug: string) => {
  if (!quote.value) return;
  try {
    await deleteQuoteArticle(articleSlug);
    const index = quote.value.quoteArticles.findIndex((a) => a.slug === articleSlug);
    if (index !== -1) quote.value.quoteArticles.splice(index, 1);
    await showToast('Article deleted');
  } catch {
    await showToast('Failed to delete article', 'danger');
  }
};

const downloadPdf = async () => {
  if (!quote.value) return;
  try {
    const blob = await downloadQuotePdf(quote.value.slug);
    const url = window.URL.createObjectURL(new Blob([blob]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `quote-${quote.value.slug}.pdf`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    await showToast('PDF downloaded');
  } catch {
    await showToast('Failed to download PDF', 'danger');
  }
};

watch(
  () => route.params.slug,
  (newSlug) => {
    if (typeof newSlug === 'string') loadQuote(newSlug);
  },
  { immediate: true }
);
</script>

<template>
  <ApplicationViewport title="Quote Details">
    <IonHeader>
      <IonButtons slot="start">
        <IonButton fill="clear" @click="$router.back()">
          <IonIcon :icon="arrowBackOutline" slot="start" /> Back
        </IonButton>
      </IonButtons>

      <IonTitle>{{ quote?.clientName || 'Quote' }}</IonTitle>

      <IonButtons slot="end" v-if="isOwner">
        <IonButton fill="clear" @click="openEditModal">
          <IonIcon :icon="createOutline" />
        </IonButton>
        <IonButton fill="clear" color="danger" @click="confirmDeleteQuote">
          <IonIcon :icon="trashOutline" />
        </IonButton>
        <IonButton fill="clear" @click="downloadPdf">
          <IonIcon :icon="downloadOutline" />
        </IonButton>
      </IonButtons>
    </IonHeader>

    <IonContent class="ion-padding">
      <p><strong>Email:</strong> {{ quote?.clientEmail }}</p>
      <p><strong>Address:</strong> {{ quote?.clientAddress }}</p>
      <p><strong>Phone:</strong> {{ quote?.clientPhone }}</p>
      <p v-if="quote?.description"><strong>Notes:</strong> {{ quote.description }}</p>

      <IonList v-if="quote?.quoteArticles?.length">
        <IonListHeader>
          <IonLabel>Articles</IonLabel>
          <IonButton size="small" fill="outline" @click="openArticleModal()">
            <IonIcon :icon="addCircleOutline" slot="start" /> Add Article
          </IonButton>
        </IonListHeader>

        <IonItem v-for="article in quote.quoteArticles" :key="article.slug">
          <IonLabel>
            <h2>{{ article.description }}</h2>
            <p>
              Quantity: {{ article.quantity }} – Unit Price: {{ article.unitPrice.amount }}
              {{ article.unitPrice.symbol }} {{ article.vatPercent }}% VAT)
            </p>
          </IonLabel>
          <IonButton size="small" fill="clear" @click="openArticleModal(article)">
            <IonIcon :icon="createOutline" />
          </IonButton>
          <IonButton size="small" color="danger" fill="clear" @click="handleDeleteArticle(article.slug)">
            <IonIcon :icon="trashOutline" />
          </IonButton>
        </IonItem>
      </IonList>

      <div v-else class="empty-state">
        <IonIcon :icon="documentTextOutline" size="large" />
        <p>No articles in this quote yet.</p>
        <IonButton @click="openArticleModal()">Add First Article</IonButton>
      </div>
    </IonContent>

    <IonAlert
      :is-open="showDeleteConfirm"
      header="Delete Quote"
      message="Are you sure you want to delete this quote?"
      :buttons="[
        { text: 'Cancel', role: 'cancel', handler: () => (showDeleteConfirm = false) },
        { text: 'Delete', role: 'destructive', handler: handleDeleteQuote },
      ]"
      @didDismiss="showDeleteConfirm = false" />

    <IonModal :is-open="showEditModal" @didDismiss="showEditModal = false">
      <IonHeader>
        <IonToolbar><IonTitle>Edit Quote</IonTitle></IonToolbar>
      </IonHeader>
      <IonContent class="ion-padding">
        <IonItem>
          <IonLabel position="stacked">Customer Name</IonLabel>
          <IonInput v-model="editCustomerName" />
        </IonItem>
        <IonItem>
          <IonLabel position="stacked">Customer Email</IonLabel>
          <IonInput v-model="editCustomerEmail" />
        </IonItem>
        <IonItem>
          <IonLabel position="stacked">Customer Address</IonLabel>
          <IonInput v-model="editCustomerAddress" />
        </IonItem>
        <IonItem>
          <IonLabel position="stacked">Customer Phone</IonLabel>
          <IonInput v-model="editCustomerPhone" />
        </IonItem>
        <IonItem>
          <IonLabel position="stacked">Notes</IonLabel>
          <IonTextarea v-model="editNotes" />
        </IonItem>
        <IonButton expand="block" @click="submitEdit">Save</IonButton>
      </IonContent>
    </IonModal>

    <IonModal :is-open="showArticleModal" @didDismiss="showArticleModal = false">
      <IonHeader>
        <IonToolbar>
          <IonTitle>{{ editingArticle ? 'Edit Article' : 'Add Article' }}</IonTitle>
        </IonToolbar>
      </IonHeader>
      <IonContent class="ion-padding">
        <IonItem>
          <IonLabel position="stacked">Description</IonLabel>
          <IonInput v-model="articleDescription" />
        </IonItem>
        <IonItem>
          <IonLabel position="stacked">Quantity</IonLabel>
          <IonInput type="number" v-model.number="articleQuantity" />
        </IonItem>
        <IonItem>
          <IonLabel position="stacked">Unit Price</IonLabel>
          <IonInput type="number" v-model.number="articlePrice" />
        </IonItem>
        <IonItem>
          <IonLabel position="stacked">Currency</IonLabel>
          <CurrencySelector v-model="articleCurrency" />
        </IonItem>
        <IonItem>
          <IonLabel position="stacked">VAT %</IonLabel>
          <IonInput type="number" v-model.number="articleVatPercent" />
        </IonItem>
        <IonButton expand="block" @click="submitArticle">
          {{ editingArticle ? 'Update Article' : 'Add Article' }}
        </IonButton>
      </IonContent>
    </IonModal>
  </ApplicationViewport>
</template>

<style scoped>
.empty-state {
  text-align: center;
  margin-top: 2rem;
  color: var(--ion-color-medium);
}
</style>
