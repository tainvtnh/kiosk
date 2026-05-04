package com.tnh.kiosk.configs;

import java.util.List;
import java.util.Map;

public class StoredProcedureConfig {
    private String name;
    private List<String> inParams;
    private List<String> outParams;
    private Map<String, String> outParamTypes; // <== kiểu dữ liệu của từng OUT param

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

    public Map<String, String> getOutParamTypes() { 
        return outParamTypes; 
    }
    public void setOutParamTypes(Map<String, String> outParamTypes) { 
        this.outParamTypes = outParamTypes; 
    }

    public String getParamType(String paramName) {
        if (outParamTypes != null && outParamTypes.containsKey(paramName)) {
            return outParamTypes.get(paramName);
        }
        // hoặc có thể có thêm map inParamTypes nếu bạn muốn
        return "VARCHAR"; // kiểu mặc định nếu không có
    }
}