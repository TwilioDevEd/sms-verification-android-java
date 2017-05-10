package com.twilio.androidsms.controllers;

import com.twilio.androidsms.App;
import com.twilio.androidsms.services.SmsVerificationService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@WebAppConfiguration
public class AppControllerTest {

    @ClassRule
    public static final EnvironmentVariables environmentVariables
            = new EnvironmentVariables();

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SmsVerificationService smsVerificationService;

    @BeforeClass
    public static void setupClass() throws Exception {
        environmentVariables.set("TWILIO_ACCOUNT_SID", "accountSid");
        environmentVariables.set("TWILIO_API_KEY", "apiKey");
        environmentVariables.set("SENDING_PHONE_NUMBER", "sendingPhoneNumber");
        environmentVariables.set("APP_HASH", "appHash");
        environmentVariables.set("CLIENT_SECRET", "clientSecret");
    }

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        Mockito.reset(smsVerificationService);
    }

    @Test
    public void sendVerificationSmsReturns400WhenClientSecretParameterIsAbsent() throws Exception {
        mockMvc.perform(post("/api/request")
                .param("phone", "phone"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "The client_secret and phone parameters are required"));
    }

    @Test
    public void sendVerificationSmsReturns400WhenPhoneParameterIsAbsent() throws Exception {
        mockMvc.perform(post("/api/request")
                .param("client_secret", "clientSecret"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "The client_secret and phone parameters are required"));
    }

    @Test
    public void sendVerificationSmsReturn500WhenClientSecretsDontMatch() throws Exception {
        mockMvc.perform(post("/api/request")
                .param("client_secret", "invalid")
                .param("phone", "phone"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("The client_secret parameter does not match."));
    }

    @Test
    public void sendVerificationSmsAndReturnsSuccessJson() throws Exception {
        mockMvc.perform(post("/api/request")
                .param("client_secret", "clientSecret")
                .param("phone", "phone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.time", is(900)));

        Mockito.verify(smsVerificationService,
                Mockito.times(1)).sendVerificationSms("phone");
    }

    @Test
    public void verifyCodeReturns400WhenClientSecretParameterIsAbsent() throws Exception {
        mockMvc.perform(post("/api/verify")
                .param("sms_message", "smsMessage")
                .param("phone", "phone"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "The client_secret, phone, and sms_message parameters are required"));
    }

    @Test
    public void verifyCodeReturns400WhenSmsMessageParameterIsAbsent() throws Exception {
        mockMvc.perform(post("/api/verify")
                .param("client_secret", "clientSecret")
                .param("phone", "phone"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "The client_secret, phone, and sms_message parameters are required"));
    }

    @Test
    public void verifyCodeReturns400WhenPhoneParameterIsAbsent() throws Exception {
        mockMvc.perform(post("/api/verify")
                .param("client_secret", "clientSecret")
                .param("sms_message", "smsMessage"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "The client_secret, phone, and sms_message parameters are required"));
    }

    @Test
    public void verifyCodeReturn500WhenClientSecretsDontMatch() throws Exception {
        mockMvc.perform(post("/api/verify")
                .param("client_secret", "invalid")
                .param("phone", "phone")
                .param("sms_message", "smsMessage"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("The client_secret parameter does not match."));
    }

    @Test
    public void verifyCodeReturnSuccessMessageForSuccessfulVerification() throws Exception {
        when(smsVerificationService.verifyCode("phone", "smsMessage"))
                .thenReturn(true);

        mockMvc.perform(post("/api/verify")
                .param("client_secret", "clientSecret")
                .param("phone", "phone")
                .param("sms_message", "smsMessage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.phone", is("phone")));
    }

    @Test
    public void verifyCodeReturnUnsuccessfulMessageForFailedVerification() throws Exception {
        when(smsVerificationService.verifyCode("phone", "smsMessage"))
                .thenReturn(false);

        mockMvc.perform(post("/api/verify")
                .param("client_secret", "clientSecret")
                .param("phone", "phone")
                .param("sms_message", "smsMessage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.msg", is(
                        "Unable to validate code for this phone number")));
    }

    @Test
    public void resetReturns400WhenClientSecretParameterIsAbsent() throws Exception {
        mockMvc.perform(post("/api/reset")
                .param("phone", "phone"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "The client_secret and phone parameters are required"));
    }

    @Test
    public void resetReturns400WhenPhoneParameterIsAbsent() throws Exception {
        mockMvc.perform(post("/api/reset")
                .param("client_secret", "clientSecret"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "The client_secret and phone parameters are required"));
    }

    @Test
    public void resetReturn500WhenClientSecretsDontMatch() throws Exception {
        mockMvc.perform(post("/api/reset")
                .param("client_secret", "invalid")
                .param("phone", "phone"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("The client_secret parameter does not match."));
    }

    @Test
    public void resetReturnsSuccessMessageWhenPhoneResetsSucceeds() throws Exception {
        when(smsVerificationService.resetCode("phone")).thenReturn(true);

        mockMvc.perform(post("/api/reset")
                .param("client_secret", "clientSecret")
                .param("phone", "phone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.phone", is("phone")));

        Mockito.verify(smsVerificationService, Mockito.times(1))
                .resetCode("phone");
    }

    @Test
    public void resetReturnsUnsuccessfulMessageWhenResetFails() throws Exception {
        when(smsVerificationService.resetCode("phone")).thenReturn(false);

        mockMvc.perform(post("/api/reset")
                .param("client_secret", "clientSecret")
                .param("phone", "phone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.msg",
                        is("Unable to reset code for this phone number")));

        Mockito.verify(smsVerificationService, Mockito.times(1))
                .resetCode("phone");
    }
}


