package com.lwj.FinalServer.web.net.custom.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Slf4j
@Configuration
public class RabbitConfig {
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("39.107.237.214"); // RabbitMQ 服务器地址
        connectionFactory.setPort(5672);       // RabbitMQ 服务器端口
        connectionFactory.setUsername("test"); // 用户名
        connectionFactory.setPassword("test"); // 密码
        connectionFactory.setVirtualHost("/");  // 虚拟主机
        return connectionFactory;
    }
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
       RabbitTemplate rabbitTemplate=new RabbitTemplate(connectionFactory);
       rabbitTemplate.setMessageConverter(jsonMessageConverter());

       /*
       被RabbitMq broker接收到就会触发
       * */
       rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
           @Override
           public void confirm(CorrelationData correlationData, boolean b, String s) {
                if(!b){
                    log.error("消息发送rabbitmq异常");
                }
                else{
                    log.info("消费者已接收到，correlationData===>["+correlationData.toString()+"]ack===>["+b+"]caurse===>["+s+"]");
                }
           }
       });
       /*
       * 未能正确投递到目标queue
       * */

        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                log.info("replyCode===>["+returnedMessage.getReplyCode()+"]replyText==>["+returnedMessage.getReplyText()+"]Exchange==>["+returnedMessage.getExchange()+"]Message===>["+returnedMessage.getMessage()+"]RoutingKey==>["+returnedMessage.getRoutingKey()+"]");
            }
        });

       return rabbitTemplate;
    }
    @Bean
    protected Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}