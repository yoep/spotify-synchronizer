package be.studios.yoep.spotify.synchronizer.settings;

import be.studios.yoep.spotify.synchronizer.SpotifySynchronizer;
import be.studios.yoep.spotify.synchronizer.common.ObservableWrapper;
import be.studios.yoep.spotify.synchronizer.settings.model.UserSettings;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * Service for handling the user settings.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UserSettingsService {
    @Getter
    private final ObservableWrapper<UserSettings> userSettingsObservable = new ObservableWrapper<>();
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        createApplicationSettingsDirectory();
    }

    /**
     * Save the given user settings.
     *
     * @param settings Set the user settings to save.
     * @throws SettingsException Is thrown when an error occurs during writing.
     */
    public void save(UserSettings settings) throws SettingsException {
        Assert.notNull(settings, "settings cannot be null");
        File settingsFile = getSettingsFile();

        try {
            log.debug("Saving user settings to " + settingsFile.getAbsolutePath());
            FileUtils.writeStringToFile(settingsFile, objectMapper.writeValueAsString(settings), Charset.defaultCharset());
            userSettingsObservable.set(settings);
        } catch (IOException ex) {
            throw new SettingsException("Unable to write settings to " + settingsFile.getAbsolutePath(), ex);
        }
    }

    /**
     * Get the user settings if present.
     *
     * @return Returns the user settings.
     * @throws SettingsException Is thrown when the user settings exist but couldn't be read.
     */
    public Optional<UserSettings> getUserSettings() throws SettingsException {
        if (userSettingsObservable.get() != null) {
            return Optional.of(userSettingsObservable.get());
        } else {
            return loadUserSettingsFromFile();
        }
    }

    private Optional<UserSettings> loadUserSettingsFromFile() {
        File settingsFile = getSettingsFile();

        if (settingsFile.exists()) {
            try {
                log.debug("Loading user settings from " + settingsFile.getAbsolutePath());

                UserSettings userSettings = objectMapper.readValue(settingsFile, UserSettings.class);
                userSettingsObservable.set(userSettings);

                return Optional.of(userSettings);
            } catch (IOException ex) {
                throw new SettingsException("Unable to read settings file at " + settingsFile.getAbsolutePath(), ex);
            }
        } else {
            return Optional.empty();
        }
    }

    private void createApplicationSettingsDirectory() {
        File appDir = new File(SpotifySynchronizer.APP_DIR);

        if (!appDir.exists()) {
            if (!appDir.mkdirs()) {
                log.error("Unable to create application directory in " + appDir.getAbsolutePath());
            }
        }
    }

    private File getSettingsFile() {
        return new File(SpotifySynchronizer.APP_DIR + "settings.json");
    }
}
