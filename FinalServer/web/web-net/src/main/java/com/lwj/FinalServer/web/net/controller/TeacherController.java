package com.lwj.FinalServer.web.net.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lwj.FinalServer.common.result.Result;
import com.lwj.FinalServer.model.entity.Teacher;
import com.lwj.FinalServer.model.entity.mUser;
import com.lwj.FinalServer.web.net.mapper.mUserMapper;
import com.lwj.FinalServer.web.net.service.PermissionService;
import com.lwj.FinalServer.web.net.service.TeacherService;
import com.lwj.FinalServer.web.net.vo.PageBean;
import com.lwj.FinalServer.web.net.vo.PageInfo;
import com.lwj.FinalServer.web.net.vo.TeacherVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Tag(name="教师管理")
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private mUserMapper  usermapper;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Operation(summary = "添加教师")
    @PostMapping("/addTeacher")
    public Result<String> addTeacher(@RequestBody Map data) throws ParseException {

        Teacher teacher=new Teacher();
        teacher.setTeacherid((String) data.get("teacherid"));
        teacher.setTeachername((String) data.get("teachername"));
        teacher.setTeacherbirth(formatter.parse((String) data.get("teacherbirth")));
        teacher.setTeacherphone((String) data.get("teacherphone"));
        teacher.setTeachersex(Integer.parseInt((String) data.get("teachersex")));
        teacher.setTeachertitle(Integer.valueOf((String)data.get("teachertitle")));
        teacher.setAmid((String) data.get("major"));

        QueryWrapper<Teacher>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("TeacherId",teacher.getTeacherid());
        Teacher one=teacherService.getOne(queryWrapper);
        boolean isok=false;
        if(one==null){
            isok=teacherService.saveOrUpdate(teacher);
            mUser user=new mUser();
            user.setRole(2);
            user.setUsername(teacher.getTeacherid());
            user.setId(teacher.getTeacherid());
            usermapper.insert(user);
        }else{
            return Result.ok("该工号已被使用");
        }
        if(isok) {
            return Result.ok("添加成功");
        }
        return  Result.fail();
    }

    @Operation(summary = "删除教师")
    @PostMapping("/deleteTeacher")
    public Result<String>deleteTeacher(@RequestBody Map data){
        String teachearId= (String) data.get("TeacherId");
        System.out.println(teachearId);
        QueryWrapper<Teacher> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("TeacherId",teachearId);
        Teacher teacher=teacherService.getOne(queryWrapper);
        boolean isok=teacherService.removeById(teacher);
        QueryWrapper<mUser>quser=new QueryWrapper<>();
        quser.eq("Role",2).eq("UserName",teacher.getTeacherid());
        usermapper.delete(quser);
        if(teacher==null){
            return Result.ok("查无此人");
        }
        if(!isok) return Result.fail();
        return Result.ok("删除成功");
    }

    @Operation(summary = "批量获取教师")
    @GetMapping("/getTeacher")
    public Result<PageBean<TeacherVO>>getTeacherTable(@RequestParam Map data){
        Integer pageNum= Integer.parseInt((String)data.get("page"));
        Integer pageSize= Integer.parseInt((String)data.get("limit"));
        String keyword=data.get("keyWord")==null?null:(String)data.get("keyWord");
        if (keyword != null && keyword.trim().isEmpty())
            keyword=null;
        PageBean<TeacherVO> t = teacherService.getTeacherPage(pageNum, pageSize, keyword);
        return  Result.ok(t);
    }

    @Operation(summary = "编辑教师")
    @PostMapping("/editTeacher")
    public Result<String> editTeacher(@RequestBody Map data) throws ParseException {
        String teacherid= (String) data.get("teacherid");
        QueryWrapper<Teacher> qTeacher=new QueryWrapper<>();
        qTeacher.eq("TeacherId",teacherid);
        Teacher teacher=teacherService.getOne(qTeacher);
        teacher.setTeacherid((String) data.get("teacherid"));
        teacher.setTeachername((String) data.get("teachername"));
        teacher.setTeacherbirth(formatter.parse((String) data.get("teacherbirth")));
        teacher.setTeacherphone((String) data.get("teacherphone"));
        teacher.setTeachersex(Integer.parseInt((String)data.get("teachersex")));
        teacher.setTeachertitle(Integer.parseInt((String) data.get("teachertitle")) );
        teacher.setAmid((String) data.get("amid"));
        if(teacher==null){
            return Result.fail();
        }
        boolean isflag=teacherService.saveOrUpdate(teacher);
        if(isflag){
            return  Result.ok("修改成功");
        }
        return Result.ok("修改失败");
    }

    @Operation(summary = "查询教师")
    @GetMapping("querryTeacher")
    public Result<List<Teacher>>querryTeacher(@RequestParam Map data){
        List<Teacher>tlist = null;
        if(data.get("AmId")!=null)
        {
            String Amid = (String) data.get("AmId");
            QueryWrapper<Teacher> qTeacher = new QueryWrapper<>();
            qTeacher.eq("AMId", Amid);
            tlist=teacherService.list(qTeacher);
        }
        if(data.get("Academy")!=null) {
            String Academy = (String) data.get("Academy");
            tlist=teacherService.querryByAcademy(Academy);
        }
        return Result.ok(tlist);
    }

}
