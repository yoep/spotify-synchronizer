package org.synchronizer.spotify.domain.repositories;

import org.synchronizer.spotify.domain.entities.AlbumInfoEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

public interface AlbumRepository extends PagingAndSortingRepository<AlbumInfoEntity, UUID> {
    Optional<AlbumInfoEntity> findByName(String name);
}
