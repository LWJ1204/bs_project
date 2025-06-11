<template>
    <div class="students-header">
      <el-button type="primary" @click="addStudent">新增</el-button>
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="请输入">
          <el-input placeholder="请输入关键字" v-model="searchForm.keyWord"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleStudentSearch">搜索</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="table">
        <el-table
            :data="StudentsData"
            style="width: 100%"
        >
          <el-table-column
              v-for="item in TableLabel"
              :key="item.prop"
              :width="item.width? item.width:125"
              :prop="item.prop"
              :label="item.label"
          />
          <el-table-column fixed="right" label="操作" width="200">
              <template v-slot="scope">
                <el-button @click="editStudent(scope.row)" type="primary" size="small">编辑</el-button>
                <el-button @click="deleteStudent(scope.row)" type="danger" size="small">删除</el-button>
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
        :title="action === 'add' ? '新增学生' : '编辑学生'"
        width="35%"
        :before-close="FormCancel"
  >
        <!--需要注意的是设置了:inline="true"，
      会对el-select的样式造成影响，我们通过给他设置一个class=select-clearn
      在css进行处理-->
      <el-form :inline="true"  :model="StudentForm" :rules="rules" ref="useForm">
        <el-row>
          <el-col :span="12">
            <el-form-item label="学生姓名" prop="StudentName">
              <el-input v-model="StudentForm.StudentName" placeholder="请输入学生姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
              <el-form-item label="学号" prop="StudentId">
              <el-input v-model="StudentForm.StudentId" placeholder="请输入学生学号" :disabled="action==='edit'"/>
            </el-form-item>
          </el-col>
        </el-row>
      
        <el-row>
        <el-col :span="12">
          <el-form-item class="select-clearn" label="性别" prop="StudentSexLabel">
            <el-select  v-model="StudentForm.StudentSex" placeholder="请选择">
              <el-option label="男" value="1" />
              <el-option label="女" value="0" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="出生日期" prop="StudentBirth">
            <el-date-picker
              ref="dataPicker"
              v-model="StudentForm.StudentBirth"
              type="date"
              placeholder="请输入"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item  class="select-clearn"  label="学院名称" prop="StudentAcademy">
            <el-select v-model="StudentForm.academy" placeholder="请选择学院">
              <el-option v-for="item in AMInfo" :key="item.academy" :label="item.academy" :value="item.academy"/>
              </el-select>  
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item  class="select-clearn" label="专业名称" prop="StudentMajor">
            <el-select
                v-model="StudentForm.major"
                placeholder="请选择专业"
                :disabled="!StudentForm.academy"
              >
              <el-option
                v-for="item in MajorList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              />
              </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item  class="select-clearn" label="班级名称" prop="StudentClass">
            <el-select
                v-model="StudentForm.StudentClass"
                placeholder="请选择班级"
                :disabled="!StudentForm.major"
              >
              <el-option
                v-for="item in ClassList"
                  :key="item.classid"
                  :label="item.classname"
                  :value="item.classid"
              />
              </el-select>
          </el-form-item>
        </el-col>
          <el-col :span="12">
            <el-form-item label="学生联系方式" prop="StudentPhone">
              <el-input v-model="StudentForm.StudentPhone" placeholder="请输入学生联系方式" />
            </el-form-item>
          </el-col>
        </el-row>
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
import {ref,getCurrentInstance,reactive, onMounted,watch, nextTick} from "vue"
const {proxy}=getCurrentInstance()
import { ElMessage,ElMessageBox } from "element-plus";

//学生数据
const StudentsData=ref([])
//添加学院专业教师信息
const AMInfo=ref([])
const MajorList=ref([])
const ClassList=ref([])
const searchForm=reactive({
  keyWord:'',
})
//表头
const TableLabel=reactive([
        {
            prop:"studentname",
            label:"学生姓名",
            width:120
        },
        {
            prop:"studentid",
            label:"学号",
            width:120
        },
        {
            prop:"StudentSexLabel",
            label:"性别",
        },
        {
            prop:"StudentAge",
            label:"年龄",
        },
        {
            prop:"className",
            label:"所属班级",
        },
        {
            prop:"studentphone",
            label:"联系方式",
            width:300
        },
        {
            prop:"academy",
            label:"所属学院",
            width:300
        },
        {
          
            prop:"major",
            label:"所属专业",
            width:300,
        },
])
//新增编辑数据表单
const StudentForm=reactive({})
//编辑dialog
const action=ref('add')
const dialogVisible=ref(false)
//数据总数
const total=ref(10)
//每页数据
const mylimit=ref(10)
//获取学生数据_fun
const getStudentsData=async(config)=>{
  const data=await proxy.$api.getStudentsData(config)
  console.log("接收到的数据",data)
  const currentYear = new Date().getFullYear(); // 获取当前年份
  StudentsData.value = data.list.map(item => ({
        ...item,
        StudentAge: String(currentYear - new Date(item.studentbirth).getFullYear()), // 计算年龄
        StudentSexLabel: item.studentsex == '1' ? "男" : "女"
      }));
    total.value=data.count;
}
//config
const mypage=ref(1)
const initconfig={
  page:mypage.value,
  limit:10
}
// 分页_fun
const handleChange = (mypage) => {
  const config=reactive({
    keyWord:searchForm.keyWord,
    page:mypage,
    limit:mylimit
  })
  getStudentsData(config);
};
// 搜索学生_fun
const handleStudentSearch = () => {
  const config=reactive({
    page:mypage.value,
    limit:10,
    keyWord:searchForm.keyWord
  })
    getStudentsData(config);
};    

//AM_fun
const getAMInfo=async()=>{
    try{
      const data=await proxy.$api.getAMInfo();
      AMInfo.value=data.map(item=>({
        ...item
      }));
      console.log("AMInfo",AMInfo.value);
    }catch(error){
      ElMessage.error("获取专业失败：" + error.message);
    }
}
//表格规则
const rules=reactive({
  "StudentName":[{required:true,message:"请输入学生姓名",trigger:'blur'}],
  "StudentId":[{required:true,message:"请输入学生工号",trigger:'blur'}],   
  "StudentSex":[{required:true,mesge:"性别",trigger:'blur'}],
  "StudentAge":[{required:true,message:"请输入学生年龄",trigger:'blur'},
  ],
  "StudentRoom":[{required:true,messaage:"请输入学生班级",trigger:'blur'}],
  "StudentPhone":[
    {required:true,message:"请输入学生电话",trigger:'blur'},
    { pattern: /^1[3-9]\d{9}$/,message: '请输入有效的电话号码',trigger: 'blur',}
  ],
  "StudentMajor":[{required:false,message:"请输入学生所属专业",trigger:'blur'}],
  "StudentAcademy":[{required:false,messaage:"请输入学生所属学院",trigger:'blur'}
  ]
})

//新增学生_btn
const addStudent=()=>{
    action.value="add";
    dialogVisible.value=true;
    // 重置表单
    nextTick(() => {
      StudentForm.StudentBirth=null
      StudentForm.academy=null
      StudentForm.major=null
      StudentForm.StudentSexLabel=null,
      StudentForm.StudentSex=null,
      proxy.$refs["useForm"].resetFields();
      console.log('add',StudentForm.StudnetBirth)
  });
}

//时间处理
const timeFormat=(mtime)=>{
  const time=new Date(mtime);
  const year=time.getFullYear();
  const month=time.getMonth()+1;
  const date=time.getDate();
  function addd0(m){
    return m<10?"0"+m:m
  }
  return year+"-"+addd0(month)+"-"+addd0(date);
}
//表单提交
const onSubmit=()=>{
  proxy.$refs['useForm'].validate(
    async(vaild)=>{
      if(vaild){
        let res=null;
        StudentForm.StudentBirth=/^\d{4}-\d{2}-\d{2}$/.test(StudentForm.StudentBirth)?StudentForm.StudentBirth:timeFormat(StudentForm.StudentBirth)
        if(action.value==='add'){
            console.log("add",StudentForm);  
            console.log('add',StudentForm.StudentClass) 
            res=await proxy.$api.addStudent(StudentForm)
          }
        else{
            console.log("edit",StudentForm)
            if(initstu==1||initstu==0) StudentForm.StudentClass=parseInt(StudentForm.classid)
            res=await proxy.$api.editStudent(StudentForm)
            console.log("edit",res)
        }
        if(res){
          dialogVisible.value=false;
          FormCancel();
          const config={
            page:mypage.value,
            limit:10,
            keyWord:searchForm.keyWord
          }
          getStudentsData(config)
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
          })
      }
    }
  )
}

// 获取班级信息_fun
const getClassesData = async (config) => {
  try {

    const data = await proxy.$api.querryClasses(config);

    ClassList.value = data.map(item => ({
      ...item
    }));
    console.log("ClassesData", data);
  } catch (error) {
    console.error("Error fetching classes data:", error);
    ElMessage.error("获取班级数据失败：" + error.message);
  }
};


//删除学生_fun
const deleteStudent=(val)=>{
  let delay=500;
  ElMessageBox.confirm("确定删除该学生吗？").then(
    async ()=>{
      const res=await proxy.$api.deleteStudent({StudentId:val.studentid});
      ElMessage({
          showClose:true,
          message:res,
          type:'success',
        }
      )
      setTimeout(() => {
        const config = {
          page: mypage.value,
          limit: 10,
          keyWord: searchForm.keyWord
        };
        getStudentsData(config); // 刷新数据
      }, delay);
    }
  )
}

//关闭_btn
const FormCancel=()=>{
  //获取到表单dom，执行resetFields重置表单
  proxy.$refs["useForm"].resetFields()
  StudentForm.major=undefined;
  StudentForm.academy=undefined
  StudentForm.StudentClass=undefined
  //关闭对话框
  dialogVisible.value=false
}

let initmajor=0;
let initstu=0;
//编辑学生_fun
const editStudent=(val)=>{
    action.value='edit';
    dialogVisible.value=true;
    initmajor=0;
    initstu=0;
    nextTick(()=>{
      Object.assign(StudentForm,{...val,StudentName:val.studentname,StudentId:val.studentid,
        StudentBirth:val.studentbirth,
        StudentMajor:val.major,
        StudentClass:val.className,
        StudentPhone:val.studentphone,
        StudentSexLabel:val.StudentSexLabel,
        StudentClassLabel:val.className,
        StudentClass:val.className,
        StudentSex:''+val.studentsex})
    })

}

//监听Academy
watch(()=>{return StudentForm.academy},(newValue,oldValue)=>{
  console.log("edit nacademy"+initmajor,newValue)
  console.log("edit oacademy"+initmajor,oldValue)
  initmajor+=1;
  const selectAcademy=AMInfo.value.find((academy)=>academy.academy===newValue);
    if(selectAcademy){
      MajorList.value=selectAcademy.children;
    }
    console.log("edit major",MajorList)
   if(initmajor>=1&&oldValue!=undefined){
       StudentForm.major=""
      StudentForm.StudentClass=""

   }
   return;

})

watch(()=>{return StudentForm.major},(newValue,oldValue)=>{
  initstu+=1;
  ClassList.value=[]
  if(newValue!=undefined&&initstu!=1){
    const config={
      AmId:newValue
    }
    getClassesData(config)
    if(initstu>=2)StudentForm.StudentClass=""
  }else{
    const config={
      AmId:StudentForm.amid
    }
    getClassesData(config)
  }
})
//挂载
onMounted(()=>{
    
    getStudentsData(initconfig),
    getAMInfo()
})

</script>
  
  <style scoped lang="less">
  .students-header {
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