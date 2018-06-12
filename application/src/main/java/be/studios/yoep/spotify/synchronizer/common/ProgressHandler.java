package be.studios.yoep.spotify.synchronizer.common;

import be.studios.yoep.spotify.synchronizer.ui.UIText;
import be.studios.yoep.spotify.synchronizer.ui.lang.MainMessage;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The progress handler manages the progress status bar at the bottom of the screen for the process.
 * This handler expects the {@link #progressLabel} and {@link #progressBar} to be set by the view as this component is initialized before the view is being
 * initialized.
 */
@Log4j2
@EqualsAndHashCode
@ToString
@Component
@RequiredArgsConstructor
public class ProgressHandler {
    private final UIText uiText;

    private Timer repeat;
    @Setter
    private Label additionalInformationLabel;
    @Setter
    private Label progressLabel;
    @Setter
    private ProgressBar progressBar;
    @Getter
    private Type type = Type.PROGRESSION;
    @Getter
    private int processed;
    @Getter
    private int failed;
    @Getter
    private int total;

    @PostConstruct
    public void initialize() {
        new JFXPanel();
        Platform.runLater(() -> {
            repeat = new Timer();
            repeat.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        if (total > 0 && progressBar.getProgress() != 1) {
                            int totalProcessed = processed + failed;
                            progressBar.setProgress((double) totalProcessed / total);
                            progressLabel.setText(uiText.get(MainMessage.PROGRESSION, totalProcessed, total));

                            if (failed > 0) {
                                additionalInformationLabel.setText(uiText.get(MainMessage.PROGRESSION_FAILED, failed));
                            }
                        }
                    });
                }
            }, 1000, 500);
        });
    }

    /**
     * Set the progress text to show which doesn't use a progression total.
     * The progress bar will be changed to continuous.
     *
     * @param text Set the text to show.
     */
    public void setProcess(String text) {
        Assert.notNull(progressLabel, "progressLabel has not been initialized");
        Assert.notNull(progressBar, "progressBar has not been initialized");
        this.type = Type.CONTINUOUS;
        Platform.runLater(() -> {
            progressBar.setStyle("-fx-accent: #0095c7;");
            progressBar.setProgress(-1);
            progressLabel.setText(text);
            additionalInformationLabel.setText("");
        });
    }

    /**
     * Increase the processed amount.
     */
    public void increaseProcessed() {
        processed++;
    }

    /**
     * Start a new progression which uses a total.
     * The progress bar will be changed from continuous to block progress.
     *
     * @param total Set the total for the progression system.
     */
    public void start(Integer total) {
        Assert.notNull(progressLabel, "progressLabel has not been initialized");
        Assert.notNull(additionalInformationLabel, "additionalInformationLabel has not been initialized");
        Assert.notNull(progressBar, "progressBar has not been initialized");
        this.type = Type.PROGRESSION;
        this.total = total;
        this.processed = 0;
        this.failed = 0;

        Platform.runLater(() -> {
            progressBar.setStyle("-fx-accent: #0095c7;");
            progressBar.setProgress(0);
            progressLabel.setText("");
            additionalInformationLabel.setText("");
        });
    }

    /**
     * Set that the process failed due to an exception.
     * This text will be shown on the left side of the status bar.
     *
     * @param text Set the text to show.
     */
    public void failure(String text) {
        Platform.runLater(() -> {
            progressBar.setStyle("-fx-accent: red;");
            progressBar.setProgress(1);
            progressLabel.setText(text);
            additionalInformationLabel.setText("");
        });
    }

    /**
     * Set that the process has been completed.
     *
     * @param text Set the text to show.
     */
    public void success(String text) {
        Platform.runLater(() -> {
            progressBar.setStyle("-fx-accent: green;");
            progressBar.setProgress(1);
            progressLabel.setText(text);
        });
    }

    /**
     * Set the process additional information text.
     * This text will be shown on the left side of the status bar.
     *
     * @param text Set the text to show.
     */
    public void information(String text) {
        Platform.runLater(() -> {
            additionalInformationLabel.setText(text);
        });
    }

    public void addFailure() {
        failed++;
        Platform.runLater(() -> progressBar.setStyle("-fx-accent: red;"));
    }

    public enum Type {
        CONTINUOUS,
        PROGRESSION
    }
}
