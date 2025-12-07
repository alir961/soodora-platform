<script setup lang="ts">
import { IonLabel, IonIcon } from '@ionic/vue';
import { cloudDownloadOutline } from 'ionicons/icons';
import { ref } from 'vue';

const props = defineProps<{
  fieldLabel?: string;
  label?: string;
  accept?: string;
}>();

const emit = defineEmits<{
  (e: 'file-selected', file: File): void;
}>();

const selectedFile = ref<File | null>(null);

const onFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files[0]) {
    selectedFile.value = target.files[0];
    emit('file-selected', selectedFile.value);
  }
};
</script>

<template>
  <div class="file-upload-container">
    <IonLabel v-if="props.fieldLabel" position="stacked" class="field-label">
      {{ props.fieldLabel }}
    </IonLabel>

    <IonLabel class="file-upload-button">
      <ion-icon :icon="cloudDownloadOutline" />
      {{ props.label ?? 'Choose File' }}
      <input type="file" :accept="props.accept ?? '*/*'" @change="onFileChange" />
    </IonLabel>

    <span v-if="selectedFile" class="file-name">
      {{ selectedFile.name }}
    </span>
  </div>
</template>

<style scoped>
.file-upload-container {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin: 0.75rem 0;
}

.field-label {
  font-size: 0.9rem;
  color: var(--ion-color-medium);
  margin-bottom: 0.25rem;
}

.file-upload-button {
  display: inline-block;
  padding: 8px 15px;
  background-color: var(--ion-color-secondary);
  color: white;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.file-upload-button input[type='file'] {
  position: absolute;
  left: 0;
  top: 0;
  opacity: 0;
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.file-name {
  font-size: 0.85rem;
  color: #555;
}
</style>
