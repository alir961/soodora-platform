import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';
import AuthView from '@/view/user/AuthenticationView.vue';
import HomeView from '@/view/explore/ExploreView.vue';
import ProfileView from '@/view/user/ProfileView.vue';
import UserProfileView from '@/view/user/UserProfileView.vue';
import MarketPlaceView from '@/view/marketplace/MarketplaceView.vue';
import NotFoundView from '@/view/NotFoundView.vue';
import MaintenanceView from '@/view/MaintenanceView.vue';
import CommentView from '@/view/explore/CommentView.vue';
import LinkPageView from '@/view/create/LinkPageView.vue';
import SearchView from '@/view/search/SearchView.vue';
import PaymentResultView from '@/view/marketplace/PaymentResultView.vue';
import MySaleView from '@/view/marketplace/MySaleView.vue';
import ConfirmationView from '@/view/user/ConfirmationView.vue';
import MediaKitCreateView from '@/view/create/MediaKitCreateView.vue';
import MediaKitEditView from '@/view/mediakit/MediaKitEditView.vue';
import QuoteCreateView from '@/view/create/QuoteCreateView.vue';
import QuoteDetailsView from '@/view/quote/QuoteDetailsView.vue';
import QuoteView from '@/view/quote/QuoteView.vue';
import ProductFormView from '@/view/marketplace/ProductFormView.vue';
import PostFormView from '@/view/explore/PostFormView.vue';
import PlaylistView from '@/view/playlist/PlaylistView.vue';
import PlaylistCreateView from '@/view/playlist/PlaylistCreateView.vue';
import PlaylistDetailsView from '@/view/playlist/PlaylistDetailsView.vue';
import PlaylistMeView from '@/view/playlist/PlaylistMeView.vue';
import CreateView from '@/view/create/CreateView.vue';

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
    path: '/posts/:slug/comments',
    name: 'CommentView',
    component: CommentView,
  },
  {
    path: '/create',
    name: 'Create',
    component: CreateView,
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
    path: '/search',
    name: 'Search',
    component: SearchView,
  },
  {
    path: '/authentication',
    name: 'Authentication',
    component: AuthView,
  },
  {
    path: '/confirm',
    name: 'Email Confirmation',
    component: ConfirmationView,
  },
  {
    path: '/profile',
    name: 'Profile',
    component: ProfileView,
  },
  {
    path: '/user/:slug',
    name: 'User',
    component: UserProfileView,
  },
  {
    path: '/marketplace',
    name: 'Marketplace',
    component: MarketPlaceView,
  },
  {
    path: '/marketplace/payment/success',
    name: 'PaymentSuccess',
    component: PaymentResultView,
    props: {
      title: 'Your payment has been validated, you will soon receive a confirmation of your order.',
      button: 'Back to marketplace',
    },
  },
  {
    path: '/marketplace/payment/cancel',
    name: 'PaymentCancel',
    component: PaymentResultView,
    props: {
      title: 'Your payment has been cancelled, you will not be charged.',
      button: 'Back to marketplace',
    },
  },
  {
    path: '/marketplace/mysales',
    name: 'MySales',
    component: MySaleView,
  },
  {
    path: '/mediakits/create',
    name: 'MediaKitCreation',
    component: MediaKitCreateView,
  },
  {
    path: '/mediakits/:slug',
    name: 'MediaKitEdit',
    component: MediaKitEditView,
  },
  {
    path: '/playlists',
    name: 'PlaylistView',
    component: PlaylistView,
  },
  {
    path: '/playlists/create',
    name: 'PlaylistCreateView',
    component: PlaylistCreateView,
  },
  {
    path: '/playlists/:slug',
    name: 'PlaylistDetailsView',
    component: PlaylistDetailsView,
  },
  {
    path: '/playlists/me',
    name: 'PlaylistMeView',
    component: PlaylistMeView,
  },
  {
    path: '/quotes/create',
    name: 'QuoteCreateView',
    component: QuoteCreateView,
  },
  {
    path: '/quotes/:slug',
    name: 'QuoteDetailsView',
    component: QuoteDetailsView,
  },
  {
    path: '/quotes/me',
    name: 'QuoteView',
    component: QuoteView,
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
  {
    path: '/marketplace/product/create',
    component: ProductFormView,
  },
  {
    path: '/marketplace/product/:slug/edit',
    component: ProductFormView,
  },
  {
    path: '/explore/post/create',
    component: PostFormView,
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
