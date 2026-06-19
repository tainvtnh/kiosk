package com.tnh.kiosk.controllers;

import com.tnh.kiosk.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("${system.api-prefix}/authen")
@Slf4j
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Value("${app.auth.username}")
    private String configUsername;

    @Value("${app.auth.password}")
    private String configPassword;

    @Value("${app.auth.hospital-id}")
    private String configHospitalId;

    @Value("${app.auth.full-name:Người dùng hệ thống}")
    private String fullName;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String hospitalId = body.get("HOSPITALID");

        if (!configUsername.equals(username)
                || !configPassword.equals(password)
                || !configHospitalId.equalsIgnoreCase(hospitalId)) {

            Map<String, Object> error = new LinkedHashMap<>();
            error.put("code", 1);
            error.put("comment", "Sai tên đăng nhập, mật khẩu hoặc mã CSKCB");
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // Sinh token
        String accessToken = jwtUtil.generateAccessToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);

        // Lấy thời điểm hết hạn access token
        Date expDate = jwtUtil.getExpirationDate(accessToken);
        String expiration = expDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));

        // Dữ liệu trả về
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("access_token", accessToken);
        data.put("refresh_token", refreshToken);
        data.put("full_name", fullName);
        data.put("expiration", expiration);
        data.put("id", 1);
        data.put("username", username);

        // Kết quả cuối cùng
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 0);
        result.put("message", "Thành công");
        result.put("data", data);

        return ResponseEntity.ok(result);
    }
}
