package com.lwj.FinalServer.web.net.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwj.FinalServer.model.entity.Am;
import com.lwj.FinalServer.model.entity.Classes;
import com.lwj.FinalServer.model.entity.Student;
import com.lwj.FinalServer.model.entity.Teacher;
import com.lwj.FinalServer.web.net.mapper.AmMapper;
import com.lwj.FinalServer.web.net.mapper.ClassesMapper;
import com.lwj.FinalServer.web.net.mapper.StudentMapper;
import com.lwj.FinalServer.web.net.mapper.TeacherMapper;
import com.lwj.FinalServer.web.net.service.ClassesService;
import com.lwj.FinalServer.web.net.service.StudentService;
import com.lwj.FinalServer.web.net.vo.ClassVO;
import com.lwj.FinalServer.web.net.vo.PageBean;
import com.lwj.FinalServer.web.net.vo.StudentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClassesServiceImpl extends ServiceImpl<ClassesMapper, Classes> implements ClassesService {
    @Autowired
    private ClassesMapper cmapper;
    @Autowired
    private TeacherMapper tmapper;
    @Autowired
    private StudentMapper smapper;
    @Autowired
    private AmMapper amapper;
    @Override
    public PageBean<ClassVO> getClassesPage(Integer PageNum, Integer PageSize, String keyword) {
        PageBean<ClassVO>ans=new PageBean<>();
        Page<Classes>setPage=new Page<>(PageNum,PageSize);
        QueryWrapper<Classes>QClass=new QueryWrapper<>();
        QueryWrapper<Am>QAm=new QueryWrapper<>();
        QueryWrapper<Teacher>Qteacher=new QueryWrapper<>();
        QueryWrapper<Student>Qstudent=new QueryWrapper<>();
        if(keyword!=null){
            List<String> amIds = getAMList(keyword);
            List<String>teacherIds=getTeacherList(keyword);
            List<String>StudentIds=getStudentList(keyword);
            if (amIds.isEmpty()) {
                amIds.add("SELECT 1 WHERE 1=0"); // 使用不可能存在的值
            }
            if (teacherIds.isEmpty()) {
                teacherIds.add("SELECT 1 WHERE 1=0"); // 使用不可能存在的值
            }
            if (StudentIds.isEmpty()) {
                StudentIds.add("SELECT 1 WHERE 1=0"); // 使用不可能存在的值
            }
            String inClause0 = String.join(", ", amIds.stream()
                    .map(amId -> "'" + amId + "'") // 用单引号括起来
                    .collect(Collectors.toList()));
            String inClause1 = String.join(", ",teacherIds.stream()
                    .map(Id -> "'" + Id + "'") // 用单引号括起来
                    .collect(Collectors.toList()));
            String inClause2 = String.join(", ", StudentIds.stream()
                    .map(amId -> "'" + amId + "'") // 用单引号括起来
                    .collect(Collectors.toList()));
            QClass.or(wrapper->wrapper
                    .like("ClassName",keyword)
                    .or().inSql("AMId",inClause0)
                    .or().inSql("TeacherId",inClause1)
                    .or().inSql("StudentId",inClause2)
            );

        }


        Page<Classes>Cpage=cmapper.selectPage(setPage,QClass);
        ans.setCount(Cpage.getTotal());
        List<Classes>list=Cpage.getRecords();
        List<ClassVO>alist=new ArrayList<>();
        for(Classes t:list){
            QAm.clear();
            Qteacher.clear();
            Qstudent.clear();
            QAm.eq("AMId",t.getAmid());
            Qstudent.eq("StudentId",t.getStudentid());
            Qteacher.eq("TeacherId",t.getTeacherid());
            Am am=amapper.selectOne(QAm);
            //获取学生
            Student s=smapper.selectOne(Qstudent);
            String sname=s==null?null:s.getStudentname();
            String sphone=s==null?null:s.getStudentphone();
//            获取教师
            Teacher teacher=tmapper.selectOne(Qteacher);
            String tname=teacher==null?null:teacher.getTeachername();
            String tphone=teacher==null?null:teacher.getTeacherphone();
            alist.add(new ClassVO(t,tname,tphone,sname,sphone,am.getAcademy(),am.getMajor()));
        }
        ans.setList(alist);
        return ans;
    }

    private List<String> getStudentList(String keyword) {
        QueryWrapper<Student>studentlist=new QueryWrapper<>();
        studentlist.or(wrapper->wrapper.like("StudentName",keyword)
                .or().like("StudentPhone",keyword));
        List<Student>t=smapper.selectList(studentlist);
        return t.stream()
                .map(Student::getStudentid)
                .filter(id->id!=null)
                .collect(Collectors.toList());
    }

    private List<String> getTeacherList(String keyword) {
        QueryWrapper<Teacher>teacherlist=new QueryWrapper<>();
        teacherlist.or(wrapper->wrapper.like("TeacherName",keyword)
                .or().like("TeacherPhone",keyword));
        List<Teacher>t=tmapper.selectList(teacherlist);
        return t.stream()
                .map(Teacher::getTeacherid)
                .filter(teacherid->teacherid!=null)
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
}
