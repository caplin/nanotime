package com.caplin.time;

import java.io.*;
import java.time.Instant;

public class NanoClock {
    static {
        try {
            loadNativeLibraryFromJar("nano_time");
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to load caplin_time library", ioe);
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

    public native long[] clock_gettime();

    public Instant instant() {
        long[] time = clock_gettime();
        return Instant.ofEpochSecond(time[0], time[1]);
    }
}
