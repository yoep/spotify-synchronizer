package org.synchronizer.spotify.domain.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.synchronizer.spotify.domain.entities.SyncTrackEntity;

import java.util.UUID;

public interface SyncTrackRepository extends PagingAndSortingRepository<SyncTrackEntity, UUID> {
}
