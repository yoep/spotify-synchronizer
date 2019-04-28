package org.synchronizer.spotify.authorization;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.synchronizer.spotify.ui.ViewLoader;
import org.synchronizer.spotify.ui.ViewProperties;
import org.synchronizer.spotify.views.LoginView;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationServiceTest {
    @Mock
    private ViewLoader viewLoader;
    @Mock
    private LoginView loginView;
    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    public void testStartAuthorization_whenInvoked_shouldSetUrlOnLoginView() {
        String requestUri = "http://myrequest.uri";
        Map<String, String> params = Collections.singletonMap("myParam", "myParamValue");
        String expectedUri = requestUri + "?myParam=myParamValue&show_dialog=true";
        UserRedirectRequiredException redirectRequiredException = new UserRedirectRequiredException(requestUri, params);

        authorizationService.startAuthorization(redirectRequiredException);

        verify(loginView).setUrl(expectedUri);
    }

    @Test
    public void testStartAuthorization_whenInvoked_shouldSetCallbackOnLoginView() {
        String requestUri = "http://myrequest.uri";
        UserRedirectRequiredException redirectRequiredException = new UserRedirectRequiredException(requestUri, Collections.emptyMap());

        authorizationService.startAuthorization(redirectRequiredException);

        verify(loginView).setSuccessCallback(isA(Consumer.class));
    }

    @Test
    public void testStartAuthorization_whenInvoked_shouldCallShowWindowOnViewLoader() {
        String requestUri = "http://myrequest.uri";
        UserRedirectRequiredException redirectRequiredException = new UserRedirectRequiredException(requestUri, Collections.emptyMap());

        authorizationService.startAuthorization(redirectRequiredException);

        verify(viewLoader).showWindow(isA(String.class), isA(ViewProperties.class));
    }
}