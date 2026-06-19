package com.tnh.kiosk.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "kiosk")
public class KioskProperties {

    private Map<String, QueryConfig> queries;
    private Map<String, StoredProcedureConfig> storedProcedures;

    public Map<String, QueryConfig> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, QueryConfig> queries) {
        this.queries = queries;
    }

    public Map<String, StoredProcedureConfig> getStoredProcedures() {
        return storedProcedures;
    }

    public void setStoredProcedures(Map<String, StoredProcedureConfig> storedProcedures) {
        this.storedProcedures = storedProcedures;
    }

    public static class QueryConfig {
        private String sql;

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }
    }

    public static class StoredProcedureConfig {
        private String name;
        private List<String> inParams;
        private List<String> outParams;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getInParams() {
            return inParams;
        }

        public void setInParams(List<String> inParams) {
            this.inParams = inParams;
        }

        public List<String> getOutParams() {
            return outParams;
        }

        public void setOutParams(List<String> outParams) {
            this.outParams = outParams;
        }
    }
}