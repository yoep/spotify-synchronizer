package org.synchronizer.spotify.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SettingsServiceTest {
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private SettingsService settingsService;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testSave_whenSettingsIsNull_shouldThrowIllegalArgumentException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("settings cannot be null");

        settingsService.save(null);
    }
}