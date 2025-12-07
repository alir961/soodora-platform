import { usePlayerStore } from '@/store/playerStore';
import type { NowPlaying } from '@/store/playerStore';

export interface PlaybackContext {
  contextId: string; // unique identifier for the context (e.g., 'playlist:slug', 'explore:for-you', 'profile:slug')
  tracks: NowPlaying[]; // list of tracks in this context
}

/**
 * Centralized Audio Player Service
 * This service manages a single global audio element to ensure consistent playback
 * across all views (Explore, Playlists, Profile, etc.)
 */
class AudioPlayerService {
  private audio: HTMLAudioElement | null = null;
  private playerStore: ReturnType<typeof usePlayerStore> | null = null;
  private currentTrackSlug: string | null = null;
  private playbackContext: PlaybackContext | null = null;

  constructor() {
    if (typeof window !== 'undefined') {
      this.audio = new Audio();
      this.audio.preload = 'metadata';
      this.setupAudioListeners();
    }
  }

  private setupAudioListeners() {
    if (!this.audio) return;

    // Time update
    this.audio.addEventListener('timeupdate', () => {
      if (this.playerStore && this.audio) {
        this.playerStore.updateTime(this.audio.currentTime);
      }
    });

    // Duration loaded
    this.audio.addEventListener('loadedmetadata', () => {
      if (this.playerStore && this.audio) {
        this.playerStore.updateDuration(this.audio.duration);
      }
    });

    // Track ended
    this.audio.addEventListener('ended', () => {
      if (this.playerStore) {
        this.playerStore.isPlaying = false;

        // Try to play next track from context
        if (this.playbackContext && this.currentTrackSlug) {
          const currentIndex = this.playbackContext.tracks.findIndex((t) => t.slug === this.currentTrackSlug);

          if (currentIndex !== -1 && currentIndex < this.playbackContext.tracks.length - 1) {
            // Play next track
            const nextTrack = this.playbackContext.tracks[currentIndex + 1];
            setTimeout(() => {
              this.playTrack(nextTrack, this.playbackContext!);
            }, 300);
          } else {
            // End of queue - pause at the end
            this.playerStore.updateTime(this.audio?.duration || 0);
            this.playerStore.isPlaying = false;
          }
        } else {
          // No context - just reset to beginning
          this.playerStore.updateTime(0);
        }
      }
    });

    // Play event
    this.audio.addEventListener('play', () => {
      if (this.playerStore) {
        this.playerStore.isPlaying = true;
      }
    });

    // Pause event
    this.audio.addEventListener('pause', () => {
      if (this.playerStore) {
        this.playerStore.isPlaying = false;
      }
    });

    // Error handling
    this.audio.addEventListener('error', (e) => {
      console.error('Audio playback error:', e);
      if (this.playerStore) {
        this.playerStore.isPlaying = false;
      }
    });
  }

  /**
   * Initialize the player store reference
   */
  init(playerStore: ReturnType<typeof usePlayerStore>) {
    this.playerStore = playerStore;
  }

  /**
   * Load and play a track with optional playback context
   */
  async playTrack(track: NowPlaying, context?: PlaybackContext) {
    if (!this.audio || !this.playerStore) {
      console.error('Audio service not initialized');
      return;
    }

    try {
      // Store the context if provided
      if (context) {
        this.playbackContext = context;
      }

      // If same track, just toggle play/pause
      if (this.currentTrackSlug === track.slug) {
        if (this.audio.paused) {
          await this.audio.play();
          this.playerStore.isPlaying = true;
        } else {
          this.audio.pause();
          this.playerStore.isPlaying = false;
        }
        return;
      }

      // New track - update store and load audio
      this.currentTrackSlug = track.slug;
      this.playerStore.setTrack(track);

      this.audio.src = track.audioUrl;
      this.audio.load();

      // Wait a bit for metadata to load before playing
      await new Promise((resolve) => setTimeout(resolve, 100));

      await this.audio.play();
      this.playerStore.isPlaying = true;
    } catch (error) {
      console.error('Failed to play track:', error);
      this.playerStore.isPlaying = false;
    }
  }

  /**
   * Play the current track (if paused)
   */
  async play() {
    if (!this.audio || !this.playerStore) return;

    try {
      if (this.audio.src) {
        await this.audio.play();
        this.playerStore.isPlaying = true;
      }
    } catch (error) {
      console.error('Failed to play:', error);
      this.playerStore.isPlaying = false;
    }
  }

  /**
   * Pause the current track
   */
  pause() {
    if (!this.audio || !this.playerStore) return;

    this.audio.pause();
    this.playerStore.isPlaying = false;
  }

  /**
   * Seek to a specific time
   */
  seek(time: number) {
    if (!this.audio || !this.playerStore) return;

    this.audio.currentTime = time;
    this.playerStore.updateTime(time);
  }

  /**
   * Stop and clear the current track
   */
  stop() {
    if (!this.audio || !this.playerStore) return;

    this.audio.pause();
    this.audio.currentTime = 0;
    this.audio.removeAttribute('src');
    this.audio.load(); // Reset the audio element without triggering error
    this.currentTrackSlug = null;
  }

  /**
   * Play the next track in the current context
   */
  async playNext(): Promise<boolean> {
    if (!this.playbackContext || !this.currentTrackSlug) {
      return false;
    }

    const currentIndex = this.playbackContext.tracks.findIndex((t) => t.slug === this.currentTrackSlug);

    if (currentIndex !== -1 && currentIndex < this.playbackContext.tracks.length - 1) {
      const nextTrack = this.playbackContext.tracks[currentIndex + 1];
      await this.playTrack(nextTrack, this.playbackContext);
      return true;
    }

    // End of queue - seek to end and pause
    if (this.audio && this.playerStore) {
      this.audio.pause();
      this.audio.currentTime = this.audio.duration || 0;
      this.playerStore.updateTime(this.audio.currentTime);
      this.playerStore.isPlaying = false;
    }

    return false; // No next track
  }

  /**
   * Get the current playing slug
   */
  getCurrentSlug(): string | null {
    return this.currentTrackSlug;
  }

  /**
   * Check if a specific track is currently playing
   */
  isTrackPlaying(slug: string): boolean {
    return this.currentTrackSlug === slug && !!this.audio && !this.audio.paused;
  }

  /**
   * Check if a specific track is the current track (playing or paused)
   */
  isCurrentTrack(slug: string): boolean {
    return this.currentTrackSlug === slug;
  }

  /**
   * Preload metadata for a track to get its duration
   * This creates a temporary audio element to load metadata without affecting playback
   */
  async preloadTrackMetadata(audioUrl: string): Promise<number> {
    return new Promise((resolve) => {
      const tempAudio = new Audio();
      tempAudio.preload = 'metadata';

      const onMetadata = () => {
        const duration = tempAudio.duration || 0;
        cleanup();
        resolve(duration);
      };

      const onError = () => {
        cleanup();
        resolve(0); // Return 0 on error
      };

      const cleanup = () => {
        tempAudio.removeEventListener('loadedmetadata', onMetadata);
        tempAudio.removeEventListener('error', onError);
        tempAudio.src = '';
      };

      tempAudio.addEventListener('loadedmetadata', onMetadata);
      tempAudio.addEventListener('error', onError);
      tempAudio.src = audioUrl;
    });
  }
}

// Export singleton instance
export const audioPlayerService = new AudioPlayerService();
