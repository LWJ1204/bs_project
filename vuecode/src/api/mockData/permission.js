import { pa } from 'element-plus/es/locales.mjs'
import Mock from 'mockjs'

const Admin={
  Id:'admin',
  password:'lwj20021204.'
}

const User={
  Id:'user',
  password:'lwj20021204.'
}
export default {
  getRoutes: config => {
    console.log('拦截住了')
    const { username, password ,role} = JSON.parse(config.body)
    // 先判断用户是否存在
    // 判断账号和密码是否对应
    //menuList用于后面做权限分配，也就是用户可以展示的菜单
    if (username === Admin.Id && password === Admin.password&&role==='1') {
      return {
        code: 200,
        data: {
          menuList: [
            {
              path: '/home',
              name: 'shouye',
              label: '首页',
              icon: 'house',
              url: 'Home'
            },
            {
              path: '/classes',
              name: 'banjiguanli',
              label: '班级管理',
              icon: 'DataBoard',
              url: 'Classes'
            },
            {
              path: '/teachers',
              name: 'jiaoshiguanli',
              label: '教师管理',
              icon: 'User',
              url: 'Teachers'
            },
           {
            path:'/students',
            label:"学生管理",
            icon:"User",
            children:[
              {
                path:'/students/studentsInfo',
                name:'xueshengxinxi',
                label:'学生信息',
                url:'Students'
              }
            ]
           },
            //课程管理
            {
              path:"/courses",
              label:"课程管理",
              icon:"Collection",
              children:[
                  {
                      path:"/courses/courseInfo",
                      label:"课程信息",
                      name:'kechengxinxi',
                      url:"CourseInfo",
                  },
                  {
                      path:"/courses/courseSelected",
                      label:"选课管理",
                      name:'xuankeguanli',
                      url:"CourseSelected"
                  }
              ]
            },
             // 考勤记录
            {
              path: "/records",
              label: "考勤管理",
              icon:"Location",
              children: [
                  {
                      path: "/records/recordManage",
                      label: "考勤记录管理",
                      name:'kaoqinjiluguanli',
                      url: "recordManage",
                  },

              ],
            },

          ],
          token: Mock.Random.guid(),
          message: '获取成功'
        }
      }
    } else if (username === User.Id && password ===User.password&&role=='2') {
      return {
        code: 200,
        data: {
          menuList: [
            {
              path: '/home',
              name: 'shouye',
              label: '首页',
              icon: 'house',
              url: 'Home'
            },
            {
              path: '/students',
              label: '学生管理',
              icon: 'user',
              children:[
                {  
                  path:'/students/mystudents',
                  name:'wodexuesheng',
                  url:'MyStudents',
                  label:'授课学生'
                }]
            },
             //课程管理
            {
              path:"/courses",
              label:"课程管理",
              icon:"Collection",
              children:[
                  {
                      path:"/courses/courseInfo",
                      label:"课程信息",
                      name:'kechengxinxi',
                      url:"CourseInfo",
                  },
              ]
            },
            {
               path:"/records",
               label:'考勤管理',
               icon:'Location',
               children:[
                   {
                       path:"/records/recordManage",
                       label:"考勤记录管理",
                       name:"kaoqinjiluguanli",
                       url:'recordManage'
                   },
                   {
                      path:"/records/recordSet",
                      label:"考勤设置",
                      name:"kaoqinshezhi",
                      url:'recordSet'
                   },

            
               ]
             },
          ],
          token: Mock.Random.guid(),
          message: '获取成功'
        }
      }
    } else {    
      return {
        code: -999,
        data: {
          message: '密码错误'
        }
      }
        
    }

  },
  changepassword:config=>{
    const {userId,password,role}=JSON.parse(config.body)
    if(role==='1'&&userId===Admin.Id){
        Admin.password=password;
        return {
          code:200,
          data:{
            message:"修改成功"
          }
        }
    }else if(role=='2'&&userId==User.Id){
        User.password=password;
        return {
          code:200,
          data:{
            message:"修改成功"
          }
        }
    }
    return {
      code:-99,
      data:{
        message:"没找到该用户"
      }
    }
  },

}