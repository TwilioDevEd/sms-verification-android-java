package com.twilio.androidsms.services;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class OneTimeCodeCacheTest {

    private OneTimeCodeCache subject;

    @Before
    public void setup() {
        subject = new OneTimeCodeCache();
    }

    @Test
    public void setGetAndRemoveValue() {
        subject.set("phone", 123);

        assertThat(subject.get("phone").get(), is(123));

        subject.remove("phone");

        assertFalse(subject.get("phone").isPresent());
    }
}