package com.tnh.kiosk.controllers;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tnh.kiosk.services.KioskService;
import com.tnh.kiosk.utils.PaginationUtils;

@RestController
@RequestMapping("${system.api-prefix}/his")
public class KioskController {

    private final KioskService kioskService;

    public KioskController(KioskService kioskService) {
        this.kioskService = kioskService;
    }

    // // Gọi query
    // @PostMapping("/query/{key}")
    // public List<Tuple> callQuery(@PathVariable String key, @RequestBody Map<String, Object> params) {
    //     return kioskService.executeQuery(key, params);
    // }

    // // Gọi procedure
    // @PostMapping("/{key}")
    // public Map<String, Object> callProcedure(@PathVariable String key, @RequestBody(required = false) Map<String, Object> params) {
        
    //     Map<String, Object> ciParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    //     if (params == null) {
    //         params = new HashMap<>();
    //     } else {
    //         ciParams.putAll(params);
    //     }
        
    //     return kioskService.executeProcedure(key, ciParams);
    // }

    //A003 - Lấy danh số thứ tự
    @PostMapping("/generator")
    public Map<String, Object> callProcedure(@RequestBody(required = false) Map<String, Object> params) {
        
        Map<String, Object> ciParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        if (params == null) {
            params = new HashMap<>();
        } else {
            ciParams.putAll(params);
        }
        
        Map<String, Object> result = kioskService.executeProcedure("generator", ciParams);

        Object dataObj = result.get("data");

        if (dataObj instanceof List<?>) {
            List<?> dataList = (List<?>) dataObj;

            if (!dataList.isEmpty() && dataList.get(0) instanceof Map<?, ?>) {
                Map<String, Object> firstItem = (Map<String, Object>) dataList.get(0);

                // Lấy giá trị stt
                Object sttObj = firstItem.get("stt");
                if (sttObj instanceof Number) {
                    int stt = ((Number) sttObj).intValue();
                    System.out.println("STT = " + stt);

                    // Xử lý theo mã lỗi
                    String message = getMessageByCode(stt);
                    if (message != null) {
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("code", "1");
                        errorResponse.put("message", message);
                        errorResponse.put("data", new ArrayList<>());
                        return errorResponse;
                    }
                }
            }
        }

        //return kioskService.executeProcedure("generator", ciParams);

        return result;
    }

    private String getMessageByCode(int code) {

        if (code < 5000) {
            return null;
        }

        switch (code) {
            case 5000:
                return "Bệnh nhân đã tiếp nhận trong ngày";
            case 5001:
                return "Bệnh nhân đang nằm nội trú";
            case 5002:
                return "Bệnh nhân còn thuốc";
            case 5003:
                return "Bệnh nhân tiếp nhận ở trung tâm khác";
            case 5004:
                return "Lỗi đường truyền";
            default:
                return "Bệnh nhân đã tiếp nhận!"; // không có lỗi, cho qua
        }
    }


    // A004 - Lấy danh sách dịch vụ khám
    @GetMapping("/service-catalog")
    public ResponseEntity<Map<String, Object>> getServiceCatalog(@RequestParam Map<String, Object> params) {

        // Danh sách các key hợp lệ
        List<String> allowedKeys = List.of("page", "size", "chuyenkhoaid", "khuvucid", "doituongbnid");

        // Giá trị mặc định cho từng key
        Map<String, Object> defaultValues = Map.of(
            "page", 0,
            "size", 10,
            "chuyenkhoaid", 0,
            "khuvucid", 0,
            "doituongbnid", 0
        );

        // Kiểm tra các key không hợp lệ
        try {
            validateParams(params, allowedKeys);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "code", 1,
                            "comment", ex.getMessage(),
                            "data", new ArrayList<>()
                    ));
        }

        // Dùng TreeMap để bỏ qua phân biệt hoa thường
        Map<String, Object> ciParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        Map<String, Object> lowerParams = new HashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            lowerParams.put(entry.getKey().toLowerCase(), entry.getValue());
        }

        // Tạo ciParams cuối cùng, gồm cả giá trị mặc định
        for (String key : allowedKeys) {
            Object value = lowerParams.getOrDefault(key, defaultValues.get(key));
            ciParams.put(key, value);
        }

        // Gọi stored procedure
        Map<String, Object> result = kioskService.executeProcedure("service-catalog", ciParams);

        return ResponseEntity.ok(result);
    }
    
    //A005 - Lấy danh sách phòng khám
    @GetMapping("/waiting-room")
    public ResponseEntity<Map<String, Object>> getWaitingRoom(@RequestParam Map<String, Object> params) {

        // Danh sách các key hợp lệ
        List<String> allowedKeys = List.of("page", "size", "chuyenkhoaid", "khuvucid", "doituongbnid");

        // Giá trị mặc định cho từng key
        Map<String, Object> defaultValues = Map.of(
            "page", 0,
            "size", 10,
            "chuyenkhoaid", 0,
            "khuvucid", 0,
            "doituongbnid", 0
        );

        // Kiểm tra các key không hợp lệ
        try {
            validateParams(params, allowedKeys);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "code", 1,
                            "comment", ex.getMessage(),
                            "data", new ArrayList<>()
                    ));
        }

        // Dùng TreeMap để bỏ qua phân biệt hoa thường
        Map<String, Object> ciParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        Map<String, Object> lowerParams = new HashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            lowerParams.put(entry.getKey().toLowerCase(), entry.getValue());
        }

        // Tạo ciParams cuối cùng, gồm cả giá trị mặc định
        for (String key : allowedKeys) {
            Object value = lowerParams.getOrDefault(key, defaultValues.get(key));
            ciParams.put(key, value);
        }

        // Gọi stored procedure
        Map<String, Object> result = kioskService.executeProcedure("waiting-room", ciParams);

        return ResponseEntity.ok(result);
    }

    //A006 - Lấy danh sách khu vực
    @GetMapping("/area-catalog")
    public ResponseEntity<Map<String, Object>> getAreaCatalog(@RequestParam Map<String, Object> params) {

         // Danh sách các key hợp lệ
        List<String> allowedKeys = List.of("page", "size");

        // Giá trị mặc định cho từng key
        Map<String, Object> defaultValues = Map.of(
            "page", 0,
            "size", 10
        );

        // Kiểm tra các key không hợp lệ
        try {
            validateParams(params, allowedKeys);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "code", 1,
                            "comment", ex.getMessage(),
                            "data", new ArrayList<>()
                    ));
        }

        // Dùng TreeMap để bỏ qua phân biệt hoa thường
        Map<String, Object> ciParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        Map<String, Object> lowerParams = new HashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            lowerParams.put(entry.getKey().toLowerCase(), entry.getValue());
        }

        // Tạo ciParams cuối cùng, gồm cả giá trị mặc định
        for (String key : allowedKeys) {
            Object value = lowerParams.getOrDefault(key, defaultValues.get(key));
            ciParams.put(key, value);
        }

        // Gọi stored procedure
        Map<String, Object> result = kioskService.executeProcedure("area-catalog", ciParams);
        List<Map<String, Object>> data = (List<Map<String, Object>>) result.get("data");

        int page = Integer.parseInt((String) lowerParams.getOrDefault("page", "0"));
        int size = Integer.parseInt((String) lowerParams.getOrDefault("size", "10"));
        Map<String, Object> paginated = PaginationUtils.paginate(data, page, size);

        return ResponseEntity.ok(paginated);
    }

    //A009 - Lấy danh sách tỉnh thành phố
    @GetMapping("/get-province")
    public Map<String, Object> getProvince() {

        Map<String, Object> ciParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        return kioskService.executeProcedure("get-province", ciParams);
    }

    //A011 - Lấy danh sách phường xã
    @GetMapping("/get-commune")
    public ResponseEntity<Map<String, Object>> getCommune(@RequestParam Map<String, Object> params) {

          // Danh sách các key hợp lệ
        List<String> allowedKeys = List.of("districtid");

        // Giá trị mặc định cho từng key
        Map<String, Object> defaultValues = Map.of(
            "districtid", 0
        );

        // Kiểm tra các key không hợp lệ
        try {
            validateParams(params, allowedKeys);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "code", 1,
                            "comment", ex.getMessage(),
                            "data", new ArrayList<>()
                    ));
        }

        // Dùng TreeMap để bỏ qua phân biệt hoa thường
        Map<String, Object> ciParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        Map<String, Object> lowerParams = new HashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            lowerParams.put(entry.getKey().toLowerCase(), entry.getValue());
        }

        ciParams.put("MA_TINH_THANH", lowerParams.getOrDefault("districtid", defaultValues.get("districtid")));

        return ResponseEntity.ok(kioskService.executeProcedure("get-commune", ciParams));
    }

    //A014 - Lấy danh sách dân tộc
    @GetMapping("/ethnic")
    public ResponseEntity<Map<String, Object>> getEthnic(@RequestParam Map<String, Object> params) {

          // Danh sách các key hợp lệ
        List<String> allowedKeys = List.of("page", "size");

        // Giá trị mặc định cho từng key
        Map<String, Object> defaultValues = Map.of(
            "page", 0,
            "size", 10
        );

        // Kiểm tra các key không hợp lệ
        try {
            validateParams(params, allowedKeys);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "code", 1,
                            "comment", ex.getMessage(),
                            "data", new ArrayList<>()
                    ));
        }

        // Dùng TreeMap để bỏ qua phân biệt hoa thường
        Map<String, Object> ciParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        Map<String, Object> lowerParams = new HashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            lowerParams.put(entry.getKey().toLowerCase(), entry.getValue());
        }

        // Tạo ciParams cuối cùng, gồm cả giá trị mặc định
        for (String key : allowedKeys) {
            Object value = lowerParams.getOrDefault(key, defaultValues.get(key));
            ciParams.put(key, value);
        }

        // Gọi stored procedure
        Map<String, Object> result = kioskService.executeProcedure("ethnic", ciParams);
        List<Map<String, Object>> data = (List<Map<String, Object>>) result.get("data");

        int page = Integer.parseInt((String) lowerParams.getOrDefault("page", "0"));
        int size = Integer.parseInt((String) lowerParams.getOrDefault("size", "10"));
        Map<String, Object> paginated = PaginationUtils.paginate(data, page, size);

        return ResponseEntity.ok(paginated);
    }

    //A015 - Lấy danh sách nghề nghiệp
    @GetMapping("/career")
    public ResponseEntity<Map<String, Object>> getCareer(@RequestParam Map<String, Object> params) {

        // Danh sách các key hợp lệ
        List<String> allowedKeys = List.of("page", "size");

        // Giá trị mặc định cho từng key
        Map<String, Object> defaultValues = Map.of(
            "page", 0,
            "size", 10
        );

        // Kiểm tra các key không hợp lệ
        try {
            validateParams(params, allowedKeys);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "code", 1,
                            "comment", ex.getMessage(),
                            "data", new ArrayList<>()
                    ));
        }

        // Dùng TreeMap để bỏ qua phân biệt hoa thường
        Map<String, Object> ciParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        Map<String, Object> lowerParams = new HashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            lowerParams.put(entry.getKey().toLowerCase(), entry.getValue());
        }

        // Tạo ciParams cuối cùng, gồm cả giá trị mặc định
        for (String key : allowedKeys) {
            Object value = lowerParams.getOrDefault(key, defaultValues.get(key));
            ciParams.put(key, value);
        }

        // Gọi stored procedure
        Map<String, Object> result = kioskService.executeProcedure("career", ciParams);
        List<Map<String, Object>> data = (List<Map<String, Object>>) result.get("data");

        int page = Integer.parseInt((String) lowerParams.getOrDefault("page", "0"));
        int size = Integer.parseInt((String) lowerParams.getOrDefault("size", "10"));
        Map<String, Object> paginated = PaginationUtils.paginate(data, page, size);

        return ResponseEntity.ok(paginated);
    }

    void validateParams(Map<String, Object> params, List<String> allowedKeys) {
        List<String> normalizedAllowedKeys = allowedKeys.stream()
                .map(String::toLowerCase)
                .toList();

        List<String> invalidKeys = params.keySet().stream()
                .filter(key -> !normalizedAllowedKeys.contains(key.toLowerCase()))
                .toList();

        if (!invalidKeys.isEmpty()) {
            throw new IllegalArgumentException("Invalid parameters: " + String.join(", ", invalidKeys));
        }
    }

}