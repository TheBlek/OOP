package ru.nsu.kuklin.dsl;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Stub test class. This is needed for jacoco to generate something.
 */
public class TaskRunConfigTest {
    @Test
    private void testRunConfigBuilder() {
        var config = new TaskRunConfig("Task").withExcludeTests();
        assertTrue(config.excludeTests());
        assertEquals("Task", config.task());
    }
}
