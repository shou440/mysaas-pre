package com.xd.pre.modules.pay.dto;

import lombok.Data;

@Data
public class PaymentStateDto {
    private String payment_id;
    private Integer payment_state;
}
