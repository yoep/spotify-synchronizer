package be.studios.yoep.spotify.synchronizer.common;

import be.studios.yoep.spotify.synchronizer.settings.UserSettingsService;
import be.studios.yoep.spotify.synchronizer.settings.model.Logging;
import be.studios.yoep.spotify.synchronizer.settings.model.UserSettings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Getter
@Log4j2
@Service
@RequiredArgsConstructor
public class LoggingService {
    private static final String LOG_APPENDER_NAME = "Async";

    private final UserSettingsService settingsService;
    private final Logger coreLogger = (Logger) LogManager.getRootLogger();

    private Appender appender;

    @PostConstruct
    public void init() {
        this.appender = coreLogger.getAppenders().get(LOG_APPENDER_NAME);
        Optional<UserSettings> userSettingsOptional = settingsService.getUserSettings();

        if (userSettingsOptional.isPresent()) {
            Logging logging = userSettingsOptional.get().getLogging();

            setLevel(logging.getLevel());

            if (logging.isLogfileEnabled()) {
                enableLogfile();
            } else {
                disableLogfile();
            }
        }
    }

    /**
     * Verify if the logfile is enabled in the logging.
     *
     * @return Returns true when logfile is enabled, else false.
     */
    public boolean isLogfileEnabled() {
        return coreLogger.getAppenders().containsKey(LOG_APPENDER_NAME);
    }

    /**
     * Enable the logfile in the logger (if not already enabled).
     */
    public void enableLogfile() {
        if (!coreLogger.getAppenders().containsKey(LOG_APPENDER_NAME)) {
            log.debug("Added log file appender");
            coreLogger.addAppender(appender);
        }
    }

    /**
     * Disabled logfile in the logger (if not already disabled).
     */
    public void disableLogfile() {
        if (!coreLogger.getAppenders().containsKey(LOG_APPENDER_NAME)) {
            log.debug("Removed log file appender");
            Appender appender = coreLogger.getAppenders().get(LOG_APPENDER_NAME);
            coreLogger.removeAppender(appender);
        }
    }

    /**
     * Set the log level of the logger.
     *
     * @param level Set the log level.
     */
    public void setLevel(Level level) {
        Configurator.setLevel("be.studios.yoep.spotify.synchronizer", level);
    }
}
