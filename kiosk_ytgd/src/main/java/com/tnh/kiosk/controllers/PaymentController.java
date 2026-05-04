package com.tnh.kiosk.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tnh.kiosk.dtos.ApiMessageDTO;
import com.tnh.kiosk.dtos.payments.QRPaymentResponse;
import com.tnh.kiosk.forms.payments.QRPaymentRequest;
import com.tnh.kiosk.services.QRService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${system.api-prefix}/his/payment")
public class PaymentController {


    private QRService qrService;

    public PaymentController(QRService qrService) {
        this.qrService = qrService;
    }

    @PostMapping("/qr-generator")
    public String qrStringGenerator(@RequestBody Map<String, String> params) {

        String merchantCode = params.get("merchantCode");
        String merchantName = params.get("merchantName");
        String amount = params.get("amount");

        try {
        String result = qrService.generateQRCode(merchantCode, merchantName, amount);
        return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/thanhtoanqrcode")
    public ResponseEntity<ApiMessageDTO<QRPaymentResponse>> processQRPayment(@RequestBody QRPaymentRequest request) {
        log.info("Received QR Payment request: {}", request);

        try {
            QRPaymentResponse response = qrService.processQRPayment(request);

            ApiMessageDTO<QRPaymentResponse> result = ApiMessageDTO.<QRPaymentResponse>builder()
            .message("Thanh toán thành công")
            .data(response)
            .result(true)
            .code(HttpStatus.OK.value())
            .build();

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error processing QR Payment: {}", e.getMessage(), e);

            ApiMessageDTO<QRPaymentResponse> error = ApiMessageDTO.<QRPaymentResponse>builder()
                    .message("Lỗi xử lý thanh toán: " + e.getMessage())
                    .data(null)
                    .result(false)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

}