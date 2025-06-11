<template>
    <div class="header">
        <div class="left-content">
            <el-button size="small" @click="changeCollapse">
                <component class="icons" is="menu"></component>
            </el-button>
            <el-breadcrumb separator-class="el-icon-arrow-right">
                <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                <el-breadcrumb-item v-if="current" :to="current.path">{{ current.label }}</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="right-content">
            <el-dropdown>
                <span class="el-dropdown-link">
                    <img :src="getImageUrl('user')" class="user" />
                </span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item @click="handleShow">修改密码</el-dropdown-item>
                        <el-dropdown-item @click="handleLoginOut">退出</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
             </el-dropdown>
        </div>

    </div>
    <el-dialog 
        v-model="showvisable"
        :title="action ='修改密码'"
        width="35%"
        :before-close="handleClose">
        <el-form  :inline="true" :model="pwdform" :rules="rule" ref="passwordform">
            <el-row style="justify-content: center;width: 100%;">
               
                 <el-form-item label="工号" prop="username">
                     <el-input v-model="pwdform.username" placeholder="输入工号" style="width: 100%;"/>
                 </el-form-item>

            </el-row>
            <el-row style="justify-content: center;width: 100%;">
                
                    <el-form-item label="输入密码" prop="password">
                        <el-input type="password" v-model="pwdform.password" placeholder="输入密码"/>
                    </el-form-item>
            </el-row >
            <el-row style="justify-content: center;width: 100%;"> 
                    <el-form-item label="确认密码" prop="Apassword">
                        <el-input tyep='password' v-model="pwdform.Apassword" placeholder="确认密码"/>
                    </el-form-item>

            </el-row>
            <el-row style="justify-content: flex-end">
                <el-form-item>
                    <el-button type="primary" @click="handleClose">取消</el-button>
                    <el-button type="primary" @click="onSubmit">确定</el-button>
                </el-form-item>
            </el-row>
        </el-form>
    </el-dialog>
</template>

<script setup>
import{ref,reactive,computed,getCurrentInstance} from "vue"
import {useAllDataStore} from"@/stores"
import {useRouter} from "vue-router"
import { ElMessage } from "element-plus";
const getImageUrl = (user) => {
    return new URL(`../assets/images/${user}.png`, import.meta.url).href;
};
//密码表单
const pwdform=reactive({})
//退出_fun
const router=useRouter()
console.log(router.getRoutes());
const store=useAllDataStore();
// 验证确认密码
const validatePassword = (rule, value, callback) => {
    if (value !== pwdform.password) {
        callback(new Error('两次输入的密码不一致'));
    } else {
        callback();
    }
};
//确认登录用户
const validateUser=(rule,value,callback)=>{
    if(value!==store.state.username){
        callback(new Error("请输入正确用户名"))
    }else{
        callback();
    }
}
const rule = ref({
    username: [
        { required: true, message: '请输入工号', trigger: 'blur' },
        {validator:validateUser,trigger:'blur'}
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
    ],
    Apassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        { validator: validatePassword, trigger: 'blur' }
    ]
});
const {proxy}=getCurrentInstance()

//更换状态_btn
const changeCollapse=()=>{
    store.state.isCollapse=!store.state.isCollapse
}

const showvisable=ref(false)
const handleLoginOut=()=>{
    console.log("退出")
    console.log(router.getRoutes());
    router.push({name:"denglu"})
    store.clean()

}

const handleShow=()=>{
    showvisable.value=true;    
}
const handleClose=()=>{
    proxy.$refs['passwordform'].resetFields();
    showvisable.value=false;
}

//确认——fun
const onSubmit=()=>{
    proxy.$refs['passwordform'].validate(
        async(vaild)=>{
            if(vaild){
                let res=null;
                pwdform.role=store.state.role
                res=await proxy.$api.changePassword(pwdform)
                console.log("更改密码："+res)
                if(res){
                    showvisable.value=false;
                    proxy.$refs['passwordform'].resetFields();
                }
            }
        }
    )
}
const current=computed(()=>store.state.currentMenu)
</script>

<style lang="less" scoped>
    .header{
        display: flex;
        justify-content: space-between;
        align-items: center;
        width: 100%;
        height: 100%;
        background-color: #333;
    }
    .icons{
        width: 20px;
        height: 20px;
    }
    .right-content{
        .user{
            width: 40px;
            height: 40px;
            border-radius: 50%;
        }
    }
    .left-content{
        display: flex;
        align-items: center;
        .el-button{
            margin-right: 20px;
        }
    }

    :deep(.el-breadcrumb__item) {
    .el-breadcrumb__inner {
        color: #fff !important; /* 设置面包屑字体颜色为白色 */
        cursor: pointer !important; /* 设置鼠标样式为指针 */
    }
}
</style>

