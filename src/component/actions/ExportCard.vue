<script setup lang="ts">
import { PropType, ref, computed } from 'vue';
import { IonIcon } from '@ionic/vue';
import { shareOutline } from 'ionicons/icons';
import { Post } from '@/entity/Post';
import { Product } from '@/entity/Product';
import {
  createPostCanvas,
  createProductCanvas,
  downloadCanvas,
  ExportTheme,
  CustomColors,
} from '@/graphics/canvasExport';
import { showToast } from '@/utils';

const props = defineProps({
  item: {
    required: true,
    type: Object as PropType<Post | Product>,
  },
  type: {
    required: true,
    type: String as PropType<'post' | 'product'>,
  },
});

const showExportModal = ref(false);
// Base theme controls content rendering (light/dark)
const selectedBaseTheme = ref<Exclude<ExportTheme, 'transparent'>>('black');
const previewDataUrl = ref<string>('');
const isGeneratingPreview = ref(false);
const showFullscreenPreview = ref(false);

const customAccentColor = ref('');
// Orbs removed

const showColorPicker = ref(false);
const hue = ref(195);
const saturation = ref(92);
const brightness = ref(99);
const isDraggingSL = ref(false);
const isDraggingHue = ref(false);
const slPickerRef = ref<HTMLElement | null>(null);
const hueSliderRef = ref<HTMLElement | null>(null);

const presetColors = ['#7dd3fc', '#c084fc', '#f472b6', '#34d399', '#fbbf24', '#fb923c', '#ef4444', '#176bfd'];

const hsvToHex = (h: number, s: number, v: number): string => {
  s /= 100;
  v /= 100;

  const c = v * s;
  const x = c * (1 - Math.abs(((h / 60) % 2) - 1));
  const m = v - c;

  let r = 0,
    g = 0,
    b = 0;

  if (h >= 0 && h < 60) {
    r = c;
    g = x;
    b = 0;
  } else if (h >= 60 && h < 120) {
    r = x;
    g = c;
    b = 0;
  } else if (h >= 120 && h < 180) {
    r = 0;
    g = c;
    b = x;
  } else if (h >= 180 && h < 240) {
    r = 0;
    g = x;
    b = c;
  } else if (h >= 240 && h < 300) {
    r = x;
    g = 0;
    b = c;
  } else {
    r = c;
    g = 0;
    b = x;
  }

  const toHex = (n: number) =>
    Math.round((n + m) * 255)
      .toString(16)
      .padStart(2, '0');
  return `#${toHex(r)}${toHex(g)}${toHex(b)}`;
};

const hexToHsv = (hex: string): { h: number; s: number; v: number } => {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
  if (!result) return { h: 0, s: 100, v: 100 };

  const r = parseInt(result[1], 16) / 255;
  const g = parseInt(result[2], 16) / 255;
  const b = parseInt(result[3], 16) / 255;

  const max = Math.max(r, g, b);
  const min = Math.min(r, g, b);
  const d = max - min;

  let h = 0;
  const s = max === 0 ? 0 : (d / max) * 100;
  const v = max * 100;

  if (d !== 0) {
    switch (max) {
      case r:
        h = ((g - b) / d + (g < b ? 6 : 0)) * 60;
        break;
      case g:
        h = ((b - r) / d + 2) * 60;
        break;
      case b:
        h = ((r - g) / d + 4) * 60;
        break;
    }
  }

  return { h: Math.round(h), s: Math.round(s), v: Math.round(v) };
};

const currentPickerColor = computed(() => hsvToHex(hue.value, saturation.value, brightness.value));

const slGradientStyle = computed(() => ({
  background: `linear-gradient(to top, #000, transparent), 
               linear-gradient(to right, #fff, hsl(${hue.value}, 100%, 50%))`,
}));

const slHandleStyle = computed(() => ({
  left: `${saturation.value}%`,
  top: `${100 - brightness.value}%`,
}));

const hueHandleStyle = computed(() => ({
  left: `${(hue.value / 360) * 100}%`,
}));

const handleSLStart = (e: MouseEvent | TouchEvent) => {
  isDraggingSL.value = true;
  handleSLMove(e);
  document.addEventListener('mousemove', handleSLMove);
  document.addEventListener('mouseup', handleSLEnd);
  document.addEventListener('touchmove', handleSLMove);
  document.addEventListener('touchend', handleSLEnd);
};

const handleSLMove = (e: MouseEvent | TouchEvent) => {
  if (!slPickerRef.value) return;

  const rect = slPickerRef.value.getBoundingClientRect();
  const clientX = 'touches' in e ? e.touches[0].clientX : e.clientX;
  const clientY = 'touches' in e ? e.touches[0].clientY : e.clientY;

  let x = (clientX - rect.left) / rect.width;
  let y = (clientY - rect.top) / rect.height;

  x = Math.max(0, Math.min(1, x));
  y = Math.max(0, Math.min(1, y));

  saturation.value = Math.round(x * 100);
  brightness.value = Math.round((1 - y) * 100);
};

const handleSLEnd = () => {
  isDraggingSL.value = false;
  document.removeEventListener('mousemove', handleSLMove);
  document.removeEventListener('mouseup', handleSLEnd);
  document.removeEventListener('touchmove', handleSLMove);
  document.removeEventListener('touchend', handleSLEnd);
};

const handleHueStart = (e: MouseEvent | TouchEvent) => {
  isDraggingHue.value = true;
  handleHueMove(e);
  document.addEventListener('mousemove', handleHueMove);
  document.addEventListener('mouseup', handleHueEnd);
  document.addEventListener('touchmove', handleHueMove);
  document.addEventListener('touchend', handleHueEnd);
};

const handleHueMove = (e: MouseEvent | TouchEvent) => {
  if (!hueSliderRef.value) return;

  const rect = hueSliderRef.value.getBoundingClientRect();
  const clientX = 'touches' in e ? e.touches[0].clientX : e.clientX;

  let x = (clientX - rect.left) / rect.width;
  x = Math.max(0, Math.min(1, x));

  hue.value = Math.round(x * 360);
};

const handleHueEnd = () => {
  isDraggingHue.value = false;
  document.removeEventListener('mousemove', handleHueMove);
  document.removeEventListener('mouseup', handleHueEnd);
  document.removeEventListener('touchmove', handleHueMove);
  document.removeEventListener('touchend', handleHueEnd);
};

const hexInputValue = ref('');

const handleHexInput = (event: Event) => {
  const target = event.target as HTMLInputElement;
  let value = target.value.toUpperCase();

  if (!value.startsWith('#')) {
    value = '#' + value;
  }

  hexInputValue.value = value;

  if (/^#[0-9A-F]{6}$/i.test(value)) {
    const hsv = hexToHsv(value);
    hue.value = hsv.h;
    saturation.value = hsv.s;
    brightness.value = hsv.v;
  }
};

const openColorPicker = () => {
  const hsv = hexToHsv(customAccentColor.value);
  hue.value = hsv.h;
  saturation.value = hsv.s;
  brightness.value = hsv.v;
  hexInputValue.value = customAccentColor.value.toUpperCase();
  showColorPicker.value = true;
};

const closeColorPicker = () => {
  customAccentColor.value = currentPickerColor.value;
  showColorPicker.value = false;
  generatePreview(selectedBaseTheme.value);
};

const customColors = computed<CustomColors | undefined>(() => {
  if (!customAccentColor.value) return undefined;
  return {
    accent: customAccentColor.value,
  };
});

const open = () => {
  showExportModal.value = true;
  selectedBaseTheme.value = 'black';
  generatePreview(selectedBaseTheme.value);
};

const closeModal = () => {
  showExportModal.value = false;
  selectedBaseTheme.value = 'black';
  previewDataUrl.value = '';
  showColorPicker.value = false;
};

const selectBaseTheme = (theme: Exclude<ExportTheme, 'transparent'>) => {
  selectedBaseTheme.value = theme;
  generatePreview(selectedBaseTheme.value);
};

const selectPresetColor = (color: string) => {
  customAccentColor.value = color;

  const hsv = hexToHsv(color);
  hue.value = hsv.h;
  saturation.value = hsv.s;
  brightness.value = hsv.v;
  generatePreview(selectedBaseTheme.value);
};

const resetToDefaultColor = () => {
  customAccentColor.value = '';
  generatePreview(selectedBaseTheme.value);
};

const generatePreview = async (theme: ExportTheme) => {
  isGeneratingPreview.value = true;
  try {
    const canvas =
      props.type === 'post'
        ? await createPostCanvas(props.item as Post, theme, customColors.value)
        : await createProductCanvas(props.item as Product, theme, customColors.value);

    previewDataUrl.value = canvas.toDataURL();
  } catch (error) {
    console.error('Failed to generate preview:', error);
  } finally {
    isGeneratingPreview.value = false;
  }
};

const confirmExport = async () => {
  if (!selectedBaseTheme.value) return;

  try {
    const canvas =
      props.type === 'post'
        ? await createPostCanvas(props.item as Post, selectedBaseTheme.value, customColors.value)
        : await createProductCanvas(props.item as Product, selectedBaseTheme.value, customColors.value);

    downloadCanvas(canvas);
    closeModal();
  } catch (error: unknown) {
    const message = `Failed to share ${props.type}. ` + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
};

defineExpose({
  open,
});

const openFullscreenPreview = () => {
  showFullscreenPreview.value = true;
};

const closeFullscreenPreview = () => {
  showFullscreenPreview.value = false;
};
</script>

<template>
  <Teleport to="body">
    <transition name="modal-fade">
      <div v-if="showExportModal" class="export-modal-overlay" @click="closeModal">
        <div class="export-modal" @click.stop>
          <button class="close-btn" @click="closeModal">✕</button>

          <div class="modal-content">
            <h3 class="modal-title">Share {{ type }}</h3>

            <div class="main-row">
              <div class="preview-section" @click="openFullscreenPreview">
                <div v-if="isGeneratingPreview" class="preview-loading">
                  <div class="loading-spinner"></div>
                </div>
                <img v-else-if="previewDataUrl" :src="previewDataUrl" :alt="`${type} preview`" class="preview-image" />
              </div>

              <div class="controls-section">
                <div class="theme-buttons">
                  <button
                    class="control-btn"
                    :class="{ active: selectedBaseTheme === 'black' }"
                    @click="selectBaseTheme('black')">
                    Dark
                  </button>
                  <button
                    class="control-btn"
                    :class="{ active: selectedBaseTheme === 'white' }"
                    @click="selectBaseTheme('white')">
                    Light
                  </button>
                </div>

                <div class="color-options-bar">
                  <button
                    class="default-color-btn"
                    :class="{ active: !customAccentColor }"
                    @click="resetToDefaultColor">
                    <span class="slash-icon">∅</span>
                  </button>
                  <button
                    v-for="color in presetColors"
                    :key="color"
                    class="preset-color"
                    :class="{ active: customAccentColor === color }"
                    :style="{ backgroundColor: color }"
                    @click="selectPresetColor(color)"></button>
                  <button
                    class="color-picker-trigger"
                    :style="{ backgroundColor: customAccentColor }"
                    @click="openColorPicker">
                    <span class="picker-label">Custom</span>
                  </button>
                </div>
              </div>
            </div>

            <transition name="picker-fade">
              <div v-if="showColorPicker" class="color-picker-overlay" @click="closeColorPicker">
                <div class="color-picker-modal" @click.stop>
                  <div class="picker-header">
                    <span class="picker-title">Pick a color</span>
                    <button class="picker-close" @click="closeColorPicker">✕</button>
                  </div>

                  <div
                    ref="slPickerRef"
                    class="sl-picker"
                    :style="slGradientStyle"
                    @mousedown="handleSLStart"
                    @touchstart.prevent="handleSLStart">
                    <div class="sl-handle" :style="slHandleStyle"></div>
                  </div>

                  <div
                    ref="hueSliderRef"
                    class="hue-slider"
                    @mousedown="handleHueStart"
                    @touchstart.prevent="handleHueStart">
                    <div class="hue-handle" :style="hueHandleStyle"></div>
                  </div>

                  <div class="color-preview-row">
                    <div class="color-preview-swatch" :style="{ backgroundColor: currentPickerColor }"></div>
                    <input
                      type="text"
                      class="hex-input"
                      :value="currentPickerColor.toUpperCase()"
                      @input="handleHexInput"
                      maxlength="7"
                      placeholder="#000000" />
                  </div>
                </div>
              </div>
            </transition>

            <button class="share-btn" :disabled="!selectedBaseTheme || isGeneratingPreview" @click="confirmExport">
              <ion-icon :icon="shareOutline"></ion-icon>
              <span>Download & Share</span>
            </button>
          </div>
        </div>
      </div>
    </transition>

    <transition name="preview-fade">
      <div v-if="showFullscreenPreview" class="fullscreen-overlay" @click="closeFullscreenPreview">
        <div class="fullscreen-content">
          <button class="fullscreen-close" @click="closeFullscreenPreview">✕</button>
          <img
            v-if="previewDataUrl"
            :src="previewDataUrl"
            :alt="`${type} fullscreen preview`"
            class="fullscreen-image"
            @click.stop />
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<style scoped>
.export-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(3px);
  -webkit-backdrop-filter: blur(3px);
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.export-modal {
  position: relative;
  background: linear-gradient(145deg, rgba(24, 30, 42, 0.98), rgba(18, 22, 30, 0.96));
  border-radius: 18px;
  backdrop-filter: blur(24px) saturate(180%);
  -webkit-backdrop-filter: blur(24px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow:
    0 24px 48px rgba(0, 0, 0, 0.7),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
  overflow: hidden;
  width: 340px;
}

.close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  appearance: none;
  border: 0;
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.6);
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 14px;
  font-weight: 300;
  transition: all 0.2s ease;
  z-index: 1;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
  transform: rotate(90deg);
}

.close-btn:active {
  transform: rotate(90deg) scale(0.9);
}

.modal-content {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1.25rem;
}

.modal-title {
  margin: 0;
  font-size: 1.2rem;
  font-weight: 700;
  color: #ffffff;
  text-align: center;
  letter-spacing: -0.02em;
  text-transform: capitalize;
}

.main-row {
  display: flex;
  flex-direction: row;
  gap: 1rem;
  align-items: stretch;
}

.preview-section {
  width: 160px;
  height: auto;
  align-self: stretch;
  border-radius: 12px;
  overflow: hidden;
  background: rgba(96, 165, 250, 0.06);
  border: 1px solid rgba(96, 165, 250, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.25s ease;
  flex-shrink: 0;
  padding: 12px;
  box-sizing: border-box;
}

.preview-section:hover {
  border-color: rgba(96, 165, 250, 0.25);
}

.preview-section:active {
  transform: scale(0.98);
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

.preview-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.loading-spinner {
  width: 28px;
  height: 28px;
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-top-color: #60a5fa;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.controls-section {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  flex: 1;
  width: auto;
  justify-content: flex-start;
}

.theme-buttons {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.control-btn {
  flex: 1;
  width: 100%;
  appearance: none;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.04);
  color: rgba(255, 255, 255, 0.7);
  padding: 0.4rem 0.75rem;
  min-height: 38px;
  border-radius: 10px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
}

.control-btn:hover {
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.9);
  border-color: rgba(255, 255, 255, 0.18);
}

.control-btn.active {
  background: linear-gradient(135deg, #2563eb, #60a5fa);
  color: #ffffff;
  border-color: transparent;
  box-shadow:
    0 3px 10px rgba(37, 99, 235, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.control-btn:active {
  transform: scale(0.97);
}
.color-options-bar {
  display: flex;
  gap: 0.35rem;
  padding: 0.5rem 0.6rem;
  background: rgba(0, 0, 0, 0.25);
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.06);
  overflow: hidden;
  width: 100%;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0.35rem;
  padding: 0.35rem;
}

.default-color-btn {
  width: 100%;
  aspect-ratio: 1 / 1;
  min-width: 0;
  border-radius: 8px;
  border: 2px solid rgba(255, 255, 255, 0.2);
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.25);
  background: rgba(255, 255, 255, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.default-color-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.35);
  background: rgba(255, 255, 255, 0.12);
}

.default-color-btn.active {
  border-color: #ffffff;
  box-shadow:
    0 0 0 2px rgba(255, 255, 255, 0.3),
    0 3px 10px rgba(0, 0, 0, 0.35);
}

.slash-icon {
  font-size: 24px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 300;
}

.preset-color {
  width: 100%;
  aspect-ratio: 1 / 1;
  min-width: 0;
  border-radius: 8px;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.25);
}

.preset-color:hover {
  transform: scale(1.1);
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.35);
}

.preset-color.active {
  border-color: #ffffff;
  box-shadow:
    0 0 0 2px rgba(255, 255, 255, 0.3),
    0 3px 10px rgba(0, 0, 0, 0.35);
}

.color-picker-trigger {
  width: 100%;
  aspect-ratio: auto;
  min-width: 0;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  box-sizing: border-box;
  background: conic-gradient(
    from 0deg,
    #ef4444,
    #f59e0b,
    #eab308,
    #84cc16,
    #10b981,
    #06b6d4,
    #3b82f6,
    #8b5cf6,
    #ec4899,
    #ef4444
  );
  color: #ffffff;
  font-weight: 700;
  letter-spacing: 0.5px;
  grid-column: 1 / -1;
  min-height: 42px;
  overflow: hidden;
}

.color-picker-trigger:hover {
  transform: scale(1.03);
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.35);
}

.picker-label {
  font-size: 0.75rem;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.7);
  font-weight: 800;
  background: rgba(0, 0, 0, 0.4);
  padding: 0.25rem 0.6rem;
  border-radius: 6px;
  backdrop-filter: blur(4px);
}

.color-picker-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  z-index: 10002;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.color-picker-modal {
  background: linear-gradient(145deg, rgba(30, 36, 50, 0.98), rgba(22, 26, 36, 0.96));
  border-radius: 16px;
  padding: 1rem;
  border: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.6);
  width: 260px;
}

.picker-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.75rem;
}

.picker-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: #ffffff;
}

.picker-close {
  appearance: none;
  border: 0;
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.6);
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s ease;
}

.picker-close:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.sl-picker {
  width: 100%;
  height: 160px;
  border-radius: 10px;
  position: relative;
  cursor: crosshair;
  margin-bottom: 0.75rem;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.3);
}

.sl-handle {
  position: absolute;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 2px solid #ffffff;
  box-shadow:
    0 2px 6px rgba(0, 0, 0, 0.4),
    inset 0 0 0 1px rgba(0, 0, 0, 0.1);
  transform: translate(-50%, -50%);
  pointer-events: none;
}

.hue-slider {
  width: 100%;
  height: 20px;
  border-radius: 10px;
  background: linear-gradient(
    to right,
    hsl(0, 100%, 50%),
    hsl(60, 100%, 50%),
    hsl(120, 100%, 50%),
    hsl(180, 100%, 50%),
    hsl(240, 100%, 50%),
    hsl(300, 100%, 50%),
    hsl(360, 100%, 50%)
  );
  position: relative;
  cursor: pointer;
  margin-bottom: 0.75rem;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.2);
}

.hue-handle {
  position: absolute;
  top: 50%;
  width: 8px;
  height: 24px;
  border-radius: 4px;
  background: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.2);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
  transform: translate(-50%, -50%);
  pointer-events: none;
}

.color-preview-row {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 8px;
  overflow: hidden;
}

.color-preview-swatch {
  width: 36px;
  height: 36px;
  min-width: 36px;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
  flex-shrink: 0;
}

.hex-input {
  width: 100%;
  min-width: 0;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 6px;
  padding: 0.5rem 0.6rem;
  font-family: 'SF Mono', Monaco, Consolas, monospace;
  font-size: 0.9rem;
  font-weight: 600;
  color: #ffffff;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  outline: none;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

.hex-input:focus {
  border-color: rgba(96, 165, 250, 0.5);
  background: rgba(255, 255, 255, 0.12);
}

.hex-input::placeholder {
  color: rgba(255, 255, 255, 0.3);
}

.picker-fade-enter-active {
  animation: pickerFadeIn 0.2s ease;
}

.picker-fade-leave-active {
  animation: pickerFadeIn 0.15s ease reverse;
}

@keyframes pickerFadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.share-btn {
  width: 100%;
  height: 46px;
  border: 0;
  border-radius: 10px;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #1d4ed8, #2563eb 50%, #60a5fa);
  box-shadow:
    0 5px 15px rgba(37, 99, 235, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.share-btn ion-icon {
  font-size: 16px;
}

.share-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none !important;
}

.share-btn:not(:disabled):hover {
  box-shadow:
    0 7px 20px rgba(37, 99, 235, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.share-btn:not(:disabled):active {
  transform: translateY(0);
  box-shadow:
    0 3px 10px rgba(37, 99, 235, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
}

.fullscreen-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(3px);
  -webkit-backdrop-filter: blur(3px);
  z-index: 10001;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  cursor: zoom-out;
}

.fullscreen-content {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.fullscreen-close {
  position: absolute;
  top: -16px;
  right: -16px;
  appearance: none;
  border: 0;
  background: rgba(18, 22, 30, 0.95);
  color: #ffffff;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 22px;
  font-weight: 300;
  transition: all 0.2s ease;
  z-index: 1;
  border: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.5);
}

.fullscreen-close:hover {
  background: rgba(239, 68, 68, 0.95);
  border-color: rgba(239, 68, 68, 0.4);
  transform: scale(1.08) rotate(90deg);
}

.fullscreen-close:active {
  transform: scale(0.95) rotate(90deg);
}

.fullscreen-image {
  max-width: 90vw;
  max-height: 90vh;
  object-fit: contain;
  border-radius: 12px;
  cursor: default;
  display: block;
}

.modal-fade-enter-active {
  animation: modalFadeIn 0.2s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.modal-fade-leave-active {
  animation: modalFadeIn 0.18s cubic-bezier(0.8, 0.2, 0.8, 0.2) reverse;
}

.modal-fade-enter-active .export-modal {
  animation: modalScale 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.modal-fade-leave-active .export-modal {
  animation: modalScale 0.22s cubic-bezier(0.8, 0.2, 0.8, 0.2) reverse;
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
    transform: scale(0.9) translateY(20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.preview-fade-enter-active {
  animation: previewFadeIn 0.25s ease;
}

.preview-fade-leave-active {
  animation: previewFadeIn 0.2s ease reverse;
}

@keyframes previewFadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@media (max-width: 380px) {
  .main-row {
    flex-direction: column;
    align-items: center;
  }

  .preview-section {
    width: 100%;
    height: 220px;
  }

  .controls-section {
    width: 100%;
    height: auto;
    flex-direction: column;
    gap: 0.6rem;
  }
}
</style>
