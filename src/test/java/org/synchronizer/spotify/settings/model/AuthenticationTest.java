package org.synchronizer.spotify.settings.model;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AuthenticationTest {
    @Test
    public void testSetAccessToken_whenAccessTokenIsDifferent_shouldNotifyObservers() {
        AtomicBoolean observerInvoked = new AtomicBoolean();
        Authentication authentication = new Authentication();
        authentication.addObserver((o, arg) -> observerInvoked.set(true));

        authentication.setAccessToken(new OAuth2AccessTokenWrapper());

        assertTrue(observerInvoked.get());
    }

    @Test
    public void testSetAccessToken_whenAccessTokenIsTheSame_shouldNotNotifyObservers() {
        AtomicBoolean observerInvoked = new AtomicBoolean();
        Authentication authentication = new Authentication();
        OAuth2AccessTokenWrapper accessToken = new OAuth2AccessTokenWrapper();

        authentication.setAccessToken(accessToken);
        authentication.addObserver((o, arg) -> observerInvoked.set(true));

        authentication.setAccessToken(accessToken);

        assertFalse(observerInvoked.get());
    }
}