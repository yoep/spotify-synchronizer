package org.synchronizer.spotify.domain.entities;

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
@Table(name = "SYNC_TRACK")
public class SyncTrackEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    private TrackInfoEntity spotifyTrack;

    @OneToOne
    private TrackInfoEntity localTrack;
}
