package com.tnh.kiosk.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.tnh.kiosk.configs.StoredProcedureConfig;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "kiosk")
public class StoredProcedureProperties {

    private Map<String, StoredProcedureConfig> configs = new HashMap<>();

    public Map<String, StoredProcedureConfig> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, StoredProcedureConfig> configs) {
        this.configs = configs;
    }
}