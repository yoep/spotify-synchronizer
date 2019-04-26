package org.synchronizer.spotify.views.sections;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.synchronizer.spotify.ui.ScaleAwareImpl;

import java.util.Optional;

@Log4j2
@Controller
@RequiredArgsConstructor
public class SettingsSection extends ScaleAwareImpl {
    @Setter
    private Runnable onGoBack;

    public void goBack() {
        Optional.ofNullable(onGoBack)
                .ifPresent(Runnable::run);
    }
}
