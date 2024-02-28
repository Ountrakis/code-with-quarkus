package org.acme.util;

public class ExceptionUtil {

    private ExceptionUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static TMFException createTMFException(int status,
                                                  String message) {
        return TMFException.Builder.builder(status, message)
                .withStatusCode(status)
                .build();
    }
}
