package com.lwj.FinalServer.web.net.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lwj.FinalServer.common.Utils.MqUtils;
import com.lwj.FinalServer.common.result.Result;
import com.lwj.FinalServer.model.entity.Course;
import com.lwj.FinalServer.model.entity.CourseSelect;
import com.lwj.FinalServer.model.entity.Mq;
import com.lwj.FinalServer.web.net.service.CourseSelectService;
import com.lwj.FinalServer.web.net.service.CourseService;
import com.lwj.FinalServer.web.net.service.MqService;
import com.lwj.FinalServer.web.net.vo.CourseVO;
import com.lwj.FinalServer.web.net.vo.PageBean;
import com.lwj.FinalServer.web.net.vo.TeacherVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name="课程信息管理")
@RestController
@RequestMapping("/api/course")
public class CourseController {
    @Autowired
    private CourseService cservice;
    @Autowired
    private CourseSelectService csservice;
    @Autowired
    private MqService mqservice;
    @Operation(summary = "学生查询选课信息")
    @PostMapping("querrycourse")
    public Result<List<CourseVO>>QuerryByStu(@RequestBody Map data){
        String stuid= (String) data.get("StudentId");
        Integer CourseSemester=Integer.parseInt((String) data.get("CourseSemester"));
        String CourseYear= (String) data.get("CourseYear");
        List<CourseVO> clist=csservice.getCourseList(stuid,CourseSemester,CourseYear);
        if(clist.isEmpty()) return Result.ok(clist);
        return Result.ok(clist);
    }

    @Operation(summary = "添加课程")
    @PostMapping("addCourse")
    public Result<String> addCourse(@RequestBody Map data) {
        String CourseId = (String) data.get("CourseId");
        String CourseAcademy = (String) data.get("CourseAcademy");
        String CourseFinish = (String) data.get("CourseFinish");
        String CourseName = (String) data.get("CourseName");
        Integer CourseNum = Integer.parseInt((String) data.get("CourseNum"));
        String CourseRoomId = (String) data.get("CourseRoom");
        Integer CourseSemester = (Integer) data.get("CourseSemester");
        String TeacherId = (String) data.get("CourseTeacher");
        Double CourseValue = Double.valueOf(data.get("CourseValue").toString());
        String CourseYear = (String) data.get("CourseYear");
        String CourseTime = (String) data.get("CourseTime");
        Course course = new Course(CourseId, CourseName, TeacherId, CourseRoomId, CourseValue, CourseFinish, CourseNum, CourseTime, CourseYear, CourseSemester, CourseAcademy);
        QueryWrapper<Course> qCourse = new QueryWrapper<>();
        qCourse.eq("CourseId", CourseId);
        Course one = cservice.getOne(qCourse);
        if (one == null) {
            cservice.save(course);
            return Result.ok("添加成功");
        }
        return Result.ok("添加失败,该课程号已经存在");
    }

    @Operation(summary = "查询课程信息")
    @GetMapping("/getCourseInfoData")
    public Result<PageBean<CourseVO>> getCourse(@RequestParam Map data) {
        Integer pageNum = Integer.parseInt((String) data.get("page"));
        Integer pageSize = Integer.parseInt((String) data.get("limit"));
        String keyword = data.get("keyWord") == null ? null : (String) data.get("keyWord");
        String TeacherId=data.get("TeacherId")==null?null:(String)data.get("TeacherId");
        if (keyword != null && keyword.trim().isEmpty())
            keyword = null;

        PageBean<CourseVO> t = cservice.getCoursePage(pageNum, pageSize, keyword,TeacherId);
        return Result.ok(t);
    }


    @Operation(summary = "删除课程")
    @PostMapping("/deleteCourseInfo")
    public Result<String>deleteCourse(@RequestBody Map data){
        try
        {
            String CourseId = (String) data.get("CourseId");
            QueryWrapper<Course>qc=new QueryWrapper<>();
            qc.eq("CourseId",CourseId);
            Course c=cservice.getOne(qc);
            if(c!=null){
                QueryWrapper<CourseSelect>queryWrapper=new QueryWrapper<>();
                queryWrapper.eq("CourseId",CourseId).eq("CourseState",1);
                List<CourseSelect>cslist=csservice.list(queryWrapper);
                if(cslist!=null&&!cslist.isEmpty()){
                    MqUtils mqUtils=new MqUtils();
                    QueryWrapper<Mq>qmq=new QueryWrapper<>();
                    for (CourseSelect cs:cslist){
                        qmq.clear();
                        qmq.eq("CourseId",cs.getCourseid()).eq("StudentId",cs.getStudentid());
                        Mq mq=mqservice.getOne(qmq);
                        mq.setDel_flag(0);
                        mqservice.remove(qmq);
                        mqUtils.mqOperate(mq);
                    }
                }
                queryWrapper.clear();
                queryWrapper.eq("CourseId",CourseId);
                boolean isok=csservice.remove(queryWrapper);
                List<CourseSelect>list=csservice.list(queryWrapper);
                isok=cservice.remove(qc);
                if(isok && list.isEmpty()){
                    return Result.ok("删除成功");
                }
            }else{
                return Result.ok("无该课程");
            }
        }
        catch (Exception e){
            return Result.fail();
        }
        return Result.fail();
    }

    @Operation(summary = "查询课程")
    @PostMapping("querryCourseInfo")
    public Result<List<Course>>querryCourse(@RequestBody Map data){
        QueryWrapper<Course>qc=new QueryWrapper<>();
        qc.eq("TeacherId",data.get("TeacherId"));
        List<Course> cs=cservice.list(qc);
        return Result.ok(cs);
    }
    @Operation(summary = "修改课程信息")
    @PostMapping("editCourseInfo")
    public Result<String> editCourse(@RequestBody Map data) {
        try{
            QueryWrapper<Course>qc=new QueryWrapper<>();
            String courseId= (String) data.get("CourseId");
            qc.eq("CourseId",courseId);
            Course course=cservice.getOne(qc);
            if(course==null){
                return Result.ok("不存在该课程");
            }
            String CourseAcademy = (String) data.get("CourseAcademy");
            String CourseFinish = (String) data.get("CourseFinish");
            String CourseName = (String) data.get("CourseName");
            Integer CourseNum = (Integer) data.get("CourseNum");
            String CourseRoomId = (String) data.get("CourseRoom");
            Integer CourseSemester = (Integer) data.get("CourseSemester");
            String TeacherId = (String) data.get("CourseTeacher");
            Double CourseValue = Double.valueOf(data.get("CourseValue").toString());
            String CourseYear = (String) data.get("CourseYear");
            String CourseTime = (String) data.get("CourseTime");

            course.setAcademy(CourseAcademy);
            course.setCoursefinish(CourseFinish);
            course.setClassroomid(CourseRoomId);
            course.setCoursenum(CourseNum);
            course.setCoursesemester(CourseSemester);
            course.setCoursevalue(CourseValue);
            course.setCourseyear(CourseYear);
            course.setCoursetime(CourseTime);
            course.setCoursename(CourseName);
            course.setTeacherid(TeacherId);

            boolean isok=cservice.saveOrUpdate(course);
            if(isok){
                return Result.ok("修改成功");
            }
            return Result.ok("修改失败");

        }
        catch (Exception e){
            return Result.ok("修改失败");
        }
    }
}
