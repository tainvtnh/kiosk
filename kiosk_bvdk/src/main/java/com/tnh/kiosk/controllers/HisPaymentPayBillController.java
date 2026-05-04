package com.tnh.kiosk.controllers;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tnh.kiosk.dtos.payments.HisPayBillResponse;
import com.tnh.kiosk.forms.payments.HisPayBillRequest;
import com.tnh.kiosk.services.payment.BillService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${system.api-prefix}/his/sublinical-payment")
public class HisPaymentPayBillController {

    private BillService billService;

    public HisPaymentPayBillController(BillService billService) {
        this.billService = billService;
    }

    @PostMapping()
    public HisPayBillResponse processPayBill(@RequestBody HisPayBillRequest request) {
        HisPayBillResponse response = billService.payBill(request);
        return response;
    }
}