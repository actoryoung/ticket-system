import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import Layout from '@/components/Layout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'tickets',
        name: 'TicketList',
        component: () => import('@/views/tickets/TicketListView.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'tickets/create',
        name: 'CreateTicket',
        component: () => import('@/views/tickets/CreateTicketView.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'tickets/:id',
        name: 'TicketDetail',
        component: () => import('@/views/tickets/TicketDetailView.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/dashboard/StatisticsView.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else if (to.meta.requiresAdmin && userStore.user?.role !== 'admin') {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
