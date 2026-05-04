package com.tnh.kiosk.dtos.payments;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class HisCheckTransResponse {
    String code;
    String message;
    DataResult data;
    
    @Data
    public static class DataResult {
        String checksum;
        String trangThaiThanhToan;
        BigDecimal soTien;
        String maHoSo;
        String soHoaDon;
    }
}
