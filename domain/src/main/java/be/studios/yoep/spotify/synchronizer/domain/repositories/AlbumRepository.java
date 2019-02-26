package be.studios.yoep.spotify.synchronizer.domain.repositories;

import be.studios.yoep.spotify.synchronizer.domain.entities.AlbumInfoEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

public interface AlbumRepository extends PagingAndSortingRepository<AlbumInfoEntity, UUID> {
    Optional<AlbumInfoEntity> findByName(String name);
}
