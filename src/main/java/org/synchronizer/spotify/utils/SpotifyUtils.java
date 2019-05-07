package org.synchronizer.spotify.utils;

import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.synchronizer.spotify.SpotifySynchronizer;

import java.util.Optional;

public class SpotifyUtils {
    private static final RestTemplate REST_TEMPLATE = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(RestTemplate.class);

    private SpotifyUtils() {
    }

    /**
     * Get the image mime type of the given {@code uri}.
     *
     * @param uri The image uri.
     * @return Returns the image mime type.
     */
    public static Optional<String> getImageMimeType(String uri) {
        Assert.hasText(uri, "uri cannot be empty");

        return Optional.ofNullable(REST_TEMPLATE.headForHeaders(uri).getContentType())
                .map(MediaType::toString);
    }

    public static byte[] getImage(String uri) {
        Assert.hasText(uri, "uri cannot be empty");

        return REST_TEMPLATE.getForObject(uri, byte[].class);
    }
}
