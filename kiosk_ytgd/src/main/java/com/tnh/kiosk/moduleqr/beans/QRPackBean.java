package com.tnh.kiosk.moduleqr.beans;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;

@ToString
@Builder
@Getter
@Setter
public class QRPackBean {

    private QRBean qrBean;
    private String qrData;

}
