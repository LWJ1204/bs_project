package com.lwj.FinalServer.web.net.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lwj.FinalServer.common.result.Result;
import com.lwj.FinalServer.model.entity.RecordState;
import com.lwj.FinalServer.model.entity.mRecord;
import com.lwj.FinalServer.web.net.custom.Component.WebSocketServer;
import com.lwj.FinalServer.web.net.custom.Retention.DataDesensitization;
import com.lwj.FinalServer.web.net.mapper.RecordMapper;
import com.lwj.FinalServer.web.net.service.RecordStateService;
import com.lwj.FinalServer.web.net.vo.PageBean;
import com.lwj.FinalServer.web.net.vo.RecordStateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Tag(name="考勤记录信息管理")
@RestController
@RequestMapping("/api/record")
public class RecordController {
    @Autowired
    private RecordStateService rservice;
    @Autowired
    private RecordMapper rmapper;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Operation(summary = "获取考情信息")
    @GetMapping("getRecordData")
    public Result<PageBean<RecordStateVO>>getRecordPage(@RequestParam Map data, HttpServletRequest request){
        String username= (String) request.getAttribute("username");
        Integer role= (Integer) request.getAttribute("role");
        String UserId= (String) request.getAttribute("UserId");
        Integer pageNum= Integer.parseInt((String)data.get("page"));
        Integer pageSize= Integer.parseInt((String)data.get("limit"));
        String keyword=data.get("keyWord")==null?null:(String)data.get("keyWord");
        String teacherid=data.get("TeacherId")==null?null:(String)data.get("TeacherId");
        if(role==1) teacherid=null;
        if(role==3||(teacherid!=null&&!username.equals(teacherid))) return Result.fail(-99,"该用户不具备该权限");
        if (keyword != null && keyword.trim().isEmpty())
            keyword=null;
        PageBean<RecordStateVO> t = rservice.getPageRecord(pageNum, pageSize, keyword,teacherid);
        return  Result.ok(t);
    }

    @Operation(summary = "编辑考勤信息")
    @PostMapping("/editRecordData")
    public Result<String>editCourseSelect(@RequestBody Map data,HttpServletRequest request){
        if((Integer) request.getAttribute("role")!=1) return Result.fail(-99,"无权限");
        try
        {
            Integer RecordId = (Integer) data.get("RecordId");
            String stuid=(String)data.get("studentid");
            Integer state = (Integer) data.get("RecordState");
            QueryWrapper<RecordState> qc=new QueryWrapper<>();
            qc.eq("RecordId",RecordId).eq("StudentId",stuid);
            // 创建一个实体对象，设置需要更新的字段
            RecordState recordState = new RecordState();
            recordState.setRecordstate(state);
            boolean isok= rservice.update(recordState,qc);
            if (isok){
                return Result.ok("修改成功");
            }


            return Result.ok("修改失败");
        }
        catch (Exception e){
            return Result.fail();
        }
    }

    @Operation(summary = "考勤信息")
    @PostMapping("getmyRecordState")
    @DataDesensitization
    public Result<PageBean<RecordStateVO>>getMyRecordState(@RequestBody Map data){
        Integer PageNum= (Integer) data.get("page");
        Integer PageSize= (Integer)data.get("limit");
        Integer RecordId= (Integer)data.get("RecordId");
        String KeyWord=data.get("keyWord")==null?null:(String)data.get("KeyWord");
        Integer State=data.get("state")==null?null: (Integer)data.get("state");
        PageBean<RecordStateVO>list=rservice.getPageMyRecordState(PageNum,PageSize,KeyWord,State,RecordId);
        return Result.ok(list);
    }

    @Operation(summary = "创建考勤")
    @PostMapping("createRecord")
    public Result<mRecord>createRecord(@RequestBody Map data) throws ParseException {
        String CourseId= (String) data.get("CourseId");
        String CourseRoom= (String) data.get("CourseRoom");
        Date SetTime=formatter.parse((String) data.get("RecordSetTime"));
        Date EndTime=formatter.parse((String)data.get("RecordEndTime"));
        mRecord record=rservice.create(CourseId,CourseRoom,SetTime,EndTime);
        if(record==null) Result.fail();
        return Result.ok(record);
    }

    @Operation(summary = "进行考勤")
    @PostMapping("stuarr")
    public Result<String>addstustate(@RequestBody Map data,HttpServletRequest request) throws ParseException {
        Integer RecordId= (Integer) data.get("RecordId");
        String StudentId= (String) data.get("StudentId");
        String Code= (String) data.get("Code");
        QueryWrapper<mRecord>qrecord=new QueryWrapper<>();
        qrecord.eq("RecordId",RecordId);
        QueryWrapper<RecordState>qrs=new QueryWrapper<>();
        qrs.eq("RecordId",RecordId).eq("StudentId",StudentId);
        Date ArrTime=formatter.parse((String)data.get("ArrTime"));
        mRecord mrecord=rmapper.selectOne(qrecord);

        String mmessage= (String) data.get("message");
        String m=RecordId+"_+"+StudentId+"_+"+data.get("ArrTime")+"_+"+Code;
        // ArrTime 是 Date 类型，表示到达时间
        Date now = new Date();
        // 计算时间差（单位：毫秒）
        long timeDiff = now.getTime()-ArrTime.getTime() ;
        if(timeDiff>60*1000) return Result.fail(-99,"超时了");
        if(!mmessage.equals(DigestUtils.sha256Hex(m))) return Result.fail(-99,"摘要不匹配");

        RecordState rm=rservice.getOne(qrs);
        if(mrecord!=null){
            Date SetTime=mrecord.getRecordsettime();
            Date EndTime=mrecord.getRecordendtime();
            if(ArrTime.after(SetTime)&&ArrTime.before(EndTime)){
                rm.setRecordarrtime(ArrTime);
                rm.setRecordstate(0);
                rm.setCode(Code);
                rservice.update(rm,qrs);
                WebSocketServer.sendToOne(String.valueOf(rm.getRecordid()),rm.getStudentid());
                return Result.ok("签到成功");
            }
            return Result.ok("签到失败");
        }
        return Result.ok("签到失败");

    }

    @Operation(summary = "获取当前时间")
    @GetMapping("getTime")
    public Result<Long>getTime(){
        // 获取当前北京时间
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        // 转换为时间戳（毫秒）
        long timestamp = now.toInstant().toEpochMilli();
        return Result.ok(timestamp);
    }

    @Operation(summary = "手机端获取考勤信息")
    @PostMapping("getRecord")
    public Result<List<RecordStateVO>>getRecord(@RequestBody Map data){
        String Stuid= (String) data.get("StudentId");
        String CourseID= (String) data.get("CourseId");
        List<RecordStateVO>list=rservice.getStuRecordList(Stuid,CourseID);
        if(list==null){
            return Result.ok();
        }
        return Result.ok(list);
    }
}
