<template>
    <el-row class="home" :gutter="20">
        <el-col :span="8" style="margin-top: 20px;">
            <el-card shadow="hover">
                <div class="user">
                    <img :src="getImageUrl('user')" class="user"/>
                    <div class="user-info">
                        <p class="user-info-name">{{store.state.username}}</p>
                        <p class="user-info-p">{{ userrole }}</p>
                    </div>
                </div>
            </el-card>

            <el-card shadow="hover" class="classes_table">
                <h3 class="today_classes">今日课表</h3>
                <el-table :data="TableData">
                    <el-table-column
                        v-for="(val, key) in tableLabel"
                        :key="key"
                        :prop="key"
                        :label="val"
                    ></el-table-column>
                </el-table>
            </el-card>
        </el-col>

            <el-col :span="16" style="margin-top: 20px;">
                <div class="Count">
                    <el-card
                        :body-style="{display:'flex',padding:0}"
                        v-for="item in CountDatas"
                        :key="item.name"
                    >
                        <component :is="item.icon" class="icons" :style="{background:item.color}"></component>
                        <div class="detail">
                            <p class="num">{{ item.value }}</p>
                            <p class="text">{{ item.name }}</p>
                        </div>
                    </el-card>
                </div>
                <el-card class="Graph">
                    <div ref="piechart" style="height: 540px;"></div>

                </el-card>
            </el-col>

        
    </el-row>
</template>

<script setup>
import {ref,getCurrentInstance,onMounted,reactive, computed} from "vue"
import * as echarts from 'echarts'; // 使用默认导入
const {proxy}=getCurrentInstance()
import{useAllDataStore} from "@/stores"
const getImageUrl = (user) => {
    return new URL(`../assets/images/${user}.png`, import.meta.url).href;
};
const TableData = ref([]);
const CountData=ref([])
const store=useAllDataStore();
const initconfig={
    role:store.state.role,
    UserId:store.state.username
}
const userrole=computed(()=>store.state.role=='2'?"教师":"管理员")
// 表头定义
const tableLabel = {
    classId: "课程ID",
    className: "课程名称",
    classRoom: "上课教室",
    classTeacher: "任课教师",

};

const CountDatas=ref([
                    {
                        name:"授课课程数目",
                        value:5,
                        color:"#5ab1ef",
                        icon:"GoodsFilled",
                    },
                    {
                        name:"今日请假人数",
                        value:10,
                        color:"#2ec7cb",
                        icon:"SuccessFilled",
                    },
                    {
                        name:"今日签到人数",
                        value:20,
                        color:"#ffb980",
                        icon:"StarFilled",
                    },
                    {
                        name:"今日缺勤人数",
                        value:4,
                        icon:"GoodsFilled",
                        color:"#5ab1ef",
                    }
                ])

const getTableData = async (config) => {
    console.log("执行了");
    const data = await proxy.$api.getTableData(config);
    TableData.value = data.map(item=>({
        ...item,
        classId:item.courseid,
        className:item.coursename,
        classRoom:item.courseRoom,
        classTime:item.coursetime,
        classTeacher:item.teacherName
    }))
    console.log("@@@@", data);
};

const pieOptions = reactive({
  series: [
    {
      type: 'pie',
      data: []
    }
  ]
});

const getCountData = async (config) => {
    const data = await proxy.$api.getCountData(config);
    console.log("home",data)
    // 更新 CountData 的值
    CountDatas.value = CountDatas.value.map((item, index) => {
        switch (index) {
            case 0:
                return { ...item, value: data.coursenum };
            case 1:
                return { ...item, value: data.threenum };
            case 2:
                return { ...item, value: data.zeronum };
            case 3:
                return { ...item, value: data.onezeronum };
            default:
                return item;
        }
    });

    console.log("home",CountData);
    if (Array.isArray(CountData.value)) {
        pieOptions.series[0].data = CountDatas.value
            .filter(item => {
                console.log('Filter item:', item); // 调试信息
                return (
                    typeof item.name === 'string' &&
                    item.name.trim() !== '' &&
                    item.name.endsWith('人数')
                );
            })
            .map(item => {
                console.log('Map item:', item); // 调试信息
                return {
                    name: item.name,
                    value: item.value
                };
            });
    } else {
        console.error('CountData 格式不正确:', CountData.value);
        pieOptions.series[0].data = []; // 设置为空数组，避免图表渲染错误
    }

    const pieEchart = echarts.init(proxy.$refs["piechart"]);
    pieEchart.setOption(pieOptions);
};

onMounted(()=>{
    getTableData(initconfig)
    getCountData(initconfig)
})
</script>


<style scoped lang="less">
.home{
    height: 100%;
    overflow: hidden;
    .user{
        display: flex;
        align-items: center;
        border-bottom: 1px solid #ccc;
        margin-bottom: 20px;
        img{
            width: 150px;
            height: 150px;
            border-radius: 50%;
            margin-right: 40px;
        }
        .user-info{
            p{
                line-height: 40px;
            }
            .user-info-p{
                color: #999;
            }
            .user-info-name{
                font-size: 35px;
            }
        }
    }
    .login-info{
        p{
            line-height: 30px;
            font-size: 14px;
            color:#999 ;
        }
        span{
            color: #666;
            margin-left: 60px;
        }
    }
    .classes_table{
        margin-top: 20px;
    }
    .today_classes{
            line-height: 48px;
            color: #000;
            font-family: 'Helvetica Neue', Arial, sans-serif;
            font-weight: 500; /* 调整字体粗细 */
            text-align: center;
            font-size: 20px;

    }
    .Count{
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        .el-card{
            width: 20%;
            height: 25%;
            margin-bottom: 20px;   
        }
        .icons{
            width: 80px;
            height: 80px;
            font-size: 30px;
            text-align: center;
            line-height:80px ;
            color: #fff;
        }
        .detail{
            margin-left: 15px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            .num{
                font-size:30px ;
                margin-bottom: 10px;
            }
            .text{
                font-size:15px ;
                text-align: center;
                color: #999;
            }
        }
    }
    // .Graph{
    //     display: flex;
    //     justify-content: space-between;
    // }
}
</style>