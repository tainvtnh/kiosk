package com.tnh.kiosk.services.payment;

import com.tnh.kiosk.forms.payments.HisGetBillRequest;
import com.tnh.kiosk.forms.payments.HisPayBillRequest;
import com.tnh.kiosk.forms.payments.HisCheckTransRequest;
import com.tnh.kiosk.dtos.payments.HisGetBillResponse;
import com.tnh.kiosk.dtos.payments.HisPayBillResponse;
import com.tnh.kiosk.dtos.payments.HisCheckTransResponse;
import com.tnh.kiosk.utils.ChecksumUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.*;

@Service
public class BillService {

    @Value("${ncc.api.unit-code}")
    private String UNIT_CODE;

    @Value("${ncc.api.dvtt}")
    private String DVTT;

    @Value("${ncc.security.secret-code}")
    private String SECRET_CODE;

    @Value("${ncc.api.headers.content-type}")
    private String contentType;

    private PaymentService paymentService;
    private ChecksumUtil checksumUtil;

    public BillService(PaymentService paymentService, ChecksumUtil checksumUtil) {
        this.paymentService = paymentService;
        this.checksumUtil = checksumUtil;
    }

    public HisGetBillResponse getBill(HisGetBillRequest request) {
        // 1. Kiểm tra tham số bắt buộc (customer_code)
        if (request.getMaHoSo() == null || request.getMaHoSo().isEmpty()) {

            HisGetBillResponse response = HisGetBillResponse.builder()
            .code("001")
            .message("Thiếu tham số đầu vào")
            .build();

            return response; // Mã 001: Thiếu tham số
        }

        // 2. Kiểm tra Checksum (Tùy chọn, nếu bắt buộc) unitCode|maHoSo|dvtt|secret key
        String strGetBillCheckSum = UNIT_CODE + "|" + request.getMaHoSo() + "|" + DVTT + "|" + SECRET_CODE;
        String expectedChecksum = checksumUtil.calculateInquiryChecksum(strGetBillCheckSum);
        if (!expectedChecksum.equalsIgnoreCase(request.getChecksum())) {
            HisGetBillResponse response = HisGetBillResponse.builder()
            .code("004")
            .message("Checksum không hợp lệ")
            .build();

            return response; // Mã 004: Checksum không hợp lệ
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType != null ? MediaType.valueOf(contentType) : MediaType.APPLICATION_JSON);
        headers.set("unitCode", UNIT_CODE);
        headers.set("dvtt", DVTT);

        HisGetBillResponse response = paymentService.getBill(request, headers);
        if (response != null) {
            return response;
        }

        response = HisGetBillResponse.builder()
            .code("031")
            .message("Có lỗi phát sinh từ hệ thống NCC") 
            .build();

        return response; // Mã 031: Lỗi hệ thống NCC
    }

    public HisPayBillResponse payBill(HisPayBillRequest request) {
        // 1. Kiểm tra tham số bắt buộc
        if (request.getMaHoSo() == null || request.getSoTien() == null || request.getMaGiaoDich() == null) {
            HisPayBillResponse response = HisPayBillResponse.builder()
            .code("001")
            .comment("Thiếu tham số đầu vào") 
            .build();

            return response; // Mã 001: Thiếu tham số
        }

        // 2. Kiểm tra Checksum (Tùy chọn, nếu bắt buộc)
        //Công thức MD5 hash: "maHoSo"|"maGiaoDich"|"thoiGianThanhToanUTC"|"maNb"|"trangThaiThanhToan"|"hinhThucThanhToan"|"maThietBi"|"tenThuNgan"|"maPOS"|"soTien"|"khoá bí mật trao đổi giữa HIS - EPAY"
        String strPaymentChecksum = request.getMaHoSo() + "|" +
                                    request.getMaGiaoDich() + "|" +
                                    request.getThoiGianThanhToanUTC().toString() + "|" +
                                    request.getMaNb() + "|" +
                                    request.getTrangThaiThanhToan() + "|" +
                                    request.getHinhThucThanhToan().toString() + "|" +
                                    request.getMaThietBi() + "|" +
                                    request.getTenThuNgan() + "|" +
                                    request.getMaPOS() + "|" +
                                    request.getSoTien().toString() + "|" +
                                    SECRET_CODE;

        String expectedChecksum = checksumUtil.calculatePaymentChecksum(strPaymentChecksum);
        if (!expectedChecksum.equalsIgnoreCase(request.getChecksum())) {
            HisPayBillResponse response = HisPayBillResponse.builder()
            .code("004")
            .comment("Checksum không hợp lệ") 
            .build();

            return response; // Mã 004: Checksum không hợp lệ
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType != null ? MediaType.valueOf(contentType) : MediaType.APPLICATION_JSON);

        HisPayBillResponse response;
        try {
            response = paymentService.payBill(request, headers);
            if (response != null) {
                return response;
            }
        } catch (Exception e) {
            response = HisPayBillResponse.builder()
            .code("031")
            .comment(e.getMessage()) 
            .build();
            return response;
        }

        response = HisPayBillResponse.builder()
            .code("031")
            .comment("Có lỗi phát sinh từ hệ thống NCC") 
            .build();

        return response; // Mã 031: Lỗi hệ thống NCC
    }


    public HisCheckTransResponse checkTrans(HisCheckTransRequest request) {
        // 1. Kiểm tra tham số bắt buộc
        if (request.getUnitCode() == null || request.getSoHoaDon() == null) {
            HisCheckTransResponse response = HisCheckTransResponse.builder()
            .code("001")
            .message("Thiếu tham số đầu vào") 
            .build();

            return response; // Mã 001: Thiếu tham số
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType != null ? MediaType.valueOf(contentType) : MediaType.APPLICATION_JSON);
        headers.set("unitCode", UNIT_CODE);
        headers.set("dvtt", DVTT);
        headers.set("soHoaDon", request.getSoHoaDon());

        HisCheckTransResponse response = paymentService.checkTrans(request, headers);
        if (response != null) {
            return response;
        }

        response = HisCheckTransResponse.builder()
            .code("031")
            .message("Có lỗi phát sinh từ hệ thống NCC") 
            .build();

        return response; // Mã 031: Lỗi hệ thống NCC
    }
}