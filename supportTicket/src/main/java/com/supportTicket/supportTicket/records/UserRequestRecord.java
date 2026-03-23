package com.supportTicket.supportTicket.records;

public record UserRequestRecord(
        String originalUsername,
        String username,
        String password,
        String role) {
}