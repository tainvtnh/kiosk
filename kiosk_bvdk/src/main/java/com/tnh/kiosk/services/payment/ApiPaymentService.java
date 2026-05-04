package com.tnh.kiosk.services.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ApiPaymentService {

    private final RestTemplate restTemplate = new RestTemplate();
    //private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ObjectMapper objectMapper;

    // 1️⃣ Trả về object TResponse
    public <TRequest, TResponse> TResponse sendRequest(
            String url,
            HttpMethod method,
            TRequest requestBody,
            Map<String, String> headers,
            Class<TResponse> responseClass
    ) throws Exception {  // <--- thêm throws Exception
        HttpHeaders httpHeaders = buildHeaders(headers);
        HttpEntity<String> entity = buildEntity(requestBody, httpHeaders);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url, method, entity, String.class
        );

        return objectMapper.readValue(responseEntity.getBody(), responseClass);
    }

    // 2️⃣ Trả về List<TResponse>
    public <TRequest, TResponse> List<TResponse> sendRequestForList(
            String url,
            HttpMethod method,
            TRequest requestBody,
            Map<String, String> headers,
            Class<TResponse> elementClass
    ) throws Exception { // <--- thêm throws Exception
        HttpHeaders httpHeaders = buildHeaders(headers);
        HttpEntity<String> entity = buildEntity(requestBody, httpHeaders);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url, method, entity, String.class
        );

        return objectMapper.readValue(
                responseEntity.getBody(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, elementClass)
        );
    }

    // 3️⃣ Trả về Map<String, Object>
    public <TRequest> Map<String, Object> sendRequestForMap(
            String url,
            HttpMethod method,
            TRequest requestBody,
            Map<String, String> headers
    ) throws Exception { // <--- thêm throws Exception
        HttpHeaders httpHeaders = buildHeaders(headers);
        HttpEntity<String> entity = buildEntity(requestBody, httpHeaders);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url, method, entity, String.class
        );

        return objectMapper.readValue(
                responseEntity.getBody(),
                new TypeReference<Map<String, Object>>() {}
        );
    }

    // -------------------
    // Helper
    private <TRequest> HttpEntity<String> buildEntity(TRequest requestBody, HttpHeaders headers) throws Exception {
        String body = requestBody != null ? objectMapper.writeValueAsString(requestBody) : null;
        return new HttpEntity<>(body, headers);
    }

    private HttpHeaders buildHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (headers != null) {
            headers.forEach(httpHeaders::set);
        }
        return httpHeaders;
    }
}