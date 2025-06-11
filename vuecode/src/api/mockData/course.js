import Mock from 'mockjs'

// get请求从config.url获取参数，post从config.body中获取参数
function param2Obj(url) {
  const search = url.split('?')[1]
  if (!search) {
    return {}
  }
  return JSON.parse(
    '{"' +
    decodeURIComponent(search)
      .replace(/"/g, '\\"')
      .replace(/&/g, '","')
      .replace(/=/g, '":"') +
    '"}'
  )
}

let CourseInfoList = []
let CourseSelectedList=[]
const count = 200
//模拟200条用户数据
for (let i = 0; i < count; i++) {
  CourseInfoList.push(
    Mock.mock({
        CourseName:"高等数学"+Mock.Random.integer(1,300),//课程名称
        CourseId:Mock.Random.guid(),//课程号
        CourseTeacherName:Mock.Random.cname(),//授课教师
        CourseTeacherId:Mock.Random.guid(),//教师工号
        CourseRoom:"博三A"+Mock.Random.integer(101,404),//上课教室
        CourseValue:Mock.Random.float(0,3),//学分
        CourseFinish:"考察",//结课形式
        CourseNum:Mock.Random.integer(40,100),//开课人数
        CourseSelectedNum:Mock.Random.integer(10,30),
        CourseAcademy:"学院"+Mock.Random.integer(0,30),
        CourseTime:"11-14周（周二，3-4节；周三，5-6节）;15-16周（周二，3-4节；周三，5-6节）",//上课时间
        CourseYear:Mock.Random.integer(2018,2029)+"-"+Mock.Random.integer(2018,2029),
        CourseSemester:Mock.Random.integer(1,2),
      })
  ),
  CourseSelectedList.push(
    Mock.mock({
        CourseName:"高等数学"+Mock.Random.integer(1,300),//课程名称
        CourseId:Mock.Random.guid(),//课程号
        CourseTeacher:Mock.Random.cname(),//授课教师
        CourseRoom:"博三A"+Mock.Random.integer(101,404),//上课教室
        CourseStudent:Mock.Random.cname(),//选课学生
        CourseStudentId:Mock.Random.guid(),
        CourseState:Mock.Random.integer(0,1),
        CourseAcademy:Mock.Random.cname(),
    })
  )
}

export default {
  //获取课程信息
  getCourseInfoList: config => {
    if (!config || !config.url) {
      return {
        code: 400,
        message: 'Invalid request'
      }
    }
    const { keyWord, page = 1, limit = 10 } = param2Obj(config.url)
    console.log("course searchInline")
    // 筛选数据
    const mockList = CourseInfoList.filter(item => {
      if (keyWord) {
        // 检查多个字段是否包含关键词
        return (
          item.CourseName.indexOf(keyWord) !== -1 ||
          item.CourseAcademy.indexOf(keyWord) !== -1 ||
          item.CourseFinish.indexOf(keyWord) !== -1 ||
          item.CourseRoom.indexOf(keyWord) !== -1 ||
          item.CourseFinish.indexOf(keyWord)!==-1||
          item.CourseTeacher.indexOf(keyWord)!==-1
        )
      }
      return true
    })

    // 分页处理
    const start = (page - 1) * limit
    const end = page * limit
    const pageList = mockList.slice(start, end)
    console.log("分页数据",pageList)
    return {
      code: 200,
      data: {
        list: pageList,
        count: mockList.length
      }
    }
  },
  //添加课程信息
  addCourseInfo:config=>{
    const {CourseName,CourseId,CourseTeacherName,CourseTeacherId,CourseRoom,CourseValue,CourseFinish,CourseNum,CourseAcademy,CourseTime,CourseYear,CourseSemester}=JSON.parse(config.body);
    console.log("addCourseinline")
    CourseInfoList.unshift({
     CourseName:CourseName,
     CourseId:CourseId,
     CourseTeacherName:CourseTeacherName,
     CourseTeacherId:CourseTeacherId,
     CourseRoom:CourseRoom,
     CourseValue:parseFloat(CourseValue),
     CourseFinish:CourseFinish,
     CourseNum:parseInt(CourseNum),
     CourseSelectedNum:0,
     CourseAcademy:CourseAcademy,
     CourseTime:CourseTime,
     CourseYear:CourseYear,
     CourseSemester:parseInt(CourseSemester),
    });
    return {
      code:200,
      data:{
        message:"添加成功",
      }
    }
  },
  //编辑课程信息
  editCourseInfo:config=>{
    const {CourseName,CourseId,CourseTeacherName,CourseTeacherId,CourseRoom,CourseValue,CourseFinish,CourseNum,CourseAcademy,CourseTime,CourseYear,CourseSemester}=JSON.parse(config.body);
    CourseInfoList.some(u => {
      if (u.CourseId == CourseId) {
        u.CourseName=CourseName,
        u.CourseTeacherName=CourseTeacherName,
        u.CourseTeacherId=CourseTeacherId,
        u.CourseRoom=CourseRoom,
        u.CourseValue=CourseValue,
        u.CourseFinish=CourseFinish,
        u.CourseNum=CourseNum,
        u.CourseAcademy=CourseAcademy,
        u.CourseTime=CourseTime,
        u.CourseYear=CourseYear,
        u.CourseSemester=CourseSemester
        return true
      }
    })
    return {
      code: 200,
      data: {
        message: '编辑成功'
      }
    }
  },
  //删除课程信息
  deleteCourseInfo:config=>{
    const {CourseId}=JSON.parse(config.body)
    if(!CourseId){
      return {
        code:-99,
        data:{
          message:"不存在该用户",
        }
      }
    }else{
      CourseInfoList=CourseInfoList.filter(u=>u.CourseId!=CourseId)
      return {
        code:200,
        data:{
          message:"删除成功"
        }
      }
    }
  },
  //添加选课信息
  addSelectCourse:config=>{
    const {StudentName,StudentId,CourseId}=JSON.parse(config.body);
    const index=CourseInfoList.findIndex(item=>item.CourseId==CourseId)
    if(index==-1){
      return {
        code:-99,
        data:{
          message:"不存在该课程",
        }
      }
    }else{
      console.log('添加前',CourseSelectedList);
      CourseSelectedList.unshift({
        CourseName:CourseInfoList[index].CourseName,
        CourseId:CourseInfoList[index].CoursId,
        CourseTeacher:CourseInfoList[index].CourseTeacher,
        CourseRoom:CourseInfoList[index].CourseRoom,
        CourseStudent:StudentName,
        CourseState:0,
        CourseAcademy:CourseInfoList[index].CourseAcademy,
        CourseStudentId:StudentId,
      });
      console.log('添加后',CourseSelectedList)
      return {
        code:200,
        data:{
          message:'添加成功',
        }
      }
    }

  },
  //获取选课信息
  getCourseSelectedList: config => {
    if (!config || !config.url) {
      return {
        code: 400,
        message: 'Invalid request'
      }
    }

    const { keyWord, page = 1, limit = 10 } = param2Obj(config.url)

    // 筛选数据
    const mockList = CourseSelectedList.filter(item => {
      if (keyWord) {
        // 检查多个字段是否包含关键词
        return (
          item.CourseName.indexOf(keyWord) !== -1 ||
          item.CourseAcademy.indexOf(keyWord) !== -1 ||
          item.CourseRoom.indexOf(keyWord) !== -1 ||
          item.CourseStudent.indexOf(keyWord)!==-1||
          item.CourseTeacher.indexOf(keyWord)!==-1
        )
      }
      return true
    })

    // 分页处理
    const start = (page - 1) * limit
    const end = page * limit
    const pageList = mockList.slice(start, end)
    console.log("分页数据",pageList)
    return {
      code: 200,
      data: {
        list: pageList,
        count: mockList.length
      }
    }
  },
  //修改选课状态
  editSelectCourse:config=>{
    const {CourseId,CourseState,StudentId}=JSON.parse(config.body);
    const index=CourseSelectedList.findIndex(item=>item.CourseId==CourseId&&item.CourseStudentId==StudentId);
    if(index==-1){
      return {
        code:-99,
        data:{
          message:'未找到该信息'
        }
      }
    }else{
      CourseSelectedList[index].CourseState=parseInt(CourseState);
      return {
        code:200,
        data:{
          message:'修改成功',
        }
      }
    }
  },
  //修改选课状态
  deleteSelectCourse:config=>{
    const {CourseId,StudentId}=JSON.parse(config.body);
    if(!CourseId||!StudentId){
      return {
        code:-99,
        data:{
          message:"不存在",
        }
      }
    }
    CourseSelectedList=CourseSelectedList.filter(item=>item.CourseId!=CourseId||item.CourseStudentId!=StudentId);
    return {
      code:200,
      data:{
        message:"删除成功"
      }
    }
  },
  //查询课程信息
  querryCourseInfo:config=>{
    console.log('拦截住了',config)
    const {TeacherName,TeacherId}=JSON.parse(config.body)
    const templist=CourseInfoList.filter(item=>{
        if(TeacherName&&TeacherId){
          return (item.CourseTeacherName===TeacherName&&item.CourseTeacherId===TeacherId)
        }
        return true
    })
    
    return {
      code:200,
      data:{
        list:templist,
        message:"获取成功"
      }
    }

  }
}
