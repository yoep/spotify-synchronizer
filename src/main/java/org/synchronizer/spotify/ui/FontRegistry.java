package org.synchronizer.spotify.ui;

import javafx.scene.text.Font;
import org.springframework.util.Assert;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FontRegistry {
    private static final String FONT_DIRECTORY = "/fonts/";
    private static final FontRegistry INSTANCE = new FontRegistry();
    private final Map<String, Font> loadedFonts = new HashMap<>();

    private FontRegistry() {
    }

    /**
     * Get the instance of the {@link FontRegistry}.
     *
     * @return Returns the instance.
     */
    public static FontRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Load the given font file.
     *
     * @param filename The font filename to load from the {@link #FONT_DIRECTORY}.
     * @param size     The size of the font.
     */
    public Font loadFont(String filename, double size) {
        Assert.notNull(filename, "filename cannot be null");

        if (loadedFonts.containsKey(filename))
            return loadedFonts.get(filename);

        return loadFontResource(filename, size);
    }

    private Font loadFontResource(String filename, double size) {
        URL resource = getClass().getResource(FONT_DIRECTORY + filename);
        Font font = Font.loadFont(resource.toExternalForm(), size);

        loadedFonts.put(filename, font);

        return font;
    }
}
