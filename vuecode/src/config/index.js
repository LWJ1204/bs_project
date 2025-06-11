// 当前的环境
const env = import.meta.env.MODE || 'prod'

const EnvConfig = {
  development: {
    baseApi: 'http://10.4.206.129:8080/api/',
    mockApi: 'https://mock.apifox.cn/m1/4068509-0-default/api',
  },
  prod: {
    baseApi: 'http://10.4.206.129:8080/api',
    mockApi: 'https://mock.apifox.cn/m1/4068509-0-default/api',
  },
}

export default {
  env,
  mock:false,
  ...EnvConfig[env]
}