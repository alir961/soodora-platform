import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

import './theme/variables.css';
import 'ionicons/dist/ionicons/ionicons.esm.js';

import { configureAxios } from '@/axiosConfiguration';
import { createPinia } from 'pinia';

const app = createApp(App).use(router);
const pinia = createPinia();

configureAxios();

router.isReady().then(() => {
  app.use(pinia);
  app.mount('#app');
});
