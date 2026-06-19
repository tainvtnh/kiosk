package com.tnh.vietqr.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InvoiceData {
    @JsonProperty("invoice_code")
    private String invoiceCode;

    @JsonProperty("status")
    private String status;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("invoice_expiry_time")
    private String invoiceExpiryTime;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("next_action")
    private NextAction nextAction;

    @Data
    public static class NextAction {
        @JsonProperty("type")
        private String type;

        @JsonProperty("qr_code")
        private String qrCode;
    }
}
