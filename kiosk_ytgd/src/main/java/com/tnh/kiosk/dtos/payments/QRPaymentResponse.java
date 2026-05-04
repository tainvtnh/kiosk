package com.tnh.kiosk.dtos.payments;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QRPaymentResponse {

    private String code;
    private String message;
    private String data;
    private String checksum;

    @Data
    public static class data {
        private String txnId;
    }
}
