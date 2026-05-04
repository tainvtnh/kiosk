package com.tnh.kiosk.dtos.payments;

import java.math.BigDecimal;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HisGetBillResponse {
    String code;
    String message;
    HisBillPeriod data;

    @Data
    public static class HisBillPeriod {
        String checksum;
        String qrCode;
        BigDecimal soTien;
        String maHoSo;
        String soHoaDon;
    }
}
