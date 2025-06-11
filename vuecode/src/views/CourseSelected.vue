<template>
    <div class="CourseSelectHeader">
      <el-form :inline="true" :modle="searchForm">
        <el-form-item label="请输入">
          <el-input placeholder="请输入关键字" v-model="searchForm.keyWord"></el-input>
        </el-form-item>
        <el-form-item>
            <el-button @click="SelectCourseSearch" type="primary">搜索</el-button>
        </el-form-item>
      </el-form>
    </div>
  
    <div class="table">
      <el-table
        :data="courseSelectedData"
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
          <template v-slot="scope" v-if="item.prop === 'CourseStateLabel'">
            <div :style="{ color: getStateColor(scope.row.CourseState) }">
              {{ scope.row.CourseStateLabel }}
            </div>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="200">
          <template v-slot="scope">
            <el-button @click="agreeSelect(scope.row)" type="primary" size="small">修改</el-button>
            <el-button @click="deleteSelect(scope.row)" type="danger" size="small">删除</el-button>
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
 import { ElMessage,ElMessageBox, linkEmits } from 'element-plus';
import { ref, getCurrentInstance, reactive, onMounted } from 'vue';
 
 const { proxy } = getCurrentInstance();
 //选课数据
 const courseSelectedData = ref([]);
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
      prop: "CourseStudent",
      label: "学生姓名",
    },
    {
      prop: "CourseStateLabel",
      label: "选课状态",
      width: 200,
    },
    {
      prop: "CourseAcademy",
      label: "开课学院",
      width: 300,
    },
 ]);
//搜索表单
const searchForm=reactive({
  keyWord:''
})

const mypage=ref(1)
 //每页展示数据数目
 const mylimit=ref(10)
 //数据总数
 const total=ref(20)
 // 根据选课状态返回颜色_fun
 const getStateColor = (state) => {
    switch (state) {
      case 1:
        return "green"; // 已选状态显示为绿色
      case 0:
        return "red"; // 未选状态显示为红色
      default:
        return "black"; // 默认颜色为黑色
    }
 };

//获取选课信息_fun
const getCourseSelectedData = async (config) => {
  try {
    const data = await proxy.$api.getCourseSelectedData(config);
     courseSelectedData.value = data.list.map((item) => ({
      ...item,
      CourseName:item.courseName,
      CourseId:item.courseid,
      CourseRoom:item.CourseRoom,
      CourseTeacher:item.teacherName,
      CourseStudent:item.studentName,
      CourseAcademy:item.academy,
      CourseRoom:item.courseRoom,
      CourseState:parseInt(item.coursestate),
      CourseStateLabel:item.coursestate =='1'?"同意":"待处理",
    }));
    total.value=data.count
  } catch (error) {
    console.error("Error fetching course info data:", error);
  }
  
};

// 分页_fun
const handleChange = (mypage) => {
  const config=reactive({
    keyWord:searchForm.keyWord,
    page:mypage,
    limit:mylimit
  })
  getCourseSelectedData(config);
};
//关键词搜索_fun
const SelectCourseSearch=()=>{
  const config=reactive({
      page:mypage.value,
      limit:10,
      keyWord:searchForm.keyWord
  })
  getCourseSelectedData(config);
}

//同意选课_fun
const agreeSelect=async (val)=>{
    const state=1-parseInt(val.CourseState);
    console.log('修改后的状态',state)
    let res=null;
    res=await proxy.$api.editSelectCourse({CourseId:val.CourseId,StudentId:val.studentid,CourseState:state});
    if(res){
      let delay=100
      setTimeout(() => {
        const config = {
          page: mypage.value,
          limit: 10,
          keyWord: searchForm.keyWord
        };
        getCourseSelectedData(config); // 刷新数据
      }, delay);

      ElMessage({
              showClose:true,
              message:res,
              type:"info",
            })
    }else{
      ElMessage({
        showClose:true,
        message:"修改失败",
        type:'erro'
      })
    }
}

//删除选课_fun
const deleteSelect=async (val)=>{
  ElMessageBox.confirm("确定删除该申请吗？").then(
    async ()=>{
      await proxy.$api.deleteSelectCourse({CourseId:val.courseid,StudentId:val.studentid});
      ElMessage({
          showClose:true,
          message:"删除成功",
          type:'success',
        }
      )
      const config={
        page:1,
        limit:10,
        keyWord:searchForm.keyWord
      }
      getCourseSelectedData(config);
    }
  )
}

onMounted(() => {
  const config={
    page:1,
    limit:10,
  }
  getCourseSelectedData(config);
});
</script>
  
<style scoped lang="less">
.CourseSelectHeader {
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