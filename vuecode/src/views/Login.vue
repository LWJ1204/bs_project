<template>
    <div class="body-login">
       <el-form :model="loginform" ref="rloginform" :rules="rule" class="el-form el-form--default el-form--label-right login-container">
            <h1>欢迎登录</h1>
            <el-form-item prop="username">
                <el-input type="input"  v-model="loginform.username" placeholder="请输入用户名"></el-input>
            </el-form-item>
            <el-form-item prop="password">
                <el-input type="password" prop="password" v-model="loginform.password" placeholder="请输入密码"></el-input>
            </el-form-item>
            <!-- 验证码展示和输入框 -->
            <el-form-item prop="captcha">
                <el-input v-model="loginform.captcha" placeholder="请输入验证码">
                    <template v-slot:append>
                        <img :src="captcha" alt="验证码" @click="Getcapcha" style="cursor:pointer; height: 38px;">
                    </template>
                </el-input>
            </el-form-item>
            <el-form-item prop ='role'>
                <el-radio-group v-model="loginform.role">
                    <el-radio value="1" size="small">管理员</el-radio>
                    <el-radio value="2" size="small">教师</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item class="button-container">
                <el-button @click="handleLogin" type="primary" style="display: flex;width:80%;">登录</el-button>
            </el-form-item>

            <div >
                未有账号？<a @click="tRegister" class="register-link">点击注册</a>
            </div>
       </el-form>
    </div>
</template>

<script setup>
import {ref,getCurrentInstance,reactive, onMounted,watch, nextTick} from "vue"
const {proxy}=getCurrentInstance()
import { ElMessage,ElMessageBox } from "element-plus";
import {useRouter} from 'vue-router'
import {useAllDataStore} from "@/stores"
//设备指纹_fun
const generateFingerprint=()=>{
    const ua = navigator.userAgent;
    const resolution = `${window.screen.width}x${window.screen.height}`;
    const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
    const plugins = Array.from(navigator.plugins).map(p => p.name).join(',');
    const fingerprint = btoa(ua + resolution + timezone + plugins);
    return fingerprint;
}

// 登录表单
const loginform = reactive({
    username: '',
    password: '',
    role: '', // 初始化角色为空,
    fingerprint:generateFingerprint(),
})
const rule =reactive({
    'username':[{required:true,message:"请输入工号",trigger:'blur'}],
    'password':[{required:true,message:"请输入密码",trigger:'blur'}],
    'role':[{required:true,message:"请选择身份",trigger:'blur'}],
    "captcha":[{required:true,message:"请输入验证码",trigger:blur}]
})
const store=useAllDataStore()
const captcha=ref("0");
const keyvalue=ref("0")
const router=useRouter()
//切换注册界面——fun
const tRegister=()=>{
    console.log("点击注册");
    router.push({
        name:'zhuce'
    })
}


console.log('fun',store.state.token)
//登录_fun
const handleLogin= ()=>{
    proxy.$refs['rloginform'].validate(
        async(valid)=>{
            if(valid){
                
                console.log(loginform)
                let res=null;
                loginform.captchakey=keyvalue.value;
                loginform.role=parseInt(loginform.role)
                res=await proxy.$api.login(loginform)
                console.log("获得的数据",res)
                console.log("logintoken",store.state.token)
                if(res){
                    console.log("login",res.state);
                    if(res.state=="成功登录"){
                        store.updateMenuList(res.menuList)
                        store.state.token=res.token
                        store.state.username=loginform.username
                        store.state.role=loginform.role
                        store.state.uid=res.id;
                        store.addMenu(router)
                        router.push({name:'shouye'})
                    }

                    ElMessage({
                        showClose:true,
                        message:res.state,
                        type:"info",
                    })
                }
            }else{
                ElMessage({
                showClose:true,
                message:"请输入正确的内容",
                type:"error",
          })}
        }
    );
}


const Getcapcha=async()=>{
    const data=await proxy.$api.getcaptcha();
    if(data){
        captcha.value=data.image
        keyvalue.value=data.key;
    }
}
onMounted(()=>{
    Getcapcha();
})
</script>

<style scoped lang="less">
.body-login {
    width: 100%;
    height: 100%;
    background-size: 100%;
    overflow: hidden;
    display: flex;
    justify-content: center;
    align-items: center;
    text-align: center;
    background-image: url("/src/assets/images/login.jpg");
    background-color: #f0f2f5; /* 添加背景颜色 */
}

.login-container {
    width: 400px;
    background-color: #fff;
    border: 1px solid #ccc; /* 修复边框颜色 */
    border-radius: 15px;
    padding: 30px 35px 15px 35px;
    box-shadow: 0 0 25px #cacaca;
    margin: 0; /* 移除外边距，由父容器控制居中 */
    text-align: center;
    h1 {
        text-align: center;
        margin-bottom: 20px;
        color: #505450;
    }
        /* 针对按钮的样式调整 */
    .button-container {
        margin-top: 5px; /* 设置按钮上边距 */
        :deep(.el-form-item__content){
            width: 100%;
            justify-content: center;
        }

    }

}



.register-link {
    color: blue; /* 设置链接颜色为蓝色 */
    text-decoration: none; /* 去掉下划线 */
    cursor: pointer; /* 鼠标悬停时显示手型 */
}

.register-link:hover {
    text-decoration: underline; /* 鼠标悬停时显示下划线 */
}
</style>