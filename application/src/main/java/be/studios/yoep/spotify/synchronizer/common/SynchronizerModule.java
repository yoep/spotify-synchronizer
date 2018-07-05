package be.studios.yoep.spotify.synchronizer.common;

import be.studios.yoep.spotify.synchronizer.common.deserializers.FileDeserializer;
import be.studios.yoep.spotify.synchronizer.common.deserializers.LoggingLevelDeserializer;
import be.studios.yoep.spotify.synchronizer.common.deserializers.UserSettingsDeserializer;
import be.studios.yoep.spotify.synchronizer.common.serializers.FileSerializer;
import be.studios.yoep.spotify.synchronizer.settings.model.UserSettings;
import be.studios.yoep.spotify.synchronizer.spotify.AlbumTypeDeserializer;
import be.studios.yoep.spotify.synchronizer.spotify.api.v1.AlbumType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import org.apache.logging.log4j.Level;

import java.io.File;

public final class SynchronizerModule extends SimpleModule {
    public SynchronizerModule() {
        super("Spotify synchronization module", PackageVersion.VERSION);

        addDeserializer(AlbumType.class, new AlbumTypeDeserializer());
        addDeserializer(Level.class, new LoggingLevelDeserializer());
        addDeserializer(File.class, new FileDeserializer());
        addDeserializer(UserSettings.class, new UserSettingsDeserializer());
        addSerializer(File.class, new FileSerializer());
    }
}
