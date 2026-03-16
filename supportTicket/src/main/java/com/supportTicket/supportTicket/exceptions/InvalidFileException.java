package com.supportTicket.supportTicket.exceptions;

public class InvalidFileException extends RuntimeException {

    public InvalidFileException(String message) {
        super(message);
    }
}