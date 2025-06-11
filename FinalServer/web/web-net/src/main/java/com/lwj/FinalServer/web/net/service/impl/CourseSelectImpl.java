package com.lwj.FinalServer.web.net.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwj.FinalServer.model.entity.*;
import com.lwj.FinalServer.web.net.mapper.*;
import com.lwj.FinalServer.web.net.service.CourseSelectService;
import com.lwj.FinalServer.web.net.vo.ClassVO;
import com.lwj.FinalServer.web.net.vo.CourseSelectVO;
import com.lwj.FinalServer.web.net.vo.CourseVO;
import com.lwj.FinalServer.web.net.vo.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CourseSelectImpl extends ServiceImpl<CourseSelectMapper, CourseSelect> implements CourseSelectService {
    @Autowired
    private CourseSelectMapper csmapper;
    @Autowired
    private CourseMapper cmapper;
    @Autowired
    private AmMapper amapper;
    @Autowired
    private TeacherMapper tmapper;
    @Autowired
    private StudentMapper smapper;
    @Autowired
    private ClassroomMapper crmapper;
    @Override
    public PageBean<CourseSelectVO> getCourseSelectPage(Integer PageNum, Integer PageSize, String keyword) {
        PageBean<CourseSelectVO>ans=new PageBean<>();
        Page<CourseSelect> setPage=new Page<>(PageNum,PageSize);

        QueryWrapper<CourseSelect>QClassSelect=new QueryWrapper<>();
        QueryWrapper<Course>QCourse=new QueryWrapper<>();
        QueryWrapper<Classes> QClass=new QueryWrapper<>();
        QueryWrapper<Classroom>qClassRoom=new QueryWrapper<>();
        QueryWrapper<Teacher>Qteacher=new QueryWrapper<>();
        QueryWrapper<Student>Qstudent=new QueryWrapper<>();


        if(keyword!=null){
            List<String>CourseIds=getCourseList(keyword);
            List<String>StudentIds=getStudentList(keyword);
            if(CourseIds.isEmpty()){
                CourseIds.add("SELECT 1 WHERE 1=0");
            }
            if (StudentIds.isEmpty()) {
                StudentIds.add("SELECT 1 WHERE 1=0"); // 使用不可能存在的值
            }

            String inClause1 = String.join(", ", StudentIds.stream()
                    .map(Id -> "'" + Id + "'") // 用单引号括起来
                    .collect(Collectors.toList()));
            String inClause2 = String.join(", ", CourseIds.stream()
                    .map(Id -> "'" + Id + "'") // 用单引号括起来
                    .collect(Collectors.toList()));
            QClassSelect.or(wrapper->wrapper
                    .like("CourseState",getStage(keyword))
                    .or().inSql("CourseId",inClause2)
                    .or().inSql("StudentId",inClause1)
            );

        }


        Page<CourseSelect>Cpage=csmapper.selectPage(setPage,QClassSelect);
        ans.setCount(Cpage.getTotal());
        List<CourseSelect>list=Cpage.getRecords();
        List<CourseSelectVO>alist=new ArrayList<>();

        String coursename=null;
        String courseroom=null;
        String academy=null;
        String tname=null;
        String sname=null;
        for(CourseSelect t:list){
            Qteacher.clear();
            Qstudent.clear();
            QCourse.clear();
            qClassRoom.clear();
            coursename=null;
            courseroom=null;

            String courseid=t.getCourseid();
            QCourse.eq("CourseId",courseid);
            Course course=cmapper.selectOne(QCourse);
            if(course!=null){
                String classroomid=course.getClassroomid();
                qClassRoom.eq("ClassRoomId",classroomid);
                Classroom csr=crmapper.selectOne(qClassRoom);
                courseroom=csr==null?null:csr.getClassroomlou()+csr.getClassroomqu()+"-"+csr.getClassroomname();
                academy=course.getAcademy();
                String teacherid=course.getTeacherid();
                coursename=course.getCoursename();
                Qteacher.eq("TeacherId",teacherid);
            }
            //获取学生
            Qstudent.eq("StudentId",t.getStudentid());
            Student s=smapper.selectOne(Qstudent);
            sname=s==null?null:s.getStudentname();
//            获取教师
            Teacher teacher=tmapper.selectOne(Qteacher);
            tname=teacher==null?null:teacher.getTeachername();
            alist.add(new CourseSelectVO(t,coursename,courseroom, tname,sname,academy));
        }
        ans.setList(alist);
        return ans;
    }

    @Override
    public List<CourseVO> getCourseList(String stuid, Integer courseSemester, String courseYear) {
        List<CourseVO> alist=new ArrayList<>();
        QueryWrapper<CourseSelect>qcs=new QueryWrapper<>();
        QueryWrapper<Course>qc=new QueryWrapper<>();
        qcs.eq("StudentId",stuid).eq("CourseState",1);
        List<CourseSelect>cslist=csmapper.selectList(qcs);
        List<String>cid= cslist.stream().map(CourseSelect::getCourseid).filter(Objects::nonNull).toList();
        qc.eq("CourseYear",courseYear).eq("CourseSemester",courseSemester);
        if(!cid.isEmpty())
        {
            qc.in("CourseId",cid);
        }else{
            return  alist;
        }
        List<Course>clist=cmapper.selectList(qc);
        QueryWrapper<Teacher>qteacher=new QueryWrapper<>();
        QueryWrapper<Classroom>qclassroom=new QueryWrapper<>();
        for (Course course : clist) {
            //获取教师姓名
            qteacher.clear();
            qteacher.eq("TeacherId", course.getTeacherid());
            Teacher teacher= tmapper.selectOne(qteacher);
            String teachername=teacher==null?null:teacher.getTeachername();
            //获取课程教师
            qclassroom.clear();
            qclassroom.eq("ClassRoomId", course.getClassroomid());
            Classroom classroom = crmapper.selectOne(qclassroom);
            String courseroom = classroom.getClassroomlou() + classroom.getClassroomqu() + "-" + classroom.getClassroomname();
            alist.add(new CourseVO(course, teachername, 0, courseroom));
        }
        return alist;
    }

    private Integer getStage(String keyword) {
        try{
            if(keyword.equals("待处理"))return 0;
            if(keyword.equals("同意")) return 1;
        }
        catch (Exception e){
            return null;
        }
        return  null;
    }

    private List<String> getCourseList(String keyword) {
        QueryWrapper<Course>QCourse=new QueryWrapper<>();
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
        QCourse.or(wrapper -> wrapper.like("CourseName", keyword)
                        .or().like("Academy",keyword)
                        .or().inSql("TeacherId", inClause0)
                        .or().inSql("ClassRoomId",inClause1));
        List<Course>tlist=cmapper.selectList(QCourse);
        return tlist.stream()
                .map(Course::getCourseid)
                .filter(id->id!=null)
                .collect(Collectors.toList());
    }

    private List<String> getClassRoomList(String keyword) {
        QueryWrapper<Classroom>classroomlist=new QueryWrapper<>();
        classroomlist.or(wrapper->wrapper.like("ClassRoomQu",keyword)
                .or().like("ClassRoomName",keyword)
                .or().like("ClassRoomLou",keyword)

        );
        List<Classroom>t=crmapper.selectList(classroomlist);
        return t.stream()
                .map(Classroom::getClassroomid)
                .filter(id->id!=null)
                .collect(Collectors.toList());
    }

    private List<String> getStudentList(String keyword) {
        QueryWrapper<Student>studentlist=new QueryWrapper<>();
        studentlist.or(wrapper->wrapper.like("StudentName",keyword));
        List<Student>t=smapper.selectList(studentlist);
        return t.stream()
                .map(Student::getStudentid)
                .filter(id->id!=null)
                .collect(Collectors.toList());
    }

    private List<String> getTeacherList(String keyword) {
        QueryWrapper<Teacher>teacherlist=new QueryWrapper<>();
        teacherlist.or(wrapper->wrapper.like("TeacherName",keyword));
        List<Teacher>t=tmapper.selectList(teacherlist);
        return t.stream()
                .map(Teacher::getTeacherid)
                .filter(teacherid->teacherid!=null)
                .collect(Collectors.toList());
    }


}
