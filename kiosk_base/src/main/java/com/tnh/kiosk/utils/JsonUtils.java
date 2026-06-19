package com.tnh.kiosk.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class JsonUtils {

    private JsonUtils() {
    }

    public static List<Object> toJson(List<Tuple> results) {
        var json = new ArrayList<>();
        var mapper = new ObjectMapper();

        if (results == null) {
            return json;
        }

        results.forEach(t -> {
            var cols = t.getElements();
            var one = mapper.createObjectNode();

            cols.forEach(col -> {
                if (Double.class.isAssignableFrom(col.getJavaType())) {
                    var value = (Double) t.get(col.getAlias());
                    one.put(col.getAlias(), value != null ? String.format("%.2f", value) : " ");
                } else {
                    var value = t.get(col.getAlias());
                    one.put(col.getAlias(), value != null ? value.toString() : " ");
                }
            });
            json.add(one);
        });
        return json;
    }

    public static List<Object> readDataFromResource(String path) {
        List<Object> resultList = new ArrayList<>();
        try {
            var classPathResource = new ClassPathResource(path);
            var in = classPathResource.getInputStream();
            var jsonTxt = IOUtils.toString(in, StandardCharsets.UTF_8);
            resultList = Arrays.asList(new GsonBuilder().create().fromJson(jsonTxt, Object[].class));
        } catch (IOException e) {
            log.error(LogStyleHelper.error("Error reading data from resource: {}"), e.getMessage());
            return resultList;
        }
        return resultList;
    }

    public static void main(String[] args) {
        var data = readDataFromResource("data.json");
        log.info(LogStyleHelper.info("Data: {}"), data);
    }
}