
import axios from "axios";



//创建一个axios实例
const request = axios.create({
    baseURL: 'http://localhost:9090/api/',
    
})

// //添加请求拦截器
// request.interceptors.request.use(
//     (config) =>{
//         //从localStorage获取JWT token
//         const token=localStorage.getItem('token');
//         console.log("--==",token);
//         if(token){
//             config.headers.Authorization = `Bearer ${token}`;
//             console.log("--==",config);
//         }
//         return config;
//     },

//     (error)=>{
//         return Promise.reject(error)
//     }
// );

// 请求拦截器
request.interceptors.request.use(
    config => {
      // 在请求发送之前设置token
      const token = localStorage.getItem('token'); // 从localStorage获取token
      if (token) {
        config.headers['token'] = token; // 设置请求头
      }
      return config;
    },
    error => {
      // 处理请求错误
      return Promise.reject(error);
    }
  );
  



export default request
