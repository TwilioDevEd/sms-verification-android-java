package com.twilio.androidsms.controllers;

import com.twilio.androidsms.models.ConfigurationModel;
import com.twilio.androidsms.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {

    @Autowired
    private ConfigurationService configurationService;

    @RequestMapping(method = RequestMethod.GET, value = "/config")
    public ConfigurationModel getConfig() {
        return configurationService.getConfiguration();
    }
}
