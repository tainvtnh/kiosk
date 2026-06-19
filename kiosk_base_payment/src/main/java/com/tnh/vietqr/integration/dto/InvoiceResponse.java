package com.tnh.vietqr.integration.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InvoiceResponse {
    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("message")
    private String message;

    @JsonAlias({ "data", "invoice" })
    @JsonProperty("data")
    private InvoiceData data;
}
