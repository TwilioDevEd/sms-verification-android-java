package com.twilio.androidsms.controllers.requests;

import static org.springframework.util.StringUtils.isEmpty;

public class VerifyCodeRequest extends BaseApiRequest{

    private String smsMessage;

    public VerifyCodeRequest() {
    }

    public VerifyCodeRequest(String clientSecret, String phone, String smsMessage) {
        super(clientSecret, phone);
        this.smsMessage = smsMessage;
    }

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
