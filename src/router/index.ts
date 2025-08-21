import { createRouter, createWebHistory } from 'vue-router';
import { RouteRecordRaw } from 'vue-router';
import AdministrationView from '@/view/AdministrationView.vue';
import NotFoundView from '@/view/NotFoundView.vue';
import MaintenanceView from '@/view/MaintenanceView.vue';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/administration',
  },
  {
    path: '/administration',
    name: 'Administration',
    component: AdministrationView,
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
