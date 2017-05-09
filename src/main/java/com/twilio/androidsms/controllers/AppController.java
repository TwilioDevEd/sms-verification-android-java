package com.twilio.androidsms.controllers;

import com.twilio.androidsms.controllers.models.BaseAppResponse;
import com.twilio.androidsms.exceptions.ControllerException;
import com.twilio.androidsms.exceptions.MissingParametersException;
import com.twilio.androidsms.services.ConfigurationService;
import com.twilio.androidsms.services.SmsVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.StringUtils.isEmpty;

@RestController
public class AppController {

    @Autowired
    private SmsVerificationService smsVerificationService;

    @Autowired
    private ConfigurationService configurationService;

    @RequestMapping(method = RequestMethod.POST, value = "/api/request")
    public BaseAppResponse sendVerificationCode(
            @RequestParam(value = "client_secret", required = false) String clientSecret,
            @RequestParam(value = "phone", required = false) String phone) {

        checkClientSecretAndPhoneArePresent(clientSecret, phone);
        checkClientSecretsMatch(clientSecret);

        smsVerificationService.sendVerificationSms(phone);

        return new BaseAppResponse(true, SmsVerificationService.expirationSeconds);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/verify")
    public BaseAppResponse verifyCode(
            @RequestParam(value = "sms_message", required = false) String smsMessage,
            @RequestParam(value = "client_secret", required = false) String clientSecret,
            @RequestParam(value = "phone", required = false) String phone) {

        if(isEmpty(smsMessage) || isEmpty(clientSecret) || isEmpty(phone)) {
            throw new MissingParametersException(
                    "The client_secret, phone, and sms_message parameters are required");
        }
        checkClientSecretsMatch(clientSecret);

        boolean result = smsVerificationService.verifyCode(phone, smsMessage);
        String message = !result ? "Unable to validate code for this phone number" : null;

        return new BaseAppResponse(result, phone, message);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/reset")
    public BaseAppResponse resetCode(
            @RequestParam(value = "client_secret", required = false) String clientSecret,
            @RequestParam(value = "phone", required = false) String phone) {

        checkClientSecretAndPhoneArePresent(clientSecret, phone);
        checkClientSecretsMatch(clientSecret);

        smsVerificationService.reset(phone);

        return new BaseAppResponse(true, SmsVerificationService.expirationSeconds);
    }

    private void checkClientSecretAndPhoneArePresent(@RequestParam(value = "client_secret", required = false) String clientSecret, @RequestParam(value = "phone", required = false) String phone) {
        if(isEmpty(clientSecret) || isEmpty(phone)) {
            throw new MissingParametersException(
                    "The client_secret and phone parameters are required");
        }
    }

    private void checkClientSecretsMatch(@RequestParam(value = "client_secret", required = false) String clientSecret) {
        if(!clientSecret.equals(configurationService.getConfiguration().getClientSecret())) {
            throw new ControllerException("The client_secret parameter does not match.");
        }
    }
}
