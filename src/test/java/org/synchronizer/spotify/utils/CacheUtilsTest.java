package org.synchronizer.spotify.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;

public class CacheUtilsTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testGetCacheDirectory_shouldReturnTheExpectedResult() {
        String result = CacheUtils.getCacheDirectory();

        assertTrue(result.contains(".spotify-synchronizer\\cache\\"));
    }

    @Test
    public void testWriteToCache_whenFileIsNull_shouldThrowIllegalArgumentException() throws IOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("file cannot be null");

        CacheUtils.writeToCache(null, new SerializeableClass(), false);
    }

    @Test
    public void testWriteToCache_whenContentIsNull_shouldThrowIllegalArgumentException() throws IOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("content cannot be null");

        CacheUtils.writeToCache(createTempFile(), null, false);
    }

    @Test
    public void testReadFromCache_whenFileIsNull_shouldThrowIllegalArgumentException() throws IOException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("file cannot be null");

        CacheUtils.readFromCache(null);
    }

    private File createTempFile() throws IOException {
        return Files.createTempFile("spotify-synchronizer", "test").toFile();
    }

    private class SerializeableClass implements Serializable {
    }
}