package com.caplin.time;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
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

    @Benchmark @BenchmarkMode(Mode.SampleTime)
    public long measureGetSystemCurrentCurrentTime(){
        return System.currentTimeMillis();
    }

    @Benchmark @BenchmarkMode(Mode.SampleTime)
    public Instant measureGetSystemClockTime(){
        return systemClock.instant();
    }

    @Benchmark @BenchmarkMode(Mode.SampleTime)
    public Instant measureGetNanoClockTime(){
        return nanoClock.instant();
    }
}
