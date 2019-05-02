package org.synchronizer.spotify.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.synchronizer.spotify.SpotifySynchronizer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.zip.CRC32;

@Log4j2
public class CacheUtils {
    private static final String DIRECTORY = "cache";

    private CacheUtils() {
    }

    /**
     * Get the cache directory.
     *
     * @return Returns the cache directory.
     */
    public static String getCacheDirectory() {
        return SpotifySynchronizer.APP_DIR + DIRECTORY + File.separator;
    }

    /**
     * Write the given content to the cache file.
     *
     * @param file    The cache file to write to.
     * @param content The cache content.
     * @param append  Set if the content needs to be appended at the end of the cache file or not.
     * @throws IOException Is thrown when the cache file couldn't be written.
     */
    public static void writeToCache(File file, Serializable content, boolean append) throws IOException {
        byte[] serializedContent = SerializationUtils.serialize(content);


        FileUtils.writeByteArrayToFile(file, serializedContent, append);
    }

    /**
     * Cache the given image.
     *
     * @param image The image to cache.
     * @return Returns the cache location path.
     */
    public static String writeImageToCache(byte[] image) {
        File file = new File(getCacheDirectory() + createChecksum(image));

        try {
            CacheUtils.writeToCache(file, image, false);

            return file.getAbsolutePath();
        } catch (IOException e) {
            log.error("Failed to cache image with error " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Read the given cache file.
     *
     * @param file The cache file to read.
     * @param <T>  The type of the cache content.
     * @return Returns the cached content.
     * @throws IOException Is thrown when the cache file could not be read.
     */
    public static <T> T readFromCache(File file) throws IOException {
        return SerializationUtils.deserialize(FileUtils.readFileToByteArray(file));
    }

    private static long createChecksum(byte[] content) {
        CRC32 checksum = new CRC32();
        checksum.update(content);

        return checksum.getValue();
    }
}
