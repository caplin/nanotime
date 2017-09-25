/*
 * Copyright 2017 Caplin Systems Ltd
 */
package com.caplin.time;

import org.junit.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.assertTrue;

public class NanoClockTest {
    @Test
    public void testCorrectTime() {
        Instant caplintime = new NanoClock().instant();
        Instant systemtime = Clock.systemUTC().instant();
        assertTrue(String.format("Incorrect time, caplin_time returned '%s' and System time was '%s'", caplintime.toString(), systemtime.toString()),
                Duration.between(caplintime,systemtime).abs().toMillis() <= 1);
    }
}