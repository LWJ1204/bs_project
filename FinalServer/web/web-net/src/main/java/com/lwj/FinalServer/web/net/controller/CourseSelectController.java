package com.lwj.FinalServer.web.net.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lwj.FinalServer.common.Utils.MqUtils;
import com.lwj.FinalServer.common.result.Result;
import com.lwj.FinalServer.model.entity.Course;
import com.lwj.FinalServer.model.entity.CourseSelect;
import com.lwj.FinalServer.model.entity.Mq;
import com.lwj.FinalServer.model.entity.Student;
import com.lwj.FinalServer.web.net.mapper.MqMapper;
import com.lwj.FinalServer.web.net.service.CourseSelectService;
import com.lwj.FinalServer.web.net.service.CourseService;
import com.lwj.FinalServer.web.net.service.MqService;
import com.lwj.FinalServer.web.net.service.StudentService;
import com.lwj.FinalServer.web.net.vo.CourseSelectVO;
import com.lwj.FinalServer.web.net.vo.PageBean;
import com.lwj.FinalServer.web.net.vo.TeacherVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name="选课信息管理")
@RestController
@RequestMapping("api/courseselect")
public class CourseSelectController {

    @Autowired
    private CourseSelectService csservice;
    @Autowired
    private CourseService cservice;
    @Autowired
    private MqService mqservice;
    @Autowired
    private StudentService service;

    private MqUtils mputils=new MqUtils();

    @Operation(summary = "添加选课信息")
    @PostMapping("/addSelectCourse")
    public Result<String>addCourseSelect(@RequestBody Map data){
        try
        {
            QueryWrapper<CourseSelect>qCS=new QueryWrapper<>();
            QueryWrapper<Course>qC=new QueryWrapper<>();
            QueryWrapper<Student>qS=new QueryWrapper<>();
            String CourseId = (String) data.get("CourseId");
            String StudentId = (String) data.get("StudenId");
            String StudentName = (String) data.get("StudentName");
            if(CourseId!=null&&StudentId!=null){
                //设置选课信息，课程，学生的查询条件
                qCS.eq("CourseId",CourseId).eq("CourseState",1);
                qC.eq("CourseId",CourseId);
                qS.eq("StudentId",StudentId);
                List<CourseSelect>list=csservice.list(qCS);
                Course c=cservice.getOne(qC) ;
                if(c.getCoursenum()>list.size()){
                    Student stu=service.getOne(qS);
                    if(stu!=null){
                        qCS.clear();
                        qCS.eq("CourseId",CourseId).eq("StudentId",stu.getStudentid());
                        //查找是否已经存在该学生关于该课程的选课信息
                        CourseSelect select=csservice.getOne(qCS);
                        if(select!=null) return Result.ok("该选课信息已存在");
                        boolean isok=csservice.save(new CourseSelect(CourseId,StudentId,0));
                        if(isok){
                            return Result.ok("成功添加选课信息");
                        }

                    }
                    return Result.ok("该学生不存在");

                }
                return Result.ok("该课程已选满");
            }
        }
        catch (Exception e)
        {
            return Result.fail();
        }
        return Result.fail();
    }

    @Operation(summary = "获取选课信息")
    @GetMapping("/getCourseSelectedData")
    public Result<PageBean<CourseSelectVO>>getCourseSelct(@RequestParam Map data){
        Integer pageNum= Integer.parseInt((String)data.get("page"));
        Integer pageSize= Integer.parseInt((String)data.get("limit"));
        String keyword=data.get("keyWord")==null?null:(String)data.get("keyWord");
        if (keyword != null && keyword.trim().isEmpty())
            keyword=null;
        PageBean<CourseSelectVO> t = csservice.getCourseSelectPage(pageNum, pageSize, keyword);
        return  Result.ok(t);
    }
    @Operation(summary = "编辑选课信息")
    @PostMapping("/editSelectCourse")
    public Result<String>editCourseSelect(@RequestBody Map data){
        try
        {
            String Courseid = (String) data.get("CourseId");
            String stuid=(String)data.get("StudentId");
            Integer state = (Integer) data.get("CourseState");
            QueryWrapper<CourseSelect>qc=new QueryWrapper<>();
            qc.eq("CourseId",Courseid).eq("CourseState",1);
            List<CourseSelect>courseSelects=csservice.list(qc);
            int selectnum=courseSelects.size();
            qc.clear();
            // 创建一个实体对象，设置需要更新的字段
            CourseSelect courseSelect = new CourseSelect();
            courseSelect.setCoursestate(state);
            QueryWrapper<Course>qCourse=new QueryWrapper<>();
            qCourse.eq("CourseId",Courseid);
            Course course=cservice.getOne(qCourse);
            int coursenum=course.getCoursenum();
            qc.eq("CourseId",Courseid).eq("StudentId",stuid);
            QueryWrapper<Mq>qmq=new QueryWrapper<>();
            qmq.eq("CourseId",Courseid).eq("StudentId",stuid);
            boolean isok=true;
            if(state==1){
                if(coursenum>=selectnum+1){
                    isok=csservice.update(courseSelect,qc);
                    Mq mq=new Mq();
                    mq.setCourseid(Courseid);
                    mq.setDelaytype(null);
                    mq.setDel_flag(state);
                    mq.setBinding(null);
                    mq.setStudentid(stuid);
                    mq.setExchangetype(null);
                    mqservice.saveOrUpdate(mq);
                    mputils.mqOperate(mq);
                }
                else{
                    return Result.ok("选课人数已满");
                }
            }
            else {
                isok=csservice.update(courseSelect, qc);
                Mq mq=mqservice.getOne(qmq);
                mq.setDel_flag(state);
                mqservice.update(mq,qmq);
                mputils.mqOperate(mq);
            }
            if (isok){
                return Result.ok("修改成功");
            }


            return Result.ok("修改失败");
        }
        catch (Exception e){
            return Result.fail();
        }
    }

    @Operation(summary = "删除选课信息")
    @PostMapping("/deleteSelectCourse")
    public Result<String>deleteCourseSelect(@RequestBody Map data){
        try
        {
            String Courseid = (String) data.get("CourseId");
            String stuid=(String)data.get("StudentId");
            QueryWrapper<CourseSelect>qc=new QueryWrapper<>();
            QueryWrapper<Mq>qmq=new QueryWrapper<>();
            qc.eq("CourseId",Courseid).eq("StudentId",stuid);
            qmq.eq("CourseId",Courseid).eq("StudentId",stuid);
            Mq mq=mqservice.getOne(qmq);
            mq.setDel_flag(0);
            mputils.mqOperate(mq);
            mqservice.remove(qmq);
            boolean isok= csservice.remove(qc);
            if (isok){
                return Result.ok("删除成功");
            }
            return Result.ok("修改失败");
        }
        catch (Exception e){
            return Result.fail();
        }
    }
}
