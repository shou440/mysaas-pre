package com.xd.pre;


import com.xd.MyMqttStub;
import com.xd.MyWeixinStub;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 系统入口,开启事务支持
 */
@EnableTransactionManagement
@SpringBootApplication
public class PreSystemApplication {

    public static void main(String[] args)   {

        //启动微信模块功能
        MyWeixinStub.getTheMyWeixinStub().StartWeixin();

        SpringApplication.run(PreSystemApplication.class, args);


        }
    }


