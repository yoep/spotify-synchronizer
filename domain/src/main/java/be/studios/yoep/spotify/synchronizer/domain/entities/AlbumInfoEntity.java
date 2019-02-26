package be.studios.yoep.spotify.synchronizer.domain.entities;

import be.studios.yoep.spotify.synchronizer.domain.AlbumInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ALBUM")
public class AlbumInfoEntity implements AlbumInfo {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String imageUri;
}
