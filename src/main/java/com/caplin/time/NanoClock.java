package com.caplin.time;

import java.io.*;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class NanoClock extends Clock {
    static {
        try {
            loadNativeLibraryFromJar("nanotime");
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to load nanotime distributable", ioe);
        }
    }

    private static void loadNativeLibraryFromJar(String nativeLibrary) throws IOException {
        String osName = System.getProperty("os.name").toLowerCase();
        String extension = "." + (osName.contains("win") ? "dll" : osName.contains("mac") ? "dylib" : "so");

        File temp = File.createTempFile(nativeLibrary, extension);
        if (!temp.exists()) {
            throw new FileNotFoundException("File " + temp.getAbsolutePath() + " does not exist.");
        }

        try (InputStream is = NanoClock.class.getResourceAsStream(String.format("/native/%s%s", nativeLibrary, extension));
             OutputStream os = new FileOutputStream(temp)) {
            if (is == null) {
                throw new FileNotFoundException(String.format("Executable file 'native/caplin_time%s' was not found inside JAR.", extension));
            }

            byte[] buffer = new byte[1024];
            int readBytes;
            while ((readBytes = is.read(buffer)) != -1) {
                os.write(buffer, 0, readBytes);
            }
        } catch (Throwable e) {
            temp.delete();
            throw e;
        }

        try {
            System.load(temp.getAbsolutePath());
        } finally {
            temp.deleteOnExit();
        }
    }

    private final ZoneId zone;

    NanoClock() {
        this.zone = ZoneId.systemDefault();
    }

    NanoClock(ZoneId zone) {
        this.zone = zone;
    }

    public native long[] clock_gettime();

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
        long[] time = clock_gettime();
        return Instant.ofEpochSecond(time[0], time[1]);
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
