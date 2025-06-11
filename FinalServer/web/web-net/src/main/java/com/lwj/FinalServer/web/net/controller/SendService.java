package com.lwj.FinalServer.web.net.controller;

import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SendService {
    @Autowired
    private  RabbitTemplate rabbitTemplate;
    public void sendMessage(String ExchangeName,String routingkey,Object msg) {
       /*
       发送失败后返回队列
       * */
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.convertAndSend(
                ExchangeName,
                routingkey,
                msg,
                message -> {
                    message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return message;
                },
                new CorrelationData(UUID.randomUUID().toString())
        );
    }
}
