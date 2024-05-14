// import { createApp } from 'vue'
// import App from './App.vue'
// import router from "@/router/router";
// import 'element-plus/dist/index.css'
// import ElementPlus from 'element-plus'; // 导入 Element Plus
//
// app.use(ElementPlus);
// const app = createApp(App);
// createApp(App).use(router).mount('#app', {
//     compilerOptions: {
//         isCustomElement: tag => tag.startsWith('el-')
//     }
// });
//
//
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import router from "@/router/router";
import 'element-plus/dist/index.css'
import App from './App.vue'
import 'video.js/dist/video-js.css';
import 'videojs-contrib-hls';


const app = createApp(App)

app.use(ElementPlus)
app.use(router)
app.mount('#app')
