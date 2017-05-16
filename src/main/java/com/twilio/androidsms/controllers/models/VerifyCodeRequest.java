package com.twilio.androidsms.controllers.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import static org.springframework.util.StringUtils.isEmpty;

public class VerifyCodeRequest extends BaseApiRequest{

    private String smsMessage;

    public VerifyCodeRequest() {
    }

    public VerifyCodeRequest(String clientSecret, String phone, String smsMessage) {
        super(clientSecret, phone);
        this.smsMessage = smsMessage;
    }

    @JsonProperty("sms_message")
    public String getSmsMessage() {
        return smsMessage;
    }

    public void setSmsMessage(String smsMessage) {
        this.smsMessage = smsMessage;
    }

    public boolean isNotValid() {
        return super.isNotValid() || isEmpty(smsMessage);
    }
}
