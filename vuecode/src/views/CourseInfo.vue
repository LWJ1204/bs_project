<template>
 <div class="courseinfo-header">
      <el-button type="primary" @click="addCourseInfo">新增</el-button>
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="请输入">
          <el-input placeholder="请输入关键字" v-model="searchForm.keyWord"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleCourseSearch">搜索</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="table">
        <el-table
            :data="CourseInfoData"
            style="width: 100%"
        >
          <el-table-column
              v-for="item in TableLabel"
              :key="item.prop"
              :width="item.width? item.width:125"
              :prop="item.prop"
              :label="item.label"
          />
          <el-table-column fixed="right" label="操作" width="200" v-if="role=='1'">
              <template v-slot="scope">
                <el-button @click="editCourseInfo(scope.row)" type="primary" size="small">编辑</el-button>
                <el-button @click="selectCourseInfo(scope.row)" type="success" size="small">选择</el-button>
                <el-button @click='deleteCourseInfo(scope.row)' type="danger" size="small">删除</el-button>
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

    <el-dialog
        v-model="dialogVisible"
        :title="getTitle()"
        width="35%"
        :before-close="FormCancel"
  >
      <el-form :inline="true"  :model="CourseInfoForm" :rules="rules" ref="useForm">
        <el-row>
          <el-col :span="12">
            <el-form-item label="课程名" prop="CourseName">
              <el-input v-model="CourseInfoForm.CourseName" placeholder="请输入课程名称" :disabled="action==='addStu'"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
              <el-form-item label="课程号" prop="CourseId">
              <el-input v-model="CourseInfoForm.CourseId" placeholder="请输入课程号" :disabled="action!=='add'"/>
            </el-form-item>
          </el-col>
        </el-row>       
        <el-row>
        <el-col :span="12">
          <el-form-item  class="select-clearn"  label="开课学院" prop="CourseAcademy">
            <el-select v-model="CourseInfoForm.CourseAcademy" placeholder="请选择学院" :disabled="action==='addStu'" >
              <el-option v-for="item in AMInfo" :key="item.academy" :label="item.academy" :value="item.academy" />
              </el-select>  
          </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item  label="开课人数" prop="CourseNum">
              <el-input v-model="CourseInfoForm.CourseNum" placeholder="请输入开课人数" :disabled="action==='addStu'" />
            </el-form-item>
          </el-col>    
      </el-row>
        <el-row>
        <el-col :span="12">
            <el-form-item   class="select-clearn" label="开课学年" prop="CourseYear">
              <el-select v-model="CourseInfoForm.CourseYear" placeholder="请输入开课学年" :disabled="action==='addStu'" >
                 <el-option v-for="item in yearlist" :key="item.value" :label="item.label" :value="item.value"/>
                </el-select>  
            </el-form-item>
          </el-col>
          <el-col :span="12">
              <el-form-item class="select-clearn" label="开课学期" prop="CourseSemester">
                <el-select  v-model="CourseInfoForm.CourseSemester" placeholder="请选择" :disabled="action==='addStu'">
                <el-option :label="1" :value=1 />
                <el-option :label="2" :value=2 />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
        <el-col :span="12">
            <el-form-item  class="select-clearn" label="授课教师" prop="CourseTeacher">
              <el-select v-model="CourseInfoForm.CourseTeacher" placeholder="请输入授课教师" :disabled="action==='addStu'">
                <el-option
                  v-for="item in teachers"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="授课教室" prop="CourseRoom" class="select-clearn">
              <el-tree-select
                      v-model="CourseInfoForm.CourseRoom"
                      :data="ClassRoom"
                      :render-after-expand="false"
                      show-checkbox
                      style="width: 240px"
                      :disabled="action==='addStu'"
                    />
              
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="学分" prop="CourseValue">
              <el-input v-model="CourseInfoForm.CourseValue" placeholder="请输入课程学分" :disabled="action==='addStu'"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结课形式" prop="CourseFinish">
              <el-input v-model="CourseInfoForm.CourseFinish" placeholder="请输入结课形式" :disabled="action==='addStu'"/>
            </el-form-item>
          </el-col>
        </el-row>
        <div v-if="action==='add'">
            <h3 style="margin-bottom: 6px; margin-top: 3px;"> 添加上课时间</h3>
            <el-form :model="CourseTimeForm" :rules="coursetimerules" ref="timeform">
                <el-row>
                  <el-col :span="12" >
                    <el-form-item class="select-clearn">
                      <el-select v-model="CourseTimeForm.sweek" placeholder="开始周" class="select-width">
                        <el-option
                            v-for="item in weeklist"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12" >
                    <el-form-item class="select-clearn">
                      <el-select v-model="CourseTimeForm.eweek" placeholder="结束周" class="select-width">
                      <el-option
                            v-for="item in weeklist"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        />
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row>
                  <el-col :span="12" >
                    <el-form-item class="select-clearn">
                      <el-tree-select
                          v-model="CourseTimeForm.date"
                          :data="datelist"
                          multiple
                          :render-after-expand="false"
                          show-checkbox
                          class="select-width"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12" >
                    <el-form-item class="select-clearn">
                      <el-button type="primary" @click="addCourseTime">添加</el-button>
                    </el-form-item>
                  </el-col>
                </el-row>
            </el-form>
        </div>
        <el-row v-if="action==='add'">
          <el-col :span="24">
            <el-tag
              v-for="item in coursetimelist"
              :key="item"
              closable
              :disable-transitions="false"
              @close="deleteTag(item)"
            >
              {{ item }}
            </el-tag>
          </el-col>
        </el-row>
        <el-row v-if="action==='edit'" >
          <el-col :span='24'>
            <el-form-item  label="上课时间" prop="CourseTime" style="width: 100%;">
              <el-input v-model="CourseInfoForm.CourseTime" placeholder="请输入上课时间" />
            </el-form-item>
          </el-col>
        </el-row>
        <div v-if="action==='addStu'">
          <h3 style="margin-bottom: 8px; margin-top: 3px;"> 添加选课学生</h3>
          <el-form :model="addStuForm" ref="addstuform" :rules="addsturules">
            <el-row>
                <el-col :span="12">
                  <el-form-item class="select-clearn" label="学生姓名" prop="addStudentName">
                    <el-input v-model="addStuForm.StudentName" placeholder="请输入选课学生姓名" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item class="select-clearn" label="学生学号" prop="addStudentId">
                    <el-input v-model="addStuForm.StudenId" placeholder="请输入选课学生学号" />
                  </el-form-item>
                </el-col>
            </el-row>
          </el-form>
        </div>
        <el-row style="justify-content: flex-end">
        <el-form-item>
          <el-button type="primary" @click="FormCancel">取消</el-button>
          <el-button type="primary" @click="onSubmit">确定</el-button>
        </el-form-item>
        </el-row>
      </el-form>
    </el-dialog>
  </template>
  
<script setup>
import {ref,getCurrentInstance,reactive, onMounted,watch,nextTick,computed} from "vue"
import { ElMessage,ElMessageBox } from "element-plus";
import {useAllDataStore} from "@/stores"
import { number } from "echarts";

const {proxy}=getCurrentInstance()
const CourseInfoData=ref([])
const role=ref('1')
let store=useAllDataStore()
role.value=store.state.role
console.log("testrole",role.value)
const ClassRoom=ref([])
ClassRoom.value=store.getClassRoom()
const id=ref("0")
id.value=null;
//编辑dialog
const action=ref('add')
const dialogVisible=ref(false)
const mypage=ref(1)
//数据总数
const total=ref(10)
//学年列表
const yearlist=ref([])
//周数列表
const weeklist=ref([])
//星期列表
const datelist=ref([])
//选择教师列表
const teachers=ref([])
const date2=['周一','周二','周三','周四','周五','周六','周日']
//每页数据
const mylimit=ref(10)
//表头数据
const TableLabel=reactive([
    {
        prop:"CourseName",
        label:"课程名",
        width:150
    },
    {
        prop:"CourseId",
        label:"课程号",
        width:150
    },
    {
        prop:"CourseTeacherName",
        label:"授课教师",
    },
    {
        prop:"CourseRoom",
        label:"教室",
    },
    {
        prop:"CourseValue",
        label:"学分",
    },
    {
        prop:"CourseFinish",
        label:"结课形式",
        width:200
    },
    {
        prop:"CourseAcademy",
        label:"开课学院",
        width:300
    },
    {
      prop:"CourseSelected",
      label:"选课人数"
    },
    {
      prop:"CourseTime",
      label:"上课时间",
      width:300
    },
    {
      prop:"CourseYear",
      label:"开课学年",
      width:150,
    },
    {
      prop:"CourseSemester",
      label:"开课学期",
      width:150
    }
])
//课程信息表单
const CourseInfoForm=reactive({})
//选课表单
const addStuForm=reactive({})
//添加学院专业教师信息
const AMInfo=ref([])
//搜索信息表单
const searchForm=reactive({
  keyWord:'',
})
//课程时间表单
const CourseTimeForm=reactive({
  sweek:0,
  eweek:1,
  date:'',
})
//添加课程规则
const rules=reactive({
    "CourseName":[{required:true,message:"请输入课程名",trigger:'blur'}],
    "CourseId":[{required:true,message:"请输入课程号",trigger:'blur'}],   
    "CourseNum":[{required:true,mesge:"请输入开课人数",trigger:'blur'}],
    "CourseYear":[{required:true,message:"请输入开课学年",trigger:'blur'},
    ],
    "CourseSemester":[{required:true,messaage:"请输入开课学期",trigger:'blur',type:number}],
    "CourseTeacher":[{required:true,message:"请输入授课教师",trigger:'blur'}],
    "CourseFinish":[{required:true,messaage:"请输入结课形式",trigger:'blur'}],
    "CourseRoom":[{required:true,message:"请输入教室",trigger:'blur'}],
    "CourseValue":[{required:true,message:"请输入学分",trigger:'blur',type:number}],
    "CourseAcademy":[{required:true,messaage:"请输入开课学院",trigger:'blur'}],
    "CourseTime":[
      {required:false,messaage:"请输入课程时间",trigger:'blur'},
      // { pattern: /^(\d+-\d+周（(周[一二三四五六日]，\d+-\d+节)(；周[一二三四五六日]，\d+-\d+节)*）)(;(\d+-\d+周（(周[一二三四五六日]，\d+-\d+节)(；周[一二三四五六日]，\d+-\d+节)*）))*$/,message: '请输入有效的课程时间',trigger: 'blur',}
    ]  
})
//课程时间规则
const coursetimerules = reactive({
  sweek: [
    { required: true, message: "请选择开始周", trigger: "change" }
  ],
  eweek: [
    { required: true, message: "请选择结束周", trigger: "change" },
    {
      validator: (rule, value, callback) => {
        if (value < CourseTimeForm.sweek) {
          callback(new Error("结束周必须大于等于开始周"));
        } else {
          callback();
        }
      },
      trigger: "change"
    }
  ]
});
//添加学生规则
const addsturules=reactive({
  StudentName:[{required:true,message:"请输入选课学生姓名",trigger:"blur"}],
  StudentId:[{required:true,messaage:"请输入学生学号",trigger:"blur"}]
})
const coursetimelist=ref([])
//获取课程信息_fun
const getCourseInfoData=async (config)=>{
  const data=await proxy.$api.getCourseInfoData(config)
  console.log("课程信息处理前",data)
  CourseInfoData.value = data.list.map(item => ({
    ...item,
    CourseName:item.coursename,
    CourseId:item.courseid,
    CourseTeacher:item.teachername,
    CourseRoom:item.CourseRoom,
    CourseTeacherName:item.teacherName,
    CourseFinish:item.coursefinish,
    CourseValue:item.coursevalue,
    CourseAcademy:item.academy,
    CourseYear:item.courseyear,
    CourseSemester:item.coursesemester,
    CourseTime:item.coursetime,
    CourseTeacherId:item.teacherid,
    CourseNum:item.coursenum,
    CourseSelected:item.courseSelectNum+"/"+item.coursenum,
  }));
  console.log("CourseInfodata处理后",CourseInfoData)
  total.value=data.count
}
// 分页_fun
const handleChange = (mypage) => {
  const config=reactive({
    keyWord:searchForm.keyWord,
    page:mypage,
    limit:mylimit,
    TeacherId:id.value,
  })
  getCourseInfoData(config);
};
//获取title_fun
const getTitle=()=>{
  if(action.value==='add'){
    return '新增课程'
  }else if(action.value==='edit'){
    return "编辑课程"
  }else if(action.value==='addstu'){
    return "添加选课信息"
  }
}
//AM_fun
const getAMInfo=async()=>{
    try{
      const data=await proxy.$api.getAMInfo();
      AMInfo.value=data.map(item=>({
        ...item,
      }));
      console.log("AMInfo",AMInfo.value);
    }catch(error){
      ElMessage.error("获取专业失败：" + error.message);
    }
}
// 搜索课程信息_fun
const handleCourseSearch=()=>{
  const config=reactive({
      page:1,
      limit:10,
      keyWord:searchForm.keyWord,
      TeacherId:id.value
  })
  getCourseInfoData(config);
};
//初始化学年/周数信息_fun
const initYearWeekDate=()=>{
  const currentYear = new Date().getFullYear(); // 获取当前年份
  for(let i=-5;i<5;i++){
    let syear=currentYear+i;
    let eyear=syear+1;
    yearlist.value.push({
      label:syear+"-"+eyear,
      value:syear+"-"+eyear,
    })
  }
  //week
  for(let i=1;i<30;i++){
    weeklist.value.push({
      label:""+i,
      value:i,
    })
  }
  // datelist
  for (let i = 1; i <= 7; i++) {
    const weekdate = date2[i - 1]; // 注意这里是 i-1，因为 date2 是从 0 开始的
    const children = [];
    for (let k = 0; k < 5; k++) {
      children.push({
        label: `${weekdate} ${2 * k + 1}-${2 * k + 2}节`,
        value: `${weekdate} ${2 * k + 1}-${2 * k + 2}节`,
      });
    }
    datelist.value.push({
      label: weekdate,
      value: weekdate,
      children: children,
    });
  }

} 
//获取教师信息_fun
const querryTeacherData=async(config)=>{
  try{
    console.log("querryTeacherData被调用",config);
    const data=await proxy.$api.querryTeacher(config);
    teachers.value=data.map(item=>({
      label:item.teacherid+"-"+item.teachername,
      value:item.teacherid,
    }))
    console.log(teachers)
  }catch(error){
    console.error("Error fetching classes data:", error);
    ElMessage.error("获取教师数据失败：" + error.message);
  }
}
//清除课程时间表——fun
const clearCourseTimeForm=()=>{
  CourseTimeForm.sweek=null
  CourseTimeForm.eweek=null
  CourseTimeForm.date=null
}
//添加课程时间_fun
const addCourseTime=()=>{
  console.log("clicked")
  proxy.$refs["timeform"].validate((valid) => {
    if (valid) {
      console.log('验证成功')
      if(CourseTimeForm.sweek<=CourseTimeForm.eweek){
         if(CourseTimeForm.date){
            const item=CourseTimeForm.sweek+"-"+CourseTimeForm.eweek+"周"+"("+CourseTimeForm.date+")";
            coursetimelist.value.push(item)
            console.log("coursetimelist",coursetimelist)
            clearCourseTimeForm();
         }else{
          ElMessage({
          showClose: true,
          message: "请输入每周安排",
          type: "error"
        });
         }
      }else{
        ElMessage({
          showClose: true,
          message: "结束周应大于等于开始周",
          type: "error"
        });
      }  
    }
  });
}
//删除课程时间_fun
const deleteTag=(val)=>{
    const index=coursetimelist.value.indexOf(val);
    if(index!=-1){
      coursetimelist.value.splice(index,1);
    }
    console.log("coursetimelist",coursetimelist)
}
 //新增课程_btn
 const addCourseInfo=()=>{
   action.value="add";
   nextTick(()=>{
      CourseInfoForm.CourseTeacher=undefined;
      CourseInfoForm.CourseRoom=undefined;
    })
   dialogVisible.value=true;

   teachers.value=[];
   coursetimelist.value=[]
 }

//选课_btn
const selectCourseInfo=(val)=>{
      action.value='addStu'
      dialogVisible.value=true
      coursetimelist.value=val.CourseTime.split(";")
      console.log(val)
      addStuForm.StudenId=""
      addStuForm.StudentName=""
      nextTick(()=>{
        Object.assign(CourseInfoForm,{...val,CourseTeacher:val.CourseTeacherName+'-'+val.CourseTeacherId})
 
      })
}
//删除课程信息_btn
const deleteCourseInfo=(val)=>{
  ElMessageBox.confirm("确定删除该课程吗？").then(
    async ()=>{
      await proxy.$api.deleteCourseInfo({CourseId:val.CourseId});
      ElMessage({
          showClose:true,
          message:"删除成功",
          type:'success',
        }
      )
      let delay=500;
      setTimeout(() => {
        const config={
        page:mypage.value,
        limit:10,
        keyWord:searchForm.keyWord,
        TeacherId:id.value,
      }
      getCourseInfoData(config);
      }, delay);
    
    }
  )
}

//关闭
const FormCancel=()=>{
  //获取到表单dom，执行resetFields重置表单
  proxy.$refs["useForm"].resetFields()
  //关闭对话框
  dialogVisible.value=false
}

//提交表单
const onSubmit=()=>{
  if(action.value!=='addStu'){
    proxy.$refs['useForm'].validate(
      async(vaild)=>{
        if(vaild&&coursetimelist.value.length!==0){
          let res=null;
          console.log(CourseInfoForm.CourseYear)
          if(action.value==='add'){          
            const timetemp=coursetimelist.value.join(";")
            console.log("coursetime",timetemp)
            CourseInfoForm.CourseTime=timetemp;
          }
          const parts=CourseInfoForm.CourseTeacher.split("-",1);
          CourseInfoForm.CourseTeacherName=parts[0];
          CourseInfoForm.CourseTeacherId=parts[1];
          CourseInfoForm.CourseValue=parseFloat(CourseInfoForm.CourseValue)
          if(action.value==='add'){
              console.log("add",CourseInfoForm);   
              res=await proxy.$api.addCourseInfo(CourseInfoForm)
            }
          else if(action.value==='edit'){
            if(initcnt==1) {
              CourseInfoForm.CourseNum=parseInt(CourseInfoForm.CourseNum);
              CourseInfoForm.CourseValue=parseFloat(CourseInfoForm.CourseValue)
            }
            console.log('edit',CourseInfoForm)
            res=await proxy.$api.editCourseInfo(CourseInfoForm)
          }
          if(res){
            const config={
              page:mypage.value,
              limit:10,
              keyWord:searchForm.keyWord,
              TeacherId:id.value,
            }
            dialogVisible.value=false;
            proxy.$refs['useForm'].resetFields();
            getCourseInfoData(config)

          }
        }else{
            ElMessage({
              showClose:true,
              message:"请输入正确的内容",
              type:"error",
            })
        }
      }
    )
  }else{
    proxy.$refs['addstuform'].validate(
      async(valid)=>{
        if(valid){
          let res=null;
          addStuForm.CourseId=CourseInfoForm.CourseId;
          if(action.value==='addStu'){
            console.log('addStu',addStuForm)
            res=await proxy.$api.addSelectCourse(addStuForm);
          }
          if(res){
            dialogVisible.value=false;
            proxy.$refs['addstuform'].resetFields();
            ElMessage({
              showClose:true,
              message:res,
              type:"info"
            })
          }

        }else{
          ElMessage({
              showClose:true,
              message:"添加选课信息失败",
              type:"error"
            })

        }
      }
    )
  }
}

//样式_fun
const headerStyle = computed(() => {
    if (store.state.role === '1') {
        return {
            display: 'flex',
            justifyContent: 'space-between'
        };
    } else {
        return {
            display: 'flex',
            justifyContent: 'flex-end'
        };
    }
});

let initcnt=0;
//编辑表单_btn
const editCourseInfo=(val)=>{
    action.value='edit';
    initcnt=0;
    dialogVisible.value=true;
    coursetimelist.value=val.CourseTime.split(";")
    console.log(val)
    nextTick(()=>{
      Object.assign(CourseInfoForm,{...val,CourseTeacher:val.CourseTeacherName+'-'+val.CourseTeacherId})
    })
}
//监听Academy
watch(()=>{return CourseInfoForm.CourseAcademy},(newValue)=>{
      if(newValue==="") {
        teachers.value=[];
      }
      initcnt+=1;
      console.log("watch"+initcnt,CourseInfoForm.CourseAcademy)
      CourseInfoForm.CourseRoom=CourseInfoForm.classroomid;
      CourseInfoForm.CourseTeacher=CourseInfoForm.teacherid;
    if(CourseInfoForm.CourseAcademy!==''&&CourseInfoForm.CourseAcademy!=undefined){    
      if(initcnt!=1)CourseInfoForm.CourseTeacher=""  
      const getTeacherListConfig=reactive({
          Academy:CourseInfoForm.CourseAcademy,
        });
      querryTeacherData(getTeacherListConfig);
  }
})
onMounted(()=>{

  if(store.state.role=="2"){
    id.value=store.state.username
  }
  const config={
    page:mypage.value,
    limit:10,
    TeacherId:id.value,
  }
  getCourseInfoData(config),
  initYearWeekDate(),
  getAMInfo()
})


</script>
  
<style scoped lang="less">
.courseinfo-header {
  display: flex;
  justify-content: space-between;
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