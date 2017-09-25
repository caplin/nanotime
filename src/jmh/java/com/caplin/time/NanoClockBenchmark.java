/*
 * Copyright 2017 Caplin Systems Ltd
 */
package com.caplin.time;

import org.openjdk.jmh.annotations.*;

import java.time.Clock;
import java.time.Instant;

@State(Scope.Benchmark)
public class NanoClockBenchmark {
    private Clock nanoClock;
    private Clock systemClock;

    @Setup
    public void setup(){
        nanoClock = new NanoClock();
        systemClock = Clock.systemDefaultZone();
    }

    @Benchmark @BenchmarkMode(Mode.AverageTime)
    public long measureGetSystemCurrentTime(){
        return System.currentTimeMillis();
    }

    @Benchmark @BenchmarkMode(Mode.AverageTime)
    public Instant measureGetSystemClockTime(){
        return systemClock.instant();
    }

    @Benchmark @BenchmarkMode(Mode.AverageTime)
    public Instant measureGetNanoClockTime(){
        return nanoClock.instant();
    }
}
