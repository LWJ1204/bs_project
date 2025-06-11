<template>
    <el-aside :width="width">
        <el-menu
            background-color="#545c64"
            text-color="#fff"
            :collapse="store.state.isCollapse"
            :collapse-transition="false"
            :default-active="activeMenu"
        >
            <h3 v-show="!store.state.isCollapse">考勤管理系统</h3>
            <h3 v-show="store.state.isCollapse">考勤</h3>
            <el-menu-item 
                v-for="item in noChildren"
                :index="item.path"
                :key="item.path"
                :icon="item.icon"
                @click="handleMenu(item)"
            >
                <component class="icons" :is="item.icon"></component>
                <span>{{ item.label }}</span>
                </el-menu-item>

            <el-sub-menu 
                v-for="item in hasChildren"
                :index="item.path"
                :key="item.path"
            >
                <template #title>
                    <component class="icons" :is="item.icon"></component>
                    <span>{{ item.label }}</span>
                </template>
                <el-menu-item-group>
                    <el-menu-item 
                        v-for="subItem in item.children"
                        :index="subItem.path"
                        :key="subItem.path"
                        @click="handleMenu(subItem)"
                    >
                        <span>{{ subItem.label }}</span>
                    </el-menu-item>
                </el-menu-item-group>
            </el-sub-menu>
        </el-menu>
    </el-aside>
</template>

<script setup>

    import { ref, reactive, computed, toRef } from 'vue'
    import {useAllDataStore}from '@/stores'
    import { useRoute,useRouter } from 'vue-router'
    // 左侧列表内容
    // const list = reactive([
    //     //首页信息
    //     {
    //         path:"/home",
    //         label:"首页",
    //         name:'shouye',
    //         icon:"house",
    //         url:"/home"
    //     },
    //     //班级信息
    //     {
    //         path:"/classes",
    //         label:"班级管理",
    //         name:'banjiguanli',
    //         icon:"DataBoard",
    //         url:"/classes"
    //     },
    //     //教师信息
    //     {
    //         path:"/teachers",
    //         label:"教师管理",
    //         name:'jiaoshiguanli',
    //         icon:"User",
    //         url:"/teachers"
    //     },
    //     //学生管理
    //     {
    //         path:"/students",
    //         label:"学生管理",
    //         icon:"User",
    //         children:[
    //             {
    //                 path:"/students/studentsInfo",
    //                 name:'xueshengxinxi',
    //                 label:'学生信息',
    //                 url:'/studentsinfo'
    //             },
    //             {
    //                 path:'/students/mystudetns',
    //                 name:'wodexuesheng',
    //                 label:'授课学生',
    //                 url:'/mystudents'
    //             }
    //         ]
    //     },
      
    //     //课程管理
    //     {
    //         path:"/courses",
    //         label:"课程管理",
    //         icon:"Collection",
    //         children:[
    //             {
    //                 path:"/courses/courseInfo",
    //                 label:"课程信息",
    //                 name:'kechengxinxi',
    //                 url:"/coursesInfo",
    //             },
    //             {
    //                 path:"/courses/courseSelected",
    //                 label:"选课管理",
    //                 name:'xuankeguanli',
    //                 url:"/courses_selected_page"
    //             }
    //         ]
    //     },

    //     // 考勤记录
    //     {
    //         path: "/records",
    //         label: "考勤管理",
    //         icon:"Location",
    //         children: [
    //             {
    //                 path: "/records/recordManage",
    //                 label: "考勤记录管理",
    //                 name:'kaoqinjiluguanli',
    //                 url: "/record_manage_page",
    //             },
    //             {
    //                 path: "/records/recordSet",
    //                 label: "考勤设置",
    //                 name:'kaoqinshezhi',
    //                 url: "/record_setting_page"
    //             },
    //             {
    //                 path: "/record_analysis",
    //                 label: "考勤记录分析",
    //                 url: "/record_analysis_page"
    //             },
    //         ],
    //     },
    // ])
    const list=computed(()=>store.state.menuList)
    const noChildren = computed(() => list.value.filter(item => !item.children))
    const hasChildren = computed(() => list.value.filter(item => item.children))
    
    const store=useAllDataStore()
    const width=computed(()=>store.state.isCollapse?"64px":"180px")



    const router=useRouter()
    const route=useRoute()
    const activeMenu=computed(()=>route.path)
    const handleMenu=(item)=>{
        router.push({
            name:item.name
        })
        store.selectMenu(item)
    }
    
</script>

<style lang="less" scoped>
    .icons{
        width: 18px;
        height: 18px;
        margin-right: 5px;
    }

    .el-menu{
        border-right: none;
        h3{
            line-height: 48px;
            color: #fff;
            text-align: center;
        }
        span{
            text-align: center;
        }
    }
    .el-aside{
        height: 100%;
        background-color: #545c64;
    }

</style>