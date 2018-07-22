package be.studios.yoep.spotify.synchronizer.views.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class TimeSliderComponent implements Initializable, MediaPlayerComponent {
    private MediaPlayer mediaPlayer;

    @FXML
    public Slider audioTimeSlider;
    @FXML
    public Text currentTime;
    @FXML
    public Text totalTime;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        audioTimeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (audioTimeSlider.isValueChanging()) {
                mediaPlayer.seek(Duration.millis(newValue.doubleValue()));
            }
        });
        audioTimeSlider.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                mediaPlayer.setMute(true);
            } else {
                mediaPlayer.setMute(false);
            }
        });
    }

    @Override
    public void onReady() {
        long duration = (long) mediaPlayer.getTotalDuration().toSeconds();

        audioTimeSlider.setMax(mediaPlayer.getTotalDuration().toMillis());
        totalTime.setText(String.format("%02d:%02d", (duration % 3600) / 60, (duration % 60)));
    }

    @Override
    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        registerMediaPlayerEvents();
    }

    @Override
    public void setPlayerDisabledState(boolean disabled) {
        audioTimeSlider.setDisable(disabled);
    }

    private void registerMediaPlayerEvents() {
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            long time = (long) newValue.toSeconds();

            audioTimeSlider.setValue(newValue.toMillis());
            currentTime.setText(String.format("%02d:%02d", (time % 3600) / 60, (time % 60)));
        });
    }
}
