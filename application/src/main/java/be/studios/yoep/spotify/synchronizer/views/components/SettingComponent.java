package be.studios.yoep.spotify.synchronizer.views.components;

import be.studios.yoep.spotify.synchronizer.settings.model.UserSettings;

public interface SettingComponent {
    /**
     * Applies the settings from the component.
     */
    UserSettings apply(UserSettings currentUserSettings);
}