package org.synchronizer.spotify.views;

import org.synchronizer.spotify.SpotifySynchronizer;
import org.synchronizer.spotify.domain.AlbumInfo;
import org.synchronizer.spotify.media.MediaPlayerService;
import org.synchronizer.spotify.settings.UserSettingsService;
import org.synchronizer.spotify.settings.model.UserInterface;
import org.synchronizer.spotify.settings.model.UserSettings;
import org.synchronizer.spotify.synchronize.SynchronisationService;
import org.synchronizer.spotify.synchronize.model.SpotifyTrack;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.ui.ScaleAwareImpl;
import org.synchronizer.spotify.ui.SizeAware;
import org.synchronizer.spotify.ui.UIText;
import org.synchronizer.spotify.ui.lang.MainMessage;
import org.synchronizer.spotify.views.components.MusicItemContextMenu;
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

@Log4j2
@Component
@RequiredArgsConstructor
public class MainView extends ScaleAwareImpl implements Initializable, SizeAware {
    private final SynchronisationService synchronisationService;
    private final UserSettingsService settingsService;
    private final MediaPlayerService mediaPlayerService;
    private final UIText uiText;

    @FXML
    public TableView<SyncTrack> musicList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        musicList.setItems(synchronisationService.getTracks());
        synchronisationService.getTracks().addListener((ListChangeListener<SyncTrack>) c -> {
            if (c.next()) {
                musicList.refresh();
            }
        });

        initializeColumns();
        initializeRowFactory();
        initializePlayerActions();
        synchronisationService.init();
    }

    @Override
    public void setInitialSize(Stage window) {
        UserSettings userSettings = settingsService.getUserSettingsOrDefault();
        UserInterface userInterface = userSettings.getUserInterface();

        window.setWidth(userInterface.getWidth());
        window.setHeight(userInterface.getHeight());
        window.setMaximized(userInterface.isMaximized());
    }

    @Override
    public void onSizeChange(Number width, Number height, boolean isMaximized) {
        UserSettings userSettings = settingsService.getUserSettingsOrDefault();
        UserInterface userInterface = userSettings.getUserInterface();

        userInterface.setWidth(width.floatValue());
        userInterface.setHeight(height.floatValue());
        userInterface.setMaximized(isMaximized);
        userSettings.setUserInterface(userInterface);
    }

    private void onPlayLocalTrack(ActionEvent event) {
        SyncTrack syncTrack = getSelectedItem();

        if (syncTrack != null && syncTrack.getLocalTrack().isPresent()) {
            event.consume();
            mediaPlayerService.play(syncTrack.getLocalTrack().get());
        }
    }

    private void onPlaySpotify(ActionEvent e) {
        SpotifyTrack spotifyTrack = getCurrentSelectedSpotifyTrack();

        if (spotifyTrack != null) {
            e.consume();
            HostServicesFactory.getInstance(SpotifySynchronizer.APPLICATION_CONTEXT.getBean(SpotifySynchronizer.class))
                    .showDocument(spotifyTrack.getSpotifyUri());
        }
    }

    private void onPlayPreview(ActionEvent e) {
        SpotifyTrack spotifyTrack = getCurrentSelectedSpotifyTrack();

        if (spotifyTrack != null) {
            e.consume();
            mediaPlayerService.play(spotifyTrack);
        } else {
            log.warn("Spotify track not available for current selected row " + getSelectedItem());
        }
    }

    private void onTrackMouseClick(MouseEvent e) {
        TableRow<SyncTrack> trackRow = (TableRow<SyncTrack>) e.getSource();

        if (e.getClickCount() == 2 && !trackRow.isEmpty()) {
            e.consume();
            SyncTrack track = trackRow.getItem();
            mediaPlayerService.play(track);
            musicList.refresh();
        }
    }

    private SyncTrack getSelectedItem() {
        return this.musicList.getSelectionModel().getSelectedItem();
    }

    private SpotifyTrack getCurrentSelectedSpotifyTrack() {
        SyncTrack selectedItem = getSelectedItem();
        return ofNullable(selectedItem)
                .flatMap(SyncTrack::getSpotifyTrack)
                .orElse(null);
    }

    private TableColumn<SyncTrack, String> createColumn(String text, Function<SyncTrack, String> fieldMapping) {
        TableColumn<SyncTrack, String> column = new TableColumn<>(text);
        column.setCellFactory(cell -> new TableCell<SyncTrack, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                SyncTrack track = (SyncTrack) ofNullable(this.getTableRow())
                        .map(Cell::getItem)
                        .orElse(null);

                if (track != null) {
                    setText(empty ? null : fieldMapping.apply(track));

                    if (track.isLocalTrackAvailable()) {
                        if (track.isSynchronized()) {
                            setTextFill(Paint.valueOf("#00be00"));
                        } else {
                            setTextFill(Paint.valueOf("#0000be"));
                        }
                    } else {
                        setTextFill(Paint.valueOf("#be0000"));
                    }

                    boolean isTrackBeingPlayed = mediaPlayerService.getCurrentTrack().filter(e -> e == track).isPresent();

                    if (isTrackBeingPlayed) {
                        setStyle("-fx-background-color: #88ffb9");
                    } else {
                        setBackground(Background.EMPTY);
                    }
                }
            }
        });
        return column;
    }

    private void initializeColumns() {
        TableColumn<SyncTrack, String> titleColumn = createColumn(uiText.get(MainMessage.TITLE_TRACK), SyncTrack::getTitle);
        TableColumn<SyncTrack, String> artistColumn = createColumn(uiText.get(MainMessage.ARTIST_TRACK), SyncTrack::getArtist);
        TableColumn<SyncTrack, String> albumColumn = createColumn(uiText.get(MainMessage.ALBUM_TRACK), e -> ofNullable(e.getAlbum())
                .map(AlbumInfo::getName)
                .orElse(null));

        titleColumn.setSortable(true);
        artistColumn.setSortable(true);
        albumColumn.setSortable(true);
        titleColumn.setSortType(TableColumn.SortType.ASCENDING);
        artistColumn.setSortType(TableColumn.SortType.ASCENDING);
        albumColumn.setSortType(TableColumn.SortType.ASCENDING);

        musicList.getColumns().addAll(asList(titleColumn, artistColumn, albumColumn));
        musicList.getSortOrder().addAll(asList(artistColumn, albumColumn));
        musicList.sort();
    }

    private void initializeRowFactory() {
        this.musicList.setRowFactory(param -> {
            TableRow<SyncTrack> row = new TableRow<>();
            row.setContextMenu(MusicItemContextMenu.builder()
                    .onPlayPreview(this::onPlayPreview)
                    .onPlayLocalTrack(this::onPlayLocalTrack)
                    .onPlaySpotify(this::onPlaySpotify)
                    .build());
            row.setOnMouseClicked(this::onTrackMouseClick);
            return row;
        });
    }

    private void initializePlayerActions() {
        this.mediaPlayerService.setOnNext(track -> {
            ArrayList<SyncTrack> tracks = new ArrayList<>(this.musicList.getItems());
            int nextTrackIndex = tracks.indexOf(track) + 1;

            if (nextTrackIndex >= tracks.size())
                nextTrackIndex = 0;

            this.mediaPlayerService.play(tracks.get(nextTrackIndex));
        });
    }
}
