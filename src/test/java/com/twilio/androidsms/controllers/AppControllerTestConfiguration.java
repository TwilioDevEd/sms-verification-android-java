package com.twilio.androidsms.controllers;

import com.twilio.androidsms.services.SmsVerificationService;
import com.twilio.androidsms.services.TwilioClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class AppControllerTestConfiguration {

    @Bean
    @Primary
    public SmsVerificationService smsVerificationService() {
        return Mockito.mock(SmsVerificationService.class);
    }

    @Bean
    @Primary
    public TwilioClient twilioClient() {
        return Mockito.mock(TwilioClient.class);
    }
}
