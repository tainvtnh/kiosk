package com.tnh.vietqr.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class KioskCreateInvoiceRequest {
    private String amount;
    private String description;
    private String paymentType; // "CONGKHAM" or "DVKT"
    private String maHoSo;
    private String maNb;
    private String maThietBi;
    private String tenThuNgan;
    private String maPOS;
    private String phieuThuId;
    private String checksum;
    private Integer invoiceExpirySeconds;
    private List<Map<String, Object>> ctDetails; // Used for DVKT (list of services)
}
