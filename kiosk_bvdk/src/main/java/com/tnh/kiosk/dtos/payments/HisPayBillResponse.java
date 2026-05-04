package com.tnh.kiosk.dtos.payments;

import java.math.BigDecimal;

import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HisPayBillResponse {
    String code;
    String comment;
    DataResult data;

    @Data
    public static class DataResult {
        String maHoaDonDienTu;
        HoaDon hoaDon;
        String checksum;
        String maNb;
        BigDecimal soTien;
        ChiDinh chiDinh;
        Boolean trangThaiThanhToan;
        String maHoSo;
        String soPhieuThu;
    }

    @Data
    public static class HoaDon {
        String soHoaDon;
        Boolean trangThaiPhatHanh;
        String maHoaDon;
        String kyHieuHoaDon;
    }

    @Data
    public static class ChiDinh {
        String soPhieu;
        java.util.List<ChiTietChiDinh> chiTietChiDinh;
    }

    @Data
    public static class ChiTietChiDinh {
        Integer dichVuId;
        BigDecimal soTien;
    }
}
