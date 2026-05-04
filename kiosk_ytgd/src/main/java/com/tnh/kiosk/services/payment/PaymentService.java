package com.tnh.kiosk.services.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import com.tnh.kiosk.dtos.payments.HisGetBillResponse;
import com.tnh.kiosk.dtos.payments.HisPayBillResponse;
import com.tnh.kiosk.dtos.payments.HisCheckTransResponse;
import com.tnh.kiosk.forms.payments.HisPayBillRequest;
import com.tnh.kiosk.forms.payments.HisGetBillRequest;
import com.tnh.kiosk.forms.payments.HisCheckTransRequest;

@Slf4j
@Service
public class PaymentService {

    @Value("${ncc.api.base-url}")
    private String apiBaseUrl;

    private ApiPaymentService apiPaymentService;

    public PaymentService(ApiPaymentService apiPaymentService) {
        this.apiPaymentService = apiPaymentService;
    }

    public HisGetBillResponse getBill(HisGetBillRequest request, HttpHeaders headers) {
        String url = apiBaseUrl + "/getbill";
        HisGetBillResponse response;
        try {
            response = apiPaymentService.sendRequest(url, HttpMethod.POST, request, headers.toSingleValueMap(), HisGetBillResponse.class);
        } catch (Exception e) {
            //throw new RuntimeException("Error calling getbill API: " + e.getMessage(), e);
            log.info("Error calling checktrans API: " + e.getMessage());
            throw new RuntimeException("Error calling getbill API");
        }
        log.info("Response from NCC getbill: {}", response);
        return response;
    }

    public HisPayBillResponse payBill(HisPayBillRequest request, HttpHeaders headers) {
        String url = apiBaseUrl + "/paybill";
        HisPayBillResponse response;
        try {
            response = apiPaymentService.sendRequest(url, HttpMethod.POST, request, headers.toSingleValueMap(), HisPayBillResponse.class);
        } catch (Exception e) {
            //throw new RuntimeException("Error calling payBill API: " + e.getMessage(), e);
            log.info("Error calling checktrans API: " + e.getMessage());
            throw new RuntimeException("Error calling payBill API");
        }

        log.info("Response from NCC paybill: {}", response);
        return response;
    }

    public HisCheckTransResponse checkTrans(HisCheckTransRequest request, HttpHeaders headers) {
        String url = apiBaseUrl + "/checktrans";
        HisCheckTransResponse response;
        try {
            response = apiPaymentService.sendRequest(url, HttpMethod.POST, null, headers.toSingleValueMap(), HisCheckTransResponse.class);
        } catch (Exception e) {
             log.info("Error calling checktrans API: " + e.getMessage());
             throw new RuntimeException("Error calling checktrans API");
        }
        log.info("Response from NCC checktrans: {}", response);
        return response;
    }
}
