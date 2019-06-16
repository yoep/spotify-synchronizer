package org.synchronizer.spotify.views.model;

import lombok.Builder;
import lombok.Value;
import org.synchronizer.spotify.settings.model.FilterType;

@Value
@Builder
public class FilterCriteria {
    private FilterType filterType;
}
