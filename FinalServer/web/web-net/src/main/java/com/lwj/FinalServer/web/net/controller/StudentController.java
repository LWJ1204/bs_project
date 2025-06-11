package com.lwj.FinalServer.web.net.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lwj.FinalServer.common.result.Result;
import com.lwj.FinalServer.model.entity.Classes;
import com.lwj.FinalServer.model.entity.Student;
import com.lwj.FinalServer.model.entity.Teacher;
import com.lwj.FinalServer.model.entity.mUser;
import com.lwj.FinalServer.web.net.mapper.mUserMapper;
import com.lwj.FinalServer.web.net.service.ClassesService;
import com.lwj.FinalServer.web.net.service.PermissionService;
import com.lwj.FinalServer.web.net.service.StudentService;
import com.lwj.FinalServer.web.net.vo.PageBean;
import com.lwj.FinalServer.web.net.vo.StudentVO;
import com.lwj.FinalServer.web.net.vo.TeacherVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Tag(name="学生管理")
@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private ClassesService classesService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private mUserMapper userMapper;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Operation(summary = "添加学生")
    @PostMapping("/addStudent")
    public Result<String> addTeacher(@RequestBody Map data) throws ParseException {

        Student student=new Student();
        student.setStudentid((String) data.get("StudentId"));
        student.setStudentname((String) data.get("StudentName"));
        student.setStudentbirth(formatter.parse((String) data.get("StudentBirth")));
        student.setStudentphone((String) data.get("StudentPhone"));
        student.setStudentsex(Integer.parseInt((String)data.get("StudentSex")));
        student.setClassid(String.valueOf(data.get("StudentClass")));
        QueryWrapper<Student>qStudent=new QueryWrapper<>();
        qStudent.eq("StudentId",student.getStudentid());
        Student one=studentService.getOne(qStudent);
        boolean isenouth=true;
        if(data.get("StudentClass")!=null){
            QueryWrapper<Classes>Qclass=new QueryWrapper<>();
            Qclass.eq("ClassId",data.get("StudentClass"));
            Classes classes=classesService.getOne(Qclass);
            if(classes.getSelectNum()+1>classes.getClassnum()){
                isenouth=false;
            }else {
                classes.setSelectNum(classes.getSelectNum()+1);
                classesService.saveOrUpdate(classes);
            }

        }
        if(!isenouth) return Result.ok("该班级人数已满");
        boolean isok=false;
        if(one==null){
            isok=studentService.saveOrUpdate(student);
            mUser muser=new mUser();
            //创建该学生用户
            muser.setId(student.getStudentid());
            muser.setRole(3);
            muser.setUsername(student.getStudentid());
            permissionService.saveOrUpdate(muser);
        }else{
            return Result.ok("该学号号已被使用");
        }
        if(isok) return Result.ok("添加成功");
        return Result.ok("添加失败");
    }

    @Operation(summary = "批量获取学生")
    @GetMapping("/getStudents")
    public Result<PageBean<StudentVO>>getStudent(@RequestParam Map data){
        Integer pageNum= Integer.parseInt((String)data.get("page"));
        Integer pageSize= Integer.parseInt((String)data.get("limit"));
        String keyword=data.get("keyWord")==null?null:(String)data.get("keyWord");
        if (keyword != null && keyword.trim().isEmpty())
            keyword=null;
        PageBean<StudentVO> t = studentService.getStudentPage(pageNum, pageSize, keyword);
        return  Result.ok(t);
    }

    @Operation(summary = "编辑学生")
    @PostMapping("/editStudent")
    public Result<String> editStudent(@RequestBody Map data) throws ParseException {
        String StudentId= (String) data.get("studentid");
        QueryWrapper<Student> qStu=new QueryWrapper<>();
        qStu.eq("StudentId",StudentId);
        Student student=studentService.getOne(qStu);
        student.setStudentname((String) data.get("StudentName"));
        student.setStudentbirth(formatter.parse((String) data.get("StudentBirth")));
        student.setStudentphone((String) data.get("StudentPhone"));
        student.setStudentsex(Integer.parseInt((String) data.get("StudentSex")));
        student.setClassid(String.valueOf(data.get("StudentClass")));

        boolean isenouth=true;
        if(data.get("StudentClass")!=null){
            QueryWrapper<Classes>Qclass=new QueryWrapper<>();
            Qclass.eq("ClassId",data.get("StudentClass"));
            Classes classes=classesService.getOne(Qclass);
            if(classes!=null){
                if(classes.getSelectNum()+1>classes.getClassnum()){
                    isenouth=false;
                }else {
                    classes.setSelectNum(classes.getSelectNum()+1);
                    classesService.saveOrUpdate(classes);
                }
            }
        }
        if(!isenouth) return Result.ok("该班级人数已满");
        if(student==null){
            return Result.fail();
        }
        boolean isflag=studentService.saveOrUpdate(student);
        if(isflag){
            return  Result.ok("修改成功");
        }
        return Result.ok("修改失败");
    }
    @Operation(summary = "删除学生")
    @PostMapping("/deleteStudent")
    public Result<String>deleteStudent(@RequestBody Map data){
        String studentId= (String) data.get("StudentId");
        QueryWrapper<Student>qStu=new QueryWrapper<>();
        qStu.eq("StudentId",studentId);
        Student stu=studentService.getOne(qStu);
        if(stu==null){
            return Result.ok("不存在");
        }
        QueryWrapper<Classes>qClass=new QueryWrapper<>();
        if(stu.getClassid()!=null){
            qClass.eq("ClassId",stu.getClassid());
            Classes classes=classesService.getOne(qClass);
            classes.setSelectNum(classes.getSelectNum()-1);
            classesService.saveOrUpdate(classes);
        }
        boolean isok =studentService.removeById(stu);
        if(isok){
            QueryWrapper<mUser>quser=new QueryWrapper<>();
            quser.eq("Id",stu.getStudentid()).eq("Role",3);
            permissionService.remove(quser);
            return Result.ok("删除成功");
        }
        return Result.ok("删除失败");
    }

    @Operation(summary = "查询学生")
    @GetMapping("/querryStudent")
    public Result<List<Student>>querryStudent(@RequestParam Map data){
        if(data.get("classid")!=null){
            String classid= (String) data.get("classid");
            QueryWrapper<Student>qstu=new QueryWrapper<>();
            qstu.eq("ClassId",classid);
            List<Student>list=studentService.list(qstu);
            return Result.ok(list);
        }
        return Result.ok(null);
    }


    @Operation(summary = "获取学生")
    @GetMapping("/querrymystu")
    public Result<PageBean<StudentVO>>querrymyStu(@RequestParam Map data){
        Integer pageNum= Integer.parseInt((String)data.get("page"));
        Integer pageSize= Integer.parseInt((String)data.get("limit"));
        String teacherid= (String) data.get("TeacherId");
        String keyword=data.get("keyWord")==null?null:(String)data.get("keyWord");
        if (keyword != null && keyword.trim().isEmpty())
            keyword=null;
        PageBean<StudentVO> t = studentService.getMyStudentPage(pageNum, pageSize,keyword,teacherid);
        return  Result.ok(t);
    }

    @Operation(summary = "查询个人信息")
    @PostMapping("/getstuinfo")
    public Result<StudentVO>getstu(@RequestBody Map data){
        try {
            Integer role = (Integer) data.get("role");
            String StudentId = (String) data.get("StudentId");
            if(role!=3){
                return Result.fail();
            }
            StudentVO stu=studentService.getStu(StudentId);
            if(stu==null){
                return Result.fail();
            }
            return Result.ok(stu);
        }
        catch (Exception e){
            return Result.fail();
        }
    }
}
