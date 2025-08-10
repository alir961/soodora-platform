import { defineStore } from 'pinia';
import { User } from '@/entity/User';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    authenticated: false,
    user: null as User | null,
    artistSlug: null as string | null,
  }),
  getters: {
    isAuthenticated: (state) => state.authenticated,
    getAuthenticatedUser: (state) => state.user,
    getAuthenticatedUserSlug: (state) => state.user?.slug ?? null,
    getAuthenticatedArtistSlug: (state) => state.artistSlug,
  },
  actions: {
    authenticate(user: User) {
      this.authenticated = true;
      this.user = user;
    },
    setArtistSlug(slug: string | null) {
      this.artistSlug = slug;
    },
    logout() {
      this.authenticated = false;
      this.user = null;
      this.artistSlug = null;
    },
  },
});
