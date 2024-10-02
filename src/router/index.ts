import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';
import HomePage from '../views/HomePage.vue'
import AccountPage from '../views/AccountPage.vue';
import MarketPlacePage from '../views/MarketPlacePage.vue'
import PlaylistPage from '../views/PlaylistPage.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/home'
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
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
