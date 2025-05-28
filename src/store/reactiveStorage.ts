import { defineStore } from 'pinia';
import { User } from '@/entity/User';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    authenticated: false,
    user: null as User | null,
  }),
  getters: {
    isAuthenticated: (state) => state.authenticated,
    getAuthenticatedUser: (state) => state.user,
    getAuthenticatedUserSlug: (state) => state.user?.slug ?? null,
  },
  actions: {
    authenticate(user: User) {
      this.authenticated = true;
      this.user = user;
    },
    logout() {
      this.authenticated = false;
      this.user = null;
    },
  },
});
