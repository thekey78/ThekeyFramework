package pe.kr.thekey.framework.web.pipeline;

import org.junit.jupiter.api.Test;
import pe.kr.thekey.framework.core.pipeline.Stage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link StageRegistry}.
 * <p>
 * The {@code StageRegistry} class is responsible for managing a collection of {@link Stage} objects,
 * ensuring uniqueness of stage IDs, and providing access to stages through their IDs.
 * <p>
 * This test class focuses on testing the behavior of the {@code getRequired} method.
 * The {@code getRequired} method retrieves a {@link Stage} by its ID.
 * If the stage does not exist, it throws an {@link IllegalStateException}.
 */
public class StageRegistryTest {

    /**
     * Test for {@code getRequired} when the stage exists in the registry.
     * Should return the corresponding {@link Stage}.
     */
    @Test
    void getRequired_whenStageExists_returnsStage() {
        // Arrange
        Stage mockStage = mock(Stage.class);
        when(mockStage.id()).thenReturn("stage1");

        StageRegistry registry = new StageRegistry(List.of(mockStage));

        // Act
        Stage result = registry.getRequired("stage1");

        // Assert
        assertNotNull(result, "The returned stage should not be null.");
        assertEquals("stage1", result.id(), "The stage ID should match the queried ID.");
    }

    /**
     * Test for {@code getRequired} when the stage does not exist in the registry.
     * Should throw an {@link IllegalStateException}.
     */
    @Test
    void getRequired_whenStageDoesNotExist_throwsIllegalStateException() {
        // Arrange
        Stage mockStage = mock(Stage.class);
        when(mockStage.id()).thenReturn("stage1");

        StageRegistry registry = new StageRegistry(List.of(mockStage));

        // Act and Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> registry.getRequired("missingStage"),
                "An exception should be thrown when the stage ID does not exist."
        );

        assertEquals("Stage not found: missingStage", exception.getMessage(), "The exception message should indicate the missing stage ID.");
    }

    /**
     * Test for {@code getRequired} when there are duplicate stage IDs in the registry.
     * The {@link StageRegistry} constructor should throw an {@link IllegalStateException}.
     */
    @Test
    void constructor_whenDuplicateStageIds_throwsIllegalStateException() {
        // Arrange
        Stage mockStage1 = mock(Stage.class);
        Stage mockStage2 = mock(Stage.class);

        when(mockStage1.id()).thenReturn("stage1");
        when(mockStage2.id()).thenReturn("stage1"); // Duplicate ID

        // Act and Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> new StageRegistry(List.of(mockStage1, mockStage2)),
                "An exception should be thrown when duplicate stage IDs are provided."
        );

        assertEquals("Duplicate stage id: stage1", exception.getMessage(), "The exception message should indicate the duplicate stage ID.");
    }
}