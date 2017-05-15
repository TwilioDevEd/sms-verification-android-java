package com.twilio.androidsms.exceptions;

public class ClientSecretsMismatch extends RuntimeException{
    public ClientSecretsMismatch(String message) {
        super(message);
    }
}
