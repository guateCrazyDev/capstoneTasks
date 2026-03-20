package com.supportTicket.supportTicket.loggingConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.slf4j.Logger;
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

@Component // Registers this filter automatically in Spring
public class HttpLoggingFilter extends OncePerRequestFilter {

    // Logger instance used to write logs
    private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);

    // Maximum allowed size for request/response body logging
    private static final int MAX_PAYLOAD_LENGTH = 5000;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Wrap request to allow reading the body multiple times
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, 1024 * 10);

        // Wrap response to capture the body before sending it to the client
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // Store start time to calculate execution duration
        long start = System.currentTimeMillis();

        try {
            // Continue normal request flow (controllers, services, etc.)
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            // Calculate how long the request took
            long duration = System.currentTimeMillis() - start;

            // Log incoming request
            logRequest(requestWrapper);

            // Log outgoing response
            logResponse(responseWrapper, duration);

            // IMPORTANT: copy response body back to client
            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {

        // Extract request body
        String body = getPayload(request.getContentAsByteArray());

        // Structured JSON log
        log.info("http_request",
                StructuredArguments.keyValue("method", request.getMethod()),
                StructuredArguments.keyValue("path", request.getRequestURI()),
                StructuredArguments.keyValue("query", request.getQueryString()),
                StructuredArguments.keyValue("ip", request.getRemoteAddr()),
                StructuredArguments.keyValue("headers", Collections.list(request.getHeaderNames())),
                StructuredArguments.keyValue("body", sanitize(body)));
    }

    private void logResponse(
            ContentCachingResponseWrapper response,
            long duration) {

        // Extract response body
        String body = getPayload(response.getContentAsByteArray());

        // Structured JSON log
        log.info("http_response",
                StructuredArguments.keyValue("status", response.getStatus()),
                StructuredArguments.keyValue("duration_ms", duration),
                StructuredArguments.keyValue("body", sanitize(body)));
    }

    private String getPayload(byte[] content) {

        // Return null if no content
        if (content == null || content.length == 0)
            return null;

        // Convert byte array to String
        String payload = new String(content, StandardCharsets.UTF_8);

        // Truncate if payload is too large
        if (payload.length() > MAX_PAYLOAD_LENGTH) {
            return payload.substring(0, MAX_PAYLOAD_LENGTH) + "...(truncated)";
        }

        return payload;
    }

    private String sanitize(String body) {

        // Avoid null pointer issues
        if (body == null)
            return null;

        // Mask sensitive data
        return body
                .replaceAll("(?i)\"password\"\\s*:\\s*\".*?\"", "\"password\":\"***\"")
                .replaceAll("(?i)\"jwt\"\\s*:\\s*\".*?\"", "\"jwt\":\"***\"")
                .replaceAll("(?i)\"token\"\\s*:\\s*\".*?\"", "\"token\":\"***\"");
    }
}