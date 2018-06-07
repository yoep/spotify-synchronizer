package be.studios.yoep.spotify.synchronizer.tika;

import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;

/**
 * Defines the MP3 properties which are available within the meta data of the file.
 */
public interface Mp3Properties extends TikaCoreProperties {
    Property ALBUM = Property.internalText(Mp3MetaData.ALBUM);
}
