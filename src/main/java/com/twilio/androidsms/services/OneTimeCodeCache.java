package com.twilio.androidsms.services;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OneTimeCodeCache {

    public void set(String phone, int code){}

    public Optional<Integer> get(String phone){
        return null;
    }

    public void remove(String phone) {

    }
}
