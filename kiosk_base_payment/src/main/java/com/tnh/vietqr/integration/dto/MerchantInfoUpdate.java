package com.tnh.vietqr.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MerchantInfoUpdate {
    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("account_no")
    private String accountNo;
}
