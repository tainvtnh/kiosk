package com.tnh.vietqr.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IpnData {
    @JsonProperty("account")
    private String account;

    @JsonProperty("va")
    private String va;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("tranDate")
    private String tranDate;

    @JsonProperty("tranId")
    private String tranId;

    @JsonProperty("description")
    private String description;
}
