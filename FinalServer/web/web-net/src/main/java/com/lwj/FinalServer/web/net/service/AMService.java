package com.lwj.FinalServer.web.net.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lwj.FinalServer.model.entity.Am;
import com.lwj.FinalServer.web.net.vo.AmVO;

import java.util.List;

public interface AMService extends IService<Am> {
    public List<AmVO>getAmList();
}
