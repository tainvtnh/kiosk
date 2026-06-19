package com.tnh.vietqr.integration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerInfo {
    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;
}
