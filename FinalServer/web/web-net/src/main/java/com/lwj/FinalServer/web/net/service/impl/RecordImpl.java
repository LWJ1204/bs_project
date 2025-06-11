package com.lwj.FinalServer.web.net.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwj.FinalServer.model.entity.*;
import com.lwj.FinalServer.web.net.controller.SendService;
import com.lwj.FinalServer.web.net.mapper.*;
import com.lwj.FinalServer.web.net.service.RecordStateService;
import com.lwj.FinalServer.web.net.vo.PageBean;
import com.lwj.FinalServer.web.net.vo.RecordStateVO;
import com.lwj.FinalServer.web.net.vo.RecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RecordImpl  extends ServiceImpl<RecordStateMapper, RecordState> implements RecordStateService {

    @Autowired
    private RecordStateMapper rmapper;
    @Autowired
    private RecordMapper mrmapper;
    @Autowired
    private CourseMapper cmapper;
    @Autowired
    private ClassroomMapper crmapper;
    @Autowired
    private TeacherMapper tmapper;
    @Autowired
    private StudentMapper smapper;
    @Autowired
    private SendService sendservice;
    @Autowired
    private CourseSelectMapper csmapper;
    @Override
    public PageBean<RecordStateVO> getPageRecord(Integer PageNum, Integer PageSize, String keyword, String teacherid) {
        PageBean<RecordStateVO> ans=new PageBean<>();
        Page<RecordState> setpage=new Page<>(PageNum,PageSize);
        QueryWrapper<mRecord>qRecord=new QueryWrapper<>();
        QueryWrapper<RecordState>qRecordState=new QueryWrapper<>();
        QueryWrapper<Course>qCourse=new QueryWrapper<>();
        QueryWrapper<Classroom>qClassRoom=new QueryWrapper<>();
        QueryWrapper<Teacher>qTeacher=new QueryWrapper<>();
        QueryWrapper<Student>qStudent=new QueryWrapper<>();
        if(teacherid!=null){
            List<Integer>recordid=getListRecord(teacherid);
            if (!recordid.isEmpty()) {
              qRecordState.and(wrapper->wrapper.in("RecordId",recordid));
            }else{
                ans.setList(new ArrayList<>());
                return  ans;
            }
        }
        if(keyword!=null){
            List<Integer>RecordIds=getListRecord(keyword);
            List<String>StudentIds=getListStudent(keyword);
            if (!RecordIds.isEmpty()) {
                qRecordState.and(wrapper->wrapper.in("RecordId",RecordIds));
            }
            if(!StudentIds.isEmpty()){
                qRecordState.and(wrapper->wrapper.in("StudentId",StudentIds));
            }
            if(getState(keyword)!=-1){
                qRecordState.and(wrapper->wrapper.eq("RecordState",getState(keyword)));
            }
        }
        Page<RecordState>page=rmapper.selectPage(setpage,qRecordState);
        ans.setCount(page.getTotal());
        List<RecordState>list=page.getRecords();
        List<RecordStateVO>alist=new ArrayList<>();
        String CourseName=null,CourseRoom=null,StudentName=null,TeacherName=null,CourseId=null;
        for(RecordState m:list){

            qRecord.clear();
            Integer recordid=m.getRecordid();
            qRecord.eq("RecordId",recordid);
            mRecord mrecord=mrmapper.selectOne(qRecord);
            if(mrecord!=null){
                qCourse.clear();
                qCourse.eq("CourseId",mrecord.getCourseid());
                Course course=cmapper.selectOne(qCourse);
                if(course!=null) {
                    CourseId=course.getCourseid();
                    CourseName = course.getCoursename();
                    qClassRoom.clear();
                    qClassRoom.eq("ClassRoomId", mrecord.getClassroomid());
                    Classroom classroom = crmapper.selectOne(qClassRoom);
                    CourseRoom = classroom == null ? null : classroom.getClassroomlou() + classroom.getClassroomqu() + "-" + classroom.getClassroomname();
                    qTeacher.clear();
                    qTeacher.eq("TeacherId", course.getTeacherid());
                    Teacher t = tmapper.selectOne(qTeacher);
                    TeacherName = t == null ? null : t.getTeachername();
                }
            }
            qStudent.clear();
            qStudent.eq("StudentId",m.getStudentid());
            Student s=smapper.selectOne(qStudent);
            StudentName=s==null?null:s.getStudentname();
            alist.add(new RecordStateVO(m,CourseName,CourseRoom,TeacherName,StudentName, CourseId,mrecord.getRecordsettime()));
        }
    ans.setList(alist);
        return ans;
    }

    @Override
    public mRecord create(String courseId, String courseRoom, Date setTime, Date endTime) {
        mRecord record=new mRecord();
        record.setCourseid(courseId);
        record.setRecordsettime(setTime);
        record.setRecordendtime(endTime);
        record.setClassroomid(courseRoom);
        int id=mrmapper.insert(record);

        //查询选课学生
        QueryWrapper<CourseSelect>qstu=new QueryWrapper<>();
        qstu.eq("CourseId",courseId).eq("CourseState",1);

        if(id>0){
            //获取选课学生列表
            List<CourseSelect>stulist=csmapper.selectList(qstu);
            //初始化签到状态为未签到
            List<RecordState>recordlist=new ArrayList<>();
            for (CourseSelect courseSelect:stulist){
                recordlist.add(new RecordState(record.getRecordid(),courseSelect.getStudentid(),1,null,null));
            }
            //签到的课程
            QueryWrapper<Course>qc=new QueryWrapper<>();
            qc.eq("CourseId",record.getCourseid());
            Course course=cmapper.selectOne(qc);
            //签到的教室
            QueryWrapper<Classroom>qcr=new QueryWrapper<>();
            qcr.eq("ClassRoomId",course.getClassroomid());
            Classroom classroom=crmapper.selectOne(qcr);
            String classroomname=classroom.getClassroomlou()+classroom.getClassroomqu()+"-"+classroom.getClassroomname();
            //返回创建结果
            RecordVo recordVo=new RecordVo(record,course.getCoursename(),classroomname,0,1);
            //广播签到要求
            sendservice.sendMessage(courseId,courseId,recordVo);
            rmapper.insert(recordlist);
            return record;

        }
        return null;
    }

    @Override
    public PageBean<RecordStateVO> getPageMyRecordState(Integer pageNum, Integer pageSize, String keyWord, Integer state, Integer recordId) {
        PageBean<RecordStateVO>ans=new PageBean<>();
        Page<RecordState>setpage=new Page<>(pageNum,pageSize);
        QueryWrapper<mRecord>qRecord=new QueryWrapper<>();
        QueryWrapper<Course>qCourse=new QueryWrapper<>();
        QueryWrapper<Classroom>qClassRoom=new QueryWrapper<>();
        QueryWrapper<Teacher>qTeacher=new QueryWrapper<>();
        QueryWrapper<Student>qStudent=new QueryWrapper<>();
        if(keyWord!=null){

        }
        QueryWrapper<RecordState> qrs=new QueryWrapper<>();
        qrs.eq("RecordId",recordId).eq("RecordState",state);
        Page<RecordState>page=rmapper.selectPage(setpage,qrs);
        if(page.getCurrent()>page.getPages()){
            setpage=new Page<>(page.getPages(),pageSize);
            page=rmapper.selectPage(setpage,qrs);
        }
        ans.setCount(page.getTotal());
        List<RecordState>tl=page.getRecords();
        List<RecordStateVO>list=new ArrayList<>();
        String CourseName=null,CourseRoom=null,StudentName=null,TeacherName=null,CourseId=null;
        for(RecordState trs:tl){
            qRecord.clear();
            Integer recordid=trs.getRecordid();
            qRecord.eq("RecordId",recordid);
            mRecord mrecord=mrmapper.selectOne(qRecord);
            if(mrecord!=null){
                qCourse.clear();
                qCourse.eq("CourseId",mrecord.getCourseid());
                Course course=cmapper.selectOne(qCourse);
                if(course!=null) {
                    CourseId=course.getCourseid();
                    CourseName = course.getCoursename();
                    qClassRoom.clear();
                    qClassRoom.eq("ClassRoomId", mrecord.getClassroomid());
                    Classroom classroom = crmapper.selectOne(qClassRoom);
                    CourseRoom = classroom == null ? null : classroom.getClassroomlou() + classroom.getClassroomqu() + "-" + classroom.getClassroomname();
                    qTeacher.clear();
                    qTeacher.eq("TeacherId", course.getTeacherid());
                    Teacher t = tmapper.selectOne(qTeacher);
                    TeacherName = t == null ? null : t.getTeachername();
                }
            }
            qStudent.clear();
            qStudent.eq("StudentId",trs.getStudentid());
            Student s=smapper.selectOne(qStudent);
            StudentName=s==null?null:s.getStudentname();
            list.add(new RecordStateVO(trs,CourseName,CourseRoom,TeacherName,StudentName, CourseId,null));
        }
        ans.setList(list);
        return ans;
    }

    @Override
    public List<RecordStateVO> getStuRecordList(String stuid, String courseID) {
        List<RecordStateVO> ans=new ArrayList<>();
        QueryWrapper<RecordState>qrs=new QueryWrapper<>();
        QueryWrapper<mRecord>qr=new QueryWrapper<>();
        qr.eq("CourseId",courseID);
        List<mRecord> rlist=mrmapper.selectList(qr);
        List<Integer> recordids=rlist.stream().map(mRecord::getRecordid).filter(Objects::nonNull).toList();
        qrs.eq("StudentId",stuid).in("RecordId",recordids);
        List<RecordState> rslist=rmapper.selectList(qrs);
        QueryWrapper<Classroom>qClassRoom=new QueryWrapper<>();
        QueryWrapper<Teacher>qTeacher=new QueryWrapper<>();
        QueryWrapper<Course>qc=new QueryWrapper<>();
        String CourseName=null,CourseRoom=null,StudentName=null,TeacherName=null,CourseId=null;
        QueryWrapper<Student>qs=new QueryWrapper<>();
        qs.eq("StudentId",stuid);
        StudentName=smapper.selectOne(qs).getStudentname();
        for(RecordState trs:rslist){
            qr.clear();
            Integer recordid=trs.getRecordid();
            qr.eq("RecordId",recordid);
            mRecord mrecord=mrmapper.selectOne(qr);
            if(mrecord!=null){
                qc.clear();
                qc.eq("CourseId",mrecord.getCourseid());
                Course course=cmapper.selectOne(qc);
                if(course!=null) {
                    CourseId=course.getCourseid();
                    CourseName = course.getCoursename();
                    qClassRoom.clear();
                    qClassRoom.eq("ClassRoomId", mrecord.getClassroomid());
                    Classroom classroom = crmapper.selectOne(qClassRoom);
                    CourseRoom = classroom == null ? null : classroom.getClassroomlou() + classroom.getClassroomqu() + "-" + classroom.getClassroomname();
                    qTeacher.clear();
                    qTeacher.eq("TeacherId", course.getTeacherid());
                    Teacher t = tmapper.selectOne(qTeacher);
                    TeacherName = t == null ? null : t.getTeachername();
                }
            }

            ans.add(new RecordStateVO(trs,CourseName,CourseRoom,TeacherName,StudentName, CourseId,mrecord.getRecordsettime()));
        }
        return ans;
    }

    private List<Integer>getListRecord(String keyword){
        QueryWrapper<mRecord>qr=new QueryWrapper<>();
        List<String> courseList=getCourseList(keyword);
        if(!courseList.isEmpty())
        {
            qr.or().in("CourseId",courseList);
            List<mRecord>t=mrmapper.selectList(qr);
            return t.stream()
                    .map(mRecord::getRecordid)
                    .filter(id -> id != null)
                    .collect(Collectors.toList());
        }
        List<Integer> t=new ArrayList<>();
        return t;
    }


    private List<String> getCourseList(String keyword) {
        QueryWrapper<Course>qr=new QueryWrapper<>();
        List<String> ClassRoomIds=getClassRoomList(keyword);
        List<String>TeacherIds=getTeacherList(keyword);

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
        qr.or(wrapper -> wrapper
                .like("CourseId", keyword)
                .or().like("CourseName", keyword)
                .or().inSql("TeacherId", inClause0)
                .or().inSql("ClassRoomId",inClause1));

        List<Course>t=cmapper.selectList(qr);
        return t.stream()
                .map(Course::getCourseid)
                .filter(id -> id != null)
                .collect(Collectors.toList());
    }

    private List<String> getTeacherList(String keyword) {
        QueryWrapper<Teacher> list = new QueryWrapper<>();
        list.or(wrapper -> wrapper
                .like("TeacherName", keyword)
                .or().like("TeacherId",keyword)
        );
        List<Teacher> t = tmapper.selectList(list);
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

    private List<String>getListStudent(String keyword){
        QueryWrapper<Student>qs=new QueryWrapper<>();
        qs.eq("StudentName",keyword);
        List<Student>sl=smapper.selectList(qs);
        return sl.stream()
                .map(Student::getStudentid)
                .filter(id -> id != null)
                .collect(Collectors.toList());
    }

    private Integer getState(String keyword) {
        try{
            if(keyword.equals("已签到")) return 0;
            if(keyword.equals("未签到"))return 1;
            if(keyword.equals("病假")) return 2;
        }catch (Exception e){
            return null;
        }
        return  -1;
    }
}
