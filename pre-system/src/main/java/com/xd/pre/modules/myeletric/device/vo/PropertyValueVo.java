package com.xd.pre.modules.myeletric.device.vo;

import lombok.Data;
import java.util.List;

//属性值Vo,用于接收mqtt网关传递过来的设备属性采集值
@Data
public class PropertyValueVo {

    private String      product_name;
    private String      device_name;
    private List<DevicePropertyListVo> propertyListVos;

}
