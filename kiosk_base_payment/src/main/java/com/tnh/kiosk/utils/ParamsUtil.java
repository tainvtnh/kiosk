package com.tnh.kiosk.utils;

import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ParamsUtil {

    /**
     * Đăng ký và set tất cả IN params cho StoredProcedureQuery
     * 
     * @param query  StoredProcedureQuery
     * @param params Map chứa tên param → giá trị
     */
    public static void registerAndSetInParams(StoredProcedureQuery query, Map<String, Object> params) {
        if (params == null) return;

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String paramName = entry.getKey();
            Object value = entry.getValue();
            Object valueOriginal = value;

            // Nếu là String, fix UTF-8 để SQL Server nhận đúng Unicode
            if (value instanceof String) {
                value = fixEncoding((String) value);

                // Kiểm tra có ký tự Unicode (có dấu) hay không
                boolean hasUnicode = !((String)value).matches("\\A\\p{ASCII}*\\z");

                value = hasUnicode ? "N'" + value + "'" : valueOriginal;

                //System.out.println("IN param: " + paramName + " = " + value);
            }

            // Đăng ký và set param
            query.registerStoredProcedureParameter(paramName, value.getClass(), ParameterMode.IN);
            query.setParameter(paramName, value);
        }
    }

    /**
     * Fix UTF-8 cho String nhận từ request/các nguồn có encoding sai
     */
    private static String fixEncoding(String input) {
        if (input == null) return null;
        // Chuyển từ ISO-8859-1 → UTF-8 (thường gặp khi lấy từ HTTP request)
        return new String(input.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }
}
