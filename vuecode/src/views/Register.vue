<template>
    <div class="body-login">
       <el-form :model="registerform" ref="Registerform" :rules="rule" class="el-form el-form--default el-form--label-right login-container">
            <h1>欢迎注册</h1>
            <el-form-item prop="username">
                <el-input type="input" v-model="registerform.username" placeholder="请输入用户名"></el-input>
            </el-form-item>
            <el-form-item prop="password">
                <el-input type="password" v-model="registerform.password" placeholder="请输入密码"></el-input>
            </el-form-item>
            <el-form-item prop="password_">
                <el-input type="password" v-model="registerform.password_" placeholder="请确认密码"></el-input>
            </el-form-item>
            <el-form-item prop="role">
                <el-radio-group v-model="registerform.role">
                    <el-radio value="1" size="small">管理员</el-radio>
                    <el-radio value="2" size="small">教师</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item class="button-container">
                <el-button @click="handleRegister" type="primary" style="display: flex;width:80%;">注册</el-button>
            </el-form-item>

       </el-form>
    </div>
</template>

<script setup>
import {ref,getCurrentInstance,reactive, onMounted,watch, nextTick} from "vue"
const {proxy}=getCurrentInstance()
import { ElMessage,ElMessageBox } from "element-plus";
import {useRouter} from 'vue-router'
const router=useRouter();
// 登录表单
const registerform = reactive({
    username: '',
    password: '',
    password_:"",
    role: "" // 初始化角色为空
})
const rule =reactive({
    'username':[{required:true,message:"请输入工号",trigger:'blur'}],
    'password':[{required:true,message:"请输入密码",trigger:'blur'}],
    'password_': [
        { required: true, message: "请确认密码", trigger: "blur" },
        {
            validator: (rule, value, callback) => {
                if (value !== registerform.password) {
                    callback(new Error("两次密码不一致"));
                } else {
                    callback();
                }
            },
            trigger: "blur"
        }
    ],
    'role':[{required:true,message:"请选择身份",trigger:'blur'}]
})
//注册_fun
const handleRegister= ()=>{
    proxy.$refs['Registerform'].validate(
        async(valid)=>{
            if(valid){
                console.log(registerform)
                let res=null;
                registerform.role=parseInt(registerform.role)
                res=await proxy.$api.registerfunction(registerform)
                console.log("获得的数据",res)
                if(res){
                    console.log("login",res.state);
                    if(res=="注册成功"){
                        router.push({name:'denglu'})
                    }

                    ElMessage({
                        showClose:true,
                        message:res,
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