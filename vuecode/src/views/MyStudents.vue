<template>
    <div class="MyStudentsHeader">
      <el-form :inline="true" :modle="searchForm">
        <el-form-item label="请输入">
          <el-input placeholder="请输入关键字" v-model="searchForm.keyWord"></el-input>
        </el-form-item>
        <el-form-item>
            <el-button @click="MystudentsSearch" type="primary">搜索</el-button>
        </el-form-item>
      </el-form>
    </div>
  
    <div class="table">
      <el-table
        :data="MyStudents"
        style="width: 100%"
      >
        <el-table-column
          v-for="item in TableData"
          :key="item.prop"
          :width="auto"
          :prop="item.prop"
          :label="item.label"
        >
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
import { ref,onMounted, reactive,getCurrentInstance } from 'vue';
const {proxy}=getCurrentInstance()
import { ElMessage,ElMessageBox } from "element-plus";
import {useAllDataStore} from "@/stores"

const store=useAllDataStore();
const id=store.state.username;
console.log("testid",id)
const mypage=ref(1)
//搜索表单
const searchForm=reactive({
    keyWord:''
})
//表格表头
const TableData=reactive([
    {
        prop:"CourseId",
        label:"课程号"
    },
    {
        prop:'CourseName',
        label:'课程名称'
    },
    {
        prop:"StudentName",
        label:'学生姓名',
    },
    {
        prop:"StudentId",
        label:"学生学号"
    },
    {
        prop:"StudentClass",
        label:"所属班级"
    },
    {
        prop:'StudentPhone',
        label:"联系方式"
    }
])
//学生数据
const MyStudents=ref([])
//学生数据总数
const total=ref(10)
//获取学生数据_fun
const getStudentsData=async(config)=>{
  const data=await proxy.$api.getMyStudentsData(config)
  console.log("接收到的数据",data)
  const currentYear = new Date().getFullYear(); // 获取当前年份
  MyStudents.value = data.list.map(item => ({
        ...item,
        StudentId:item.studentid,
        StudentName:item.studentname,
        StudentPhone:item.studentphone,
        CourseName:item.coursename,
        CourseId:item.courseid,
        StudentClass:item.className,
        StudentAge: String(currentYear - new Date(item.studentbirth).getFullYear()), // 计算年龄
        StudentSexLabel: item.studentsex == '1' ? "男" : "女"
      }));
    total.value=data.count;
}
//查询学生信息
const MystudentsSearch=()=>{
    let config={
      page:1,
      limit:10,
      keyWord:searchForm.keyWord,
      TeacherId:id,
    }
    getStudentsData(config)
}
const handleChange = (mypage) => {
  const config={
    page:mypage,
    keyWord:searchForm.keyWord,
    limit:10,
    TeacherId:id,
  }
  getStudentsData(config);
};
onMounted(()=>{
    let config={
      page:1,
      limit:10,
      TeacherId:id
    }
    getStudentsData(config)
})


</script>


<style>
.MyStudentsHeader {
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