package com.example.plantshome.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InputValidatorTest {

    @Test
    public void email_acceptsNormalAddresses() {
        assertEquals(InputValidator.Result.OK, InputValidator.email("sherry@example.com"));
        assertEquals(InputValidator.Result.OK, InputValidator.email("first.last+plants@mail.co.uk"));
        assertEquals(InputValidator.Result.OK, InputValidator.email("  padded@example.com  "));
    }

    @Test
    public void email_rejectsEmptyAndMalformed() {
        assertEquals(InputValidator.Result.REQUIRED, InputValidator.email(null));
        assertEquals(InputValidator.Result.REQUIRED, InputValidator.email("   "));
        assertEquals(InputValidator.Result.INVALID_EMAIL, InputValidator.email("sherry"));
        assertEquals(InputValidator.Result.INVALID_EMAIL, InputValidator.email("sherry@"));
        assertEquals(InputValidator.Result.INVALID_EMAIL, InputValidator.email("sherry@example"));
        assertEquals(InputValidator.Result.INVALID_EMAIL, InputValidator.email("@example.com"));
    }

    @Test
    public void required_treatsWhitespaceAsEmpty() {
        assertEquals(InputValidator.Result.REQUIRED, InputValidator.required(""));
        assertEquals(InputValidator.Result.REQUIRED, InputValidator.required("  "));
        assertEquals(InputValidator.Result.OK, InputValidator.required("Sherry"));
    }

    @Test
    public void existingPassword_onlyNeedsSomething() {
        assertEquals(InputValidator.Result.REQUIRED, InputValidator.existingPassword(""));
        assertEquals(InputValidator.Result.OK, InputValidator.existingPassword("x"));
    }

    @Test
    public void newPassword_enforcesFirebaseMinimum() {
        assertEquals(InputValidator.Result.REQUIRED, InputValidator.newPassword(null));
        assertEquals(InputValidator.Result.TOO_SHORT, InputValidator.newPassword("12345"));
        assertEquals(InputValidator.Result.OK, InputValidator.newPassword("123456"));
        // Spaces count: passwords are never trimmed.
        assertEquals(InputValidator.Result.OK, InputValidator.newPassword("  abcd"));
    }

    @Test
    public void confirmation_mustMatchExactly() {
        assertEquals(InputValidator.Result.REQUIRED, InputValidator.confirmation("secret1", ""));
        assertEquals(InputValidator.Result.MISMATCH, InputValidator.confirmation("secret1", "secret2"));
        assertEquals(InputValidator.Result.MISMATCH, InputValidator.confirmation("secret1", "secret1 "));
        assertEquals(InputValidator.Result.OK, InputValidator.confirmation("secret1", "secret1"));
    }
}
