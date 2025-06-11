import axios  from "axios";
import { ElMessage } from "element-plus";
import config from "@/config";
import {useAllDataStore} from "@/stores"
const service=axios.create({
    baseURL:config.baseApi
})
const NetError="返回值不是200"
// 添加请求拦截器
service.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    const store=useAllDataStore()
    const token=store.state.token;
    console.log("testcoken",token)

    config.headers["Access-token"] = token;
    console.log("test config",config.value)
    return config;
  }, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
  });

// 添加响应拦截器
service.interceptors.response.use(
    (res)=>{
        const {code,data,msg}=res.data
        console.log("code",res)
        if(code===200){
            return data
        }else{
            ElMessage.error(msg||NetError)
            return Promise.reject(msg||NetError)
        }
    }
);


function request(options){
    options.method=options.method||'get';
    //关于get请求参数
    if(options.method.toLowerCase()==='get'){
        options.params=options.data;
    }
    // 10.4.153.135:8080
    //192.168.229.240
    // 49.232.26.127
    service.defaults.baseURL="http://192.168.173.240:8080/api/"
    console.log("request",options);
    return service(options);
}

export default request;