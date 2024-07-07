
import axios from "axios";



//创建一个axios实例
const request = axios.create({
    baseURL: 'http://localhost:9090/api/',
    
})

//添加请求拦截器
request.interceptors.request.use(
    (config) =>{
        //从localStorage获取JWT token
        const token=localStorage.getItem('token');
        console.log("--==",token);
        if(token){
            config.headers.Authorization = `Bearer ${token}`;
            console.log("--==",config);
        }
        return config;
    },
    (error)=>{
        return Promise.reject(error)
    }
);



export default request
