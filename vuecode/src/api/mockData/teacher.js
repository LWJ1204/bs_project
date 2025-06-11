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

let List = []
const count = 200
//模拟200条用户数据
for (let i = 0; i < count; i++) {
  List.push(
    Mock.mock({
      // id: Mock.Random.guid(),
      // name: Mock.Random.cname(),
      // addr: Mock.mock('@county(true)'),
      // 'age|18-60': 1,
      // birth: Mock.Random.date(),
      // sex: Mock.Random.integer(0, 1)
       TeacherName:Mock.Random.cname(),
       TeacherId:Mock.Random.guid(),
       TeacherSex:Mock.Random.integer(0,1),
       TeacherBirth:Mock.Random.date(),
       TeacherTitle:Mock.Random.integer(0,3),
       TeacherMajor:"专业"+Mock.Random.integer(0,40)+"-"+Mock.Random.integer(0,20),
       TeacherPhone:String(String(Mock.Random.integer(10000000000,20000000000))),
       TeacherAcademy:"学院"+Mock.Random.integer(0,20),
    })
  )
}

export default {
  getTeachersList: config => {
    if (!config || !config.url) {
      return {
        code: 400,
        message: 'Invalid request'
      }
    }

    const { keyWord, page = 1, limit = 10 } = param2Obj(config.url)
    console.log("inline get teacherfun",keyWord)
    // 筛选数据
    const mockList = List.filter(item => {
      if (keyWord) {
        // 检查多个字段是否包含关键词
        return (
          item.TeacherName.indexOf(keyWord) !== -1 ||
          item.TeacherAcademy.indexOf(keyWord) !== -1 ||
          item.TeacherMajor.indexOf(keyWord) !== -1 ||
          item.TeacherTitle.indexOf(keyWord) !== -1  
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
  addTeacher:config=>{
    const {TeacherName,TeacherId,TeacherSex,TeacherBirth,TeacherTitle,TeacherPhone,TeacherAcademy,TeacherMajor}=JSON.parse(config.body);
    console.log("addinline",TeacherName,TeacherAcademy)
    List.unshift({
      TeacherName:TeacherName,
      TeacherId:TeacherId,
      TeacherSex,TeacherSex,
      TeacherBirth,TeacherBirth,
      TeacherTitle:TeacherTitle,
      TeacherMajor:TeacherMajor,
      TeacherPhone:TeacherPhone,
      TeacherAcademy:TeacherAcademy
    });
    return {
      code:200,
      data:{
        message:"添加成功",
      }
    }
  },
  deleteTeacher:config=>{
      const {TeacherId}=JSON.parse(config.body)
      if(!TeacherId){
        return {
          code:-99,
          data:{
            message:"不存在该用户",
          }
        }
      }else{
        List=List.filter(u=>u.TeacherId!=TeacherId)
        return {
          code:200,
          data:{
            message:"删除成功"
          }
        }
      }
  },
  editTeacher:config=>{
    const {TeacherName,TeacherId,TeacherSex,TeacherBirth,TeacherTitle,TeacherPhone,TeacherAcademy,TeacherMajor}=JSON.parse(config.body);
    const sex_num = parseInt(TeacherSex)
    const teachertitle=parseInt(TeacherTitle)
    List.some(u => {
      if (u.TeacherId == TeacherId) {
        u.TeacherName = TeacherName
        u.TeacherBirth = TeacherBirth
        u.TeacherTitle = teachertitle
        u.TeacherSex = sex_num
        u.TeacherPhone = TeacherPhone,
        u.TeacherAcademy=TeacherAcademy,
        u.TeacherMajor=TeacherMajor
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
  querryTeacher:config=>{
    console.log("拦截住了")
    if (!config || !config.url) {
      return {
        code: 400,
        message: 'Invalid request'
      }
    }
    return {
      code: 200,
      data: {
        list: List,
        count: List.length
      }
    }    
  },
  
}
