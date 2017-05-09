package com.twilio.androidsms.services;

import com.twilio.androidsms.exceptions.ConfigurationException;
import com.twilio.androidsms.models.ConfigurationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);
    private ConfigurationModel configurationModel;

    public ConfigurationModel getConfiguration() {
        if(configurationModel == null) {
            configurationModel = new ConfigurationModel();
        }
        return configurationModel;
    }

    @PostConstruct
    public void validateConfiguration() {
        getConfiguration();
        if (configurationModel.getTwilioApiKey() == null ||
                configurationModel.getTwilioAccountSid() == null) {
            String message = "Please copy the .env.example file to .env, " +
                    "and then add your Twilio API Key, API Secret, " +
                    "and Account SID to the .env file. " +
                    "Find them on https://www.twilio.com/console";
            logger.error(message);
            throw new ConfigurationException(message);
        }

        if (configurationModel.getSendingPhoneNumber() == null) {
            String message = "Please provide a valid phone number, " +
                    "such as +15125551212, in the .env file";
            logger.error(message);
            throw new ConfigurationException(message);
        }

        if (configurationModel.getAppHash() == null) {
            String message = "Please provide a valid Android app hash, " +
                    "which you can find in the Settings menu item " +
                    "of the Android app, in the .env file";
            logger.error(message);
            throw new ConfigurationException(message);
        }

        if (configurationModel.getClientSecret() == null) {
            String message = "Please provide a secret string to share, " +
                    "between the app and the server " +
                    "in the .env file";
            logger.error(message);
            throw new ConfigurationException(message);
        }
    }
}
