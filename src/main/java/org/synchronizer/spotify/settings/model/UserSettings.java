package org.synchronizer.spotify.settings.model;

import lombok.*;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.Observable;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings extends Observable implements Serializable {
    public static final String AUTHENTICATION_PROPERTY = "authentication";
    public static final String LOGGING_PROPERTY = "logging";
    public static final String SYNCHRONISATION_PROPERTY = "synchronization";
    public static final String USER_INTERFACE_PROPERTY = "user_interface";

    @Valid
    @Builder.Default
    private Authentication authentication = Authentication.builder().build();
    @Valid
    @Builder.Default
    private Logging logging = Logging.builder().build();
    @Valid
    @Builder.Default
    private Synchronization synchronization = new Synchronization();
    @Valid
    @Builder.Default
    private UserInterface userInterface = UserInterface.builder().build();

    public void setAuthentication(Authentication authentication) {
        if (this.authentication != authentication)
            this.setChanged();

        this.authentication = authentication;
        this.notifyObservers();
    }

    public void setLogging(Logging logging) {
        if (this.logging != logging)
            this.setChanged();

        this.logging = logging;
        this.notifyObservers();
    }

    public void setSynchronization(Synchronization synchronization) {
        if (this.synchronization != synchronization)
            this.setChanged();

        this.synchronization = synchronization;
        this.notifyObservers();
    }

    public void setUserInterface(UserInterface userInterface) {
        if (this.userInterface != userInterface)
            this.setChanged();

        this.userInterface = userInterface;
        this.notifyObservers();
    }
}
