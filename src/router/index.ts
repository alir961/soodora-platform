import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';
import AuthPage from '../views/AuthPage.vue';
import HomePage from '../views/HomePage.vue';
import AccountPage from '../views/AccountPage.vue';
import MarketPlacePage from '../views/MarketPlacePage.vue';
import PlaylistPage from '../views/PlaylistPage.vue';
import NotFoundPage from '../views/NotFoundPage.vue';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/auth'
  },
  {
    path: '/home',
    name: 'Home',
    component: HomePage
  },
  {
    path: '/account',
    name: 'Account',
    component: AccountPage
  },
  {
    path: '/marketplace',
    name: 'Marketplace',
    component: MarketPlacePage
  },
  {
    path: '/playlist',
    name: 'Playlist',
    component: PlaylistPage
  },
  {
    path: '/auth',
    name: 'Auth',
    component: AuthPage
  },
  {
    path: '/:catchAll(.*)', // Catch-all route for 404 errors
    name: 'NotFound',
    component: NotFoundPage
  }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

export default router;