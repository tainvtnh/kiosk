package com.tnh.kiosk.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tnh.kiosk.dtos.ApiMessageDTO;
import com.tnh.kiosk.exceptions.BWCGenericRuntimeException;
import com.tnh.kiosk.services.MessageService;
import com.tnh.kiosk.utils.LogStyleHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class BasewareCoreFilter extends OncePerRequestFilter {

    MessageService messageService;
    ObjectMapper objectMapper;
    AntPathMatcher pathMatcher = new AntPathMatcher();

    protected Optional<String> getAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7));
    }

    protected void sendErrorResponse(HttpServletResponse response, int statusCode, String messageKey) {
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try (var outputStream = response.getOutputStream()) {
            var errorResponse = ApiMessageDTO.<String>builder()
                    .result(false)
                    .code(statusCode)
                    .message(messageService.getMessage(messageKey))
                    //.message(messageKey)
                    .build();

            outputStream.write(objectMapper.writeValueAsBytes(errorResponse));
            outputStream.flush();
        } catch (IOException e) {
            log.error(LogStyleHelper.error("Error writing error response: {}"), e.getMessage(), e);
            throw new BWCGenericRuntimeException("Error writing error response", e);
        }
    }
}

