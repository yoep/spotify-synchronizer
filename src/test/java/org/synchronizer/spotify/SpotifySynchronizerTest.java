package org.synchronizer.spotify;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpotifySynchronizer.class)
public class SpotifySynchronizerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testMain_whenArgsIsNull_shouldThrowIllegalArgumentException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("args cannot be null");

        SpotifySynchronizer.main(null);
    }

    @Test
    public void testMain_shouldSetArguments() {
        String[] expectedResult = createArguments();

        SpotifySynchronizer.main(expectedResult);

        assertArrayEquals(expectedResult, SpotifySynchronizer.ARGUMENTS);
    }

    @Test
    public void testInit_shouldSetApplicationContext() {
        SpotifySynchronizer.main(createArguments());

        new SpotifySynchronizer().init();

        assertNotNull(SpotifySynchronizer.APPLICATION_CONTEXT);
    }

    private String[] createArguments() {
        return new String[]{"disable-ui"};
    }
}