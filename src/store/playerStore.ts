import { defineStore } from 'pinia';
import type { User } from '@/entity/User';
import type { Artist } from '@/entity/Artist';

export interface NowPlaying {
  slug: string;
  title: string;
  audioTitle?: string;
  artist?: string;
  imageUrl?: string;
  audioUrl: string;
  duration: number;
  owner?: User | Artist;
}

export const usePlayerStore = defineStore('player', {
  state: () => ({
    current: null as NowPlaying | null,
    isPlaying: false,
    currentTime: 0,
    duration: 0,
  }),
  getters: {
    hasTrack: (state) => !!state.current,
  },
  actions: {
    setTrack(track: NowPlaying) {
      this.current = track;
      this.duration = track.duration || 0;
      this.currentTime = 0;
    },
    updateTime(time: number) {
      this.currentTime = time;
    },
    updateDuration(duration: number) {
      this.duration = duration;
    },
    play() {
      this.isPlaying = true;
      document.dispatchEvent(
        new CustomEvent('player-control', {
          detail: { action: 'play', slug: this.current?.slug },
        })
      );
    },
    pause() {
      this.isPlaying = false;
      document.dispatchEvent(
        new CustomEvent('player-control', {
          detail: { action: 'pause', slug: this.current?.slug },
        })
      );
    },
    seek(time: number) {
      this.currentTime = time;
      document.dispatchEvent(
        new CustomEvent('player-control', {
          detail: { action: 'seek', time, slug: this.current?.slug },
        })
      );
    },
    clear() {
      this.current = null;
      this.isPlaying = false;
      this.currentTime = 0;
      this.duration = 0;
      document.dispatchEvent(new CustomEvent('player-control', { detail: { action: 'stop' } }));
    },
  },
});
