package be.studios.yoep.spotify.synchronizer.settings.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.Observable;

@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@NoArgsConstructor
public class UserSettings extends Observable implements Serializable {
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

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserSettings(@JsonProperty("authentication") Authentication authentication,
                        @JsonProperty("logging") Logging logging,
                        @JsonProperty("synchronization") Synchronization synchronization,
                        @JsonProperty("userInterface") UserInterface userInterface) {
        this.authentication = authentication;
        this.logging = logging;
        this.synchronization = synchronization;
        this.userInterface = userInterface;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
        processNewVariableInstance(authentication);
    }

    public void setLogging(Logging logging) {
        this.logging = logging;
        processNewVariableInstance(logging);
    }

    public void setSynchronization(Synchronization synchronization) {
        this.synchronization = synchronization;
        processNewVariableInstance(synchronization);
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
