
import { defineStore } from "pinia";
import {ref,watch} from 'vue';
function initState(){
    return {
        isColllapse:false,
        tags:[
           { 
            path:'/home',
            name:'shouye',
            label:'首页',
            icon:'home'
         },
        ],
        currentMenu:null,
        token:null,
        role:null,
        uid:null,
        username:null,
        menuList:[],
        routerList:[],
    };
}

export const useAllDataStore=defineStore('allData',()=>{
    const state=ref(initState());

    watch(state,(newobj)=>{
        console.log("state发生了改变",newobj)
        if(!newobj.token) return;
        localStorage.setItem("store",JSON.stringify(newobj))
    },{deep:true});

    function selectMenu(val){
        if(val.name==='shouye'){
            state.value.currentMenu=null;
        }else{
            state.value.currentMenu=val;
            let index=state.value.tags.findIndex(item=>item.name===val.name)
            index===-1?state.value.tags.push(val):''
        }
    }
    function updateMenu(tag){
        let index=state.value.tags.findIndex(item=>item.name===tag.name)
        state.value.tags.splice(index,1);
    }
    function updateMenuList(val){
        state.value.menuList=val;
    }
    function addMenu(router,type){
        if(type==="refresh"){
            if(JSON.parse(localStorage.getItem('store')))
            {
                state.value=JSON.parse(localStorage.getItem('store'))
                //
                state.value.routerList=[]
            }else{
                return;
            }
        }
        const menu=state.value.menuList;
        const module=import.meta.glob("../views/**/*.vue");
        const routeArr=[]
        menu.forEach((item)=>{
            if(item.children){
                item.children.forEach(val=>{
                    let url=`../views/${val.url}.vue`;
                    val.component=module[url];
                    routeArr.push(...item.children)
                });
            }else{
                let url=`../views/${item.url}.vue`
                item.component=module[url];
                routeArr.push(item)
            }
        });
        let routers=router.getRoutes();
        routers.forEach(item=>{
            if(item.name=='main'||item.name=='denglu'||item.name==='zhuce'){
                return
            }else{
                router.removeRoute(item.name)
            }
        })
        routeArr.forEach(item=>{
            state.value.routerList.push(router.addRoute('main',item))
        })
    }

    function clean(){
        state.value.routerList.forEach((item)=>{
            if(item) item();
        });
        state.value=initState();
        //删除缓存
        localStorage.removeItem('store')

    }
    function getClassRoom(){
        const ClassRoomList=[
            {
                label:"博三",
                value:"B3",
                children:[
                    {
                        label:"A",
                        value:"B31",
                        children:[
                            {
                                label:"1层",
                                value:"B311",
                                children:[
                                    {
                                        label:"B3-A101",
                                        value:"B311101",
                                    },
                                    {
                                        label:"B3-A102",
                                        value:"B311102",
                                    },
                                ]
                            },
                            {
                                label:"2层",
                                value:"B312",
                                children:[
                                    {
                                        label:"B3-A201",
                                        value:"B312201",
                                    },
                                    {
                                        label:"B3-A202",
                                        value:"B312202",
                                    },
                                ]
                            },
                            {
                                label:"3层",
                                value:"B313",
                                children:[
                                    {
                                        label:"B3-A301",
                                        value:"B313301",
                                    },
                                    {
                                        label:"B3-A302",
                                        value:"B313302",
                                    },
                                ]
                            },
                        ]

                    },
                    {
                        label:"B",
                        value:"B32",
                        children:[
                            {
                                label:"1层",
                                value:"B321",
                                children:[
                                    {
                                        label:"B3-B101",
                                        value:"B321101",
                                    },
                                    {
                                        label:"B3-B102",
                                        value:"B321102",
                                    },
                                ]
                            },
                            {
                                label:"2层",
                                value:"B3",
                                children:[
                                    {
                                        label:"B3-B201",
                                        value:"B322201",
                                    },
                                ]
                            },
                            {
                                label:"3层",
                                value:"B323",
                                children:[
                                    {
                                        label:"B3-B301",
                                        value:"B323301",
                                    },
                                    {
                                        label:"B3-B302",
                                        value:"B323302",
                                    },
                                    {
                                        label:"B3-B303",
                                        value:"B323303",
                                    },
                                ]
                            },
                        ]
                    },
                    {
                        label:"C",
                        value:"B33",
                        children:[
                           {
                            label:"1层",
                            value:"B331",
                            children:[
                                {
                                    label:"B3-C101",
                                    value:"B331101",
                                },
                                {
                                    label:"B3-C102",
                                    value:"B331102",
                                },
                                {
                                    label:"B3-C103",
                                    value:"B331103",
                                },
                            ]
                           }
                        ]
                    }
                ]
            },
            {
                label:"博四",
                value:"B4",
                children:[
                    {
                        label:"A",
                        value:"B41",
                        children:[
                            {
                                label:"1层",
                                value:"B411",
                                children:[
                                    {
                                        label:"B4-A101",
                                        value:"B411101",
                                    },
                                    {
                                        label:"B4-A102",
                                        value:"B411102",
                                    },
                                ]
                            },
                            {
                                label:"2层",
                                value:"B412",
                                children:[
                                    {
                                        label:"B4-A201",
                                        value:"B412201",
                                    },
                                    {
                                        label:"B4-A202",
                                        value:"B412202",
                                    },
                                ]
                            },
                            {
                                label:"3层",
                                value:"B413",
                                children:[
                                    {
                                        label:"B4-A301",
                                        value:"B413301",
                                    },
                                ]
                            },
                        ]

                    },
                    {
                        label:"B",
                        value:"B42",
                        children:[
                            {
                                label:"1层",
                                value:"B421",
                                children:[
                                    {
                                        label:"B4-B101",
                                        value:"B421101",
                                    },
                                    {
                                        label:"B4-B102",
                                        value:"B421102",
                                    },
                                ]
                            },
                            {
                                label:"2层",
                                value:"B422",
                                children:[
                                    {
                                        label:"B4-B201",
                                        value:"B422201",
                                    },
                                ]
                            },
                            {
                                label:"3层",
                                value:"B424",
                                children:[
                                    {
                                        label:"B4-B301",
                                        value:"B423301",
                                    },
                                ]
                            },
                        ]
                    },
  
                ]
            },
        ]
        return ClassRoomList
    }
    return {
        state,
        selectMenu,
        updateMenu,
        updateMenuList,
        addMenu,
        getClassRoom,
        clean,
    };
})