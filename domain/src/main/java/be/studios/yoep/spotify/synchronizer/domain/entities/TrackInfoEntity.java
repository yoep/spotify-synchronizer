package be.studios.yoep.spotify.synchronizer.domain.entities;

import be.studios.yoep.spotify.synchronizer.domain.TrackType;
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
@Table(name = "TRACK")
public class TrackInfoEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    @Column(nullable = false)
    private String uri;

    @Column(nullable = false)
    private TrackType type;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private AlbumInfoEntity album;
}
