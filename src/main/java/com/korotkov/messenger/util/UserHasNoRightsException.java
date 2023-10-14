package com.korotkov.messenger.util;

public class UserHasNoRightsException extends RuntimeException {
    public UserHasNoRightsException(String message) {
        super(message);
    }
}
