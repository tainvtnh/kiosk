package com.tnh.vietqr.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IpnPayload {
    @JsonProperty("data")
    private IpnData data;

    @JsonProperty("signature")
    private String signature;
}
