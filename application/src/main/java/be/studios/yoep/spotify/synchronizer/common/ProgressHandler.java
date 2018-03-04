package be.studios.yoep.spotify.synchronizer.common;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Log4j2
@Component
public class ProgressHandler {
    private Label progressText;
    private ProgressBar progressBar;

    private Type type = Type.PROGRESSION;

    /**
     * Initialize the progress handler by setting the progress label and bar.
     *
     * @param label       Set the label to display the text on.
     * @param progressBar Set the progress bar to indicate the progression system.
     */
    public void init(Label label, ProgressBar progressBar) {
        Assert.notNull(label, "label cannot be null");
        Assert.notNull(progressBar, "progressBar cannot be null");
        this.progressText = label;
        this.progressBar = progressBar;

        this.progressBar.setProgress(0);
        this.progressText.setText("");
        log.debug("Progress handler initialized");
    }

    private enum Type {
        CONTINUOUS,
        PROGRESSION
    }
}
