package com.lwj.FinalServer.common.Utils;



import com.lwj.FinalServer.model.entity.Mq;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqUtils {
    private ConnectionFactory connectionFactory ;
    /*
    * 新增消息队列
    * */

    public  void mqOperate(Mq mq){
        if (connectionFactory==null){
            connectionFactory=getConnectionFactory();
        }
        Integer ExchangeType =mq.getExchangetype();
        String ExchangeName=mq.getCourseid();
        String QueueName=mq.getStudentid();
        String binding=mq.getBinding();
        Integer delaytype=mq.getDelaytype();
        int status=mq.getDel_flag();

        RabbitAdmin rabbitAdmin=new RabbitAdmin(connectionFactory);
        if(status==1){
            //声明课程交换机
            rabbitAdmin.declareQueue(new Queue(QueueName,true,false,false,null));
            //声明学生监听的队列
            rabbitAdmin.declareExchange(new FanoutExchange(ExchangeName,true,false,null));
            //将队列和交换机进行绑定
            rabbitAdmin.declareBinding(new Binding(QueueName,Binding.DestinationType.QUEUE,ExchangeName,ExchangeName,null));
        }else {
            //解除绑定关系
            rabbitAdmin.removeBinding(new Binding(QueueName,Binding.DestinationType.QUEUE,ExchangeName,ExchangeName,null));
        }
    }

    private ConnectionFactory getConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("39.107.237.214"); // RabbitMQ 服务器地址
        connectionFactory.setPort(5672);       // RabbitMQ 服务器端口
        connectionFactory.setUsername("test"); // 用户名
        connectionFactory.setPassword("test"); // 密码
        connectionFactory.setVirtualHost("/");  // 虚拟主机
        return connectionFactory;
    }

}
