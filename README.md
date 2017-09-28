# Caplin nanotime library
JNI library for nanosecond accuracy timestamps in Java 8.

## Building the project
To build the project, run:
```
./gradlew clean build
```
This will build the C distributable for your platform, and package it up in the jar.

To amalgamate distributables for all supported platforms (Linux, Windows and Mac), run:
```
buildAllPlatformsJar
```
Note that this will only work within the Caplin domain.

## Publishing
This is a two stage build process. Note that publishing and downloading the platform specific distributables is only available within the Caplin domain.

The first stage is to build the distributable for each supported platform, and publish them to the Caplin artifactory:
```
./gradlew clean publishDistributablePublicationToMavenRepository
```

The second stage is to amalgamate the distributables within a jar, and publish the jar:
```
./gradlew clean publishAllPlatformsJarPublicationToMavenRepository
```

## Usage
```
Clock clock = new NanoClock();
Instant now = clock.instant();
```

## Benchmarks
Benchmarks can be run using:
```
./gradlew clean jmh
```

These are the benchmark results for `NanoClock.instant()` compared to `System.currentTimeMillis()` and `SystemClock.instant()`. Benchmarks were conducted on `Centos 7 64-bit`.
```
Benchmark                                       Method                      Mode  Cnt   Score   Error  Units
NanoClockBenchmark.measureGetNanoClockTime      NanoClock.instant()         avgt  200  37.287 ± 0.833  ns/op
NanoClockBenchmark.measureGetSystemClockTime    SystemClock.instant()       avgt  200  39.589 ± 0.927  ns/op
NanoClockBenchmark.measureGetSystemCurrentTime  System.currentTimeMillis()  avgt  200  30.546 ± 0.430  ns/op
```