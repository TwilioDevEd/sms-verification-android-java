package com.twilio.androidsms.services;

import com.twilio.Twilio;
import com.twilio.androidsms.models.ConfigurationModel;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TwilioClient {

    @Autowired
    private ConfigurationService configurationService;

    @PostConstruct
    public void init() {
        ConfigurationModel configuration = configurationService.getConfiguration();
        Twilio.init(configuration.getTwilioApiKey(),
                configuration.getTwilioApiSecret(),
                configuration.getTwilioAccountSid());
    }

    public String sendMessage(String phone, String message){
        PhoneNumber from = new PhoneNumber(configurationService.getConfiguration().getSendingPhoneNumber());
        PhoneNumber to = new PhoneNumber(phone);
        return Message.creator(to, from, message)
                .create()
                .getSid();
    }
}
