package org.synchronizer.spotify.domain.repositories;

import org.synchronizer.spotify.domain.TrackType;
import org.synchronizer.spotify.domain.entities.TrackInfoEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

public interface TrackRepository extends PagingAndSortingRepository<TrackInfoEntity, UUID> {
    Optional<TrackInfoEntity> findByTitleAndArtistAndType(String title, String artist, TrackType type);
}
