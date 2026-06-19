package com.tnh.vietqr.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateInvoiceRequest {
    @JsonProperty("alias")
    private String alias;

    @JsonProperty("invoice_code")
    private String invoiceCode;

    @JsonProperty("force_expire_now")
    private Boolean forceExpireNow;

    @JsonProperty("merchant")
    private MerchantInfoUpdate merchant;
}
