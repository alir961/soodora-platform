import { createApp } from 'vue';
import App from './App.vue';
import { IonicVue } from '@ionic/vue';
import router from './router';
import { fadeTransition } from './router/animations';

/* Core CSS required for Ionic components to work properly */
import '@ionic/vue/css/core.css';

/* Basic CSS for apps built with Ionic */
import '@ionic/vue/css/normalize.css';
import '@ionic/vue/css/structure.css';
import '@ionic/vue/css/typography.css';

/* Optional CSS utils that can be commented out */
import '@ionic/vue/css/padding.css';
import '@ionic/vue/css/float-elements.css';
import '@ionic/vue/css/text-alignment.css';
import '@ionic/vue/css/text-transformation.css';
import '@ionic/vue/css/flex-utils.css';
import '@ionic/vue/css/display.css';

/**
 * Ionic Dark Mode
 * -----------------------------------------------------
 * For more info, please see:
 * https://ionicframework.com/docs/theming/dark-mode
 */

/* Force dark mode always - ignore system preference */
import '@ionic/vue/css/palettes/dark.always.css';
/* @import '@ionic/vue/css/palettes/dark.class.css'; */
/* @import '@ionic/vue/css/palettes/dark.system.css'; */

/* Theme variables */
import './theme/variables.css';
import { configureAxios } from '@/axiosConfiguration';
import { createPinia } from 'pinia';

const app = createApp(App)
  .use(IonicVue, {
    mode: 'ios', // Force iOS mode for consistent mobile experience
    navAnimation: fadeTransition,
  })
  .use(router);
const pinia = createPinia();

configureAxios();

router.isReady().then(() => {
  app.use(pinia);
  app.mount('#app');
});
