package org.synchronizer.spotify.controllers.model;

import lombok.Builder;
import lombok.Value;
import org.synchronizer.spotify.settings.model.FilterType;

@Value
@Builder
public class FilterCriteria {
    private FilterType filterType;
    private boolean albumSongsVisible;
}
