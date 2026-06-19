package com.tnh.vietqr.integration;

import com.tnh.vietqr.integration.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "pvcombank-client", url = "${vietqr.pvcombank.base-url}")
public interface PvcomBankClient {

    @PostMapping(value = "/idp/oauth2/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenResponse getToken(@RequestBody String formBody);

    @PostMapping(value = "/external-public/external-public/bill/api/v1/invoices", consumes = MediaType.APPLICATION_JSON_VALUE)
    InvoiceResponse createInvoice(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("client-id") String clientIdHeader,
            @RequestBody InvoiceRequest request
    );

    @PostMapping(value = "/external-public/external-public/bill/api/v1/invoices/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    UpdateInvoiceResponse updateInvoice(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("client-id") String clientIdHeader,
            @RequestBody UpdateInvoiceRequest request
    );

    @PostMapping(value = "/external-public/external-public/notifications/api/v1/transactions", consumes = MediaType.APPLICATION_JSON_VALUE)
    TransactionQueryResponse queryTransaction(
            @RequestHeader("Authorization") String authorization,
            @RequestBody TransactionQueryRequest request
    );
}
