package com.supportTicket.supportTicket.exceptions;

public class PlaceNotFoundException extends RuntimeException {
    public PlaceNotFoundException(String placeName) {
        super("Place not found with name: " + placeName);
    }
}