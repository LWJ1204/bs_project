package com.lwj.FinalServer.web.net.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class hellocontroller {
    @GetMapping("/hello")
    public String index(){
        return "Hello World 入门小站";
    }

}
