package com.tnh.kiosk.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tnh.kiosk.services.KioskService;
import com.tnh.kiosk.utils.PaginationUtils;

@RestController
@RequestMapping("${system.api-prefix}/his")
public class KioskController {

    private final KioskService kioskService;

    public KioskController(KioskService kioskService) {
        this.kioskService = kioskService;
    }

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
        //List<String> allowedKeys = List.of("page", "size", "chuyenkhoaid", "khuvucid", "doituongbnid");
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
            String searchKey = key.toLowerCase();
            Object value = lowerParams.getOrDefault(searchKey, defaultValues.get(key));
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
        //List<String> allowedKeys = List.of("page", "size", "chuyenkhoaid", "khuvucid", "doituongbnid");
        List<String> allowedKeys = List.of("maDV");

        // Giá trị mặc định cho từng key
        Map<String, Object> defaultValues = Map.of(
            "page", 0,
            "size", 10,
            "maDV", 0
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
            String searchKey = key.toLowerCase();
            Object value = lowerParams.getOrDefault(searchKey, defaultValues.get(key));
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
            String searchKey = key.toLowerCase();
            Object value = lowerParams.getOrDefault(searchKey, defaultValues.get(key));
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

    //A007 - Lấy danh sách lịch sử điều trị
    @GetMapping("/treatment-history")
    public ResponseEntity<Map<String, Object>> getTreatmentHistory(@RequestParam Map<String, Object> params) {

        // Danh sách các key hợp lệ
        List<String> allowedKeys = List.of("tenNb", "ngaySinh", "tinhThanhPhoId", "quanHuyenId", "xaPhuongId", "gioiTinh", "soGiayToTuyThan", "loaiGiayTo");

        // Giá trị mặc định cho từng key
        Map<String, Object> defaultValues = Map.of(
            "tenNb", "Nguyen Van A",
            "ngaySinh", "01/01/2000",
            "tinhThanhPhoId", 72,
            "quanHuyenId", 799,
            "xaPhuongId", 25866,
            "gioiTinh", 0,
            "soGiayToTuyThan", "012345678912",
            "loaiGiayTo", "1"
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
            String searchKey = key.toLowerCase();
            Object value = lowerParams.getOrDefault(searchKey, defaultValues.get(key));
            ciParams.put(key, value);
        }

        // Gọi stored procedure
        Map<String, Object> result = kioskService.executeProcedure("treatment-history", ciParams);

        return ResponseEntity.ok(result);
    }

    //A008 - Cập nhật trạng thái thanh toán
    @PostMapping("/update-payment")
    public Map<String, Object> callUpdatePayment(@RequestBody Map<String, Object> params) {
        // Lấy danh sách inParams chuẩn từ config (thứ tự: maHoSo, maNb, ..., checksum)
        List<String> schema = kioskService.getInParams("update-payment"); 

        // Dùng generator để build Map đúng thứ tự tuyệt đối
        Map<String, Object> ciParams = prepareParams(schema, params);

        return kioskService.executeProcedure("update-payment", ciParams);
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

    //A012 - Cập nhật trạng thái thanh toán DVKT
    @PostMapping("/update-payment-dvkt")
    public Map<String, Object> callUpdatePaymentDVKT(@RequestBody Map<String, Object> params) {
        // Lấy danh sách inParams chuẩn từ config (thứ tự: maHoSo, maNb, ..., checksum)
        List<String> schema = kioskService.getInParams("update-payment-dvkt"); 

        // Dùng generator để build Map đúng thứ tự tuyệt đối
        Map<String, Object> ciParams = prepareParams(schema, params);

        return kioskService.executeProcedure("update-payment-dvkt", ciParams);
    }

    // A013 - Lấy danh sách dịch vụ
  

    @GetMapping("/get-sublinical-payment")
    public ResponseEntity<Map<String, Object>> getSublinicalPayment(@RequestParam Map<String, Object> params) {

        List<String> allowedKeys = List.of("code", "option");

        Map<String, Object> defaultValues = Map.of(
                "code", "012345678912",
                "option", 0
        );

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

        Map<String, Object> ciParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        Map<String, Object> lowerParams = new HashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            lowerParams.put(entry.getKey().toLowerCase(), entry.getValue());
        }

        for (String key : allowedKeys) {
            String searchKey = key.toLowerCase();
            Object value = lowerParams.getOrDefault(searchKey, defaultValues.get(key));
            ciParams.put(key, value);
        }

        // 🔥 Gọi Stored Procedure từ Service
        Map<String, Object> result = kioskService.executeProcedure("get-sublinical-payment", ciParams);

        // ================== 🔥 FIX CHI DINH (BẮT ĐẦU SỬA TỪ ĐÂY) ==================
        Object dataObj = result.get("data");

        if (dataObj instanceof List<?>) {
            ObjectMapper mapper = new ObjectMapper();
            // Ép kiểu sang List<Map> để thao tác với từng record người bệnh
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataObj;

            for (Map<String, Object> item : dataList) {
                Object chiDinhRaw = item.get("chiDinh");

                if (chiDinhRaw instanceof String && !((String) chiDinhRaw).isEmpty()) {
                    try {
                        String chiDinhStr = (String) chiDinhRaw;

                        // Kiểm tra nếu chuỗi có format dư thừa kiểu "\"chiDinh\":[...]"
                        if (chiDinhStr.contains("\"chiDinh\":")) {
                            // Bọc thêm {} để thành JSON Object hợp lệ trước khi parse
                            if (!chiDinhStr.trim().startsWith("{")) {
                                chiDinhStr = "{" + chiDinhStr + "}";
                            }
                            Map<String, Object> tempMap = mapper.readValue(chiDinhStr, new TypeReference<Map<String, Object>>() {});
                            item.put("chiDinh", tempMap.get("chiDinh"));
                        } else {
                            // Trường hợp chuỗi chỉ là mảng JSON thuần [...]
                            List<Object> chiDinhParsed = mapper.readValue(chiDinhStr, new TypeReference<List<Object>>() {});
                            item.put("chiDinh", chiDinhParsed);
                        }
                    } catch (Exception e) {
                        // Nếu lỗi parse, log ra và giữ nguyên String hoặc để null
                        System.err.println("Lỗi parse JSON chiDinh: " + e.getMessage());
                    }
                }
            }
        }
        // ================== 🔥 KẾT THÚC FIX ==================

        return ResponseEntity.ok(result);
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
            String searchKey = key.toLowerCase();
            Object value = lowerParams.getOrDefault(searchKey, defaultValues.get(key));
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
            String searchKey = key.toLowerCase();
            Object value = lowerParams.getOrDefault(searchKey, defaultValues.get(key));
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

    // A018 - Lấy thông tin bệnh nhân
    @PostMapping("/get-information-patient")
    public Map<String, Object> getInformationPatient(@RequestBody Map<String, Object> body) {

        // Danh sách các key hợp lệ để validate
        List<String> allowedKeys = List.of("code", "option");
        
        // Giá trị mặc định
        Map<String, Object> defaultValues = Map.of(
            "code", "012345678912",
            "option", 0
        );

        try {
            // Validate body request thay vì params
            validateParams(body, allowedKeys);
        } catch (IllegalArgumentException ex) {
            return buildResponse(1, ex.getMessage(), new HashMap<>());
        }

        // Xử lý Case Insensitive cho Body (nếu client gửi Code thay vì code)
        Map<String, Object> ciParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        body.forEach((key, value) -> ciParams.put(key, value));

        // Đảm bảo có đủ các key cần thiết từ defaultValues nếu body thiếu
        for (String key : allowedKeys) {
            if (!ciParams.containsKey(key)) {
                ciParams.put(key, defaultValues.get(key));
            }
        }

        // Gọi stored procedure với tham số từ body
        Map<String, Object> result = kioskService.executeProcedure("information-patient", ciParams);
        Object dataObj = result.get("data");

        if (dataObj instanceof List<?> dataList && !dataList.isEmpty()) {
           return result;
        }

        return buildResponse(1, "Không tìm thấy dữ liệu", new HashMap<>());
    }

    // A019 - Lấy tiếp nhận CV
    @PostMapping("/get-tiepnhan-cv")
    public Map<String, Object> getTiepNhanCv(@RequestBody Map<String, Object> body) {

        // Danh sách các key hợp lệ để validate
        List<String> allowedKeys = List.of("SO_THE_BHYT");
        
        // Giá trị mặc định
        Map<String, Object> defaultValues = Map.of(
            "SO_THE_BHYT", "LH2807221719247"
        );

        try {
            // Validate body request thay vì params
            validateParams(body, allowedKeys);
        } catch (IllegalArgumentException ex) {
            return buildResponse(1, ex.getMessage(), new HashMap<>());
        }

        // Xử lý Case Insensitive cho Body (nếu client gửi Code thay vì code)
        Map<String, Object> ciParams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        body.forEach((key, value) -> ciParams.put(key, value));

        // Đảm bảo có đủ các key cần thiết từ defaultValues nếu body thiếu
        for (String key : allowedKeys) {
            if (!ciParams.containsKey(key)) {
                ciParams.put(key, defaultValues.get(key));
            }
        }

        // Gọi stored procedure với tham số từ body
        Map<String, Object> result = kioskService.executeProcedure("get-tiepnhan-cv", ciParams);
        Object dataObj = result.get("data");

        if (dataObj instanceof List<?> dataList && !dataList.isEmpty()) {
           return result;
        }

        return buildResponse(1, "Không tìm thấy dữ liệu", new HashMap<>());
    }
   
    private Map<String, Object> buildResponse(Object code, String comment, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("comment", comment);
        response.put("data", data);
        return response;
    }

    public Map<String, Object> prepareParams(List<String> configInParams, Map<String, Object> userParams) {
        // Dùng LinkedHashMap để giữ đúng thứ tự của list config
        Map<String, Object> orderedParams = new LinkedHashMap<>();

        for (String paramName : configInParams) {
            Object value = userParams.get(paramName);
            
            // Xử lý riêng cho các trường hay lỗi
            if ("soTien".equals(paramName) && value != null) {
                orderedParams.put(paramName, value.toString()); // Ép về String cho nvarchar
            } 
            else if ("thoiGianThanhToanUTC".equals(paramName) && value != null) {
                orderedParams.put(paramName, value.toString().replace("T", " "));
            }
            else {
                orderedParams.put(paramName, value);
            }
        }
        return orderedParams;
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