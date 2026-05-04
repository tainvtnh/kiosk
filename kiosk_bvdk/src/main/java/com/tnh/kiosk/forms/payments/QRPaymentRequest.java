package com.tnh.kiosk.forms.payments;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class QRPaymentRequest {

    String code;
    String message;
    String msgType;
    String txnId;
    String qrTrace;
    String bankCode;
    String mobile;
    String accountNo;
    String amount;
    String payDate;
    String merchantCode;
    String terminalId;
    String name;
    String phone;
    String province_id;
    String district_id;
    String address;
    String email;
    Object addData;
    String checksum;
    
}
