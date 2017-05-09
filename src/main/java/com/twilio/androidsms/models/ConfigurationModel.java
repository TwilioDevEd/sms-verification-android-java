package com.twilio.androidsms.models;

import java.util.Map;

public class ConfigurationModel {
    

    private String twilioAccountSid;
    private String twilioApiKey;
    private String sendingPhoneNumber;
    private String appHash;
    private String clientSecret;

    public ConfigurationModel() {
        Map<String, String> env = System.getenv();
        this.twilioAccountSid = env.get("TWILIO_ACCOUNT_SID");
        this.twilioApiKey = env.get("TWILIO_API_KEY");
        this.sendingPhoneNumber = env.get("SENDING_PHONE_NUMBER");
        this.appHash = env.get("APP_HASH");
        this.clientSecret = env.get("CLIENT_SECRET");
    }



    public String getTwilioAccountSid() {
        return twilioAccountSid;
    }

    public String getTwilioApiKey() {
        return twilioApiKey;
    }

    public String getSendingPhoneNumber() {
        return sendingPhoneNumber;
    }

    public String getAppHash() {
        return appHash;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
