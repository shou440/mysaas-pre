package com.xd.pre;


import com.xd.MyMqttStub;
import com.xd.MyWeixinStub;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * 系统入口
 */
@SpringBootApplication
public class PreSystemApplication {

    public static void main(String[] args)   {

        //启动Mqtt通讯模块功能

        MyMqttStub.getTheMyMqttStub().StartService();


        //启动微信模块功能
        MyWeixinStub.getTheMyWeixinStub().StartWeixin();

        SpringApplication.run(PreSystemApplication.class, args);


        }
    }


