package be.studios.yoep.spotify.synchronizer.domain.repositories;

import be.studios.yoep.spotify.synchronizer.domain.entities.TrackInfoEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface TrackRepository extends PagingAndSortingRepository<TrackInfoEntity, UUID> {
}
