package com.tnh.kiosk.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resource")
public class ResourceController {
    private final JdbcTemplate jdbcTemplate;

    public ResourceController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        // Example: query current time from SQL Server
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT GETDATE() as now");
        return Map.of("dbNow", rows.isEmpty() ? null : rows.get(0).get("now"));
    }
}