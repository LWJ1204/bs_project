package com.lwj.FinalServer.web.net.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwj.FinalServer.model.entity.*;
import com.lwj.FinalServer.web.net.mapper.*;
import com.lwj.FinalServer.web.net.service.CourseService;
import com.lwj.FinalServer.web.net.service.StudentService;
import com.lwj.FinalServer.web.net.vo.PageBean;
import com.lwj.FinalServer.web.net.vo.StudentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    @Autowired
    private StudentMapper smapper;
    @Autowired
    private ClassesMapper cmapper;
    @Autowired
    private AmMapper amapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseSelectMapper csmapper;

    @Override
    public PageBean<StudentVO> getStudentPage(Integer PageNum, Integer PageSize, String keyword) {
        PageBean<StudentVO> ans = new PageBean<>();
        Page<Student> setPage = new Page<>(PageNum, PageSize);
        QueryWrapper<Student> QStudent = new QueryWrapper<>();
        QueryWrapper<Classes>QClass=new QueryWrapper<>();
        if (keyword != null) {
            List<Integer> classIds = getClassList(keyword);

            // 动态构建查询条件
            QStudent.or(wrapper -> wrapper
                    .like("StudentName", keyword)
                    .or().like("StudentId", keyword)
                    .or().eq("StudentSex", getSex(keyword))
            );


            // 处理 ClassId 的 IN 子句
            if (!classIds.isEmpty()) {
                String inClause1 = String.join(", ", classIds.stream()
                        .map(id -> "'" + id + "'") // 用单引号括起来
                        .collect(Collectors.toList()));
                QStudent.or().inSql("ClassId", inClause1);
            }
        }

        Page<Student> tpage = smapper.selectPage(setPage, QStudent);
        ans.setCount(tpage.getTotal());
        List<Student> list = tpage.getRecords();
        List<StudentVO> alist = new ArrayList<>();
        Am am = null;
        Classes classes = null;

        for (Student t : list) {
            QClass.clear();
            QClass.eq("ClassId", t.getClassid());
            classes = cmapper.selectOne(QClass);

            if (classes != null) {
                QueryWrapper<Am> QAM = new QueryWrapper<>();
                QAM.eq("AMId", classes.getAmid());
                am = amapper.selectOne(QAM);
                String academy = am == null ? null : am.getAcademy();
                String major = am != null ? am.getMajor() : null;
                alist.add(new StudentVO(t, major, academy, classes.getClassname(), academy, null, null));

            } else {
                alist.add(new StudentVO(t, null, null, null, null, null, null));
            }
        }
        ans.setList(alist);
        return ans;

    }

    @Override
    public PageBean<StudentVO> getMyStudentPage(Integer pageNum, Integer pageSize,  String keyword,String teacherid) {
        PageBean<StudentVO> ans = new PageBean<>();
        Page<CourseSelect> setPage = new Page<>(pageNum, pageSize);
        QueryWrapper<Student> QStudent = new QueryWrapper<>();
        QueryWrapper<Classes>QClass=new QueryWrapper<>();
        QueryWrapper<Course>qcourse=new QueryWrapper<>();
        QueryWrapper<CourseSelect> qcs=new QueryWrapper<>();
        qcs.eq("CourseState",1);

            List<String>courseid0=getCourseid(teacherid);
            if(courseid0.isEmpty()){
                courseid0.add("SELECT 1 WHERE 1=0");
            }
            String inClause = String.join(", ", courseid0.stream()
                    .map(Id -> "'" + Id + "'") // 用单引号括起来
                    .collect(Collectors.toList()));
            qcs.inSql("CourseId", inClause);


        if(keyword!=null){
            List<String>courseid=getCourseidBykey(teacherid,keyword);
            List<String>studentid=getStuentidBykey(teacherid,keyword);
            if(courseid.isEmpty()){
                courseid.add("SELECT 1 WHERE 1=0");
            }
            if(studentid.isEmpty()){
                studentid.add("SELECT 1 WHERE 1=0");
            }
            String inClause0 = String.join(", ", courseid.stream()
                    .map(Id -> "'" + Id + "'") // 用单引号括起来
                    .collect(Collectors.toList()));
            String inClause1 = String.join(", ", studentid.stream()
                    .map(Id -> "'" + Id + "'") // 用单引号括起来
                    .collect(Collectors.toList()));
            qcs.and(wrapper->wrapper.or().inSql("CourseId",inClause0)
                    .or().inSql("StudentId",inClause1));
        }
        Page<CourseSelect>page=csmapper.selectPage(setPage,qcs);
        ans.setCount(page.getTotal());
        List<CourseSelect>list=page.getRecords();
        List<StudentVO>alist=new ArrayList<>();
        for(CourseSelect cs:list){
            QStudent.clear();
            QStudent.eq("StudentId",cs.getStudentid());
            Student s=smapper.selectOne(QStudent);
            qcourse.clear();
            qcourse.eq("CourseId",cs.getCourseid());
            Course course=courseMapper.selectOne(qcourse);
            QClass.clear();
            QClass.eq("ClassId",s.getClassid());
            Classes mclass=cmapper.selectOne(QClass);
            alist.add(new StudentVO(s,null,null,mclass==null?null:mclass.getClassname(),null,course.getCourseid(),course.getCoursename()));
        }
        ans.setList(alist);
        return ans;
    }

    @Override
    public StudentVO getStu(String studentId) {
        QueryWrapper<Student>qs=new QueryWrapper<>();
        qs.eq("StudentId",studentId);
        Student stu=smapper.selectOne(qs);
        QueryWrapper<Classes>qc=new QueryWrapper<>();
        qc.eq("ClassId",stu.getClassid());
        Classes mclass=cmapper.selectOne(qc);
        QueryWrapper<Am>qam=new QueryWrapper<>();
        qam.eq("AMId",mclass.getAmid());
        Am am=amapper.selectOne(qam);
        StudentVO studentVO=new StudentVO(stu,am.getMajor(),am.getAcademy(),mclass.getClassname(),null,null,null);
        return studentVO;
    }

    private List<String> getStuentidBykey(String teacherid, String keyword) {
        QueryWrapper<Student>qs=new QueryWrapper<>();
        List<Integer> classid=getClassList(keyword);
        List<String>stuids=getStudentList(teacherid);
        // 处理 ClassId 的 IN 子句
        if (!classid.isEmpty()) {
            String inClause0= String.join(", ", classid.stream()
                    .map(id -> "'" + id + "'") // 用单引号括起来
                    .collect(Collectors.toList()));
            qs.or().inSql("ClassId", inClause0);
        }
        if(stuids.isEmpty()){
            stuids.add("SELECT 1 WHERE 1=0");
        }
        String insql1= String.join(", ", stuids.stream()
                .map(id -> "'" + id + "'") // 用单引号括起来
                .collect(Collectors.toList()));
        qs.or().like("StudentName",keyword)
                .or().like("StudentId",keyword)
                .or().like("StudentPhone",keyword)
                .inSql("StudentId",insql1);
        List<Student>t=smapper.selectList(qs);
        return t.stream()
                .map(Student::getStudentid)
                .filter(id -> id != null)
                .collect(Collectors.toList());
    }

    private List<String> getStudentList(String tid) {
        List<String>courseid=getCourseidBykey(tid,null);
        if(courseid.isEmpty()){
            courseid.add("SELECT 1 WHERE 1=0");
        }
        QueryWrapper<CourseSelect>qcs=new QueryWrapper<>();
        String inClause0= String.join(", ", courseid.stream()
                .map(id -> "'" + id + "'") // 用单引号括起来
                .collect(Collectors.toList()));
        qcs.inSql("CourseId",inClause0);
        List<CourseSelect> t=csmapper.selectList(qcs);
         return t.stream()
                .map(CourseSelect::getStudentid)
                .filter(id -> id != null)
                .collect(Collectors.toList());
    }

    private List<String> getCourseidBykey(String teacherid, String keyword) {
        QueryWrapper<Course>qc=new QueryWrapper<>();
        qc.or().like("CourseName",keyword)
                .or().like("CourseId",keyword)
                .eq("TeacherId",teacherid);
        List<Course>t=courseMapper.selectList(qc);
        return t.stream()
                .map(Course::getCourseid)
                .filter(id -> id != null)
                .collect(Collectors.toList());
    }

    private List<String> getCourseid(String teacherid) {
        QueryWrapper<Course>qc=new QueryWrapper<>();
        qc.eq("TeacherId",teacherid);
        List<Course>t=courseMapper.selectList(qc);
        return t.stream()
                .map(Course::getCourseid)
                .filter(id -> id != null)
                .collect(Collectors.toList());
    }

    private List<Integer> getClassList(String keyword) {
        List<String> amIds = getAMList(keyword);
        if (amIds.isEmpty()) {
            amIds.add("SELECT 1 WHERE 1=0"); // 使用不可能存在的值
        }
        String inClause0 = String.join(", ", amIds.stream()
                .map(amId -> "'" + amId + "'") // 用单引号括起来
                .collect(Collectors.toList()));
        QueryWrapper<Classes> list = new QueryWrapper<>();
        list.or(wrapper -> wrapper
                .like("ClassName", keyword)
                .or().inSql("AMId",inClause0)
        );
        List<Classes> t = cmapper.selectList(list);
        return t.stream()
                .map(Classes::getClassid)
                .filter(id -> id != null)
                .collect(Collectors.toList());
    }

    private List<String> getAMList(String keyword) {
        QueryWrapper<Am> amlist = new QueryWrapper<>();
        amlist.or(wrapper -> wrapper
                .like("Academy", keyword)
                .or().like("Major", keyword)
        );
        List<Am> t = amapper.selectList(amlist);
        return t.stream()
                .map(Am::getAmid)
                .filter(amid -> amid != null)
                .collect(Collectors.toList());
    }

    public Integer getSex(String keyword) {
        if ("女".equals(keyword)) return 0;
        else if ("男".equals(keyword)) return 1;
        return null; // 返回 null 表示不匹配
    }

}
