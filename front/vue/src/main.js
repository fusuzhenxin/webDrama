import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import router from "@/router/router"
import 'element-plus/dist/index.css'
import App from './App.vue'
import 'vant/lib/index.css'; // 引入 vant 样式文件
import { Swipe, SwipeItem } from 'vant'; // 引入 vant 中的 Swipe 和 SwipeItem 组件

const app = createApp(App)

// 全局注册 Swipe 和 SwipeItem 组件
app.use(router)
app.use(Swipe);
app.use(SwipeItem);
app.use(ElementPlus)
app.mount('#app')


