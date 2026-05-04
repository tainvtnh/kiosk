package com.tnh.kiosk.forms.payments;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HisPayBillRequest {
    String maHoSo;
    String maGiaoDich;

    @Data
    public static class ChiDinh {
        String soPhieu;
        List<ChiTietChiDinh> chiTietChiDinh;
    }

    @Data
    public static class ChiTietChiDinh {
        String dichVuId;
        BigDecimal soTien;
    }
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    OffsetDateTime thoiGianThanhToanUTC;
    String maNb;
    Boolean trangThaiThanhToan;
    Integer hinhThucThanhToan;
    String maThietBi;
    String tenThuNgan;
    String maPOS;
    BigDecimal soTien;
    String checksum;
}
