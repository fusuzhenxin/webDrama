import { createRouter, createWebHistory } from 'vue-router';
import HelloWorld from '@/components/HelloWorld.vue'
import Layout from "@/views/Layout.vue";
const routes = [
    {
        path: '/VideoDetail',
        name: 'videoDetail',
        component: ()=> import("../views/VideoDetail.vue")

    },
    {
        path: '/Counterattack',
        name: 'counterattack',
        component: ()=> import("../views/counterattack.vue")

    },
    {
        path: '/sweet',
        name: 'sweet',
        component: ()=>import("../views/sweet.vue")
    },
    {
        path:'/traversing',
        name: 'traversing',
        component: ()=>import("../views/traversing.vue")
    },
    {
        path:'/rebirth',
        name: 'rebirth',
        component: ()=>import("../views/rebirth.vue")
    },
    {
        path:'/search',
        name: 'search',
        component: ()=>import("../views/search.vue")
    },
    {
        path:'/korean',
        name: 'korean',
        component: ()=>import("../views/korean.vue")
    },


    {
        path: '/',
        component: Layout,
        redirect: '/HelloWorld',
        children: [  // 子路由
            {
                path: '/HelloWorld',
                component: HelloWorld
            }
        ]
    }

];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;
