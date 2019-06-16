package org.synchronizer.spotify.utils;

import org.springframework.util.Assert;

public class AssertUtils {
    private AssertUtils() {
    }

    public static String buildInstanceOfMessage(String parameter, Class<?> expectedType, Class<?> actualType) {
        Assert.notNull(expectedType, "expectedType cannot be null");
        Assert.notNull(actualType, "actualType cannot be null");

        return String.format("expected %s to be of type \"%s\" but got \"%s\" instead", parameter, expectedType.getName(), actualType.getName());
    }
}
