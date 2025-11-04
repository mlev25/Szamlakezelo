import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import LoginView from '@/views/LoginView.vue';
import RegisterView from '@/views/RegisterView.vue';
import DashboardView from '@/views/DashboardView.vue';
import AdminView from '@/views/AdminView.vue';
import ForbiddenView from '@/views/ForbiddenView.vue';
import InvoiceListView from '@/views/InvoiceListView.vue';
import InvoiceDetailView from '@/views/InvoiceDetailView.vue';
import InvoiceCreateView from '@/views/InvoiceCreateView.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login',
      meta: { public: true}
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { public: true }
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
      meta: { public: true }
    },

    //vedett utvonalak, eloszor csak teszteleshez
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardView,
      meta: { requiresAuth: true }
    },

    {
      path: '/invoices',
      name: 'invoiceList',
      component: InvoiceListView,
      meta: { requiresAuth: true }
    },

    {
      path: '/invoices/create',
      name: 'InvoiceCreate',
      component: InvoiceCreateView,
      meta: {
        requiresAuth: true,
        roles: ['BOOKKEEPER', 'ADMIN']
      }
    },

    {
      path: '/invoices/:id',
      name: 'invoiceDetails',
      component: InvoiceDetailView,
      meta: { requiresAuth: true }
    },

    {
      path: '/admin',
      name: 'admin',
      component: AdminView,
      meta: { requiresAuth: true, roles: ['ADMIN'] }
    },

    {
      path: '/forbidden',
      name: 'forbidden',
      component: ForbiddenView,
      meta: {public: true}
    }
  ],
});

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();
    const isAuth = authStore.isAuthenticated;
    const requiredRoles = to.meta.roles;

    if (to.meta.public) {
        if (isAuth && (to.path === '/login' || to.path === '/')) {
            next('/dashboard');
        } else {
            next();
        }
    } else if (to.meta.requiresAuth) {
        if (!isAuth) {
            next('/login');
        } else if (requiredRoles) {
            const hasRequiredRole = requiredRoles.some(roleName =>
                authStore.hasRole(roleName)
            );

            if (!hasRequiredRole) {
                next('/forbidden');
            } else {
                next();
            }
        } else {
            next();
        }
    } else {
        next();
    }
});

export default router
