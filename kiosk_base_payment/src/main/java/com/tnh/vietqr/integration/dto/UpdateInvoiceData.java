package com.tnh.vietqr.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateInvoiceData {
    @JsonProperty("invoice_code")
    private String invoiceCode;

    @JsonProperty("status")
    private String status;

    @JsonProperty("alias")
    private String alias;
}
