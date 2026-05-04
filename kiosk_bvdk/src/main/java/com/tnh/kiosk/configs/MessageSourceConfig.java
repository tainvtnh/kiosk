package com.tnh.kiosk.configs;

import com.tnh.kiosk.utils.LogStyleHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

@Slf4j
@Configuration
public class MessageSourceConfig {

    @Bean
    public MessageSource messageSource() {
        log.debug(LogStyleHelper.debug("Configuring i18n message source"));
        var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setCacheSeconds(3600); // 1 hour cache
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        log.debug(LogStyleHelper.debug("Configuring locale resolver with English default and Vietnamese support"));
        var localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        localeResolver.setSupportedLocales(List.of(Locale.ENGLISH, Locale.forLanguageTag("vi")));
        return localeResolver;
    }
}