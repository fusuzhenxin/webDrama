import axios from "axios";



const request = axios.create({
    baseURL: 'http://localhost:9090/apiOne/',
})


export default request
