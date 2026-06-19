package com.tnh.vietqr.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IpnResponse {
    @JsonProperty("data")
    private Object data;

    @JsonProperty("verify_signature")
    private String verifySignature;
}
