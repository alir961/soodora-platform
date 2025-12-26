import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';
import AuthView from '@/view/user/AuthenticationView.vue';
import HomeView from '@/view/explore/ExploreView.vue';
import ProfileView from '@/view/user/ProfileView.vue';
import UserProfileView from '@/view/user/UserProfileView.vue';
import MarketPlaceView from '@/view/marketplace/MarketplaceView.vue';
import NotFoundView from '@/view/NotFoundView.vue';
import MaintenanceView from '@/view/MaintenanceView.vue';
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
import AnalyticsView from '@/view/user/AnalyticsView.vue';
import ArtistEventView from '@/view/event/ArtistEventView.vue';
import CreateEventView from '@/view/event/CreateEventView.vue';
import { nextTick } from 'vue';

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
    path: '/explore/post/create',
    name: 'Create Post',
    component: PostFormView,
  },
  {
    path: '/linkhub/create',
    name: 'Create Link Hub',
    component: LinkHubCreateView,
  },
  {
    path: '/linkhub/edit/:slug',
    name: 'Edit Link Hub',
    component: LinkHubEditView,
  },
  {
    path: '/linkhub/:slug',
    name: 'Link Hub Details',
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
    name: 'My Profile',
    component: ProfileView,
  },
  {
    path: '/user/:slug',
    name: 'User Profile',
    component: UserProfileView,
  },
  {
    path: '/user/:slug/events',
    name: 'Artist Events',
    component: ArtistEventView,
  },
  {
    path: '/user/:slug/events/create',
    name: 'Create Event',
    component: CreateEventView,
  },
  {
    path: '/marketplace',
    name: 'Marketplace',
    component: MarketPlaceView,
  },
  {
    path: '/marketplace/payment/success',
    name: 'Payment Success',
    component: PaymentResultView,
    props: {
      title: 'Your payment has been validated, you will soon receive a confirmation of your order.',
    },
  },
  {
    path: '/marketplace/payment/cancel',
    name: 'Payment Cancel',
    component: PaymentResultView,
    props: {
      title: 'Your payment has been cancelled, you will not be charged.',
    },
  },
  {
    path: '/marketplace/sales',
    name: 'My Sales',
    component: MySaleView,
  },
  {
    path: '/marketplace/product/create',
    name: 'Create Product',
    component: ProductFormView,
  },
  {
    path: '/marketplace/product/:slug/edit',
    name: 'Edit Product',
    component: ProductFormView,
  },
  {
    path: '/soodorakits/create',
    name: 'Create Soodora Kit',
    component: SoodoraKitFormView,
  },
  {
    path: '/soodorakits/:slug',
    name: 'Edit Soodora Kit',
    component: SoodoraKitEditView,
  },
  {
    path: '/soodorakits/:slug/items/new',
    name: 'Create Soodora Kit Item',
    component: SoodoraKitItemFormView,
  },
  {
    path: '/soodorakits/:slug/items/:itemSlug/edit',
    name: 'Edit Soodora Kit Item',
    component: SoodoraKitItemFormView,
  },
  {
    path: '/playlists',
    name: 'My Playlists',
    component: PlaylistMeView,
  },
  {
    path: '/playlists/create',
    name: 'Create Playlist',
    component: PlaylistFormView,
  },
  {
    path: '/playlists/:slug/edit',
    name: 'Edit Playlist',
    component: PlaylistFormView,
  },
  {
    path: '/playlists/:slug',
    name: 'Playlist Details',
    component: PlaylistDetailsView,
  },
  {
    path: '/quotes/create',
    name: 'Create Quote',
    component: QuoteCreateView,
  },
  {
    path: '/quotes/:slug/edit',
    name: 'Edit Quote',
    component: QuoteCreateView,
  },
  {
    path: '/quotes/:slug',
    name: 'Quote Details',
    component: QuoteDetailsView,
  },
  {
    path: '/quotes/:quoteSlug/items/new',
    name: 'Create Quote Item',
    component: QuoteItemFormView,
  },
  {
    path: '/quotes/:quoteSlug/items/:itemSlug/edit',
    name: 'Edit Quote Item',
    component: QuoteItemFormView,
  },
  {
    path: '/quotes/me',
    name: 'My Quotes',
    component: QuoteView,
  },
  {
    path: '/analytics',
    name: 'Analytics',
    component: AnalyticsView,
  },
  {
    path: '/maintenance',
    name: 'Maintenance',
    component: MaintenanceView,
  },
  {
    path: '/:catchAll(.*)',
    name: 'Not Found',
    component: NotFoundView,
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

router.afterEach((to) => {
  return nextTick(() => {
    const title: string | symbol = to.name || 'Soodora';

    document.title = title.toString();
  });
});

export default router;
