package be.studios.yoep.spotify.synchronizer.spotify;

import be.studios.yoep.spotify.synchronizer.synchronize.model.SpotifyTrack;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class PreviewPlayerService {
    private MediaPlayer mediaPlayer;

    public void play(SpotifyTrack track) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        Media media = new Media(track.getPreviewUrl());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnError(() -> log.error(mediaPlayer.getError()));
        mediaPlayer.setOnReady(() -> mediaPlayer.play());
    }
}
