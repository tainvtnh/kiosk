package com.tnh.kiosk.repositories;

import com.tnh.kiosk.configs.StoredProcedureConfig;
import com.tnh.kiosk.enums.SqlTypeResolver;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

@Repository
public class StoredProcedureRepository {

    private final EntityManager entityManager;

    public StoredProcedureRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Map<String, Object> callProcedure(StoredProcedureConfig config, Map<String, Object> params) {
        Map<String, Object> output = new LinkedHashMap<>();

        // --- 1. Dựng StoredProcedureQuery (JPA) để giữ transaction context
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery(config.getName());

        // --- 2. Đăng ký IN params
        if (config.getInParams() != null) {
            //ParamsUtil.registerAndSetInParams(query, params);
            for (String inParam : config.getInParams()) {
                Object value = params.get(inParam);

                if (value != null) {
                    query.registerStoredProcedureParameter(inParam, value.getClass(), ParameterMode.IN);
                    query.setParameter(inParam, value);
                } else {
                    //throw new IllegalArgumentException("Missing value for IN parameter: " + inParam);
                    output.put("code", 1);
                    output.put("message", "Missing value for IN parameter: " + inParam);
                    output.put("data", new ArrayList<>());
                }
            }
        }

        // --- 3. Đăng ký OUT params (cho JPA đồng bộ connection)
        if (config.getOutParams() != null) {
            for (String outParam : config.getOutParams()) {
                query.registerStoredProcedureParameter(outParam, Object.class, ParameterMode.OUT);
            }
        }

        //query.execute();

        // --- 4. Dùng Hibernate để truy cập Connection gốc JDBC
        List<Map<String, Object>> tuples = new ArrayList<>();

        entityManager.unwrap(Session.class).doWork(connection -> {
            // Tạo chuỗi gọi procedure: {call PROC_NAME(?, ?, ?)}
            StringBuilder call = new StringBuilder("{call ");
            call.append(config.getName());

            int totalParams = (config.getInParams() == null ? 0 : config.getInParams().size())
                    + (config.getOutParams() == null ? 0 : config.getOutParams().size());

            if (totalParams > 0) {
                call.append("(");
                call.append(String.join(",", Collections.nCopies(totalParams, "?")));
                call.append(")");
            }
            call.append("}");

            try (CallableStatement stmt = connection.prepareCall(call.toString())) {
                int index = 0;

                // Gán IN param
                if (config.getInParams() != null) {
                    for (String key : config.getInParams()) {
                        index++;
                        
                        //stmt.setObject(index, params.get(key));

                        Object value = params.get(key);
                        
                        if (value == null) {
                            int sqlType = SqlTypeResolver.resolve(config.getParamType(key));
                            stmt.setNull(index, sqlType);
                        } else if (value instanceof java.util.Date) {
                            stmt.setTimestamp(index, new java.sql.Timestamp(((Date) value).getTime()));
                        } else {
                            stmt.setObject(index, value);
                        }
                    }
                }

                // Gán OUT param
                if (config.getOutParams() != null) {
                    Map<String, String> typeMap = config.getOutParamTypes();
                    for (String key : config.getOutParams()) {
                        String sqlTypeName = (typeMap != null) ? typeMap.get(key) : null;
                        int sqlType = SqlTypeResolver.resolve(sqlTypeName);
                        index++;
                        stmt.registerOutParameter(index, sqlType);
                    }
                }

                // Thực thi
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs != null) {
                        ResultSetMetaData meta = rs.getMetaData();
                        int colCount = meta.getColumnCount();

                        List<String> columnNames = new ArrayList<>();
                        for (int i = 1; i <= colCount; i++) {
                            columnNames.add(meta.getColumnLabel(i));
                        }

                        while (rs.next()) {
                            Map<String, Object> row = new LinkedHashMap<>();
                            for (String col : columnNames) {
                                row.put(col, rs.getObject(col));
                            }
                            tuples.add(row);
                        }
                    }
                }


                // --- 6. Lấy OUT params (nếu có)
                Map<String, Object> outValues = new LinkedHashMap<>();
                if (config.getOutParams() != null) {
                    int offset = (config.getInParams() == null ? 0 : config.getInParams().size());
                    int i = 1;
                    for (String key : config.getOutParams()) {
                        Object value = stmt.getObject(offset + i++);
                        outValues.put(key, value);
                    }
                    output.put("outParams", outValues);
                }
                // --- 7. Gộp kết quả trả về
                output.put("code", 0);
                output.put("message", "Thành công");
                output.put("data", tuples);
            } catch (Exception ex) {
                output.put("code", 1);
                output.put("message", ex.getMessage());
                output.put("data", new ArrayList<>());
            }
        });


        return output;
    }
}
