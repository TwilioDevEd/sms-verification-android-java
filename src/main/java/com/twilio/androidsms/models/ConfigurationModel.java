package com.twilio.androidsms.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import io.github.cdimascio.dotenv.Dotenv;

public class ConfigurationModel {

    private String twilioAccountSid;
    private String twilioApiKey;
    private String twilioApiSecret;
    private String sendingPhoneNumber;
    private String appHash;
    private String clientSecret;

    public ConfigurationModel() {
        Dotenv env = Dotenv.configure().ignoreIfMissing().load();
        this.twilioAccountSid = env.get("TWILIO_ACCOUNT_SID");
        this.twilioApiKey = env.get("TWILIO_API_KEY");
        this.twilioApiSecret = env.get("TWILIO_API_SECRET");
        this.sendingPhoneNumber = env.get("SENDING_PHONE_NUMBER");
        this.appHash = env.get("APP_HASH");
        this.clientSecret = env.get("CLIENT_SECRET");
    }

    public ConfigurationModel(String appHash) {
        this.appHash = appHash;
    }

    @JsonProperty("TWILIO_API_SECRET")
    public String getTwilioApiSecret() {
        return twilioApiSecret;
    }
    @JsonProperty("TWILIO_ACCOUNT_SID")
    public String getTwilioAccountSid() {
        return twilioAccountSid;
    }
    @JsonProperty("TWILIO_API_KEY")
    public String getTwilioApiKey() {
        return twilioApiKey;
    }
    @JsonProperty("SENDING_PHONE_NUMBER")
    public String getSendingPhoneNumber() {
        return sendingPhoneNumber;
    }
    @JsonProperty("APP_HASH")
    public String getAppHash() {
        return appHash;
    }
    @JsonProperty("CLIENT_SECRET")
    public String getClientSecret() {
        return clientSecret;
    }
}
