package com.twilio.androidsms.controllers;

import com.twilio.androidsms.controllers.models.BaseApiRequest;
import com.twilio.androidsms.controllers.models.BaseAppResponse;
import com.twilio.androidsms.controllers.models.VerifyCodeRequest;
import com.twilio.androidsms.exceptions.ClientSecretsMismatch;
import com.twilio.androidsms.exceptions.MissingParametersException;
import com.twilio.androidsms.services.ConfigurationService;
import com.twilio.androidsms.services.SmsVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiController {

    @Autowired
    private SmsVerificationService smsVerificationService;

    @Autowired
    private ConfigurationService configurationService;

    @RequestMapping(method = RequestMethod.POST, value = "/api/request")
    public BaseAppResponse sendVerificationCode(@RequestBody BaseApiRequest request) {

        checkBaseApiRequestIsValid(request);
        checkClientSecretsMatch(request);

        smsVerificationService.sendVerificationSms(request.getPhone());

        return new BaseAppResponse(true, SmsVerificationService.expirationSeconds);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/verify")
    public BaseAppResponse verifyCode(@RequestBody VerifyCodeRequest request) {
        if(request.isNotValid()) {
            throw new MissingParametersException(
                    "The client_secret, phone, and sms_message parameters are required");
        }
        checkClientSecretsMatch(request);

        boolean result = smsVerificationService.verifyCode(request.getPhone(), request.getSmsMessage());
        String message = !result ? "Unable to validate code for this phone number" : null;

        return new BaseAppResponse(result, request.getPhone(), message);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/api/reset")
    public BaseAppResponse resetCode(@RequestBody BaseApiRequest request) {

        checkBaseApiRequestIsValid(request);
        checkClientSecretsMatch(request);

        boolean result = smsVerificationService.resetCode(request.getPhone());
        String message = !result ? "Unable to reset code for this phone number" : null;

        return new BaseAppResponse(result, request.getPhone(), message);
    }

    private void checkBaseApiRequestIsValid(@RequestBody BaseApiRequest request) {
        if(request.isNotValid()) {
            throw new MissingParametersException(
                    "The client_secret and phone parameters are required");
        }
    }

    private void checkClientSecretsMatch(BaseApiRequest request) {
        if(!request.getClientSecret().equals(configurationService.getConfiguration().getClientSecret())) {
            throw new ClientSecretsMismatch("The client_secret parameter does not match.");
        }
    }
}
