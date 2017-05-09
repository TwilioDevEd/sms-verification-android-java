package com.twilio.androidsms.services;

import org.springframework.stereotype.Service;

@Service
public class SmsVerificationService {

    public static final int expirationSeconds = 900;

    public boolean sendVerificationSms(String phone) {
        return false;
    }

    public boolean verifyCode(String phone, String smsMessage) {
        return false;
    }

    public void reset(String phone) {

    }
}
