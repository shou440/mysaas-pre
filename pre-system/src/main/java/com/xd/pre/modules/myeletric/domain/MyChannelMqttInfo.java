package com.xd.pre.modules.myeletric.domain;

import lombok.Data;

//mqtt通道详细配置信息
@Data
public class MyChannelMqttInfo {

    //通道ID
    private int channel_id;

    //通道名称
    private String channel_name;

    //服务器IP
    private String server_ip;

    //服务器端口
    private int server_port;

    //客户端ID
    private String client_id;

    //密码
    private String pass_word;

    //QS数据服务质量
    private int qs_level;
}
