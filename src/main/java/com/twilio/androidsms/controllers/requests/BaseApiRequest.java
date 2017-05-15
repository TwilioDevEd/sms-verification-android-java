package com.twilio.androidsms.controllers.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import static org.springframework.util.StringUtils.isEmpty;


public class BaseApiRequest {
    private String clientSecret;
    private String phone;

    public BaseApiRequest() {
    }

    public BaseApiRequest(String clientSecret, String phone) {

        this.clientSecret = clientSecret;
        this.phone = phone;
    }

    @JsonProperty("client_secret")
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @JsonProperty("phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isNotValid() {
        return isEmpty(clientSecret) || isEmpty(phone);
    }
}
