package com.unittestdemo;

import com.unittestdemo.util.Utility;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilityValidator {

    @Test
    public void edittext_wrongInput_ReturnsZero() {
        assertEquals(1234, Utility.parseNumber("1234"));
    }
    @Test
    public void edittext_correctInput_ReturnsNumber() {
        assertEquals(1234, Utility.parseNumber("1234"));
    }
    @Test
    public void edittext_double_ReturnsNumber() throws NumberFormatException {
        assertEquals(1234, Utility.parseNumber("1234"));
    }
}
