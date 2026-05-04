package com.tnh.kiosk.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tnh.kiosk.dtos.ApiMessageDTO;
import com.tnh.kiosk.dtos.payments.HisGetBillResponse;
import com.tnh.kiosk.dtos.payments.QRPaymentResponse;
import com.tnh.kiosk.forms.payments.HisGetBillRequest;
import com.tnh.kiosk.forms.payments.QRPaymentRequest;
import com.tnh.kiosk.services.QRService;
import com.tnh.kiosk.services.payment.BillService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${system.api-prefix}/public/${ncc.api.dvtt}/vantin/${ncc.api.unit-code}")
public class HisPaymentGetBillController {


    private QRService qrService;
    private BillService billService;

    public HisPaymentGetBillController(QRService qrService, BillService billService) {
        this.qrService = qrService;
        this.billService = billService;
    }

    @PostMapping()
    public ResponseEntity<ApiMessageDTO<HisGetBillResponse>> processGetBill(@RequestBody HisGetBillRequest request) {

        try {
            HisGetBillResponse response = billService.getBill(request);
            ApiMessageDTO<HisGetBillResponse> result = ApiMessageDTO.<HisGetBillResponse>builder()
            .message("Vấn tin thành công")
            .data(response)
            .result(true)
            .code(HttpStatus.OK.value())
            .build();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing QR Payment: {}", e.getMessage(), e);

            ApiMessageDTO<HisGetBillResponse> error = ApiMessageDTO.<HisGetBillResponse>builder()
            .message("Lỗi vấn tin: " + e.getMessage())
            .data(null)
            .result(false)
            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}