package com.tnh.kiosk;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.tnh.kiosk.utils.DefaultProfileUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@EnableAsync
@ConfigurationPropertiesScan
@SpringBootApplication(scanBasePackages = {"com.tnh.kiosk", "com.tnh.vietqr"})
@EnableJpaRepositories(basePackages = {"com.tnh.vietqr.repository"})
@EntityScan(basePackages = {"com.tnh.vietqr.entity"})
@EnableFeignClients(basePackages = {"com.tnh.vietqr.integration"})
@EnableScheduling
public class KioskApplication {

    private final Environment env;
    private Instant startTime;

    public KioskApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
        var app = new SpringApplication(KioskApplication.class);
        DefaultProfileUtils.addDefaultProfile(app);
        app.run(args);
    }

    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStart() {
        this.startTime = Instant.now();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logApplicationStartup() {
        if (startTime == null) {
            startTime = Instant.now();
        }

        var protocol = Optional.ofNullable(env.getProperty("server.ssl.key-store")).map(key -> "https").orElse("http");
        var serverPort = Optional.ofNullable(env.getProperty("server.port")).orElse("8887");
        var contextPath = Optional.ofNullable(env.getProperty("server.servlet.context-path")).orElse("/");
        var hostAddress = Optional.ofNullable(env.getProperty("server.address")).orElse("localhost");
        var activeProfiles = DefaultProfileUtils.getActiveProfiles(env);

        var startupTime = Duration.between(startTime, Instant.now());

        log.info("""
                ----------------------------------------------------------
                \tApplication '{}' is running! Access URLs:
                \tLocal: \t{}://localhost:{}{}
                \tExternal: \t{}://{}:{}{}
                \tProfile(s): \t{}
                \tStartup time: {}ms
                \tJVM Version: {}
                ----------------------------------------------------------""",
                env.getProperty("spring.application.name"),
                protocol, serverPort, contextPath,
                protocol, hostAddress, serverPort, contextPath,
                activeProfiles.isEmpty() ? "default" : activeProfiles,
                startupTime.toMillis(),
                System.getProperty("java.version"));
    }
}
