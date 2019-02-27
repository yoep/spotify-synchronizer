package be.studios.yoep.spotify.synchronizer.synchronize;

import be.studios.yoep.spotify.synchronizer.domain.entities.AlbumInfoEntity;
import be.studios.yoep.spotify.synchronizer.domain.repositories.AlbumRepository;
import be.studios.yoep.spotify.synchronizer.synchronize.model.Album;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;

    @Transactional
    public AlbumInfoEntity synchronizeAlbum(Album album) {
        AlbumInfoEntity entity = albumRepository.findByName(album.getName())
                .orElse(AlbumInfoEntity.builder()
                        .name(album.getName())
                        .imageUri(album.getImageUri())
                        .image(album.getImage())
                        .build());

        entity.setImage(album.getImage());
        entity.setImageUri(album.getImageUri());

        return albumRepository.save(entity);
    }
}
