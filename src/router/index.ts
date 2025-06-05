import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';
import AuthView from '@/view/user/AuthenticationView.vue';
import HomeView from '@/view/ExploreView.vue';
import AccountView from '@/view/user/AccountView.vue';
import MarketPlaceView from '@/view/marketplace/MarketplaceView.vue';
import PlaylistView from '@/view/playlist/PlaylistView.vue';
import PlaylistDetailsView from '@/view/playlist/PlaylistDetailsView.vue';
import NotFoundView from '@/view/NotFoundView.vue';
import MaintenanceView from '@/view/MaintenanceView.vue';
import LinkPageView from '@/view/LinkPageView.vue';
import ProfileView from '@/view/user/ProfileView.vue';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/explore',
  },
  {
    path: '/explore',
    name: 'Explore',
    component: HomeView,
  },
  {
    path: '/linkpages',
    name: 'LinkPageCreate',
    component: LinkPageView,
  },
  {
    path: '/linkpages/:slug',
    name: 'LinkPageDetail',
    component: LinkPageView,
  },
  {
    path: '/authentication',
    name: 'Authentication',
    component: AuthView,
  },
  {
    path: '/account',
    name: 'Account',
    component: AccountView,
  },
  {
    path: '/user/:slug',
    name: 'User',
    component: ProfileView,
  },
  {
    path: '/marketplace',
    name: 'Marketplace',
    component: MarketPlaceView,
  },
  {
    path: '/playlist',
    name: 'Playlist',
    component: PlaylistView,
  },
  {
    path: '/playlists/:slug',
    name: 'PlaylistDetails',
    component: PlaylistDetailsView,
  },
  {
    path: '/maintenance',
    name: 'Maintenance',
    component: MaintenanceView,
  },
  {
    path: '/:catchAll(.*)',
    name: 'NotFound',
    component: NotFoundView,
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
