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

    @PostMapping("/generator")
    public Map<String, Object> callProcedure(@RequestBody(required = false) Map<String, Object> params) {
        Map<String, Object> result = executeAndFormat("generator", params, false);

        Object dataObj = result.get("data");
        if (dataObj instanceof List<?> dataList && !dataList.isEmpty()) {
            if (dataList.get(0) instanceof Map<?, ?> firstItem) {
                Object sttObj = firstItem.get("stt");
                if (sttObj instanceof Number number) {
                    int stt = number.intValue();
                    String message = getMessageByCode(stt);
                    if (message != null) {
                        return buildResponse(stt, message, new ArrayList<>());
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
        return switch (code) {
            case 5000 -> "Bệnh nhân đã tiếp nhận trong ngày";
            case 5001 -> "Bệnh nhân đang nằm nội trú";
            case 5002 -> "Bệnh nhân còn thuốc";
            case 5003 -> "Bệnh nhân tiếp nhận ở trung tâm khác";
            case 5004 -> "Lỗi đường truyền";
            default -> "Bệnh nhân đã tiếp nhận!";
        };
    }

    @GetMapping("/service-catalog")
    public ResponseEntity<Map<String, Object>> getServiceCatalog(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(executeAndFormat("service-catalog", params, true));
    }

    @GetMapping("/waiting-room")
    public ResponseEntity<Map<String, Object>> getWaitingRoom(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(executeAndFormat("waiting-room", params, true));
    }

    @GetMapping("/area-catalog")
    public ResponseEntity<Map<String, Object>> getAreaCatalog(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(executeAndFormat("area-catalog", params, true));
    }

    @GetMapping("/treatment-history")
    public ResponseEntity<Map<String, Object>> getTreatmentHistory(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(executeAndFormat("treatment-history", params, false));
    }

    @PostMapping("/update-payment")
    public Map<String, Object> callUpdatePayment(@RequestBody Map<String, Object> params) {
        return executeAndFormat("update-payment", params, false);
    }

    @GetMapping("/get-province")
    public Map<String, Object> getProvince() {
        return executeAndFormat("get-province", null, false);
    }

    @GetMapping("/get-commune")
    public ResponseEntity<Map<String, Object>> getCommune(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(executeAndFormat("get-commune", params, false));
    }

    @PostMapping("/update-payment-dvkt")
    public Map<String, Object> callUpdatePaymentDVKT(@RequestBody Map<String, Object> params) {
        return executeAndFormat("update-payment-dvkt", params, false);
    }

    @GetMapping("/get-sublinical-payment")
    public ResponseEntity<Map<String, Object>> getSublinicalPayment(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = executeAndFormat("get-sublinical-payment", params, false);
        Object dataObj = result.get("data");

        if (dataObj instanceof List<?>) {
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataObj;

            for (Map<String, Object> item : dataList) {
                Object chiDinhRaw = item.get("chiDinh");
                if (chiDinhRaw instanceof String chiDinhStr && !chiDinhStr.isEmpty()) {
                    try {
                        if (chiDinhStr.contains("\"chiDinh\":")) {
                            if (!chiDinhStr.trim().startsWith("{")) {
                                chiDinhStr = "{" + chiDinhStr + "}";
                            }
                            Map<String, Object> tempMap = mapper.readValue(chiDinhStr, new TypeReference<Map<String, Object>>() {});
                            item.put("chiDinh", tempMap.get("chiDinh"));
                        } else {
                            List<Object> chiDinhParsed = mapper.readValue(chiDinhStr, new TypeReference<List<Object>>() {});
                            item.put("chiDinh", chiDinhParsed);
                        }
                    } catch (Exception e) {
                        System.err.println("Lỗi parse JSON chiDinh: " + e.getMessage());
                    }
                }
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/ethnic")
    public ResponseEntity<Map<String, Object>> getEthnic(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(executeAndFormat("ethnic", params, true));
    }

    @GetMapping("/career")
    public ResponseEntity<Map<String, Object>> getCareer(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(executeAndFormat("career", params, true));
    }

    @PostMapping("/get-information-patient")
    public Map<String, Object> getInformationPatient(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = executeAndFormat("information-patient", body, false);
        Object dataObj = result.get("data");

        if (dataObj instanceof List<?> dataList && !dataList.isEmpty()) {
            return result;
        }
        return buildResponse(1, "Không tìm thấy dữ liệu", new HashMap<>());
    }

    @PostMapping("/get-tiepnhan-cv")
    public Map<String, Object> getTiepNhanCv(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = executeAndFormat("get-tiepnhan-cv", body, false);
        Object dataObj = result.get("data");

        if (dataObj instanceof List<?> dataList && !dataList.isEmpty()) {
            return result;
        }
        return buildResponse(1, "Không tìm thấy dữ liệu", new HashMap<>());
    }

    @GetMapping("/get-pvcom-test")
    public Map<String, Object> getPvcomTest() {
        return executeAndFormat("get-pvcom-test", null, false);
    }

    private Map<String, Object> executeAndFormat(String key, Map<String, Object> params, boolean supportPagination) {
        List<String> schema = kioskService.getInParams(key);

        List<String> allowedKeys = schema != null ? new ArrayList<>(schema) : new ArrayList<>();
        if (supportPagination) {
            allowedKeys.add("page");
            allowedKeys.add("size");
        }
        if ("get-commune".equals(key)) {
            allowedKeys.add("districtid");
        }

        if (params != null) {
            validateParams(params, allowedKeys);
        }

        Map<String, Object> userParamsCI = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        if (params != null) {
            userParamsCI.putAll(params);
        }

        Map<String, Object> orderedParams = new LinkedHashMap<>();
        for (String paramName : schema) {
            Object value = "MA_TINH_THANH".equals(paramName) && userParamsCI.containsKey("districtid")
                    ? userParamsCI.get("districtid")
                    : userParamsCI.get(paramName);

            if (value != null) {
                if ("soTien".equals(paramName)) {
                    value = value.toString();
                } else if ("thoiGianThanhToanUTC".equals(paramName)) {
                    value = value.toString().replace("T", " ");
                }
            }
            orderedParams.put(paramName, value);
        }

        Map<String, Object> result = kioskService.executeProcedure(key, orderedParams);

        if (supportPagination) {
            boolean hasPageOrSize = userParamsCI.containsKey("page") || userParamsCI.containsKey("size");
            if (hasPageOrSize || "area-catalog".equals(key) || "ethnic".equals(key) || "career".equals(key)) {
                int page = 0;
                int size = 10;
                if (userParamsCI.containsKey("page")) {
                    try {
                        page = Integer.parseInt(userParamsCI.get("page").toString());
                    } catch (NumberFormatException ignored) {}
                }
                if (userParamsCI.containsKey("size")) {
                    try {
                        size = Integer.parseInt(userParamsCI.get("size").toString());
                    } catch (NumberFormatException ignored) {}
                }

                Object dataObj = result.get("data");
                if (dataObj instanceof List<?>) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataObj;
                    return PaginationUtils.paginate(dataList, page, size);
                }
            }
        }

        return result;
    }

    private Map<String, Object> buildResponse(Object code, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    private void validateParams(Map<String, Object> params, List<String> allowedKeys) {
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