package com.lwj.FinalServer.web.net.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwj.FinalServer.common.Utils.JwtUtil;
import com.lwj.FinalServer.model.entity.mUser;
import com.lwj.FinalServer.web.net.mapper.mUserMapper;
import com.lwj.FinalServer.web.net.service.PermissionService;
import com.lwj.FinalServer.web.net.vo.CaptchaVo;
import com.lwj.FinalServer.web.net.vo.LoginVo;
import com.lwj.FinalServer.web.net.vo.Menu;
import com.lwj.FinalServer.web.net.vo.MenuItem;
import com.wf.captcha.SpecCaptcha;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class PermissionImpl extends ServiceImpl<mUserMapper,mUser> implements PermissionService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private mUserMapper mapper;
    @Override
    public CaptchaVo getCaptcha() {
        SpecCaptcha specCaptcha=new SpecCaptcha(130,48,4);
        specCaptcha.setCharType(SpecCaptcha.TYPE_DEFAULT);
        String code=specCaptcha.text().toLowerCase();
        String key = "net:login:" + UUID.randomUUID();
        String image = specCaptcha.toBase64();
        redisTemplate.opsForValue().set(key, code, 60,TimeUnit.SECONDS);

        return new CaptchaVo(image, key);
    }

    @Override
    public LoginVo login(Map data, String ip) {
        LoginVo loginVo=new LoginVo();

        Integer role= (Integer) data.get("role");
        String code=redisTemplate.opsForValue().get(String.valueOf(data.get("captchakey")));
        if(code==null&&role!=3){
            loginVo.setState("验证码已过期");
            return loginVo;
        }


        if(role==3||code.equalsIgnoreCase((String) data.get("captcha"))){
            //获取传递的参数
            String useranme= (String) data.get("username");
            String pwd= (String) data.get("password");
            //查询设置
            QueryWrapper<mUser>q=new QueryWrapper<>();
            q.eq("Id",useranme).eq("Role",role);
            mUser m=mapper.selectOne(q);
            //未创建该用户
            if(m==null){
                loginVo.setState("不存在该用户");
                return loginVo;
            }
            //用户未注册
            if(m.getPassword()==null){
                loginVo.setState("该用户为注册");
                return loginVo;
            }
            if(!m.getPassword().equals(DigestUtils.md5Hex(pwd))){
                loginVo.setState("用户或密码错误");
                return loginVo;
            }
            //创建token
            String token= JwtUtil.createToke(m.getId(),m.getUsername(),m.getRole(), (String) data.get("fingerprint"),ip);
            loginVo.setToken(token);
            //根据用户返回不同的路由
            if(role==1) loginVo.setMenuList(getAdmin());
            if(role==2)loginVo.setMenuList(getTeacher());
            loginVo.setState("成功登录");
            return loginVo;
        }
        loginVo.setState("验证码输入错误");
        return loginVo;
    }

    @Override
    public String register(Integer role, String username, String password) {
        QueryWrapper<mUser>qu=new QueryWrapper<>();
        qu.eq("Role",role).eq("UserName",username);
        mUser mu=mapper.selectOne(qu);
        password=DigestUtils.md5Hex(password);
        if(mu.getPassword()==null){
            mu.setId(username);
            mu.setPassword(password);
            mapper.update(mu,qu);
            return "注册成功";
        }
        return "该用户已经注册过";
    }


    public static List<Menu> getAdmin() {
        List<Menu> menuList = new ArrayList<>();

        // 首页
        Menu home = new Menu();
        home.setPath("/home");
        home.setName("shouye");
        home.setLabel("首页");
        home.setIcon("house");
        home.setUrl("Home");
        menuList.add(home);

        // 班级管理
        Menu classes = new Menu();
        classes.setPath("/classes");
        classes.setName("banjiguanli");
        classes.setLabel("班级管理");
        classes.setIcon("DataBoard");
        classes.setUrl("Classes");
        menuList.add(classes);

        // 教师管理
        Menu teachers = new Menu();
        teachers.setPath("/teachers");
        teachers.setName("jiaoshiguanli");
        teachers.setLabel("教师管理");
        teachers.setIcon("User");
        teachers.setUrl("Teachers");
        menuList.add(teachers);

        // 学生管理
        Menu students = new Menu();
        students.setPath("/students");
        students.setLabel("学生管理");
        students.setIcon("User");

        MenuItem studentsInfo = new MenuItem();
        studentsInfo.setPath("/students/studentsInfo");
        studentsInfo.setName("xueshengxinxi");
        studentsInfo.setLabel("学生信息");
        studentsInfo.setUrl("Students");

        List<MenuItem> studentsChildren = new ArrayList<>();
        studentsChildren.add(studentsInfo);
        students.setChildren(studentsChildren);
        menuList.add(students);

        // 课程管理
        Menu courses = new Menu();
        courses.setPath("/courses");
        courses.setLabel("课程管理");
        courses.setIcon("Collection");

        MenuItem courseInfo = new MenuItem();
        courseInfo.setPath("/courses/courseInfo");
        courseInfo.setLabel("课程信息");
        courseInfo.setName("kechengxinxi");
        courseInfo.setUrl("CourseInfo");

        MenuItem courseSelected = new MenuItem();
        courseSelected.setPath("/courses/courseSelected");
        courseSelected.setLabel("选课管理");
        courseSelected.setName("xuankeguanli");
        courseSelected.setUrl("CourseSelected");

        List<MenuItem> coursesChildren = new ArrayList<>();
        coursesChildren.add(courseInfo);
        coursesChildren.add(courseSelected);
        courses.setChildren(coursesChildren);
        menuList.add(courses);

        // 考勤管理
        Menu records = new Menu();
        records.setPath("/records");
        records.setLabel("考勤管理");
        records.setIcon("Location");

        MenuItem recordManage = new MenuItem();
        recordManage.setPath("/records/recordManage");
        recordManage.setLabel("考勤记录管理");
        recordManage.setName("kaoqinjiluguanli");
        recordManage.setUrl("recordManage");

        List<MenuItem> recordsChildren = new ArrayList<>();
        recordsChildren.add(recordManage);
        records.setChildren(recordsChildren);
        menuList.add(records);

        return menuList;
    }

    public static List<Menu>getTeacher(){
        List<Menu> menuList = new ArrayList<>();

        // 首页
        Menu home = new Menu();
        home.setPath("/home");
        home.setName("shouye");
        home.setLabel("首页");
        home.setIcon("house");
        home.setUrl("Home");
        menuList.add(home);

        // 学生管理
        Menu students = new Menu();
        students.setPath("/students");
        students.setLabel("学生管理");
        students.setIcon("user");

        MenuItem mystudents = new MenuItem();
        mystudents.setPath("/students/mystudents");
        mystudents.setName("wodexuesheng");
        mystudents.setLabel("授课学生");
        mystudents.setUrl("MyStudents");

        List<MenuItem> studentsChildren = new ArrayList<>();
        studentsChildren.add(mystudents);
        students.setChildren(studentsChildren);
        menuList.add(students);

        // 课程管理
        Menu courses = new Menu();
        courses.setPath("/courses");
        courses.setLabel("课程管理");
        courses.setIcon("Collection");

        MenuItem courseInfo = new MenuItem();
        courseInfo.setPath("/courses/courseInfo");
        courseInfo.setLabel("课程信息");
        courseInfo.setName("kechengxinxi");
        courseInfo.setUrl("CourseInfo");

        List<MenuItem> coursesChildren = new ArrayList<>();
        coursesChildren.add(courseInfo);
        courses.setChildren(coursesChildren);
        menuList.add(courses);

        // 考勤管理
        Menu records = new Menu();
        records.setPath("/records");
        records.setLabel("考勤管理");
        records.setIcon("Location");

        MenuItem recordManage = new MenuItem();
        recordManage.setPath("/records/recordManage");
        recordManage.setLabel("考勤记录管理");
        recordManage.setName("kaoqinjiluguanli");
        recordManage.setUrl("recordManage");

        MenuItem recordSet = new MenuItem();
        recordSet.setPath("/records/recordSet");
        recordSet.setLabel("考勤设置");
        recordSet.setName("kaoqinshezhi");
        recordSet.setUrl("recordSet");

        List<MenuItem> recordsChildren = new ArrayList<>();
        recordsChildren.add(recordManage);
        recordsChildren.add(recordSet);
        records.setChildren(recordsChildren);
        menuList.add(records);

        return menuList;
    }
}
