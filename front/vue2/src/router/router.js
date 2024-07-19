import { createRouter, createWebHistory } from 'vue-router';
import HelloWorld from '@/components/HelloWorld.vue'
import Layout from "@/views/Layout.vue";
import { ref } from 'vue'
const routes = [
    {
        path:'/login',
        component: ()=>import('@/views/login.vue')
    },

    {
        path: '/',
        component: Layout,
        redirect: '/HelloWorld',
        children: [  // 子路由
            {
                path: '/HelloWorld',
                component: HelloWorld
            },

            // 视频文件管理
            {
                path: 'fileList',
                component: () => import('@/views/file/fileList.vue'),
            },
            //视频详情
            {
                path: 'detailsList',
                component: () => import('@/views/details/detailsList.vue'),
            },

             //新闻资讯详情
             {
                path: 'NewsList',
                component: () => import('@/views/News/NewsList.vue'),
            },


            //admin
            {
                path: 'adminList',
                component: ()=>import('@/views/admin/adminList.vue')
            },

            {
                path: 'crawlerList',
                component: ()=>import('@/views/crawler/crawlerList.vue')
            },
            {
                path: 'personal',
                component: ()=>import('@/views/personal.vue')
            }

        ]
    }

];

const router = createRouter({
    history: createWebHistory(),
    routes
});

const token=ref(null);
router.beforeEach((to, from, next) => {
    if (to.path === '/login') {
        next()
     } else {
     token.value = localStorage.getItem('token')
    if (token.value === null) {
     next('/login')
     } else {
     next()
     }
}
})
export default router;
