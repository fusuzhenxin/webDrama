import { createRouter, createWebHistory } from 'vue-router';
import HelloWorld from '@/components/HelloWorld.vue'
import Layout from "@/views/Layout.vue";
// import { ref } from 'vue'
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
            },
            {
                path: 'user',
                component: ()=>import('@/views/User.vue')
            },
            {
                path: 'role',
                component: ()=>import('@/views/role.vue')
            },
            {
                path: '/404',
                component: ()=>import('@/views/404.vue')
            },
            {
                path: '/password',
                component: ()=>import('@/views/Password.vue')
            }


        ]
    }

];

const router = createRouter({
    history: createWebHistory(),
    routes
});


// router.beforeEach((to, from, next) => {
//     const userRole=localStorage.getItem("userType")
//     const menu=localStorage.getItem("menu")
//     const paths = menu.map(item => item.path);
//     console.log('userType',userRole);
    

//     // if (to.path === '/login') {
//     //     next()
//     //  } else {
//     //  token.value = localStorage.getItem('token')
//     // if (token.value === null) {
//     //  next('/login')
//     //  } else {
//     //  next()
//     //  }

//   if (!userRole && to.path !== '/login') {
//     next('/login'); // 未登录用户重定向到登录页面
//   } else if (to.meta.roles && !to.meta.roles.includes(userRole)) {
//     next('/unauthorized'); // 未授权用户重定向
//   } else {
//     next(); // 放行
//   }
// })
// const token=ref(null)

function getAllPaths(menu) {
    let paths = [];
    menu.forEach(item => {
        if (item.path) {
            paths.push(item.path);
        }
        if (item.children) {
            paths = paths.concat(getAllPaths(item.children));
        }
    });
    return paths;
}

router.beforeEach((to, from, next) => {
    const userRole = localStorage.getItem("userType");
    const menu = JSON.parse(localStorage.getItem("menus")) || []; // Ensure menu is parsed correctly
    const paths=getAllPaths(menu)

    console.log('userType', userRole);
    console.log('paths', menu);

    // Check if the target route is in the allowed paths
    if (paths.includes(to.path) || to.path=='/login' || to.path == '/HelloWorld' || to.path == '/personal'|| to.path=='/password') {
        next(); // Allow navigation
    } else {
        next(false); // Prevent navigation, do not redirect
    }
});



export default router;
