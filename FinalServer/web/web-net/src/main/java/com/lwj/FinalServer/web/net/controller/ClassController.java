package com.lwj.FinalServer.web.net.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lwj.FinalServer.common.result.Result;
import com.lwj.FinalServer.model.entity.Classes;
import com.lwj.FinalServer.web.net.service.ClassesService;
import com.lwj.FinalServer.web.net.vo.ClassVO;
import com.lwj.FinalServer.web.net.vo.PageBean;
import com.lwj.FinalServer.web.net.vo.TeacherVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name="班级信息管理")
@RestController
@RequestMapping("/api/class")
public class ClassController {

    @Autowired
    private ClassesService classesService;

    @Operation(summary = "添加班级")
    @PostMapping("/addClass")
    public Result<String> addClass(@RequestBody Map data){
       Classes classes=new Classes();
       classes.setClassnum((Integer)data.get("classnum"));
       classes.setClassname((String)data.get("classname"));
       classes.setAmid((String)data.get("major"));
       classes.setTeacherid((String)data.get("teacherid"));
       classes.setStudentid((String)data.get("studentid"));
        QueryWrapper<Classes>qClass=new QueryWrapper<>();
        qClass.eq("ClassName",classes.getClassname());
        Classes one=classesService.getOne(qClass);
        boolean isok=false;
        if(one==null){
            isok =classesService.saveOrUpdate(classes);
        }else{
            return Result.ok("已存在该班级");
        }
        if(isok) return Result.ok("添加成功");
        return Result.ok("添加失败");
    }


    @Operation(summary = "批量获取班级")
    @GetMapping("/getClasses")
    public Result<PageBean<ClassVO>>getClassTable(@RequestParam Map data){
        Integer pageNum= Integer.parseInt((String)data.get("page"));
        Integer pageSize= Integer.parseInt((String)data.get("limit"));
        String keyword=data.get("keyWord")==null?null:(String)data.get("keyWord");
        if (keyword != null && keyword.trim().isEmpty())
            keyword=null;
        PageBean<ClassVO> t = classesService.getClassesPage(pageNum, pageSize, keyword);
        return  Result.ok(t);
    }

    @Operation(summary ="编辑班级")
    @PostMapping("/editClass")
    public Result<String>editClass(@RequestBody Map data){
        Integer ClassId= (Integer) data.get("classid");
        QueryWrapper<Classes>qClass=new QueryWrapper<>();
        qClass.eq("ClassId",ClassId);
        Classes one=classesService.getOne(qClass);
        if(one==null){
            return Result.ok("没有该班级");
        }
        String amid= (String) data.get("major");
        String classname= (String) data.get("classname");
        Integer classnum= (Integer) data.get("classnum");
        String teacherid= (String) data.get("teacherid");
        String studentid= (String) data.get("studentid");
        one.setStudentid(studentid);
        one.setAmid(amid);
        one.setClassnum(classnum);
        if(teacherid!=null)one.setTeacherid(teacherid);
        one.setClassname(classname);
        boolean isok=classesService.saveOrUpdate(one);
        if(isok){
            return Result.ok("修改成功");
        }
        return Result.ok("修改失败");
    }

    @Operation(summary = "删除班级")
    @PostMapping("/deleteClass")
    public Result<String>deleteClass(@RequestBody Map data){
        Integer id= (Integer) data.get("id");
        QueryWrapper<Classes>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("ClassId",id);
        Classes one=classesService.getOne(queryWrapper);
        if(one==null){
            return Result.ok("该班级不存在");
        }
        boolean isok=classesService.removeById(one);
        if (isok) return Result.ok("成功删除");
        return Result.ok("删除失败");
    }

    @Operation(summary = "查询班级")
    @GetMapping("/querryClasses")
    public Result<List<Classes>>querryClass(@RequestParam Map  data){
        String amid= (String) data.get("AmId");
        QueryWrapper<Classes>q=new QueryWrapper<>();
        q.eq("AMId",amid);
        List<Classes>list=classesService.list(q);
        return Result.ok(list);
    }
}
