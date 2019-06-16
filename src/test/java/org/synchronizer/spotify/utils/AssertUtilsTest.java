package org.synchronizer.spotify.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class AssertUtilsTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testBuildInstanceOfMessage_whenExpectedTypeIsNull_shouldThrowIllegalArgumentException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("expectedType cannot be null");

        AssertUtils.buildInstanceOfMessage("", null, ExpectedException.class);
    }

    @Test
    public void testBuildInstanceOfMessage_whenActualTypeIsNull_shouldThrowIllegalArgumentException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("actualType cannot be null");

        AssertUtils.buildInstanceOfMessage("", ExpectedException.class, null);
    }

    @Test
    public void testBuildInstanceOfMessage_shouldReturnTheExpectedResult() {
        String expectedResult = "expected  to be of type \"org.junit.rules.ExpectedException\" but got \"java.lang.IllegalArgumentException\" instead";

        String result = AssertUtils.buildInstanceOfMessage("", ExpectedException.class, IllegalArgumentException.class);

        assertEquals(expectedResult, result);
    }
}