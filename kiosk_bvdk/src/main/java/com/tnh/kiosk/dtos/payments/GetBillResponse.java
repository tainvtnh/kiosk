package com.tnh.kiosk.dtos.payments;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetBillResponse {
    private String resultCode;
    private String resultDesc;
    private String customerId;
    private String customerName;
    private String customerAddr;
    private String billId;
    private String totalAmount;
    private List<BillPeriod> data;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class BillPeriod {
        private String period;
        private List<BillDetail> data;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class BillDetail {
        private String billId;
        private String amount;
        private String remark;
    }
}
