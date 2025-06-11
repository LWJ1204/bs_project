<template>
  <div class="classes-header">
    <el-button type="primary" @click="AddClick">新增</el-button>
    <el-form :inline="true" :model="searchForm">
      <el-form-item label="请输入">
        <el-input placeholder="请输入关键词" v-model="searchForm.keyWord"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleClassSearch">搜索</el-button>
      </el-form-item>
    </el-form>
  </div>

  <div class="table">
    <el-table
      :data="ClassesData"
      style="width: 100%"
    >
      <el-table-column
        v-for="item in TableLabel"
        :key="item.prop"
        :width="item.width ? item.width : 125"
        :prop="item.prop"
        :label="item.label"
      />
      <el-table-column fixed="right" label="操作" width="200">
        <template v-slot="scope">
          <el-button @click="handleEdit(scope.row)" type="primary" size="small">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
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
    :title="action == 'add' ? '新增班级' : '编辑班级'"
    width="35%"
    :before-close="handleClose"
  >
       <!--需要注意的是设置了:inline="true"，
		会对el-select的样式造成影响，我们通过给他设置一个class=select-clearn
		在css进行处理-->
    <el-form :inline="true"  :model="ClassForm" :rules="rules" ref="useForm">
      <el-row>
        <el-col :span="12">
          <el-form-item label="班级名称" prop="classname">
            <el-input v-model="ClassForm.classname" placeholder="请输入班级名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="班级人数" prop="classnum">
            <el-input v-model="ClassForm.classnum" placeholder="请输入班级人数" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item  class="select-clearn"  label="学院名称" prop="Academy">
            <el-select
                v-model="ClassForm.academy"
                placeholder="请选择学院"
                >
                <el-option
                  v-for="item in AMInfo"
                  :key="item.academy"
                  :label="item.academy"
                  :value="item.academy"
                />
              </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item  class="select-clearn" label="专业名称" prop="Major">
            <el-select
                v-model="ClassForm.major"
                placeholder="请选择专业"
                :disabled="!ClassForm.academy"
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
          <el-form-item  class="select-clearn" label="班主任" prop="Teacher">
                <el-select
                  v-model="ClassForm.teacher"
                  :options="TeacherList"
                  placeholder="请选择班主任"
                  :disabled="!ClassForm.major"
                >
                <el-option
                  v-for="item in TeacherList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              />
                </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item  class="select-clearn" label="班长" prop="Student">
                <el-select
                  v-model="ClassForm.student"
                  :options="StudentList"
                  placeholder="请选择班长"
                  :disabled="!ClassForm.major"
                >
                <el-option
                  v-for="item in StudentList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              />
                </el-select>
          </el-form-item>
        </el-col>

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

<script lang="js" setup>
import { ref, reactive, onMounted, getCurrentInstance, watch,nextTick } from "vue";
import { ElMessage,ElMessageBox} from "element-plus";
import { number } from "echarts";
const { proxy } = getCurrentInstance();

// 表头数据
const TableLabel = reactive([
  {
    prop: "classname",
    label: "班级名称",
    width: 200,
  },
  {
    prop: "major",
    label: "专业名称",
    width: 200,
  },
  {
    prop: "academy",
    label: "学院名称",
    width: 200,
  },
  {
    prop: "classnum",
    label: "班级人数",
    width: 200,
  },
  {
    prop: "teacherName",
    label: "班主任",
    width: 150,
  },
  {
    prop: "teacherPhone",
    label: "班主任联系方式",
    width: 300,
  },
  {
    prop: "monitorName",
    label: "班长",
  },
  {
    prop: "monitorPhone",
    label: "班长联系方式",
    width: 300,
  },
]);
const mypage=ref(1)
//添加学院专业教师信息
const AMInfo=ref([])
const MajorList=ref([])
const TeacherList=ref([
])
const StudentList=ref([])
// 表单
const searchForm = reactive({
      keyWord: "",
});

//总数据个数
const total=ref(10)
//每页展示数
const mylimit=ref(10)

//编辑dialog
const action=ref('add')
const dialogVisible=ref(false)
const ClassForm=reactive({
})

// 班级数据
const ClassesData = ref([]);
// 获取班级信息_fun
const getClassesData = async (config) => {
  try {
    const data = await proxy.$api.getClassesData(config);
    ClassesData.value = data.list.map(item => ({
      ...item,
    }));
    total.value= data.count;
    console.log("ClassesData", data);
  } catch (error) {
    console.error("Error fetching classes data:", error);
    ElMessage.error("获取班级数据失败：" + error.message);
  }
};
//获取教师信息_fun
const getTeacherData=async(config)=>{
  try{
    console.log("getTeacherData被调用",config);
    const data=await proxy.$api.querryTeacher(config);
    if(data!=null){
      TeacherList.value=data.map(item=>({
      label:item.teacherid+"-"+item.teachername,
      value:item.teacherid,
    }))
    }

    console.log("TeacherList",TeacherList.value)
  }catch(error){
    console.error("Error fetching classes data:", error);
    ElMessage.error("获取教师数据失败：" + error.message);
  }
}

const getStudentData=async(config)=>{
  try{
    console.log("getStudent被调用",config);
    const data=await proxy.$api.querryStudent(config);
    if(data!=null){
      StudentList.value=data.map(item=>({
      label:item.studentid+"-"+item.studentname,
      value:item.studentid,
    }))
    }
    console.log("StudentList",StudentList.value);
  }catch(error){
    console.error("Error fetching classes data:", error);
    ElMessage.error("获取学生数据失败：" + error.message);
  }
}
// 搜索班级_fun
const handleClassSearch = () => {
  const config=reactive({
      page:mypage.value,
      limit:10,
      keyWord:searchForm.keyWord
  })
  getClassesData(config);
};
// 分页_fun
const handleChange = (mypage) => {
  const config=reactive({
    keyWord:searchForm.keyWord,
    page:mypage,
    limit:mylimit
  })
  getClassesData(config);
};


//删除_fun
const handleDelete=(val)=>{
  ElMessageBox.confirm("确定删除该班级吗？").then(
    async ()=>{
      await proxy.$api.deleteClass({id:val.classid});
      const delay = 500; // 延迟时间，单位为毫秒（这里设置为1秒）
      setTimeout(() => {
        const config = {
          page: mypage.value,
          limit: 10,
          keyWord: searchForm.keyWord
        };
        getClassesData(config); // 刷新数据
      }, delay);
      ElMessage({
          showClose:true,
          message:"删除成功",
          type:'success',
        }
      )
    }
  )

}
//AM_fun
const getAMInfo=async()=>{
    try{
      const data=await proxy.$api.getAMInfo();
      console.log("AMdata",data)
      AMInfo.value=data.map(item=>({
        ...item
      }));
    }catch(error){
      ElMessage.error("获取专业失败：" + error.message);
    }
}
//提交表单
const onSubmit=()=>{
  proxy.$refs['useForm'].validate(
    async(vaild)=>{
      if(vaild){
        let res=null;
        console.log("ClassForm subbefor",ClassForm)
        console.log("initmajor",initmajor)
        if(initmajor<=1&&initteacher<=1) ClassForm.major=ClassForm.amid
        if(action.value==='add'){
            ClassForm.teacherid=ClassForm.teacher==null?null:ClassForm.teacher.substring(0, 4);
            ClassForm.studentid=ClassForm.student==null?null:ClassForm.student.substring(0, 8);
            ClassForm.classnum=parseInt(ClassForm.classnum);
            console.log("add",ClassForm);   
            res=await proxy.$api.addClass(ClassForm)
          }
        else{

            console.log("338 initteacher",initteacher)
            ClassForm.teacherid=ClassForm.teacher==null?null:ClassForm.teacher.substring(0, 4);
            ClassForm.studentid=ClassForm.student==null?null:ClassForm.student.substring(0, 8);
            ClassForm.classnum=parseInt(ClassForm.classnum);
            console.log("edit",ClassForm)
            res=await proxy.$api.editClass(ClassForm)
            console.log("edit",res)
        }
        if(res){
          ElMessage({
            showClose:true,
            message:res,
            type:"info",
          })
          const config={
            page:mypage.value,
            limit:10,
            keyWord:searchForm.keyWord
          }
          dialogVisible.value=false;
          proxy.$refs['useForm'].resetFields();
          getClassesData(config)
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

const rules=reactive({
  "classname":[{required:true,meeage:"请输入班级名称",trigger:'blur'}],
  "major":[{required:true,meeage:"请输入班级所属专业",trigger:'blur'}],   
  "academy":[{required:true,meeage:"请输入班级所属学院",trigger:'blur'}],
  "classnum":[{required:true,meeage:"请输入班级人数",trigger:'blur'},
    {type:number, pattern: /^\d+$/,message: '班级人数必须是数字', trigger: 'blur'},
  ],
  "teacher":[{required:false,meeage:"请输入教师姓名",trigger:'blur'}],
  "monitor":[{required:false,meeage:"请输入班长姓名",trigger:'blur'}],
})
//添加
const AddClick=()=>{
  action.value="add";
  ClassForm.academy=""
  ClassForm.teacher=""
  ClassForm.classnum=""
  ClassForm.classid=undefined;
  ClassForm.major=undefined;
  nextTick(() => {
    proxy.$refs["useForm"].resetFields();
    ClassForm.teacher=null;
    ClassForm.studentid=null;
  });
  dialogVisible.value=true;

}
//取消
const handleClose=()=>{
  dialogVisible.value=false;
  //获取到表单dom，执行resetFields重置表单
  nextTick(() => {
    proxy.$refs["useForm"].resetFields();
    ClassForm.academy=undefined
    ClassForm.teacher=undefined
    ClassForm.classnum=undefined
  });

}
let initmajor=0;
let initteacher=0;

const getstuname = (id, name) => {
    if (id == null || id == undefined || id == "") {
        console.log("id is null");
        return null;
    }
    if (name == null || name == undefined || name == "") {
        console.log("name is null");
        return null;
    }
    return id + "-" + name;
};
//编辑
const handleEdit=(val)=>{
    action.value="edit";
    dialogVisible.value=true;
    initmajor=0;
    initteacher=0;
    console.log("test",val)
    nextTick(() => { 
        Object.assign(ClassForm, {
          teacher:getstuname(val.teacherid,val.teacherName),
          student:getstuname(val.studentid,val.monitorName),
          major:val.major,
            ...val,
          });   
    });
    console.log("ClassForm",ClassForm)
}


//监听Academy
watch(()=>{return ClassForm.academy},(newValue,oldValue)=>{

  initmajor=initmajor+1;
  console.log("academy change"+initmajor,newValue,oldValue)
  console.log("academy change ClassForm"+initmajor,ClassForm)
  TeacherList.value=[]
   const selectAcademy=AMInfo.value.find((academy)=>academy.academy===newValue);
    if(selectAcademy){
      MajorList.value=selectAcademy.children;
    }
    if(initmajor!=1&&oldValue!=undefined){
      ClassForm.major=""
      ClassForm.teacher=""
      ClassForm.student=""
    }
    return;
})
watch(()=>{return ClassForm.major},(newValue,oldValue)=>{
  initteacher+=1;
  console.log("461 initteacher",initteacher);
  TeacherList.value=[]
  StudentList.value=[]
  console.log("major change"+initteacher,newValue,oldValue);
  console.log("major change ClassForm"+initteacher,ClassForm)
  if(initteacher>=1&&newValue!=undefined){
    if(initteacher==1) newValue=ClassForm.amid;
    const config={AmId:newValue};
    getTeacherData(config)
    const sconfig={classid:ClassForm.classid}
    getStudentData(sconfig)
  }
  if(initteacher>1&&oldValue!=undefined){
       ClassForm.student=""
       ClassForm.teacher=""
  }
  return;
})

const initconfig={
  page:mypage.value,
  limit:10
}
// 初始化数据
onMounted(() => {
  getClassesData(initconfig); // 默认不传递参数
  getAMInfo()
});
</script>

<style scoped lang="less">
.classes-header {
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