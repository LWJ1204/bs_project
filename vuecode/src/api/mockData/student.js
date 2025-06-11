import Mock, { mock } from 'mockjs'

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

let List = []
const count = 200
//模拟200条用户数据
for (let i = 0; i < count; i++) {
  List.push(
    Mock.mock({
       StudentName:Mock.Random.cname(),
       StudentId:Mock.Random.guid(),
       StudentSex:Mock.Random.integer(0,1),
       StudentBirth:Mock.Random.date(),
       StudentClass:'计算机-'+Mock.Random.integer(0,10)+"班",
       StudentPhone:String(String(Mock.Random.integer(10000000000,20000000000))),
       StudentAcademy:'学院'+Mock.Random.integer(0,20),
       StudentMajor:'专业'+Mock.Random.integer(0,20)+"-"+Mock.Random.integer(0,20),
    })
  )
}

let MyStudents=[]

for (let i = 0; i < count; i++) {
  MyStudents.push(
    Mock.mock({
       StudentName:Mock.Random.cname(),
       StudentId:Mock.Random.guid(),
       CourseName:"高等数学"+Mock.Random.integer(0,4),
       StudentClass:'计算机-'+Mock.Random.integer(0,10)+"班",
       StudentPhone:String(String(Mock.Random.integer(10000000000,20000000000))),
       CourseId:Mock.Random.guid(),
    })
  )
}

export default {

  getStudentsList: config => {
    if (!config || !config.url) {
      return {
        code: 400,
        message: 'Invalid request'
      }
    }

    const { keyWord, page = 1, limit = 10 } = param2Obj(config.url)
    console.log("searchStudentInline",keyWord)  
    // 筛选数据
    const mockList = List.filter(item => {
      if (keyWord) {
        // 检查多个字段是否包含关键词
        return (
          item.StudentName.indexOf(keyWord) !== -1 ||
          item.StudentAcademy.indexOf(keyWord) !== -1 ||
          item.StudentClass.indexOf(keyWord) !== -1 ||
          item.StudentMajor.indexOf(keyWord) !== -1 // 显式检查不等于 -1
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
  addStudent:config=>{
    const {StudentName,StudentId,StudentSex,StudentClass,StudentBirth,StudentPhone,StudentAcademy,StudentMajor}=JSON.parse(config.body);
    console.log("addinline",StudentName,StudentAcademy)
    List.unshift({
      StudentName:StudentName,
      StudentId:StudentId,
      StudentSex:parseInt(StudentSex),
      StudentClass:StudentClass,
      StudentBirth,StudentBirth,
      StudentMajor:StudentMajor,
      StudentPhone:StudentPhone,
      StudentAcademy:StudentAcademy
    });
    return {
      code:200,
      data:{
        message:"添加成功",
      }
    }
  },
  editStudent:config=>{
    const {StudentName,StudentId,StudentSex,StudentBirth,StudentClass,StudentPhone,StudentAcademy,StudentMajor}=JSON.parse(config.body);
    const sex_num = parseInt(StudentSex)
    List.some(u => {
      if (u.StudentId == StudentId) {
        u.StudentName = StudentName,
        u.StudentBirth = StudentBirth,
        u.StudentSex = sex_num,
        u.StudentClass=StudentClass,
        u.StudentPhone = StudentPhone,
        u.StudentAcademy=StudentAcademy,
        u.StudentMajor=StudentMajor
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
  deleteStudent:config=>{
    const {StudentId}=JSON.parse(config.body)
    if(!StudentId){
      return {
        code:-99,
        data:{
          message:"不存在该用户",
        }
      }
    }else{
      List=List.filter(u=>u.StudentId!=StudentId)
      return {
        code:200,
        data:{
          message:"删除成功"
        }
      }
    }
  },
  querryStudent:config=>{
    if (!config || !config.url) {
      return {
        code: 400,
        message: 'Invalid request'
      }
    }
    const {TeacherID,keyWord,page=1,limit=10}=param2Obj(config.url)
    // 筛选数据
    const mockList = MyStudents.filter(item => {
      if (keyWord) {
        // 检查多个字段是否包含关键词
        return (
          item.StudentName.indexOf(keyWord) !== -1 ||
          item.CourseName.indexOf(keyWord) !== -1 ||
          item.StudentClass.indexOf(keyWord) !== -1 ||
          item.StudentPhone.indexOf(keyWord) !== -1 // 显式检查不等于 -1
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
  }
}
