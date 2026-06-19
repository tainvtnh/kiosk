package com.tnh.vietqr.controller;

import com.tnh.vietqr.dto.KioskCreateInvoiceRequest;
import com.tnh.vietqr.integration.dto.InvoiceResponse;
import com.tnh.vietqr.integration.dto.IpnPayload;
import com.tnh.vietqr.integration.dto.IpnResponse;
import com.tnh.vietqr.integration.dto.TransactionQueryResponse;
import com.tnh.vietqr.integration.dto.UpdateInvoiceResponse;
import com.tnh.vietqr.service.PvcomBankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${system.api-prefix}/vietqr")
public class VietQrController {

    private final PvcomBankService pvcomBankService;

    public VietQrController(PvcomBankService pvcomBankService) {
        this.pvcomBankService = pvcomBankService;
    }

    /**
     * Kiosk initiates a collection payment via VietQR.
     */
    @PostMapping("/invoices")
    public ResponseEntity<InvoiceResponse> createInvoice(@RequestBody KioskCreateInvoiceRequest request) {
        InvoiceResponse response = pvcomBankService.createInvoice(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Kiosk cancels/expires a pending payment request.
     */
    @PostMapping("/invoices/cancel")
    public ResponseEntity<UpdateInvoiceResponse> cancelInvoice(@RequestParam String invoiceCode) {
        UpdateInvoiceResponse response = pvcomBankService.cancelInvoice(invoiceCode);
        return ResponseEntity.ok(response);
    }

    /**
     * Kiosk queries the state of a payment.
     */
    @GetMapping("/invoices/{invoiceCode}")
    public ResponseEntity<TransactionQueryResponse> queryTransaction(@PathVariable String invoiceCode) {
        TransactionQueryResponse response = pvcomBankService.queryTransaction(invoiceCode);
        return ResponseEntity.ok(response);
    }

    /**
     * Public endpoint invoked by PVcomBank to notify payment success.
     */
    @PostMapping("/ipn")
    public ResponseEntity<IpnResponse> receiveIpn(@RequestBody IpnPayload payload) {
        IpnResponse response = pvcomBankService.processIpn(payload);
        return ResponseEntity.ok(response);
    }
}
