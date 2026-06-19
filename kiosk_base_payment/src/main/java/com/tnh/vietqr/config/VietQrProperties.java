package com.tnh.vietqr.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "vietqr.pvcombank")
public class VietQrProperties {
    private String baseUrl;
    private String clientId;
    private String clientSecret;
    private Merchant merchant = new Merchant();
    private Security security = new Security();
    private Kiosk kiosk = new Kiosk();

    @Data
    public static class Merchant {
        private String clientId;
        private String name;
        private String accountNo;
    }

    @Data
    public static class Security {
        private String pvcombankPublicKey;
    }

    @Data
    public static class Kiosk {
        private String checksumKey;
    }
}
