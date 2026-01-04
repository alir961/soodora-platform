<script setup lang="ts">
import { markRaw, onMounted, onUnmounted, PropType, ref, watch } from 'vue';
import mapboxgl, { LngLatBounds, Map, Marker, Popup } from 'mapbox-gl';
import { Event } from '@/entity/Event';
import 'mapbox-gl/dist/mapbox-gl.css';
import { formatDate } from '@/utils';

const props = defineProps({
  events: {
    type: Array as PropType<Event[]>,
    required: true,
  },
});

const mapContainer = ref<HTMLElement | null>(null);
const mapInstance = ref<Map | null>(null);
const markers: Marker[] = [];

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
  resizeObserver = new ResizeObserver(() => {
    map.resize();
  });
  resizeObserver.observe(mapContainer.value);
});

watch(
  () => props.events,
  () => {
    updateMarkers();
    fitMapToMarkers();
  },
  { deep: true }
);

const updateMarkers = () => {
  const map: Map | null = mapInstance.value as Map | null;

  if (!map) return;

  markers.forEach((marker) => marker.remove());
  markers.length = 0;

  props.events.forEach((event) => {
    const container = document.createElement('div');
    const visual = document.createElement('div');

    container.className = 'marker-container';
    visual.className = 'marker-visual';

    if (event.thumbnailUrl) visual.style.backgroundImage = `url('${event.thumbnailUrl}')`;
    if (container.appendChild(visual)) {
      const marker = new Marker({ element: container, anchor: 'bottom' });
      const popup = new Popup({ offset: 50 }).setHTML(`
        <div class="event-popup">
          <h3>${event.title}</h3>
          <p>${event.type.replaceAll('_', ' ')} • ${formatDate(event.dateTime)}</p>
          <a href="${event.url}" target="_blank">Visit Website</a>
          <img src="${event.thumbnailUrl}" alt="Event Thumbnail"/>
        </div>
      `);

      marker.setLngLat({ lat: event.latitude, lon: event.longitude });
      marker.setPopup(popup);
      marker.addTo(map);
      markers.push(marker);
    }
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
  width: 44px;
  height: 52px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  cursor: pointer;
}

.marker-visual {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background-color: #2563eb;
  background-size: cover;
  background-position: center;
  border: 2px solid #ffffff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);

  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    border-color 0.2s;
  position: relative;
}

.marker-visual::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-top: 8px solid #ffffff;
}

.marker-container:hover .marker-visual {
  transform: scale(1.15);
  box-shadow: 0 8px 16px rgba(59, 130, 246, 0.6);
  border-color: #60a5fa;
  z-index: 10;
}

.event-popup {
  color: black;
  background-size: cover;
  width: 100%;

  & h3 {
    margin: 0;
  }

  & p {
    margin: 0;
  }
}
</style>
