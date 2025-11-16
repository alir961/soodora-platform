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
import LinkHubCreateView from '@/view/linkhub/LinkHubCreateView.vue';
import SearchView from '@/view/search/SearchView.vue';
import PaymentResultView from '@/view/marketplace/PaymentResultView.vue';
import MySaleView from '@/view/marketplace/MySaleView.vue';
import ConfirmationView from '@/view/user/ConfirmationView.vue';
import SoodoraKitFormView from '@/view/soodorakit/SoodoraKitFormView.vue';
import SoodoraKitEditView from '@/view/soodorakit/SoodoraKitEditView.vue';
import SoodoraKitItemFormView from '@/view/soodorakit/SoodoraKitItemFormView.vue';
import QuoteCreateView from '@/view/quote/QuoteFormView.vue';
import QuoteDetailsView from '@/view/quote/QuoteDetailsView.vue';
import QuoteView from '@/view/quote/QuoteView.vue';
import QuoteItemFormView from '@/view/quote/QuoteItemFormView.vue';
import ProductFormView from '@/view/marketplace/ProductFormView.vue';
import PostFormView from '@/view/explore/PostFormView.vue';
import PlaylistFormView from '@/view/playlist/PlaylistFormView.vue';
import PlaylistDetailsView from '@/view/playlist/PlaylistDetailsView.vue';
import PlaylistMeView from '@/view/playlist/PlaylistView.vue';
import LinkHubEditView from '@/view/linkhub/LinkHubEditView.vue';
import LinkHubDetailView from '@/view/linkhub/LinkHubDetailView.vue';

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
    path: '/linkhub/create',
    name: 'LinkHubCreate',
    component: LinkHubCreateView,
  },
  {
    path: '/linkhub/edit/:slug',
    name: 'LinkHubEdit',
    component: LinkHubEditView,
  },
  {
    path: '/linkhub/:slug',
    name: 'LinkHubDetail',
    component: LinkHubDetailView,
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
    path: '/soodorakits/create',
    name: 'SoodoraKitCreation',
    component: SoodoraKitFormView,
  },
  {
    path: '/soodorakits/:slug/items/new',
    name: 'SoodoraKitItemCreate',
    component: SoodoraKitItemFormView,
  },
  {
    path: '/soodorakits/:slug/items/:itemSlug/edit',
    name: 'SoodoraKitItemEdit',
    component: SoodoraKitItemFormView,
  },
  {
    path: '/soodorakits/:slug',
    name: 'SoodoraKitEdit',
    component: SoodoraKitEditView,
  },
  {
    path: '/playlists',
    name: 'PlaylistView',
    component: PlaylistMeView,
  },
  {
    path: '/playlists/create',
    name: 'PlaylistCreateView',
    component: PlaylistFormView,
  },
  {
    path: '/playlists/:slug/edit',
    name: 'PlaylistEditView',
    component: PlaylistFormView,
  },
  {
    path: '/playlists/:slug',
    name: 'PlaylistDetailsView',
    component: PlaylistDetailsView,
  },
  {
    path: '/playlists/me',
    redirect: '/playlists',
  },
  {
    path: '/quotes/create',
    name: 'QuoteCreateView',
    component: QuoteCreateView,
  },
  {
    path: '/quotes/:slug/edit',
    name: 'QuoteEditView',
    component: QuoteCreateView,
  },
  {
    path: '/quotes/:slug',
    name: 'QuoteDetailsView',
    component: QuoteDetailsView,
  },
  {
    path: '/quotes/:quoteSlug/items/new',
    name: 'QuoteItemCreateView',
    component: QuoteItemFormView,
  },
  {
    path: '/quotes/:quoteSlug/items/:itemSlug/edit',
    name: 'QuoteItemEditView',
    component: QuoteItemFormView,
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
