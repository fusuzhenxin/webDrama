import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import router from "@/router/router"
import 'element-plus/dist/index.css'
import App from './App.vue'

const app = createApp(App)

// 全局注册 Swipe 和 SwipeItem 组件
app.use(router)
app.use(ElementPlus)
app.mount('#app')


