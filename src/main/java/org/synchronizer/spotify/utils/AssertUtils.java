package org.synchronizer.spotify.utils;

public class AssertUtils {
    private AssertUtils() {
    }

    public static String buildInstanceOfMessage(String parameter, Class<?> expectedType, Class<?> actualType) {
        return String.format("expected %s to be of type \"%s\" but got \"%s\" instead", parameter, expectedType.getName(), actualType.getName());
    }
}
