/*
 * Copyright 2017 Caplin Systems Ltd
 */
package com.caplin.time;

import java.io.*;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * A clock providing access to system time with nanosecond precision.
 * <p>
 * Instances of this class are used to find the current instant, which can be
 * interpreted using the stored time-zone to find the current date and time.
 * <p>
 * Note that the {@link Instant} returned by {@link Clock#instant()} is a timestamp
 * since epoch, and therefore has no zone. The stored timezone is part of the API
 * to allow the {@link Clock} object to be used to obtain current time.
 * <p>
 * See {@link Instant} and {@link Clock} for more information.
 */
public class NanoClock extends Clock {
    static {
        try {
            loadNativeLibraryFromJar("nanotime");
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to load nanotime distributable", ioe);
        }
    }

    private static void loadNativeLibraryFromJar(String nativeLibrary) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        String extension = "." + (os.contains("win") ? "dll" : os.contains("mac") ? "dylib" : "so");

        // Check architecure for running on ARM systems, then load the ARM .so flavor
        if (System.getProperty("os.arch").toLowerCase().equals("arm")){
            nativeLibrary = nativeLibrary + "-ARM";
        }

        // Create a temporary file for the distributable, we cannot load it from the jar.
        File temp = File.createTempFile(nativeLibrary, extension);
        if (!temp.exists()) {
            throw new FileNotFoundException("File " + temp.getAbsolutePath() + " does not exist.");
        }

        // Copy the distributable from the jar to the temporary file.
        try (InputStream inputStream = NanoClock.class.getResourceAsStream(String.format("/native/%s%s", nativeLibrary, extension));
             OutputStream outputStream = new FileOutputStream(temp)) {
            if (inputStream == null) {
                throw new FileNotFoundException(String.format("Executable file 'native/nanotime%s' was not found inside JAR.", extension));
            }

            byte[] buffer = new byte[1024];
            int readBytes;
            while ((readBytes = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, readBytes);
            }
        } catch (Throwable e) {
            temp.delete();
            throw e;
        }

        // Load the distributable
        try {
            System.load(temp.getAbsolutePath());
        } finally {
            temp.deleteOnExit();
        }
    }

    private final ZoneId zone;

    /**
     * Create a {@link Clock} with nanosecond precision. Zone will be set to the system default.
     * <p>
     * Note that the stored timezone is part of the API to allow the {@link Clock} object to be
     * used to obtain current time, and is not used by {@link NanoClock} itself.
     */
    public NanoClock() {
        this.zone = ZoneId.systemDefault();
    }

    /**
     * Create a {@link Clock} with nanosecond precision, and the provided Zone.
     * <p>
     * Note that the stored timezone is part of the API to allow the {@link Clock} object to be
     * used to obtain current time, and is not used by {@link NanoClock} itself.
     */
    public NanoClock(ZoneId zone) {
        this.zone = zone;
    }

    // Returns a 64 bit long, the first 32 bits are the seconds since epoch,
    // and the other 32 bits are the nanoseconds. This is to improve performance.
    // This will only work up until 2038!
    private native long clock_gettime();

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        if (this.zone.equals(zone)) {
            return this;
        }
        return new NanoClock(zone);
    }

    @Override
    public Instant instant() {
        long time = clock_gettime();

        // Decode the long, the first 32 bits are the seconds since epoch,
        // and the other 32 bits are the nanoseconds.
        return Instant.ofEpochSecond( time >> 32, time & 0xFFFFFFFFL);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NanoClock) {
            return zone.equals(((NanoClock) obj).zone);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return zone.hashCode() + 1;
    }

    @Override
    public String toString() {
        return "NanoClock[" + zone + "]";
    }
}
