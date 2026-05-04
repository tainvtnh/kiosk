package com.tnh.kiosk.utils;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.List;

@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public final class DefaultProfileUtils {

    private static final String DEFAULT_PROFILE = "dev";

    private DefaultProfileUtils() {
    }

    public static void addDefaultProfile(SpringApplication app) {
        app.setDefaultProperties(Collections.singletonMap("spring.profiles.default", DEFAULT_PROFILE));
        log.info(LogStyleHelper.info("Default profile set to '{}'"), DEFAULT_PROFILE);
    }

    public static List<String> getActiveProfiles(Environment env) {
        var profiles = env.getActiveProfiles();
        return profiles.length > 0 ? List.of(profiles) : List.of(DEFAULT_PROFILE);
    }
}