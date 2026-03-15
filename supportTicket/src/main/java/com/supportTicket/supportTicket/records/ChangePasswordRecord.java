package com.supportTicket.supportTicket.records;

public record ChangePasswordRecord(
        String username,
        String oldPassword,
        String newPassword) {
}