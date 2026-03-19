package com.supportTicket.supportTicket.loggingConfig;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.logstash.logback.argument.StructuredArguments;

@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(HttpLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper =
                new ContentCachingRequestWrapper(request, 1024 * 10);
        ContentCachingResponseWrapper responseWrapper =
                new ContentCachingResponseWrapper(response);

        long start = System.currentTimeMillis();

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - start;

            logRequest(requestWrapper);
            logResponse(responseWrapper, duration);

            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String body = new String(
                request.getContentAsByteArray(),
                StandardCharsets.UTF_8
        );

        log.info("http_request",
            StructuredArguments.keyValue("method", request.getMethod()),
            StructuredArguments.keyValue("path", request.getRequestURI()),
            StructuredArguments.keyValue("query", request.getQueryString()),
            StructuredArguments.keyValue("body", sanitize(body))
        );
    }

    private void logResponse(
            ContentCachingResponseWrapper response,
            long duration
    ) {
        String body = new String(
                response.getContentAsByteArray(),
                StandardCharsets.UTF_8
        );

        log.info("http_response",
            StructuredArguments.keyValue("status", response.getStatus()),
            StructuredArguments.keyValue("duration_ms", duration),
            StructuredArguments.keyValue("body", sanitize(body))
        );
    }

    private String sanitize(String body) {
        if (body == null) return null;


        return body
	        .replaceAll("(?i)\"password\"\\s*:\\s*\".*?\"", "\"password\":\"***\"")
	        .replaceAll("(?i)\"jwt\"\\s*:\\s*\".*?\"", "\"jwt\":\"***\"");

    }
}
