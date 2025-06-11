import { isMethodSignature } from "typescript";
import request from "./requset";
import { da } from "element-plus/es/locales.mjs";



export default{
    //请求首页表格数据
    getTableData(data){
        return request(
            {
                url:"/home/getTableData",
                method:"post",
                data,    
            }
        );
    },
    //请求教室信息
    getClassRoom(data){
        return request({
            url:"/classroom/getClassRoom",
            method:"get",
            data,
        })
    },
    //请求首页Count数据
    getCountData(data){
        return request(
            {
                url:"/home/getCountData",
                method:"post",
                data,
            }
        );
    },
    //请求学院专业信息
    getAMInfo(){
        return request(
            {
                url:"/home/getAMInfo",
                method:"get",
            }
        )
    },
    //请求班级信息
    getClassesData(data){
        return request(
            {
                url:"/class/getClasses",
                method:"get",
                data,
            }
        );
    },
    addClass(data){
        return request({
            url:"/class/addClass",
            method:"post",
            data,
        })
    },
    //删除班级信息
    deleteClass(data){
        request({
            url:"/class/deleteClass",
            method:'post',
            data,
        })
    },
    //编辑班级信息
    editClass(data){
        return request({
            url:"/class/editClass",
            method:"post",
            data,
        })
    },
    //查询班级信息
    querryClasses(data){
        return request({
            url:"/class/querryClasses",
            method:"get",
            data,
        })
    },
    //请求教师信息
    getTeachersData(data){
        return request(
            {
                url:"/teacher/getTeacher",
                method:"get",
                data
            }
        );
    },

    //添加教师信息
    addTeacher(data){
        return request({
            url:"/teacher/addTeacher",
             method:"post",
             data,
        })
    },
    //删除教师
    deleteTeacher(data){
        return request({
            url:'/teacher/deleteTeacher',
            method:'post',
            data
        })
    },
    //编辑教师信息
    editTeacher(data){
        return request({
            url:'/teacher/editTeacher',
            method:'post',
            data,
        })
    },
    //查询教师信息
    querryTeacher(data){
        return request({
            url:'/teacher/querryTeacher',
            method:'get',
            data,
        })
    },

    //请求学生信息
    getStudentsData(data){
        return request(
            {
                url:"/student/getStudents",
                method:"get",
                data,
            }
        );
    },
    //添加学生
    addStudent(data){
        return request({
            url:'/student/addStudent',
            method:'post',
            data
        })
    },
    //删除学生
    deleteStudent(data){
        return request({
            url:'/student/deleteStudent',
            method:'post',
            data
        })
    },
    //查询学生
    querryStudent(data){
        return request({
            url:"/student/querryStudent",
            method:"get",
            data,
        })
    },
    getMyStudentsData(data){
        return request({
            url:"/student/querrymystu",
            method:"get",
            data,
        })
    },

    //编辑学生
    editStudent(data){
        return request({
            url:'/student/editStudent',
            method:'post',
            data
        })
    },
    //请求课程信息
    getCourseInfoData(data){
        return request(
            {
                url:"/course/getCourseInfoData",
                method:"get",
                data
            }
        );
    },
    //查询课程信息
    querryCourseInfo(data){
        return request({
            url:'/course/querryCourseInfo',
            method:"post",
            data,
        })
    },
    //编辑课程信息
    editCourseInfo(data){
      return request({
        url:"/course/editCourseInfo",
        method:"post",
        data,
      })  
    },
    //添加课程信息
    addCourseInfo(data){
        return request(
            {
                url:"/course/addCourse",
                method:"post",
                data
            }
        );
    },
    //删除课程信息
    deleteCourseInfo(data){
        return request({
            url:'/course/deleteCourseInfo',
            method:"post",
            data,
        })
    },
    //添加选课信息
    addSelectCourse(data){
        return request({
            url:'/courseselect/addSelectCourse',
            method:'post',
            data,
        })
    },
    //请求选课信息
    getCourseSelectedData(data){
        return request(
            {
                url:"/courseselect/getCourseSelectedData",
                method:"get",
                data,
            }
        )
    },
    //修改选课状态
    editSelectCourse(data){
        return request({
            url:"/courseselect/editSelectCourse",
            method:"post",
            data,
        })
    },
    //删除选课状态
    deleteSelectCourse(data){
        return request({
            url:"/courseselect/deleteSelectCourse",
            method:"post",
            data,
        })
    },
    //获取签到记录
    getRecordData(data){
        return request(
            {
                url:"/record/getRecordData",
                method:"get",
                data,
            }
        )
    },
    //编辑签到记录
    editRecordData(data){
        return request({
            url:'/record/editRecordData',
            method:'post',
            data,
        })
    },
    //创建签到
    createRecord(data){
        return request({
            url:'/record/createRecord',
            method:'post',
            data,
        })
    },
    //登录
    login(data){
        return request({
            url:'/permission/login',
            method:'post',
            data,
        })
    },
    getcaptcha(){
        return request({
            url:"/permission/captcha",
            method:"get",
        })
    },
    //修改密码
    changePassword(data){
        return request({
            url:'/permission/changepassword',
            method:'post',
            data
        })
    },
    //查询课程签到状态
    getmyRecordData(data){
        return request({
            url:"/record/getmyRecordState",
            method:"post",
            data,
        })
    },
    //注册
    registerfunction(data){
        return request({
            url:"/permission/register",
            method:"post",
            data,
        })
    },

};