import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    authenticated: false,
  }),
  getters: {
    isAuthenticated: (state) => state.authenticated,
  },
});
