package com.lwj.FinalServer.web.net.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwj.FinalServer.model.entity.Classroom;
import com.lwj.FinalServer.web.net.mapper.ClassroomMapper;
import com.lwj.FinalServer.web.net.service.ClassRoomService;
import com.lwj.FinalServer.web.net.service.ClassesService;
import com.lwj.FinalServer.web.net.vo.ClassRoomVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClassRoomServiceImpl extends ServiceImpl<ClassroomMapper, Classroom> implements ClassRoomService {

}
