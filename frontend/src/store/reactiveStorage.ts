import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    authenticated: false,
  }),
  actions: {
    setAuthenticated(value: boolean) {
      this.authenticated = value;
    },
  },
  getters: {
    isAuthenticated: (state) => state.authenticated,
  },
});
