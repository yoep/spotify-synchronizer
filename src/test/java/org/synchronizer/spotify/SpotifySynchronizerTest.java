package org.synchronizer.spotify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpotifySynchronizer.class)
public class SpotifySynchronizerTest {
    @Test
    public void testMain_shouldBeAbleToStartSpringContext() {
        // no-op
    }
}