package com.twilio.androidsms.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.androidsms.App;
import com.twilio.androidsms.controllers.models.BaseApiRequest;
import com.twilio.androidsms.controllers.models.VerifyCodeRequest;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@WebAppConfiguration
public class ApiControllerTest {

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
        BaseApiRequest request = new BaseApiRequest(null, "phone");
        mockMvc.perform(post("/api/request")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "The client_secret and phone parameters are required"));
    }

    @Test
    public void sendVerificationSmsReturns400WhenPhoneParameterIsAbsent() throws Exception {
        BaseApiRequest request = new BaseApiRequest("clientSecret", null);

        mockMvc.perform(post("/api/request")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "The client_secret and phone parameters are required"));
    }

    @Test
    public void sendVerificationSmsReturn400WhenClientSecretsDontMatch() throws Exception {
        BaseApiRequest request = new BaseApiRequest("invalid", "phone");

        mockMvc.perform(post("/api/request")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The client_secret parameter does not match."));
    }

    @Test
    public void sendVerificationSmsAndReturnsSuccessJson() throws Exception {
        BaseApiRequest request = new BaseApiRequest("clientSecret", "phone");

        mockMvc.perform(post("/api/request")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.time", is(900)));

        Mockito.verify(smsVerificationService,
                Mockito.times(1)).sendVerificationSms("phone");
    }

    @Test
    public void verifyCodeReturns400WhenClientSecretParameterIsAbsent() throws Exception {
        VerifyCodeRequest request = new VerifyCodeRequest(null, "phone", "smsMessage");

        mockMvc.perform(post("/api/verify")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "The client_secret, phone, and sms_message parameters are required"));
    }

    @Test
    public void verifyCodeReturns400WhenSmsMessageParameterIsAbsent() throws Exception {
        VerifyCodeRequest request = new VerifyCodeRequest("clientSecret", "phone", null);

        mockMvc.perform(post("/api/verify")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "The client_secret, phone, and sms_message parameters are required"));
    }

    @Test
    public void verifyCodeReturns400WhenPhoneParameterIsAbsent() throws Exception {
        VerifyCodeRequest request = new VerifyCodeRequest("clientSecret", null, "smsMessage");

        mockMvc.perform(post("/api/verify")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "The client_secret, phone, and sms_message parameters are required"));
    }

    @Test
    public void verifyCodeReturn400WhenClientSecretsDontMatch() throws Exception {
        VerifyCodeRequest request = new VerifyCodeRequest("invalid", "phone", "smsMessage");

        mockMvc.perform(post("/api/verify")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The client_secret parameter does not match."));
    }

    @Test
    public void verifyCodeReturnSuccessMessageForSuccessfulVerification() throws Exception {
        when(smsVerificationService.verifyCode("phone", "smsMessage"))
                .thenReturn(true);

        VerifyCodeRequest request = new VerifyCodeRequest("clientSecret", "phone", "smsMessage");

        mockMvc.perform(post("/api/verify")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.phone", is("phone")));
    }

    @Test
    public void verifyCodeReturnUnsuccessfulMessageForFailedVerification() throws Exception {
        when(smsVerificationService.verifyCode("phone", "smsMessage"))
                .thenReturn(false);

        VerifyCodeRequest request = new VerifyCodeRequest("clientSecret", "phone", "smsMessage");

        mockMvc.perform(post("/api/verify")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.msg", is(
                        "Unable to validate code for this phone number")));
    }

    @Test
    public void resetReturns400WhenClientSecretParameterIsAbsent() throws Exception {
        BaseApiRequest request = new BaseApiRequest(null, "phone");

        mockMvc.perform(post("/api/reset")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "The client_secret and phone parameters are required"));
    }

    @Test
    public void resetReturns400WhenPhoneParameterIsAbsent() throws Exception {
        BaseApiRequest request = new BaseApiRequest("clientSecret", null);

        mockMvc.perform(post("/api/reset")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "The client_secret and phone parameters are required"));
    }

    @Test
    public void resetReturn400WhenClientSecretsDontMatch() throws Exception {
        BaseApiRequest request = new BaseApiRequest("invalid", "phone");

        mockMvc.perform(post("/api/reset")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The client_secret parameter does not match."));
    }

    @Test
    public void resetReturnsSuccessMessageWhenPhoneResetsSucceeds() throws Exception {
        when(smsVerificationService.resetCode("phone")).thenReturn(true);

        BaseApiRequest request = new BaseApiRequest("clientSecret", "phone");

        mockMvc.perform(post("/api/reset")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.phone", is("phone")));

        Mockito.verify(smsVerificationService, Mockito.times(1))
                .resetCode("phone");
    }

    @Test
    public void resetReturnsUnsuccessfulMessageWhenResetFails() throws Exception {
        when(smsVerificationService.resetCode("phone")).thenReturn(false);

        BaseApiRequest request = new BaseApiRequest("clientSecret", "phone");

        mockMvc.perform(post("/api/reset")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.msg",
                        is("Unable to reset code for this phone number")));

        Mockito.verify(smsVerificationService, Mockito.times(1))
                .resetCode("phone");
    }

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
}


