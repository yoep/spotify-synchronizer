package org.synchronizer.spotify.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class CacheUtils {
    private CacheUtils() {
    }

    public static void writeToCache(File file, Serializable content, boolean append) throws IOException {
        byte[] serializedContent = SerializationUtils.serialize(content);


        FileUtils.writeByteArrayToFile(file, serializedContent, append);
    }

    public static <T> T readFromCache(File file) throws IOException {
        return SerializationUtils.deserialize(FileUtils.readFileToByteArray(file));
    }
}
