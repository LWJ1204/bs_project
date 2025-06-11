package com.lwj.FinalServer;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableRabbit
@SpringBootApplication
@EnableWebSocket
@ComponentScan(basePackages ={"com.lwj.FinalServer.common", "com.lwj.FinalServer.web.net.*"} )
public class NetApplication {
    public static void main(String[] args) {

        SpringApplication.run(NetApplication.class,args);
    }
}
