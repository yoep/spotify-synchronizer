package org.synchronizer.spotify.views.components;

import org.synchronizer.spotify.settings.model.UserSettings;

public interface SettingComponent {
    /**
     * Applies the settings from the component.
     */
    UserSettings apply(UserSettings currentUserSettings);
}
