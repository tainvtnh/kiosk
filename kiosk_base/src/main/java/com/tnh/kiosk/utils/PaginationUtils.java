package com.tnh.kiosk.utils;

import java.util.*;

public class PaginationUtils {

    /**
     * Phân trang cho một danh sách (List)
     *
     * @param data Danh sách gốc
     * @param page Trang hiện tại (bắt đầu từ 0)
     * @param size Số phần tử mỗi trang
     * @return Map chứa thông tin phân trang và dữ liệu trang hiện tại
     */
    public static <T> Map<String, Object> paginate(List<T> data, int page, int size) {
        if (data == null || data.isEmpty()) {
            return Map.of(
                    "code", 0,
                    "message", "Thành công",
                    "page", page,
                    "size", size,
                    "totalElements", 0,
                    "totalPages", 0,
                    "data", List.of()
            );
        }

        int totalElements = data.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Giới hạn chỉ số cắt trang
        int fromIndex = Math.min(page * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<T> pagedData = data.subList(fromIndex, toIndex);

        return Map.of(
                   "code", 0,
                   "message", "Thành công",
                "page", page,
                "size", size,
                "totalElements", totalElements,
                "totalPages", totalPages,
                "data", pagedData
        );
    }
}

