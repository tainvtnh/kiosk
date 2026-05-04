package com.tnh.kiosk.dtos;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiMessageDTO<T> {

    String message;
    T data;
    Boolean result;
    Integer code;
}