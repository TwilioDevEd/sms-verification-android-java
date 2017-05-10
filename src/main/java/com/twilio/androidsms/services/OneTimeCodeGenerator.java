package com.twilio.androidsms.services;

import org.springframework.stereotype.Service;

@Service
public class OneTimeCodeGenerator {
    
    private static final int CODE_LENGTH = 6;

    public int generate() {
        return (int) (Math.floor(Math.random() * (Math.pow(10, (CODE_LENGTH - 1)) * 9)) + Math.pow(10, (CODE_LENGTH - 1)));
    }
}
