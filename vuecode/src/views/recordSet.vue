<template>
  <div v-if="showvisable">
    <el-form :model="form" label-width="auto" :rules="formrule"  ref="recordform" style="max-width: 600px">
      <h3 class="form-title">{{ form.name }}</h3>
      <!-- 课程选择 -->
      <el-form-item label="课程选择" prop="CourseId">
        <el-select v-model="form.CourseId" placeholder="请选择课程" style="width: 100%">
          <el-option
            v-for="item in CourseList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <!-- 教室选择 -->
      <el-form-item label="教室选择" prop="CourseRoom">
        <el-tree-select
                      v-model="form.CourseRoom"
                      :data="CourseRoomList"
                      :render-after-expand="false"
                      show-checkbox
                      style="width: 240px"
                      :disabled="action==='addStu'"
                    />
              
      </el-form-item>
      <el-row class="date-time-picker">
    <el-col :span="11">
      <el-form-item label="开始日期" prop="StartDate">
        <el-date-picker
          v-model="form.StartDate"
          type="date"
          placeholder="选择日期"
          style="width: 100%"
          clearable
        />
      </el-form-item>
    </el-col>
    <el-col :span="2" class="text-center divider">
      <span class="text-gray-500">-</span>
    </el-col>
    <el-col :span="11">
      <el-form-item prop="StartTime">
        <el-time-picker
          v-model="form.StartTime"
          placeholder="选择时间"
          style="width: 100%"
        />
      </el-form-item>
    </el-col>
  </el-row>
  <el-row class="date-time-picker">
    <el-col :span="11">
      <el-form-item label="结束日期" prop="EndDate">
        <el-date-picker
          v-model="form.EndDate"
          type="date"
          placeholder="选择日期"
          style="width: 100%"
          clearable
        />
      </el-form-item>
    </el-col>
    <el-col :span="2" class="text-center divider">
      <span class="text-gray-500">-</span>
    </el-col>
    <el-col :span="11">
      <el-form-item prop="EndTime">
        <el-time-picker
          v-model="form.EndTime"
          placeholder="选择时间"
          style="width: 100%"
          clearable
        />
      </el-form-item>
    </el-col>
  </el-row>
  
      <!-- 按钮 -->
      <div class="form-buttons">
        <el-button type="primary" @click="CreateRecordSet" class="button-spacing">创建</el-button>
        <el-button class="button-spacing" @click="ResetRecordSet">重置</el-button>
      </div>
    </el-form>
  </div>
  <div v-if="!showvisable">
    <div class="recordheader">
    <div class="left-section">
      <el-button type="primary" @click="goback">返回设置</el-button>
    </div>
    <div class="right-section">
      <el-switch v-model="absolute"
                 active-color="#13ce66"
                 inactive-color="#ff4949">
      </el-switch>
      <span class="switch-label">去敏</span>
      <el-button type="danger" @click="Finished" class="end-button">结束</el-button>
    </div>
  </div>

    <el-tabs v-model="activeName" class="demo-tabs" @tab-click="ChangeTabState">
        <el-tab-pane 
            v-for="item in tabslabel"
            :label="item.label"
            :key="item.value"
            :value="item.value"
            :name="item.name"
          >
           <div class="table">
            <el-table
              :data="itemdatalist"
              style="width: 100%;"
              >
              <el-table-column
                prop="CourseStudentName"
                label="学生姓名"
                width="auto"
                v-if="!absolute"
                />

                <el-table-column
                prop="CourseStudentId"
                label="学生学号"
                width="auto"
                />
                <el-table-column
                prop="RecordArrTime"
                label="签到时间"
                width="auto"
                />
                
              <el-table-column
                prop="RecordStateLabel"
                label="签到状态"
                width="auto"
                >
                        <!-- 仅为 courseState 列设置动态颜色 -->
              <template v-slot="scope" >
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
              @current-change="PageChange"
            />
           </div>
        </el-tab-pane>
    </el-tabs>

  </div>
</template>
  
<script setup>
import { ref, getCurrentInstance, reactive, onMounted, watch } from 'vue';
import { ElMessage,ElMessageBox, tourEmits } from "element-plus";
const { proxy } = getCurrentInstance();
import {useAllDataStore} from "@/stores"
import{socket} from "@/stores/websocket.js"
const store=useAllDataStore()
//课程信息列表
const CourseList = ref([
])
const absolute=ref(true);
//tab列表
const tabslabel=[
{
    label:'未签到',
    value:1,
    name:1
  },
  {
    label:'已签到',
    value:0,
    name:0
  },

  {
    label:'请假',
    value:2,
    name:2
  }
]
//表格table
const TableLabel=[
  {
    label:'学生姓名',
    prop:"CourseStudentName",
  },
  {
    label:'学生学号',
    prop:'CourseStudentId',
  },
  {
    label:"签到状态",
    prop:"RecordStateLabel",
  },
  {
    label:'签到时间',
    prop:'RecordArrTime',
  }
]
//教室列表
const CourseRoomList = ref([
])
CourseRoomList.value=store.getClassRoom()
//签到表单 
const form = reactive({
  name: '考勤设置',
  CourseName: '',
  CourseId:'',
  CourseRoom: "",
  StartDate: '',
  StartTime: '',
  EndDate: '',
  EndTime: '',
})
//签到表单规则
const formrule=reactive({
  "CourseName":[{required:true,message:"请选择课程名称",trigger:'blur'}],
  'CourseId':[{required:true,message:"请选择课程号",trigger:'blur'}],
  'CourseRoom':[{required:true,message:"请选择教室",trigger:'blur'}],
  'StartDate':[{required:true,message:"请输入签到开始日期",trigger:'blur'}],
  "StartTime":[{required:true,message:'请输入签到开始时间',trigger:'blur'}],
  'EndDate':[{required:true,message:"请输入签到结束日期",trigger:'blur'}],
  "EndTime":[{required:true,message:'请输入签到结束时间',trigger:'blur'}],
})
//页面是否展示
const showvisable=ref(true)
//活动tab
const activeName=ref(0)
//recordID
let mRecordId=ref()
const mypage=ref(1)
//获取课程信息_fun
const getCourseInfo=async (config)=>{
    try{
      const data=await proxy.$api.querryCourseInfo(config);
      console.log('data',data)
      CourseList.value = data.map(item => ({
        label: item.courseid+"-"+item.coursename,
        value: item.courseid
    }));
      console.log(CourseList)
    }catch(error){
        console.error(error)
    }
}
//考勤信息
const itemdatalist=ref([])
//总数
const total=ref(10)
const currentPage=ref(1)
const url="ws://192.168.173.207:8080/websocket/"
//过滤后的数据
const filteredData=ref([])
//当前页面属性下标
const index=ref(0)
//处理时间_fun
const formatTime=(mdate,mtime)=>{
  const date=new Date(mdate);
  const time=new Date(mtime);
  const year=date.getFullYear();
  const month=String(date.getMonth()+1).padStart(2,'0');
  const day=String(date.getDate()).padStart(2,'0')
  const hours=String(time.getHours()).padStart(2,'0')
  const minutes=String(time.getMinutes()).padStart(2,'0')
  const seconds=String(time.getSeconds()).padStart(2,'0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}
//计算开始和结束之间时间差_fun
const calculateDifftime=(stime,etime)=>{
  console.log(stime,etime)
  const sminutes=new Date(stime);
  const eminutes=new Date(etime);
  console.log('start',sminutes)
  console.log('end',eminutes)
  return (eminutes.getTime()-sminutes.getTime())/(1000*60);
}
//更新tab_fun
const ChangeTabState=(val)=>{
  console.log("test tab",val.props.name)
  let config={
    page:1,
    limit:10,
    state:val.props.name,
    RecordId:mRecordId.value,
    Absolute:absolute.value,
    KeyWord:null,
  }
  getRecordState(config);
};
//修改签到状态_fun
const handleStateChange=async (val,command)=>{
    let res=null;
    res=await proxy.$api.editRecordData({RecordId:val.recordid,studentid:String(val.studentid),RecordState:parseInt(command)})
    if(res){
      let config={
        page:mypage.value,
        limit:10,
        state:activeName.value,
        RecordId:mRecordId.value,
        Absolute:absolute.value,
        KeyWord:null,
      }
     getRecordState(config);
    }else{
      ElMessage({
        showClose:true,
        message:"修改失败",
        type:'erro'
      })
    }
}

//分页_fun
const PageChange=(mpage)=>{
  let config={
      page:mpage,
      limit:10,
      state:activeName.value,
      Absolute:absolute.value,
      RecordId:mRecordId.value,
    }
    getRecordState(config)
}
 // 根据选课状态返回颜色_fun
const getStateColor = (state) => {
   console.log('state',state)
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
//创建签到_fun
const CreateRecordSet = () => {
    console.log('clicked');
    proxy.$refs['recordform'].validate(async (valid) => {
        if (valid) {
            console.log("验证成功");
            const starttime=form.StartTime //time
            const satrtdate=form.StartDate //date
            const endtime=form.EndTime   
            const enddate=form.EndDate
            const RecordSetTime=formatTime(satrtdate,starttime)
            const RecordEndTime=formatTime(enddate,endtime)
            console.log(RecordEndTime,RecordSetTime)
            //计算时间差
            const difftime=calculateDifftime(RecordSetTime,RecordEndTime)
            console.log(difftime)
            //判断间隔时间
            if(difftime<5){
              ElMessage({
                    showClose: true,
                    message: "请输入正确的时间且签到间隔大于5分钟",
                    type: "error"
                });
                return;
            }
            console.log('recordform', form);
            try {
                form.RecordSetTime=RecordSetTime
                form.RecordEndTime=RecordEndTime
                /*
                    res{
                      code,
                      recordid
                      list {studentname,studentid,recordtime,recordstate}
                    }
                 */
                const res = await proxy.$api.createRecord(form);
                if(res){
                     //获取recordid
                      mRecordId.value=res.recordid;
                      //初始化websocket 
                      socket.initWebSocket(url+mRecordId.value);
                      //连接websocket
                      socket.websocket.onopen = () => {
                        console.log('WebSocket connected');
                      };
                      socket.websocket.onmessage=handleWebSocketMessage//绑定处理方法
                      showvisable.value=false
                      absolute.value=true;
                      //初始化数据
                      let config={
                        page:1,
                        limit:10,
                        state:1,
                        RecordId:res.recordid,
                        Absolute:absolute.value,
                        KeyWord:null,
                      }
                      getRecordState(config)
                      proxy.$nextTick(() => {
                        activeName.value = 1; // 创建成功后跳转到未签到的tab
                      });

                      ElMessage({
                          showClose: true,
                          message: "创建成功",
                          type: "success"
                      });
                  }

                }catch (error) {
                  console.error('Failed to create record:', error);
                  ElMessage({
                      showClose: true,
                      message: "创建失败",
                      type: "error"
                  });
                }
        } else {
            ElMessage({
                showClose: true,
                message: "请输入完整内容",
                type: "error"
            });
        }
    });
};
//重置_btn
const ResetRecordSet=()=>{
  proxy.$refs['recordform'].resetFields();
}

let ishandle=false
//结束_btn
const Finished=()=>{
    ishandle=true
    socket.websocketOnClose();
}
//返回_btn
const goback=()=>{
  console.log("backisclicked")
  Finished();
  showvisable.value=true;
  filteredData.value=null;
  itemdatalist.value=null;
  socket.websocketOnClose();
}
//获取考勤信息——fun
const getRecordState=async (config)=>{
  try {
        const data = await proxy.$api.getmyRecordData(config); // 使用 await 等待异步操作完成
        itemdatalist.value = data.list.map((item) => {
         // 将 RecordState 转换为字符串类型
         const recordlabel = getStateLabel(item.recordstate);
         return {
             ...item,
             CourseName:item.courseName,
             CourseId:item.courseId,
             CourseStudentId:item.astudentid,
             CourseRoom:item.courseRoom,
             CourseTeacher:item.teacherName,
             CourseStudentName:item.studentName,
             RecordSetTime:item.recordSetTime,
             RecordArrTime:getlocaltime(item.recordarrtime),
             RecordStateLabel: recordlabel // 修正拼写
            };
        });
        total.value=data.count;

    } catch (error) {
        console.error("Error fetching record data:", error);
    }
}

//getLabel状态
const getStateLabel=(state)=>{
    if(state=='0') return "已签到";
    if(state=='1') return "未签到";
    if(state=='2') return "病假";
    return "未知";
}

//处理websocket内容
const handleWebSocketMessage=(event)=>{
  const data=JSON.parse(event.data);
  const state=data.state;
  const changed=data.changed;
  console.log("accept event",event);
  if(state=='1'){
    console.log("state:",state);
    socket.websocketState=true;
  }
  if(changed!=null){
    let config={
    page:mypage.value,
    limit:10,
    state:activeName.value,
    RecordId:mRecordId.value,
    Absolute:absolute.value,
    KeyWord:null,
  }
  console.log("web config",config);
    getRecordState(config);
  }
}

const getlocaltime=(time)=>{
  if(time==undefined)return null;
  const utcTime = new Date(time);
  // 转换为北京时间（UTC+8）
  const cstTime = new Date(utcTime.getTime() + 8 * 60 * 60 * 1000);

  // 格式化为指定格式
  const formattedCstTime = cstTime.toISOString().replace('T', ' ').substring(0, 19);
  return formattedCstTime;
  console.log(formattedCstTime);
}

watch(absolute,(newValue,oldValue)=>{
  console.log("set中去隐私发生了改变"+newValue)
  let config={
    page:mypage.value,
    limit:10,
    state:activeName.value,
    RecordId:mRecordId.value,
    Absolute:absolute.value,
    KeyWord:null,
  }
getRecordState(config)
})

//挂载信息
onMounted(()=>{
  //获取教师所教课程
  const config=reactive({
      TeacherId:store.state.username
  })
   getCourseInfo(config)

})

</script>
<style scoped lang="less">
.recordheader {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.left-section {
  display: flex;
  align-items: center;
}

.right-section {
  display: flex;
  align-items: center;
}

.switch-label {
  margin-left: 8px; /* 调整间距，使文字紧贴开关 */
  font-size: 14px;
  color: #606266;
}
.end-button {
  margin-left: 16px; /* 为“结束”按钮添加更大的间距 */
}
.form-title {
  margin-bottom: 20px; /* 增加h3与表单内容的距离 */
}
.date-time-picker {
  display: flex;
  align-items: center; /* 垂直居中对齐 */
  margin-bottom: 20px; /* 增加间距 */
}

.text-center {
  display: flex;
  justify-content: center;
  align-items: center;
}

.divider {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%; /* 使高度与日期和时间选择器一致 */
}

.text-gray-500 {
  color: #9ca3af;
}
.form-buttons {
  // text-align: center; /* 将按钮居中对齐 */
  margin-top: 20px; /* 增加按钮与表单内容的距离 */
  margin-bottom: 20px; /* 增加按钮与底部的距离 */
  padding: 20px; /* 增加按钮周围的空白区域 */
  background-color: #f5f5f5; /* 添加背景色，使按钮更加突出 */
  border-radius: 8px; /* 添加圆角，使按钮区域更加美观 */
}
.button-spacing {
  margin: 0 10px; /* 增加按钮之间的间距 */
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