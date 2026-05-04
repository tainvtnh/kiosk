package com.tnh.kiosk.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // Không dùng callSuper=true vì không kế thừa lớp khác
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryParam {

    String name;

    ParamMode type; // IN hoặc OUT

    Integer sqlType; // java.sql.Types

    public enum ParamMode {
        IN,
        OUT
    }

    // Ví dụ tạo param dễ dàng
    public static QueryParam in(String name, Integer sqlType) {
        return QueryParam.builder()
                .name(name)
                .type(ParamMode.IN)
                .sqlType(sqlType)
                .build();
    }

    public static QueryParam out(String name, Integer sqlType) {
        return QueryParam.builder()
                .name(name)
                .type(ParamMode.OUT)
                .sqlType(sqlType)
                .build();
    }
}