package org.synchronizer.spotify.settings.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Observable;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@EqualsAndHashCode(callSuper = false)
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
    private Synchronization synchronization = Synchronization.builder().build();
    @Valid
    @Builder.Default
    private UserInterface userInterface = UserInterface.builder().build();

    @NotNull
    public Authentication getAuthentication() {
        return ofNullable(authentication)
                .orElse(Authentication.builder().build());
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
        processNewVariableInstance(authentication);
    }

    @NotNull
    public Logging getLogging() {
        return ofNullable(logging)
                .orElse(Logging.builder().build());
    }

    public void setLogging(Logging logging) {
        this.logging = logging;
        processNewVariableInstance(logging);
    }

    @NotNull
    public Synchronization getSynchronization() {
        Optional<Synchronization> optionalSynchronization = ofNullable(this.synchronization);
        Synchronization defaultSynchronization = Synchronization.builder().build();

        if (optionalSynchronization.isPresent()) {
            return optionalSynchronization.get();
        }

        this.setSynchronization(defaultSynchronization);
        return defaultSynchronization;
    }

    public void setSynchronization(Synchronization synchronization) {
        this.synchronization = synchronization;
        processNewVariableInstance(synchronization);
    }

    @NotNull
    public UserInterface getUserInterface() {
        return ofNullable(userInterface)
                .orElse(UserInterface.builder().build());
    }

    public void setUserInterface(UserInterface userInterface) {
        this.userInterface = userInterface;
        processNewVariableInstance(userInterface);
    }

    private void processNewVariableInstance(Observable observable) {
        observable.addObserver((obs, arg) -> this.notifyObservers(arg));
        this.setChanged();
    }
}
