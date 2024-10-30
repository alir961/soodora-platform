import {createRouter, createWebHistory} from '@ionic/vue-router';
import {RouteRecordRaw} from 'vue-router';
import AuthView from '@/view/user/AuthenticationView.vue';
import HomeView from '@/view/HomeView.vue';
import AccountView from '@/view/user/AccountView.vue';
import MarketPlaceView from '@/view/marketplace/MarketplaceView.vue';
import PlaylistView from '@/view/playlist/PlaylistView.vue';
import PlaylistDetailsView from '@/view/playlist/PlaylistDetailsView.vue';
import NotFoundView from '@/view/NotFoundView.vue';
import MaintenanceView from "@/view/MaintenanceView.vue";

const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        redirect: '/home'
    },
    {
        path: '/home',
        name: 'Home',
        component: HomeView
    },
    {
        path: '/auth',
        name: 'Authentication',
        component: AuthView
    },
    {
        path: '/account',
        name: 'Account',
        component: AccountView
    },
    {
        path: '/marketplace',
        name: 'Marketplace',
        component: MarketPlaceView
    },
    {
        path: '/playlist',
        name: 'Playlist',
        component: PlaylistView
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
        component: NotFoundView
    }
];

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
});

export default router;