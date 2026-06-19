package com.tnh.vietqr.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InvoiceRequest {
    @JsonProperty("amount")
    private String amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("invoice_code")
    private String invoiceCode;

    @JsonProperty("description")
    private String description;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("payment_method_type")
    private String paymentMethodType;

    @JsonProperty("invoice_expiry_seconds")
    private Integer invoiceExpirySeconds;

    @JsonProperty("merchant")
    private MerchantInfo merchant;

    @JsonProperty("customer")
    private CustomerInfo customer;
}
