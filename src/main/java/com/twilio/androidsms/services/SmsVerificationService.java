package com.twilio.androidsms.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SmsVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(SmsVerificationService.class);

    public static final int expirationSeconds = 900;

    private final String smsBodyTemplate = "[#] Use %s as your code for the app! %s";

    @Autowired
    private OneTimeCodeGenerator generator;

    @Autowired
    private TwilioClient twilioClient;

    @Autowired
    private OneTimeCodeCache cache;

    @Autowired
    private ConfigurationService configurationService;

    public SmsVerificationService(OneTimeCodeGenerator generator, TwilioClient twilioClient, OneTimeCodeCache oneTimeCodeCache, ConfigurationService configurationService) {
        this.generator = generator;
        this.twilioClient = twilioClient;
        this.cache = oneTimeCodeCache;
        this.configurationService = configurationService;
    }

    public void sendVerificationSms(String phone) {
        logger.debug("Requesting SMS to be sent to {}", phone);

        int otp = generator.generate();
        cache.set(phone, otp);
        String message = String.format(smsBodyTemplate, otp,
                configurationService.getConfiguration().getAppHash());
        logger.debug(message
        );
        twilioClient.sendMessage(phone, message);
    }

    public boolean verifyCode(String phone, String message) {
        logger.debug("Verifying {} : {}", phone, message);
        Optional<Integer> otp = cache.get(phone);

        if(otp.isPresent() && message.contains(String.valueOf(otp.get()))) {
            logger.debug("Found otp in cache");
            return true;
        }
        logger.debug("Otp value not found in cache or the value found does not match " +
                "the one in message. Phone: {}", phone);
        return false;
    }

    public boolean resetCode(String phone) {
        logger.debug("Resetting code for: {}", phone);
        Optional<Integer> otp = cache.get(phone);
        if(otp.isPresent()) {
            cache.remove(phone);
            return true;
        }
        logger.debug("No cached otp value found for phone: {}", phone);
        return false;
    }
}
