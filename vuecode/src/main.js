import { createApp } from 'vue';
import App from './App.vue';

// 导入全局样式
import "@/assets/less/index.less";
import 'element-plus/dist/index.css';

// 导入路由
import router from './router';

// 导入Element Plus组件库
import ElementPlus from 'element-plus';
import "element-plus/dist/index.css";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
//引入api
import api from "@/api/api";

//引入pinia
import { createPinia } from "pinia";
import { useAllDataStore } from "@/stores/";

// 创建Vue应用实例
const app = createApp(App);
const pinia = createPinia();

// 使用Pinia
app.use(pinia);

const store=useAllDataStore();
store.addMenu(router,"refresh");
// 使用Element Plus组件库
app.use(ElementPlus);

// 使用路由
app.use(router);



// 注册api
app.config.globalProperties.$api = api;

// 动态注册所有图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

// 在应用挂载后调用 useAllDataStore
app.mount("#app");

