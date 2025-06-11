<template>
    <div class="teachers-header">
      <el-button type="primary" @click="AddTeacher">新增</el-button>
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="请输入">
          <el-input placeholder="请输入关键字" v-model="searchForm.keyWord"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleTeacherSearch">搜索</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table">
        <el-table
            :data="TeachersData"
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
                <el-button @click="EditTeacher(scope.row)"  type="primary" size="small">编辑</el-button>
                <el-button @click="DeleteTeacher(scope.row)" type="danger" size="small">删除</el-button>
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
    :title="action === 'add' ? '新增教师' : '编辑教师'"
    width="35%"
    :before-close="FormCancel"
  >
      <el-form :inline="true"  :model="TeacherForm" :rules="rules" ref="useForm">
        <el-row>
          <el-col :span="12">
            <el-form-item label="教师姓名" prop="teachername">
              <el-input v-model="TeacherForm.teachername" placeholder="请输入教师姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教师工号" prop="teacherid">
              <el-input v-model="TeacherForm.teacherid" placeholder="请输入教师工号" :disabled="action==='edit'"/>
            </el-form-item>
          </el-col>
        </el-row>
       <el-row>
        <el-col :span="12">
          <el-form-item class="select-clearn" label="性别" prop="teacherSexLabel">
            <el-select  v-model="TeacherForm.teacherSexLabel" placeholder="请选择">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="出生日期" prop="teacherbirth">
            <el-date-picker
              ref="datePicker"
              v-model="TeacherForm.teacherbirth"
              type="date"
              placeholder="请输入"
              style="width: 100%"
              clearable="true"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
          <el-col :span="12">
            <el-form-item  class="select-clearn" label="教师职称" prop="teacherTitle">
                <el-select v-model="TeacherForm.teacherTitleLabel" placeholder="请选择">
                    <el-option label="助教" value="助教"/>
                    <el-option label="讲师" value="讲师"/>
                    <el-option label="副教授" value="副教授"/>
                    <el-option label="教授" value="教授"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="教师联系方式" prop="teacherphone">
              <el-input v-model="TeacherForm.teacherphone" placeholder="请输入教师联系方式" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
        <el-col :span="12">
          <el-form-item  class="select-clearn"  label="学院名称" prop="academy">
            <el-select v-model="TeacherForm.academy" placeholder="请选择学院">
              <el-option v-for="item in AMInfo" :key="item.academy" :label="item.academy" :value="item.academy"/>
              </el-select>  
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item  class="select-clearn" label="专业名称" prop="major">
            <el-select
                v-model="TeacherForm.major"
                placeholder="请选择专业"
                :disabled="!TeacherForm.academy"
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

//教师数据
const TeachersData=ref([])
//添加学院专业教师信息
const AMInfo=ref([])
const MajorList=ref([])
const searchForm=reactive({
  keyWord:'',
})
//表头
const TableLabel=reactive([
        {
            prop:"teachername",
            label:"教师姓名",
            width:150
        },
        {
            prop:"teacherid",
            label:"教师工号",
            width:300
        },
        {
            prop:"teacherSexLabel",
            label:"性别",
        },
        {
            prop:"teacherAge",
            label:"年龄",
        },
        {
            prop:"teacherphone",
            label:"联系方式",
            width:300
        },
        {
            prop:"major",
            label:"所属专业",
            width:300
        },
        {
            prop:"academy",
            label:"所属学院",
            width:300
        },
        {
          prop:"teacherTitleLabel",
          label:"教师职称",
          width:300
        },
])

const mypage=ref(1)
//初始congfig
const initconfig={
  page:mypage.value,
  limit:10
}
//编辑dialog
const action=ref('add')
const dialogVisible=ref(false)
//数据总数
const total=ref(10)
//每页数据
const mylimit=ref(10)
//获取教师信息_fun  
const getTeachersData=async (config)=>{
  console.log("geteacherfun",config)
  const data = await proxy.$api.getTeachersData(config);
  console.log("getteacherfun", config);
  const currentYear = new Date().getFullYear(); // 获取当前年份
  TeachersData.value = data.list.map((item) => {
    // 计算年龄
    const teacherAge = String(currentYear - new Date(item.teacherbirth).getFullYear());
    // 性别标签
    const teacherSexLabel = item.teachersex === 1 ? "男" : "女";
    // 职称标签
    let teacherTitleLabel = "";
    if (item.teachertitle == '0') {
      teacherTitleLabel = "教授";
    } else if (item.teachertitle == '1') {
      teacherTitleLabel = "副教授";
    } else if (item.teachertitle == '2') {
      teacherTitleLabel = "讲师";
    } else if (item.teachertitle == '3') {
      teacherTitleLabel = "助教";
    }
    return {
      ...item,
      teacherAge: teacherAge,
      teacherSexLabel: teacherSexLabel,
      teacherTitleLabel: teacherTitleLabel,
    };
  });
  total.value = data.count;
}
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
// 分页_fun
const handleChange = (mypage) => {
  const config=reactive({
    keyWord:searchForm.keyWord,
    page:mypage,
    limit:mylimit.value
  })
  getTeachersData(config);
};
// 搜索教师_fun
const handleTeacherSearch = () => {
  const config=reactive({
      page:mypage.value,
      limit:10,
      keyWord:searchForm.keyWord
  })
  getTeachersData(config);
};  
//教师表  
const TeacherForm=reactive({
})
//教师表规则
const rules=reactive({
    "teachername":[{required:true,message:"请输入教师姓名",trigger:'blur'}],
    "teacherid":[{required:true,message:"请输入教师工号",trigger:'blur'}],   
    "teachersex":[{required:true,mesge:"性别",trigger:'blur'}],
    "teacherage":[{required:true,message:"请输入教师年龄",trigger:'blur'},
    ],
    "teachertitle":[{required:true,messaage:"请输入教师职称",trigger:'blur'}],
    "teacherphone":[
      {required:true,message:"请输入教师电话",trigger:'blur'},
      { pattern: /^1[3-9]\d{9}$/,message: '请输入有效的电话号码',trigger: 'blur',}
    ],
    "major":[{required:false,message:"请输入教室所属专业",trigger:'blur'}],
    "academy":[{required:false,messaage:"请输入教师所属学院",trigger:'blur'}
    ]
})
//新增事件
const AddTeacher=()=>{
    action.value="add";
    dialogVisible.value=true;
    // 重置表单
    nextTick(() => {
    TeacherForm.teacherbirth=null
    TeacherForm.teacherSexLabel=null;
    TeacherForm.teacherTitleLabel=null
    TeacherForm.major=null
    proxy.$refs["useForm"].resetFields();
    
    console.log('add',TeacherForm.teacherbirth)
  });

}
//关闭
const FormCancel=()=>{
    //获取到表单dom，执行resetFields重置表单
    proxy.$refs["useForm"].resetFields()
    //关闭对话框
    dialogVisible.value=false
}
//时间处理_fun
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

const getsex=(Sexlabel)=>{
    if(Sexlabel==="男") return '1';
    if(Sexlabel==="女") return '0';
    return null;
}
const getTitle=(TitleLable)=>{
  if(TitleLable==="教授") return '0';
  if(TitleLable==="副教授") return '1';
  if(TitleLable==="讲师") return '2';
  if(TitleLable==="助教") return '3';
}

//表单提交
const onSubmit=()=>{
  proxy.$refs['useForm'].validate(
    async(vaild)=>{
      if(vaild){
        let res=null;
        TeacherForm.teacherbirth=/^\d{4}-\d{2}-\d{2}$/.test(TeacherForm.teacherbirth)?TeacherForm.teacherbirth:timeFormat(TeacherForm.teacherbirth)
        TeacherForm.teachersex=getsex(TeacherForm.teacherSexLabel);
        TeacherForm.teachertitle=getTitle(TeacherForm.teacherTitleLabel);
        if(action.value==='add'){ 
            res=await proxy.$api.addTeacher(TeacherForm)
            console.log("res",res)
            ElMessage({
              showClose:true,
              message:res,
              type:"info"
            })
          }
        else{
            console.log("edit",TeacherForm)
            TeacherForm.amid=TeacherForm.major
            res=await proxy.$api.editTeacher(TeacherForm)
            console.log("edit",res)
        }
        if(res){
          const config=reactive({
              page:mypage.value,
              limit:10,
              keyWord:searchForm.keyWord
          })
          dialogVisible.value=false;
          proxy.$refs['useForm'].resetFields();
          getTeachersData(config)
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

let initflag=true;
//编辑教师_fun
const EditTeacher=(val)=>{
    initflag=true;
    action.value='edit';
    dialogVisible.value=true;
    console.log("TeacherForm",TeacherForm)
  
    nextTick(()=>{
      Object.assign(TeacherForm,{...val})
    })
}
//删除教师_fun
const DeleteTeacher=(val)=>{
  ElMessageBox.confirm("确定删除该教师吗？").then(
    async ()=>{
      const res=await proxy.$api.deleteTeacher({TeacherId:val.teacherid});
      console.log("删除",res)
      ElMessage({
          showClose:true,
          message:"删除成功",
          type:'success',
        }
      )
      const config=reactive({
              page:mypage.value,
              limit:10,
              keyWord:searchForm.keyWord
          })
      getTeachersData(config);
    }
  )
}
//监听Academy
watch(()=>{return TeacherForm.academy},(newValue,oldValue)=>{
  const selectAcademy=AMInfo.value.find((academy)=>academy.academy===newValue);
    if(selectAcademy){
      MajorList.value=selectAcademy.children;
    }
   if(initflag===true){
      initflag=false;
      if(TeacherForm.major!=undefined) TeacherForm.major=MajorList.value.find((major)=>major.label===TeacherForm.major).value;
      return;
   }
   TeacherForm.major=""
})

onMounted(()=>{
  getTeachersData(initconfig),
  getAMInfo()
})
</script>
  
<style scoped lang="less">
.teachers-header {
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