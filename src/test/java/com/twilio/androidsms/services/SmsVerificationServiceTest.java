package com.twilio.androidsms.services;

import com.twilio.androidsms.models.ConfigurationModel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class SmsVerificationServiceTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private OneTimeCodeGenerator generator;

    @Mock
    private TwilioClient twilioClient;

    @Mock
    private OneTimeCodeCache oneTimeCodeCache;

    @Mock
    private ConfigurationService configurationService;

    private SmsVerificationService subject;

    @Before
    public void setup() {
        subject = new SmsVerificationService(generator, twilioClient, oneTimeCodeCache, configurationService);
    }

    @Test
    public void sendVerificationSms_GeneratesOneTimeCodeAndUsesTwilioToSendIt() {
        // given
        ConfigurationModel configurationModel = new ConfigurationModel("appHash");
        when(generator.generate()).thenReturn(1);
        when(twilioClient.sendMessage("phone", "message")).thenReturn("sid");
        when(configurationService.getConfiguration()).thenReturn(configurationModel);

        // when
        subject.sendVerificationSms("phone");

        // then
        verify(generator, times(1)).generate();
        verify(twilioClient, times(1))
                .sendMessage("phone", "[#] Use 1 as your code for the app! appHash");
        verify(oneTimeCodeCache, times(1)).set("phone", 1);
    }

    @Test
    public void verifyCode_ReturnFalseWhenNoCodeFoundOnCache() {
        // given
        when(oneTimeCodeCache.get("phone")).thenReturn(Optional.empty());

        // when
        boolean result = subject.verifyCode("phone", "message");

        // then
        assertFalse(result);
    }

    @Test
    public void verifyCode_ReturnFalseWhenCodeDoesNotMatchTheOneFoundOnCache() {
        // given
        when(oneTimeCodeCache.get("phone")).thenReturn(Optional.of(123));

        // when
        boolean result = subject.verifyCode("phone", "message");

        // then
        assertFalse(result);
    }

    @Test
    public void verifyCode_ReturnTrueWhenCodeMatchesTheOneStoredOnCache() {
        // given
        when(oneTimeCodeCache.get("phone")).thenReturn(Optional.of(123));

        // when
        boolean result = subject.verifyCode("phone", "message 123");

        // then
        assertTrue(result);
    }

    @Test
    public void resetCode_ReturnTrueAndRemoveElementWhenItIsInCache() {
        // given
        when(oneTimeCodeCache.get("phone")).thenReturn(Optional.of(123));

        // when
        boolean result = subject.resetCode("phone");

        // then
        assertTrue(result);
        verify(oneTimeCodeCache, times(1)).remove("phone");
    }

    @Test
    public void resetCode_ReturnFalseElementIsNotInCache() {
        // given
        when(oneTimeCodeCache.get("phone")).thenReturn(Optional.empty());

        // when
        boolean result = subject.resetCode("phone");

        // then
        assertFalse(result);
        verify(oneTimeCodeCache, never()).remove("phone");
    }
}