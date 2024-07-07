import axios from "axios";



const request = axios.create({
    baseURL: 'http://localhost:9090/',
})

request.interceptors.request.use(
    (config)=>{
        const token=localStorage.getItem('token');
        if(token){
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error)=>{
        return Promise.reject(error)
    }
)

export default request
