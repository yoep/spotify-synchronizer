package org.synchronizer.spotify.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.springframework.stereotype.Service;
import org.synchronizer.spotify.SpotifySynchronizer;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.settings.model.Logging;

import javax.annotation.PostConstruct;
import java.io.File;
import java.time.LocalDate;

@Getter
@Log4j2
@Service
@RequiredArgsConstructor
public class LoggingService {
    private static final String LOG_APPENDER_NAME = "Async";
    private static final String FILENAME_FORMAT = "synchronizer-%s.log";
    private static final String DIRECTORY = "logs";

    private final SettingsService settingsService;
    private final Logger coreLogger = (Logger) LogManager.getRootLogger();

    private Appender appender;
    private Level level;

    @PostConstruct
    public void init() {
        updateLogger();
        getLoggingSettings().addObserver((o, arg) -> updateLogger());
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
            log.debug("Added log file appender '" + LOG_APPENDER_NAME + "' to root logger");
            coreLogger.addAppender(getLogFileAppender());
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
        this.level = level;
        Configurator.setLevel("org.synchronizer.spotify", level);
    }

    private void updateLogger() {
        Logging logging = getLoggingSettings();

        setLevel(logging.getLevel());

        if (logging.isLogfileEnabled()) {
            enableLogfile();
        } else {
            disableLogfile();
        }
    }

    private Logging getLoggingSettings() {
        return settingsService.getUserSettingsOrDefault().getLogging();
    }

    private Appender getLogFileAppender() {
        if (appender == null)
            appender = createFileAppender();

        return appender;
    }

    private Appender createFileAppender() {
        String filename = String.format(FILENAME_FORMAT, LocalDate.now());
        FileAppender fileAppender = FileAppender.newBuilder()
                .setName(LOG_APPENDER_NAME)
                .withFileName(getLogDirectoryPath() + filename)
                .withAppend(true)
                .setPropertyArray(new Property[]{
                        Property.createProperty("PID", "????"),
                })
                .setLayout(PatternLayout.newBuilder()
                        .withPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %5p ${sys:PID} --- [%t] %-40.40c{1.} : %m%n%xwEx")
                        .build())
                .build();
        fileAppender.start();

        return fileAppender;
    }

    private String getLogDirectoryPath() {
        return SpotifySynchronizer.APP_DIR + DIRECTORY + File.separator;
    }
}
