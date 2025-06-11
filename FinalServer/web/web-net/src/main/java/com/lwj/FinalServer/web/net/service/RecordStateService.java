package com.lwj.FinalServer.web.net.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lwj.FinalServer.model.entity.RecordState;
import com.lwj.FinalServer.model.entity.mRecord;
import com.lwj.FinalServer.web.net.vo.PageBean;
import com.lwj.FinalServer.web.net.vo.RecordStateVO;

import java.util.Date;
import java.util.List;

public interface RecordStateService extends IService<RecordState> {
    public PageBean<RecordStateVO>getPageRecord(Integer PageSize, Integer PageNum, String keyword, String teacherid);

    mRecord create(String courseId, String courseRoom, Date setTime, Date endTime);

    PageBean<RecordStateVO> getPageMyRecordState(Integer pageNum, Integer pageSize, String keyWord, Integer state, Integer recordId);

    List<RecordStateVO> getStuRecordList(String stuid, String courseID);
}
