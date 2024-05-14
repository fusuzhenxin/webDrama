import { createRouter, createWebHistory } from 'vue-router';
import HelloWorld from '@/components/HelloWorld.vue'
import Layout from "@/views/Layout.vue";
const routes = [

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


            //admin
            {
                path: 'adminList',
                component: ()=>import('@/views/admin/adminList.vue')
            },

            {
                path: 'crawlerList',
                component: ()=>import('@/views/crawler/crawlerList.vue')
            },

        ]
    }

];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;
