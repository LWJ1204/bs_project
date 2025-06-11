<template>
    <div class="RecordManageHeader">
      <el-form :inline="true" :modle="searchForm">
        <el-form-item label="请输入">
          <el-input placeholder="请输入关键字" v-model="searchForm.keyWord"></el-input>
        </el-form-item>
        <el-form-item>
            <el-button @click="RecordSearch" type="primary">搜索</el-button>
        </el-form-item>
      </el-form>
    </div>
            
   
  
    <div class="table">
      <el-table
        :data="recordData"
        style="width: 100%"
      >
        <el-table-column
          v-for="item in TableLabel"
          :key="item.prop"
          :width="item.width ? item.width : 125"
          :prop="item.prop"
          :label="item.label"
        >
          <!-- 仅为 courseState 列设置动态颜色 -->
          <template v-slot="scope" v-if="item.prop === 'RecordStateLabel'">
            <div :style="{ color: getStateColor(scope.row.recordstate) }">
              {{ scope.row.RecordStateLabel }}
            </div>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="200">
          <template v-slot="scope">
            <el-dropdown @command="(command)=>handleStateChange(scope.row,command)">
              <el-button type="primary">
                  修改签到状态
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
                <template #dropdown  v-slot="scope">
                  <el-dropdown-menu>
                    <el-dropdown-item command="0">已签到</el-dropdown-item>
                    <el-dropdown-item command="1">未签到</el-dropdown-item>
                    <el-dropdown-item  command="2">请假</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
            </el-dropdown>
            </template>
        </el-table-column>
      </el-table>
        <el-pagination
            class="page"
            background
            layout="prev, pager, next"
            size="small"
            :total="total"
            v-model:current-page="mypage"
            @current-change="handleChange"
          />

    </div>
  </template>
  
<script setup>
 import { ElMessage,ElMessageBox } from 'element-plus';
 import { ref, getCurrentInstance, reactive, onMounted } from 'vue';
import {useAllDataStore} from "@/stores"
const { proxy } = getCurrentInstance();
//搜索表单
const searchForm=reactive({
  keyWord:"",
})
const store=useAllDataStore();
const id=ref(0)
id.value=0;
//表格数据
const recordData = ref([]);
 //表头数据
const TableLabel = reactive([
    {
      prop: "CourseName",
      label: "课程名",
      width: 150,
    },
    {
      prop: "CourseId",
      label: "课程号",
      width: 150,
    },
    {
      prop: "CourseRoom",
      label: "教室",
    },
    {
      prop: "CourseTeacher",
      label: "授课教师",
    },
    {
      prop: "CourseStudentName",
      label: "学生姓名",
    },
    {
      prop: "CourseStudentId",
      label: "学生学号",
    },
    {
      prop: "RecordStateLabel",
      label: "签到状态",
      width: 200,
    },
    {
      prop: "RecordSetTime",
      label: "设置时间",
      width: 300,
    },
    {
        prop:"RecordArrTime",
        label:"签到时间",
        width:300
    }

 ]);
 //返回数据总数
 const total=ref(10)
 //每页数据
 const mylimit=ref(10)
 const mypage=ref(1)
 // 根据选课状态返回颜色_fun
const getStateColor = (state) => {
    switch (state) {
      case  0:
        return "green"; // 已选状态显示为绿色
      case 1:
        return "red"; // 未选状态显示为红色
      case 2:
        return "blue";
      default:
        return "black"; // 默认颜色为黑色
    }
 };
 //获取签到数据_fun
const getRecordData = async (config) => {
    try {
        const data = await proxy.$api.getRecordData(config); // 使用 await 等待异步操作完成
        recordData.value = data.list.map((item) => {
         // 将 RecordState 转换为字符串类型
         const recordlabel = getStateLabel(item.recordstate);
         return {
             ...item,
             CourseName:item.courseName,
             CourseId:item.courseId,
             CourseRoom:item.courseRoom,
             CourseStudentId:item.studentid,
             CourseTeacher:item.teacherName,
             CourseStudentName:item.studentName,
             RecordSetTime:getlocaltime(item.recordSetTime),
             RecordArrTime:getlocaltime(item.recordarrtime),
             RecordStateLabel: recordlabel // 修正拼写
            };
        });
        total.value=data.count;

    } catch (error) {
        console.error("Error fetching record data:", error);
    }
};
 // 分页_fun
const handleChange = (mypage) => {
  const config=reactive({
   
    page:mypage,
    limit:10,
    keyWord:searchForm.keyWord,
    TeacherId:id.value,
  })
  getRecordData(config);
};
//修改签到状态_fun
const handleStateChange=async (val,command)=>{
    let res=null;
    res=await proxy.$api.editRecordData({RecordId:val.recordid,studentid:String(val.studentid),RecordState:parseInt(command)})
    if(res){
      const config={
        page:mypage.value,
        limit:10,
        keyWord:searchForm.keyWord,
        TeacherId:id.value
      }
      getRecordData(config);
    }else{
      ElMessage({
        showClose:true,
        message:"修改失败",
        type:'erro'
      })
    }
}
//get状态
const getStateLabel=(state)=>{
    if(state=='0') return "已签到";
    if(state=='1') return "未签到";
    if(state=='2') return "病假";
    return "未知";
}

//搜索_fun
const RecordSearch=()=>{
  const config={
      page:1,
      limit:10,
      keyWord:searchForm.keyWord,
      TeacherId:id.value
  }
  getRecordData(config);
}


const getlocaltime=(time)=>{
  console.log("rcordmanage 215",time);
  if(time==undefined)return null;
  const utcTime = new Date(time);
  // 转换为北京时间（UTC+8）
  const cstTime = new Date(utcTime.getTime() + 8 * 60 * 60 * 1000);

  // 格式化为指定格式
  const formattedCstTime = cstTime.toISOString().replace('T', ' ').substring(0, 19);
  return formattedCstTime
 
}


onMounted(() => {
  if(store.state.role=="2")
{
  id.value=store.state.username;
}
  const config={
    page:1,
    limit:10,
    TeacherId:id.value,
  }
  getRecordData(config);
});
</script>

<style scoped lang="less">
.RecordManageHeader {
  display: flex;
  justify-content: flex-end;
}

.table {
  position: relative;
  height: 520px;
  .page {
    position: absolute;
    right: 10px;
    bottom: 25px;
  }
  .el-table__row {
    height: auto !important; /* 设置行高自适应 */
  }
  .el-table__row .cell {
    white-space: normal !important; /* 允许内容换行 */
    word-break: break-all !important; /* 允许单词内换行 */
  }
  .el-table {
    width: 100%;
    height: calc(100% - 55px);
  }
}

.select-clearn {
  display: flex;
}  

</style>