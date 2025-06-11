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

let RecordList = []
let StateLabel=["已签到","请假","未签到"]
const count = 200
let RecordId=null
//模拟200条用户数据
for (let i = 0; i < count; i++) {
  RecordList.push(
    Mock.mock({
       RecordId:Mock.Random.guid(),
       CourseName:"高等数学"+Mock.Random.integer(1,300),//课程名称
       CourseId:Mock.Random.guid(),//课程号
       CourseTeacher:Mock.Random.cname(),//授课教师
       CourseRoom:"博三A"+Mock.Random.integer(101,404),//上课教室
       CourseStudentName:Mock.Random.cname(),//选课学生
       CourseStudentId:Mock.Random.guid(),
       RecordState:Mock.Random.integer(0,2),
       RecordSetTime:Mock.Random.date() + ' ' + Mock.Random.time(),
       RecordEndTime:Mock.Random.date() + ' ' + Mock.Random.time(),
    })
  )
}
let newList=[]
//新增50条数据
const new50=()=>{
  RecordId=Mock.Random.guid()
  let Settime=Mock.Random.date() + ' ' + Mock.Random.time()
  for(let i=0;i<50;i++)
  { newList.push(
    Mock.mock({
       RecordId:RecordId,
       CourseName:"高等数学"+Mock.Random.integer(1,300),//课程名称
       CourseId:Mock.Random.guid(),//课程号
       CourseTeacher:Mock.Random.cname(),//授课教师
       CourseRoom:"博三A"+Mock.Random.integer(101,404),//上课教室
       CourseStudentName:Mock.Random.cname(),//选课学生
       RecordState:1,
       RecordSetTime:Settime,
       CourseStudentId:Mock.Random.guid(),
       RecordEndTime:null,
    })
  )}
}

export default {
  getRecordList: config => {
    if (!config || !config.url) {
      return {
        code: 400,
        message: 'Invalid request'
      }
    }
    console.log("searchRecord 捕获住了")
    const { keyWord, page = 1, limit = 10 } = param2Obj(config.url)

    // 筛选数据
    const mockList = RecordList.filter(item => {
      if (keyWord) {
        // 检查多个字段是否包含关键词
        return (
          item.CourseName.indexOf(keyWord) !== -1 ||
          item.CourseId.indexOf(keyWord) !== -1 ||
          item.CourseRoom.indexOf(keyWord) !== -1 ||
          item.CourseStudentName.indexOf(keyWord) !== -1 ||
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
  //修改考勤状态
  editRecord:config=>{
    const {RecordId,CourseStudentId,RecordState}=JSON.parse(config.body);
    RecordList.some(u=>{
      if(u.RecordId===RecordId&&u.CourseStudentId===CourseStudentId){
        u.RecordState=parseInt(RecordState)
      }
    })
    return {
      code:200,
      data:{
        message:"修改成功",
      }
    }
  },
  //添加考勤要求
  createRecord:config=>{
    const {}=JSON.parse(config.body)
    new50();
    return {
      code:200,
      data:{
         RecordId:RecordId,
         list:[[],newList,[]],
      }
    }
  }
}
