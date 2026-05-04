package com.tnh.kiosk.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.*;

@Data
@Configuration
@ConfigurationProperties(prefix = "qrinfo")
public class QRProperties {

    private Default defaultConfig;
    private Merchant merchant;
    private Address address;
    private Additional additional;

    @Data
    public static class Default {
        private String payLoad;
        private String pointOIMethod;
        private String masterMerchant;
        private String ccy;
        private String countryCode;
    }

    @Data
    public static class Merchant {
        private String code;
        private String name;
        private String cc;
    }

    @Data
    public static class Address {
        private String city;
        private String pinCode;
    }

    @Data
    public static class Additional {
        private String billNumber;
        private String terminalID;
        private String storeID;
        private String purpose;
    }
}