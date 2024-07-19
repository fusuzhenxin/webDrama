import { createRouter, createWebHistory } from 'vue-router';
import HelloWorld from '@/components/HelloWorld.vue'
import Layout from "@/views/Layout.vue";
const routes = [
    {
        path: '/videoDetails',
        name: 'videoDetail',
        component: ()=> import("../views/VideoDetail.vue"),
    },
    //娱乐新闻列表
    {
        path: '/Entertainment',
        name: 'Entertainment',
        component: ()=> import("../views/Entertainment.vue"),
    },
    //新闻详情
    {
        path: '/NewsDetails',
        name: 'NewsDetails',
        component: ()=> import("../views/NewsDetails.vue"),
    },
    {
        path: '/VideoStory',
        name: 'VideoStory',
        component: ()=> import("../views/VideoStory.vue")

    },
    //个人中心
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
        path: '/login',
        component: ()=>import ("../views/login.vue")
    },
    {
        path: '/sidebar',
        component: ()=>import("../views/Sidebar.vue")
    },
    {
        path: '/VideoUpload',
        component: ()=>import("../views/VideoUpload.vue")
    },
    {
        path: '/profile',
        component: ()=>import("../views/profile.vue")
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
