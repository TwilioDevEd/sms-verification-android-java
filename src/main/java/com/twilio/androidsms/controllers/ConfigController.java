package com.twilio.androidsms.controllers;

import com.twilio.androidsms.controllers.models.ConfigResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {

    @RequestMapping(method = RequestMethod.GET, value = "/config")
    public ConfigResponse getConfig() {
        return new ConfigResponse();
    }
}
