package com.tnh.vietqr.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransactionQueryRequest {
    @JsonProperty("account")
    private Long account;

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("clientId")
    private String clientId;
}
