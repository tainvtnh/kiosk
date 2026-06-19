package com.tnh.kiosk.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "kiosk")
@Data
public class KioskConfigs {

    private Map<String, QueryConfig> queries = new HashMap<>();
    private Map<String, StoredProcedureConfig> storedProcedures = new HashMap<>();

    @Data
    public static class QueryConfig {
        private String sql;
    }

    @Data
    public static class StoredProcedureConfig {
        private String name;
        private List<String> inParams = new ArrayList<>();
    }
}

