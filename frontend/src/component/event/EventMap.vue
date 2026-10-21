<script setup lang="ts">
import { markRaw, onMounted, onUnmounted, PropType, ref, shallowRef, watch } from 'vue';
import mapboxgl, { LngLatBounds, Map, Marker, Popup } from 'mapbox-gl';
import { Event } from '@/entity/Event';
import { escapeHtml, safeUrl } from '@/utils';
import 'mapbox-gl/dist/mapbox-gl.css';

const props = defineProps({
  events: {
    type: Array as PropType<Event[]>,
    required: true,
  },
  allowPinPlacement: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits<{
  (e: 'pinPlaced', coords: { lat: number; lng: number }): void;
  (e: 'pinCleared'): void;
}>();

const mapContainer = ref<HTMLElement | null>(null);
const mapInstance = shallowRef<Map | null>(null);
const markers: Marker[] = [];
let placedPinMarker: Marker | null = null;
let longPressTimer: number | null = null;

let resizeObserver: ResizeObserver | null = null;

const MAPBOX_TOKEN = import.meta.env.VITE_MAPBOX_GL_TOKEN as string;

onMounted(() => {
  if (!mapContainer.value) return;

  mapboxgl.accessToken = MAPBOX_TOKEN;

  const map = new Map({
    container: mapContainer.value,
    style: 'mapbox://styles/mapbox/standard-satellite',
    center: { lat: 48.85307618688131, lon: 2.3499255436418407 },
    zoom: 4,
  });

  mapInstance.value = markRaw(map);

  map.on('load', () => {
    updateMarkers();
    fitMapToMarkers();
    map.resize();
  });

  // Long press detection for pin placement
  if (props.allowPinPlacement) {
    map.on('mousedown', handlePressStart);
    map.on('mouseup', handlePressEnd);
    map.on('mouseleave', handlePressEnd);
    map.on('dragstart', handlePressEnd);
    map.on('click', handleMapClick);

    // Touch events for mobile
    map.on('touchstart', handleTouchStart);
    map.on('touchend', handlePressEnd);
    map.on('touchmove', handlePressEnd);
  }

  resizeObserver = new ResizeObserver(() => {
    map.resize();
  });
  resizeObserver.observe(mapContainer.value);
});

const handlePressStart = (e: mapboxgl.MapMouseEvent) => {
  if (!props.allowPinPlacement) return;

  longPressTimer = window.setTimeout(() => {
    placePin(e.lngLat.lat, e.lngLat.lng);
    justPlacedPin = true;
  }, 500);
};

const handleTouchStart = (e: mapboxgl.MapTouchEvent) => {
  if (!props.allowPinPlacement) return;

  longPressTimer = window.setTimeout(() => {
    const touch = e.lngLat;
    placePin(touch.lat, touch.lng);
    justPlacedPin = true;
  }, 500);
};

const handlePressEnd = () => {
  if (longPressTimer) {
    clearTimeout(longPressTimer);
    longPressTimer = null;
  }

  if (justPlacedPin) {
    justPlacedPin = false;
    ignoreNextClick = true;
    setTimeout(() => {
      ignoreNextClick = false; // Ensures next click is valid map click
    }, 400);
  }
};

const handleMapClick = () => {
  if (ignoreNextClick) {
    ignoreNextClick = false;
    return;
  }

  if (placedPinMarker) {
    clearPlacedPin();
  }
};

let ignoreNextClick = false;
let justPlacedPin = false;

const placePin = (lat: number, lng: number) => {
  const map = mapInstance.value;
  if (!map) return;

  ignoreNextClick = true;

  // Remove existing placed pin
  if (placedPinMarker) {
    placedPinMarker.remove();
  }

  // Create animated pin element
  const container = document.createElement('div');
  container.className = 'placed-pin-container';
  container.innerHTML = `
    <svg width="40" height="52" viewBox="0 0 40 52" fill="none" xmlns="http://www.w3.org/2000/svg" class="placed-pin">
      <path d="M20 0C8.954 0 0 8.954 0 20C0 35 20 52 20 52C20 52 40 35 40 20C40 8.954 31.046 0 20 0Z" fill="#2563eb"/>
      <circle cx="20" cy="20" r="8" fill="white"/>
    </svg>
  `;

  placedPinMarker = new Marker({ element: container, anchor: 'bottom' });
  placedPinMarker.setLngLat({ lat, lng: lng });
  placedPinMarker.addTo(map);

  emit('pinPlaced', { lat, lng });
};

// Expose method to clear pin
const clearPlacedPin = () => {
  if (placedPinMarker) {
    placedPinMarker.remove();
    placedPinMarker = null;
    emit('pinCleared');
  }
};

defineExpose({ clearPlacedPin });

watch(
  () => props.events,
  () => {
    updateMarkers();
    fitMapToMarkers();
  },
  { deep: true }
);

const formatDateReadable = (dateStr: string | Date) => {
  const date = new Date(dateStr);
  const day = date.toLocaleDateString('en-GB', { weekday: 'short', day: 'numeric', month: 'short' });
  const time = date.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit', hour12: false });
  return `${day} • ${time}`;
};

const updateMarkers = () => {
  const map: Map | null = mapInstance.value as Map | null;

  if (!map) return;

  markers.forEach((marker) => marker.remove());
  markers.length = 0;

  props.events.forEach((event) => {
    // Create SVG pin marker in Soodora orange
    const container = document.createElement('div');
    container.className = 'marker-container';
    container.innerHTML = `
      <svg width="40" height="52" viewBox="0 0 40 52" fill="none" xmlns="http://www.w3.org/2000/svg" class="marker-pin">
        <path d="M20 0C8.954 0 0 8.954 0 20C0 35 20 52 20 52C20 52 40 35 40 20C40 8.954 31.046 0 20 0Z" fill="#f97316"/>
        <circle cx="20" cy="20" r="8" fill="white"/>
      </svg>
    `;

    const marker = new Marker({ element: container, anchor: 'bottom' });
    const markerRadius = 20;
    const linearOffset = 25;
    const verticalGap = 15; // Added gap
    const popupOffsets = {
      top: [0, 10 + verticalGap] as [number, number],
      'top-left': [0, 10 + verticalGap] as [number, number],
      'top-right': [0, 10 + verticalGap] as [number, number],
      bottom: [0, -50 - verticalGap] as [number, number],
      'bottom-left': [linearOffset, -50 - verticalGap] as [number, number],
      'bottom-right': [-linearOffset, -50 - verticalGap] as [number, number],
      left: [markerRadius + 5 + verticalGap, -30] as [number, number],
      right: [-(markerRadius + 5 + verticalGap), -30] as [number, number],
    };

    // Title, description and links are user content: escape everything put in the HTML.
    const thumbnailUrl = safeUrl(event.thumbnailUrl);
    const eventUrl = safeUrl(event.url);
    const popup = new Popup({ offset: popupOffsets, className: 'soodora-map-popup', closeButton: false }).setHTML(`
      <div class="event-popup-content">
        ${thumbnailUrl ? `<div class="popup-image" style="background-image: url('${escapeHtml(thumbnailUrl)}')"></div>` : ''}
        
        <div class="popup-body">
          <div class="popup-header">
            <span class="popup-type">${escapeHtml(event.type.replaceAll('_', ' '))}</span>
            <h3>${escapeHtml(event.title)}</h3>
          </div>

          <div class="popup-info-row">
            <svg class="popup-icon" viewBox="0 0 512 512" xmlns="http://www.w3.org/2000/svg"><rect fill="none" stroke="currentColor" stroke-linejoin="round" stroke-width="32" x="48" y="80" width="416" height="384" rx="48"/><path fill="none" stroke="currentColor" stroke-linejoin="round" stroke-width="32" stroke-linecap="round" d="M128 48v32M384 48v32M464 160H48"/></svg>
            <span>${escapeHtml(formatDateReadable(event.dateTime))}</span>
          </div>

          ${
            event.description
              ? `
          <div class="popup-info-row description">
            <svg class="popup-icon" viewBox="0 0 512 512" xmlns="http://www.w3.org/2000/svg"><path d="M368 416H144a16 16 0 01-16-16V112a16 16 0 0116-16h224a16 16 0 0116 16v288a16 16 0 01-16 16z" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="32"/><path d="M176 176h160M176 256h160M176 336h96" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="32"/></svg>
            <p>${escapeHtml(event.description)}</p>
          </div>`
              : ''
          }

          ${
            eventUrl
              ? `
          <a href="${escapeHtml(eventUrl)}" target="_blank" rel="noopener noreferrer" class="popup-action-btn">
            <span>Visit Website</span>
            <svg class="popup-icon-sm" viewBox="0 0 512 512" xmlns="http://www.w3.org/2000/svg"><path d="M384 224v184a40 40 0 01-40 40H104a40 40 0 01-40-40V168a40 40 0 0140-40h167.48" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="32"/><path d="M336 64h112v112M224 288L440 72" fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="32"/></svg>
          </a>`
              : ''
          }
        </div>
      </div>
    `);

    marker.setLngLat({ lat: event.latitude, lon: event.longitude });
    marker.setPopup(popup);
    marker.addTo(map);
    markers.push(marker);
  });
};

const fitMapToMarkers = () => {
  const map = mapInstance.value;
  const bounds = new LngLatBounds();

  if (!map || props.events.length === 0) return;
  props.events.forEach((event) => bounds.extend({ lat: event.latitude, lon: event.longitude }));

  map.fitBounds(bounds, {
    padding: 25,
    maxZoom: 15,
  });
};

onUnmounted(() => {
  mapInstance.value?.remove();
  resizeObserver?.disconnect();
  if (longPressTimer) clearTimeout(longPressTimer);
});
</script>

<template>
  <div class="map-wrapper">
    <div ref="mapContainer" class="map-container" />
  </div>
</template>

<style scoped>
.map-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
}

.map-container {
  width: 100%;
  height: 100%;
}

:deep(.mapboxgl-popup-content) {
  border-radius: 12px;
  padding: 15px;
}
</style>

<style>
.marker-container {
  width: 40px;
  height: 52px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  cursor: pointer;
}

.marker-pin {
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.4));
  transition:
    transform 0.2s ease,
    filter 0.2s ease;
}

.marker-container:hover .marker-pin {
  transform: scale(1.15);
  filter: drop-shadow(0 6px 12px rgba(249, 115, 22, 0.5));
}

/* Placed pin styles with animation */
.placed-pin-container {
  width: 40px;
  height: 52px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  pointer-events: none;
}

.placed-pin {
  filter: drop-shadow(0 4px 12px rgba(37, 99, 235, 0.5));
  animation: pinDrop 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes pinDrop {
  0% {
    transform: translateY(-100px) scale(0.5);
    opacity: 0;
  }

  60% {
    transform: translateY(5px) scale(1.1);
  }

  80% {
    transform: translateY(-3px) scale(0.95);
  }

  100% {
    transform: translateY(0) scale(1);
    opacity: 1;
  }
}

.placed-pin-container:hover .placed-pin {
  transform: scale(1.1);
  filter: drop-shadow(0 6px 16px rgba(37, 99, 235, 0.6));
}

/* Custom Popup Styles */
.soodora-map-popup.mapboxgl-popup {
  max-width: 300px !important;
}

.soodora-map-popup .mapboxgl-popup-content {
  background: transparent !important;
  padding: 0 !important;
  box-shadow: none !important;
  border: none !important;
  border-radius: 16px !important;
}

.soodora-map-popup .mapboxgl-popup-tip {
  display: none !important;
}

.event-popup-content {
  background: rgba(15, 23, 42, 0.95);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  overflow: hidden;
  box-shadow:
    0 20px 40px rgba(0, 0, 0, 0.4),
    0 0 0 1px rgba(255, 255, 255, 0.05);
  display: flex;
  flex-direction: column;
}

.popup-image {
  height: 120px;
  width: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
}

.popup-image::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 40px;
  background: linear-gradient(to top, rgba(15, 23, 42, 0.95), transparent);
}

.popup-body {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.popup-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.popup-type {
  font-size: 10px;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #60a5fa;
  font-weight: 700;
}

.popup-header h3 {
  margin: 0;
  color: white;
  font-size: 18px;
  font-weight: 700;
  line-height: 1.2;
}

.popup-info-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  font-size: 13px;
  color: #94a3b8;
  line-height: 1.5;
}

.popup-info-row.description {
  margin-top: 4px;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.popup-info-row p {
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  color: #cbd5e1;
}

.popup-icon {
  width: 16px;
  height: 16px;
  color: #64748b;
  flex-shrink: 0;
  margin-top: 2px;
}

.popup-icon-sm {
  width: 14px;
  height: 14px;
  flex-shrink: 0;
}

.popup-action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 8px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(37, 99, 235, 0.1));
  color: #60a5fa;
  text-decoration: none;
  padding: 10px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  border: 1px solid rgba(59, 130, 246, 0.2);
  transition: all 0.2s ease;
}

.popup-action-btn:hover {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(37, 99, 235, 0.2));
  transform: translateY(-1px);
}
</style>
