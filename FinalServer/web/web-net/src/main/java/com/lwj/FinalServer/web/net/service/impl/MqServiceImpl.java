package com.lwj.FinalServer.web.net.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwj.FinalServer.model.entity.Mq;
import com.lwj.FinalServer.web.net.mapper.MqMapper;
import com.lwj.FinalServer.web.net.service.MqService;
import org.springframework.stereotype.Service;

@Service
public class MqServiceImpl extends ServiceImpl<MqMapper, Mq> implements MqService {
}
