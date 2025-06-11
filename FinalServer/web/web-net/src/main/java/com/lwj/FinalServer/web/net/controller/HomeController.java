package com.lwj.FinalServer.web.net.controller;
import com.lwj.FinalServer.common.result.Result;
import com.lwj.FinalServer.web.net.service.AMService;
import com.lwj.FinalServer.web.net.service.CourseService;
import com.lwj.FinalServer.web.net.vo.AmVO;
import com.lwj.FinalServer.web.net.vo.CountData;
import com.lwj.FinalServer.web.net.vo.CourseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name="主页信息管理")
@RestController
@RequestMapping("/api/home")
public class HomeController {
    @Autowired
    private AMService amService;
    @Autowired
    private CourseService courseService;
    @Operation(summary = "获取学院专业信息")
    @GetMapping("getAMInfo")
    public Result<List<AmVO>>GetHomeTable( ){
        List<AmVO> list=amService.getAmList();
        list.forEach(System.out::println);
        return Result.ok(list);
    }

    @Operation(summary = "获取表格数据")
    @PostMapping("getTableData")
    public Result<List<CourseVO>>getTable(@RequestBody Map data){
        String id= (String) data.get("UserId");
        Integer role= (Integer) data.get("role");
        List<CourseVO> ans=courseService.getCourseList(role,id);
        return Result.ok(ans);
    }


    @Operation(summary = "获取数据")
    @PostMapping("getCountData")
    public Result<CountData>getCount(@RequestBody Map data){
        String id= (String) data.get("UserId");
        Integer role= (Integer) data.get("role");
        CountData cd=courseService.CountData(role,id);
        return Result.ok(cd);
    }
}
