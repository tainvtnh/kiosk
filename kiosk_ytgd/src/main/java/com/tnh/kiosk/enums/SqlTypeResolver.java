package com.tnh.kiosk.enums;

import java.sql.Types;
import java.util.Locale;

public class SqlTypeResolver {

    /**
     * Chuyển tên kiểu dữ liệu (chuỗi YAML hoặc DB) sang hằng số java.sql.Types tương ứng.
     * Nếu không khớp, trả về Types.JAVA_OBJECT.
     */
    public static int resolve(String sqlType) {
        if (sqlType == null || sqlType.isBlank()) {
            return Types.JAVA_OBJECT;
        }

        switch (sqlType.trim().toUpperCase(Locale.ROOT)) {
            case "VARCHAR":
            case "CHAR":
            case "TEXT":
            case "NVARCHAR":
            case "NCHAR":
            case "NTEXT":
                return Types.VARCHAR;

            case "INT":
            case "INTEGER":
            case "SMALLINT":
                return Types.INTEGER;

            case "BIGINT":
                return Types.BIGINT;

            case "BOOLEAN":
            case "BIT":
                return Types.BOOLEAN;

            case "DECIMAL":
            case "NUMERIC":
                return Types.DECIMAL;

            case "FLOAT":
            case "DOUBLE":
            case "REAL":
                return Types.DOUBLE;

            case "DATE":
                return Types.DATE;

            case "DATETIME":
            case "TIMESTAMP":
            case "TIME":
                return Types.TIMESTAMP;

            case "BLOB":
                return Types.BLOB;

            case "CLOB":
            case "NCLOB":
                return Types.CLOB;

            case "CURSOR": // Thường dùng trong Oracle
                return -10; // OracleTypes.CURSOR

            default:
                return Types.JAVA_OBJECT;
        }
    }
}