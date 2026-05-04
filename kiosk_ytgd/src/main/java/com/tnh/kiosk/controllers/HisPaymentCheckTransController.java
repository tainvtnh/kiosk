package com.tnh.kiosk.controllers;

import java.util.Map;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tnh.kiosk.dtos.ApiMessageDTO;
import com.tnh.kiosk.dtos.payments.HisCheckTransResponse;
import com.tnh.kiosk.forms.payments.HisCheckTransRequest;
import com.tnh.kiosk.services.QRService;
import com.tnh.kiosk.services.payment.BillService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${system.api-prefix}/public/${ncc.api.dvtt}/check-trans/${ncc.api.unit-code}")
public class HisPaymentCheckTransController {


    private QRService qrService;
    private BillService billService;

    public HisPaymentCheckTransController(QRService qrService, BillService billService) {
        this.qrService = qrService;
        this.billService = billService;
    }

    @PostMapping()
    public ResponseEntity<ApiMessageDTO<HisCheckTransResponse>> processCheckTrans(@RequestParam Map<String, Object> params) {

        // Danh sách các key hợp lệ
        List<String> allowedKeys = List.of("sohoadon");

        // Giá trị mặc định cho từng key
        Map<String, Object> defaultValues = Map.of(
            "sohoadon", 0
        );

        // Kiểm tra các key không hợp lệ
        try {
            validateParams(params, allowedKeys);
        } catch (IllegalArgumentException ex) {
            ApiMessageDTO<HisCheckTransResponse> error = ApiMessageDTO.<HisCheckTransResponse>builder()
            .message("Lỗi kiểm tra trạng thái: " + ex.getMessage())
            .data(null)
            .result(false)
            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .build();
        }

        HisCheckTransRequest request = new HisCheckTransRequest();
        request.setSoHoaDon(params.get(0).toString());

        try {
            HisCheckTransResponse response = billService.checkTrans(request);
            ApiMessageDTO<HisCheckTransResponse> result = ApiMessageDTO.<HisCheckTransResponse>builder()
            .message("Kiểm tra trạng thái thành công")
            .data(response)
            .result(true)
            .code(HttpStatus.OK.value())
            .build();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing QR Payment: {}", e.getMessage(), e);

            ApiMessageDTO<HisCheckTransResponse> error = ApiMessageDTO.<HisCheckTransResponse>builder()
            .message("Lỗi kiểm tra trạng thái: " + e.getMessage())
            .data(null)
            .result(false)
            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    void validateParams(Map<String, Object> params, List<String> allowedKeys) {
        List<String> normalizedAllowedKeys = allowedKeys.stream()
                .map(String::toLowerCase)
                .toList();

        List<String> invalidKeys = params.keySet().stream()
                .filter(key -> !normalizedAllowedKeys.contains(key.toLowerCase()))
                .toList();

        if (!invalidKeys.isEmpty()) {
            throw new IllegalArgumentException("Invalid parameters: " + String.join(", ", invalidKeys));
        }
    }
}