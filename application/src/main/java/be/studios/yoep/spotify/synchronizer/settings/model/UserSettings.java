package be.studios.yoep.spotify.synchronizer.settings.model;

import lombok.*;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings extends Observable implements Observer, Serializable {
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

    @Override
    public void update(Observable o, Object arg) {
        this.notifyObservers();
    }

    private void processNewVariableInstance(Observable observable) {
        observable.addObserver(this);
        this.setChanged();
    }
}
