package com.lwj.FinalServer.web.net.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwj.FinalServer.model.entity.Am;
import com.lwj.FinalServer.web.net.mapper.AmMapper;
import com.lwj.FinalServer.web.net.service.AMService;
import com.lwj.FinalServer.web.net.vo.AmVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AMServiceImpl extends ServiceImpl<AmMapper, Am> implements AMService {

    @Autowired
    private AmMapper amMapper;
    @Override
    public List<AmVO> getAmList() {
        List<Am> ams = amMapper.selectList(null);
        List<AmVO> amvos=new ArrayList<>();
        Map<String,Integer>index=new HashMap<>();
        int cnt=0;
        for (Am am:ams){
            int i=index.getOrDefault(am.getAcademy(),-1);
            if(i==-1){
                amvos.add(new AmVO(am.getAcademy(),am.getAcademy(),am.getMajor(),am.getAmid()));
                index.put(am.getAcademy(),cnt);
                cnt++;
            }
            else{
                amvos.get(i).children.add(new AmVO.Major(am.getMajor(),am.getAmid()));
            }
        }
        return amvos;
    }
}
