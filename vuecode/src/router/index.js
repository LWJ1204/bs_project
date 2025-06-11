import { Chicken } from "@element-plus/icons-vue";
import { pageHeaderEmits } from "element-plus"
import component from "element-plus/es/components/tree-select/src/tree-select-option.mjs"
import { pa } from "element-plus/es/locales.mjs";
import {createRouter,createWebHashHistory} from "vue-router"

//制定路由规则

const routes=[
    {
        path:'/',
        name:"main",
        component:()=>import("@/views/Main.vue"),
        redirect:"/login",
        children:[
            {
                path:"home",
                name:"shouye",
                component:()=>import("@/views/Home.vue"),
            },
            {
                path:"classes",
                name:"banjiguanli",
                component:()=>import("@/views/Classes.vue")
            },
            {
                
                path:"teachers",
                name:"jiaoshiguanli",
                component:()=>import("@/views/Teachers.vue")
            },   
            {
                
                path:"/students",
                name:'xuesheng',
                redirect:"/students/studentsInfo",
                children:[
                    // {
                    //     path:'studentsInfo',
                    //     name:'xueshengxinxi',
                    //     label:"学生信息",
                    //     component:()=>import("@/views/Students.vue"),
                    // },
                    // {
                    //     path:"mystudents",
                    //     name:'wodexuesheng',
                    //     label:"授课学生",
                    //     component:()=>import("@/views/MyStudents.vue")
                    // }
                ]

            },  
            {
                path:"/courses",
                name:"kecheng",
                redirect:"/courses/courseInfo",
                children:[
                    // {
                    //     path:"courseInfo",
                    //     name:"kechengxinxi",
                    //     component:()=>import("@/views/CourseInfo.vue"),
                    // },
                    // {
                    //     path:"courseSelected",
                    //     name:"xuankeguanli",
                    //     component:()=>import("@/views/CourseSelected.vue"),
                    // },
                ]
            },
            {
                path:"/records",
                name:"records",
                redirect:"/records/recordManage",
                children:[
                    // {
                    //     path:"recordManage",
                    //     name:"kaoqinjiluguanli",
                    //     component:()=>import("@/views/recordManage.vue"),
                    // },
                    // {
                    //     path:"recordSet",
                    //     name:"kaoqinshezhi",
                    //     component:()=>import("@/views/recordSet.vue"),
                    // }

                ]
            },

        ]
    },
    {
        path:'/login',
        name:'denglu',
        url:'Login',
        component:()=>import("@/views/Login.vue")
    },
    {
        path:'/register',
        name:'zhuce',
        url:'Register',
        component:()=>import("@/views/Register.vue")
    },
];

const router=createRouter(
    {
        history:createWebHashHistory(),
        routes,
    }
);


export default router;