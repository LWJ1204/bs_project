package com.lwj.FinalServer.web.net.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwj.FinalServer.model.entity.*;
import com.lwj.FinalServer.web.net.mapper.*;
import com.lwj.FinalServer.web.net.service.ClassRoomService;
import com.lwj.FinalServer.web.net.service.CourseService;
import com.lwj.FinalServer.web.net.vo.CountData;
import com.lwj.FinalServer.web.net.vo.CourseVO;
import com.lwj.FinalServer.web.net.vo.PageBean;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    @Autowired
    CourseMapper cmapper;
    @Autowired
    CourseSelectMapper courseSelectMapper;
    @Autowired
    TeacherMapper teacherMapper;
    @Autowired
    ClassroomMapper crmapper;
    @Autowired
    RecordMapper rmappre;
    @Autowired
    RecordStateMapper rsmappre;
    @Override
    public PageBean<CourseVO> getCoursePage(Integer PageNum, Integer PageSize, String keyword, String teacherId) {
        PageBean<CourseVO> ans = new PageBean<>();
        Page<Course> setPage = new Page<>(PageNum, PageSize);
        QueryWrapper<Course> QCourse = new QueryWrapper<>();
        QueryWrapper<Teacher>QTeacher=new QueryWrapper<>();
        QueryWrapper<Classroom>qCR=new QueryWrapper<>();
        QueryWrapper<CourseSelect>QCS=new QueryWrapper<>();
        if(teacherId!=null){
            QCourse.eq("TeacherId",teacherId);
        }
        if(keyword!=null){
            List<String>TeacherIds=getTeacherList(keyword);
            List<String>ClassRoomIds=getClassRoomList(keyword);
            if(TeacherIds.isEmpty()){
                TeacherIds.add("SELECT 1 WHERE 1=0");
            }
            if(ClassRoomIds.isEmpty()){
                ClassRoomIds.add("SELECT 1 WHERE 1=0");
            }
            String inClause0 = String.join(", ", TeacherIds.stream()
                    .map(Id -> "'" + Id + "'") // 用单引号括起来
                    .collect(Collectors.toList()));
            String inClause1 = String.join(", ",ClassRoomIds.stream()
                    .map(Id -> "'" + Id + "'") // 用单引号括起来
                    .collect(Collectors.toList()));
            QCourse.or(wrapper -> wrapper
                    .like("CourseId", keyword)
                    .or().like("CourseName", keyword)
                    .or().eq("CourseValue", getCourseValue(keyword))
                    .or().eq("CourseFinish", keyword)
                    .or().like("CourseTime", keyword)
                    .or().like("CourseYear",keyword)
                    .or().eq("CourseSemester",getSemester(keyword))
                    .or().like("Academy",keyword)
                    .or().inSql("TeacherId", inClause0)
                    .or().inSql("ClassRoomId",inClause1)
            );
        }
        Page<Course> tpage = cmapper.selectPage(setPage, QCourse);
        ans.setCount(tpage.getTotal());
        List<Course> list = tpage.getRecords();
        List<CourseVO> alist = new ArrayList<>();

        int selectnum=0;
        for (Course course : list) {
            QTeacher.clear();
            QCS.clear();
            qCR.clear();
            qCR.eq("ClassRoomId",course.getClassroomid());
            QTeacher.eq("TeacherId",course.getTeacherid());
            QCS.eq("CourseId",course.getCourseid());
            Teacher teacher=teacherMapper.selectOne(QTeacher);
            String teachername=teacher!=null?teacher.getTeachername():null;
            List<CourseSelect>tlist=courseSelectMapper.selectList(QCS);
            if(tlist==null)selectnum=0;
            else selectnum=tlist.size();
            Classroom classroom=crmapper.selectOne(qCR);
            if(classroom==null)
                System.out.println("test:"+course.getClassroomid());
            String classroomname= null;
            if (classroom != null) {
                classroomname = classroom.getClassroomlou()+classroom.getClassroomqu()+"-"+classroom.getClassroomname();
            }
            alist.add(new CourseVO(course,teachername,selectnum,classroomname));
        }
        ans.setList(alist);
        return ans;

    }

    @Override
    public List<CourseVO> getCourseList(Integer role, String id) {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 获取当前年份
        int year = currentDate.getYear();
        int month=currentDate.getMonthValue();
        int semester=2;
        DayOfWeek dayofweeek=currentDate.getDayOfWeek();
        int daypos=dayofweeek.getValue();
        String weekday=getWeekday(daypos);
        if(month>8) semester=1;
        QueryWrapper<Course>qc=new QueryWrapper<>();
        qc.eq("CourseSemester",semester).like("CourseYear",year).like("CourseTime",weekday);
        if(role==2){
            qc.eq("TeacherId",id);
        }
        List<Course> list=cmapper.selectList(qc);
        List<CourseVO>alist=new ArrayList<>();
        QueryWrapper<Teacher>qt=new QueryWrapper<>();
        QueryWrapper<Classroom>qcl=new QueryWrapper<>();
        for(Course course:list){
            qt.clear();
            qt.eq("TeacherId",course.getTeacherid());
            Teacher teacher=teacherMapper.selectOne(qt);
            String teachername=teacher==null?null:teacher.getTeachername();
            qcl.clear();
            qcl.eq("ClassRoomId",course.getClassroomid());
            Classroom cl=crmapper.selectOne(qcl);
            String courseroom=cl==null?null:cl.getClassroomlou()+cl.getClassroomqu()+"-"+cl.getClassroomname();
            CourseVO courseVO=new CourseVO(course,teachername,null,courseroom);
            alist.add(courseVO);
        }
        return alist;
    }

    @Override
    public CountData CountData(Integer role, String id) {
        List<CourseVO> coursevolist=getCourseList(role,id);
        CountData cd=new CountData();
        cd.setCoursenum(coursevolist.size());
        int one=0,zero=0,three=0;
        QueryWrapper<mRecord>qr=new QueryWrapper<>();
        // 获取今天的开始时间和结束时间
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay(); // 今天的 00:00:00
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        QueryWrapper<RecordState>qrs=new QueryWrapper<>();
        for (CourseVO courseVO:coursevolist)
        {
            qr.clear();
            qr.eq("CourseId",courseVO.getCourseid()).ge("RecordSetTime",startOfDay).le("RecordEndTime",endOfDay);
            List<mRecord>mlist=rmappre.selectList(qr);
            List<Integer>ids=mlist.stream().map(mRecord::getRecordid).filter(Objects::nonNull).toList();
           if(!ids.isEmpty()){
               qrs.clear();
               qrs.eq("RecordState",0).in("RecordId",ids);
               zero+=rsmappre.selectList(qrs).size();
               qrs.clear();
               qrs.eq("RecordState",1).in("RecordId",ids);
               one+=rsmappre.selectList(qrs).size();
               qrs.clear();
               qrs.eq("RecordState",2).in("RecordId",ids);
               three+=rsmappre.selectList(qrs).size();
           }
        }

        cd.setOnezeronum(one);
        cd.setThreenum(three);
        cd.setZeronum(zero);
        return cd;
    }

    private String getWeekday(int pos){
        String []week={
          "周一",
          "周二",
          "周三",
          "周四",
          "周五",
          "周六",
          "周日",
        };
        return week[pos];
    }

    private List<String> getTeacherList(String keyword) {
        QueryWrapper<Teacher> list = new QueryWrapper<>();
        list.or(wrapper -> wrapper
                .like("TeacherName", keyword)
        );
        List<Teacher> t = teacherMapper.selectList(list);
        return t.stream()
                .map(Teacher::getTeacherid)
                .filter(id -> id != null)
                .collect(Collectors.toList());
    }

    private List<String> getClassRoomList(String keyword) {
        QueryWrapper<Classroom> list = new QueryWrapper<>();
        list.or(wrapper -> wrapper
                .like("ClassRoomName", keyword)
                .or().like("ClassRoomLou",keyword)
                .or().like("ClassRoomQu",keyword)
        );
        List<Classroom> t = crmapper.selectList(list);
        return t.stream()
                .map(Classroom::getClassroomid)
                .filter(id -> id != null)
                .collect(Collectors.toList());
    }

    private Integer getSemester(String keyword) {
        try {
            return Integer.parseInt(keyword);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double getCourseValue(String keyword) {
        try {
            return Double.parseDouble(keyword);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
