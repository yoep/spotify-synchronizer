package be.studios.yoep.spotify.synchronizer.views.components;

import be.studios.yoep.spotify.synchronizer.ui.Icons;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class VolumeComponent implements Initializable, MediaPlayerComponent {
    private MediaPlayer mediaPlayer;
    private double volume = 1.0;

    @FXML
    public Slider volumeSlider;
    @FXML
    public Text volumeIcon;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.volumeSlider.setMax(1);
        this.volumeSlider.setValue(this.volume);
        this.volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.volume = newValue.doubleValue();
            modifyVolumeIcon(newValue);
            if (mediaPlayer != null) {
                this.mediaPlayer.setVolume(newValue.doubleValue());
            }
        });
    }

    @Override
    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        this.mediaPlayer.setVolume(this.volume);
    }

    @Override
    public void setPlayerDisabledState(boolean disabled) {
        //ignore the disabled state
    }

    @Override
    public void onReady() {
        //no event need to be bound
    }

    private void modifyVolumeIcon(Number newValue) {
        double volumeValue = newValue.doubleValue();

        if (volumeValue == 0.0) {
            this.volumeIcon.setText(Icons.VOLUME_OFF);
        } else if (volumeValue < 0.5) {
            this.volumeIcon.setText(Icons.VOLUME_LOW);
        } else if (volumeValue > 0.5) {
            this.volumeIcon.setText(Icons.VOLUME_MAX);
        }
    }
}
