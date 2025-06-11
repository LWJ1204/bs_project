package com.lwj.FinalServer.web.net.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwj.FinalServer.model.entity.Am;
import com.lwj.FinalServer.model.entity.Teacher;
import com.lwj.FinalServer.web.net.mapper.AmMapper;
import com.lwj.FinalServer.web.net.mapper.TeacherMapper;
import com.lwj.FinalServer.web.net.service.TeacherService;
import com.lwj.FinalServer.web.net.vo.PageBean;
import com.lwj.FinalServer.web.net.vo.TeacherVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
    @Autowired
    private AmMapper amMapper;
    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public PageBean<TeacherVO> getTeacherPage(Integer PageNum, Integer PageSize, String keyword) {
        PageBean<TeacherVO> ans = new PageBean<>();
        Page<Teacher> setPage = new Page<>(PageNum, PageSize);
        QueryWrapper<Teacher> Qteacher = new QueryWrapper<>();
        QueryWrapper<Am> QAM = new QueryWrapper<>();

        if (keyword != null) {
            List<String> amIds = getAMList(keyword);
            if (amIds.isEmpty()) {
                amIds.add("SELECT 1 WHERE 1=0"); // 使用不可能存在的值
            }
            String inClause = String.join(", ", amIds.stream()
                    .map(amId -> "'" + amId + "'") // 用单引号括起来
                    .collect(Collectors.toList()));
            Qteacher.or(wrapper -> wrapper
                    .like("TeacherName", keyword)
                    .or().like("TeacherId", keyword)
                    .or().eq("TeacherSex", getSex(keyword))
                    .or().eq("TeacherTitle", getTitle(keyword))
                    .or().inSql("AMId", inClause)
            );
        }

        Page<Teacher> tpage = teacherMapper.selectPage(setPage, Qteacher);
        ans.setCount(tpage.getTotal());
        List<Teacher> list = tpage.getRecords();
        List<TeacherVO> alist = new ArrayList<>();

        for (Teacher t : list) {
            QAM.clear();
            QAM.eq("AMId", t.getAmid());
            Am am = amMapper.selectOne(QAM);
            alist.add(new TeacherVO(t, am != null ? am.getMajor() : null, am != null ? am.getAcademy() : null));
        }

        ans.setList(alist);
        Qteacher.clear();
        return ans;
    }

    @Override
    public List<Teacher> querryByAcademy(String academy) {
        QueryWrapper<Teacher>Qteacher=new QueryWrapper<>();
        List<String> amIds = getAMList(academy);
        if (amIds.isEmpty()) {
            amIds.add("SELECT 1 WHERE 1=0"); // 使用不可能存在的值
        }
        String inClause = String.join(", ", amIds.stream()
                .map(amId -> "'" + amId + "'") // 用单引号括起来
                .collect(Collectors.toList()));
        Qteacher.or(wrapper -> wrapper
                .or().inSql("AMId", inClause));
        return teacherMapper.selectList(Qteacher);
    }

    private List<String> getAMList(String keyword) {
        QueryWrapper<Am> amlist = new QueryWrapper<>();
        amlist.or(wrapper -> wrapper
                .like("Academy", keyword)
                .or().like("Major", keyword)
        );
        List<Am> t = amMapper.selectList(amlist);
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

    public Integer getTitle(String keyword) {
        if ("教授".equals(keyword)) return 0;
        else if ("副教授".equals(keyword)) return 1;
        else if ("讲师".equals(keyword)) return 2;
        else if ("助教".equals(keyword)) return 3;
        return null; // 返回 null 表示不匹配
    }
}