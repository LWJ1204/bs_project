
import { messageConfig } from 'element-plus'
import Mock from 'mockjs'

// get请求从config.url获取参数，post从config.body中获取参数
function param2Obj(url) {
  const search = url.split('?')[1]
  console.log("param",url)
  if (!search) {
    console.log("search",search)
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

let ClassesInfoList = []
const count = 200
//模拟200条用户数据
for (let i = 0; i < count; i++) {
  ClassesInfoList.push(
    Mock.mock({
      // id: Mock.Random.guid(),
      // name: Mock.Random.cname(),
      // addr: Mock.mock('@county(true)'),
      // 'age|18-60': 1,
      // birth: Mock.Random.date(),
      // sex: Mock.Random.integer(0, 1)
      ClassName: "计算机" + Mock.Random.integer(20, 25) + "-" + Mock.Random.integer(1, 300),
      Major: "专业"+Mock.Random.integer(0,19)+"-"+Mock.Random.integer(0,20),
      Academy: "学院"+Mock.Random.integer(0,40),
      ClassNum: Mock.Random.integer(40, 100),
      Teacher: Mock.Random.cname(),
      ClassId:Mock.Random.guid(),
      TeacherPhone: String(Mock.Random.integer(10000000000, 20000000000)),
      Monitor: Mock.Random.cname(),
      MonitorPhone: String(Mock.Random.integer(10000000000, 20000000000)),
    })
  )
}

// 获取班级列表
function getClassesList(config) {
  if (!config || !config.url) {
    return {
      code: 400,
      message: 'Invalid request'
    };
  }
  const {keyWord,StudentAcademy,StudentMajor,page=1,limit=10}=param2Obj(config.url)
  console.log("解析出来的keyWord",keyWord,StudentAcademy,StudentMajor)

  // 筛选数据
  const mockList = ClassesInfoList.filter(item => {
    if (keyWord) {
      // 检查多个字段是否包含关键词
      return (
        item.ClassName.indexOf(keyWord) !== -1 ||
        item.Major.indexOf(keyWord) !== -1 ||
        item.Academy.indexOf(keyWord) !== -1 ||
        item.Teacher.indexOf(keyWord) !== -1 ||
        // item.Teacher_Phone.indexOf(keyword) !== -1 ||
        item.Monitor.indexOf(keyWord) !== -1 
        // item.Monitor_Phone.indexOf(keyword) !== -1
      );
    }
    if(StudentAcademy&&StudentMajor){
      return item.Academy==StudentAcademy&&item.Major==StudentMajor
    }
    return true;
  });

  // 分页处理
  const start = (page - 1) * limit;
  const end = page * limit;
  const pageList = mockList.slice(start, end);

  console.log("分页数据", pageList);
  return {
    code: 200,
    data: {
      list: pageList,
      limit:pageList.length,
      pages:mockList.length
    }
  };
};

//删除班级信息  
function deleteClass(config){
    const {id}=JSON.parse(config.body)
    if(!id){
      return {
        code:100,
        message:"参数不正确",
      }
    }else{
      ClassesInfoList=ClassesInfoList.filter((u)=>u.id!==id);
      console.log(id)
      return {
        code:200,
        message:"删除成功",
      }    
    }
}

//删除班级信息  
function editClass(config){
    return {
      code:200,
      data:{ 
        message:"编辑成功",
      }
    }    
}


// 导出 getClassesList 函数
export default {
  getClassesList,deleteClass,editClass
};
