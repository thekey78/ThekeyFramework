package pe.kr.thekey.framework.web.pipeline;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PipelineValidatorTest {

    /**
     * Tests the {@code PipelineValidator#validate} method to ensure all input stage IDs exist in the registry.
     */
    @Test
    void validate_whenInputStageIdMissing_throwsIllegalStateException() {
        // Arrange
        PipelinePlan plan = mock(PipelinePlan.class);
        when(plan.inputOrder()).thenReturn(List.of("stage1", "stage2"));
        when(plan.outputOrder()).thenReturn(List.of());

        StageRegistry registry = mock(StageRegistry.class);
        when(registry.contains("stage1")).thenReturn(true);
        when(registry.contains("stage2")).thenReturn(false);

        PipelineValidator validator = new PipelineValidator(List.of());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.validate(plan, registry));
        assertEquals("Missing stage bean: stage2", exception.getMessage());
    }

    /**
     * Tests the {@code PipelineValidator#validate} method to ensure all output stage IDs exist in the registry.
     */
    @Test
    void validate_whenOutputStageIdMissing_throwsIllegalStateException() {
        // Arrange
        PipelinePlan plan = mock(PipelinePlan.class);
        when(plan.inputOrder()).thenReturn(List.of());
        when(plan.outputOrder()).thenReturn(List.of("stage1", "stage2"));

        StageRegistry registry = mock(StageRegistry.class);
        when(registry.contains("stage1")).thenReturn(true);
        when(registry.contains("stage2")).thenReturn(false);

        PipelineValidator validator = new PipelineValidator(List.of());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.validate(plan, registry));
        assertEquals("Missing stage bean: stage2", exception.getMessage());
    }

    /**
     * Tests the {@code PipelineValidator#validate} method to ensure all required stage IDs are present in the plan.
     */
    @Test
    void validate_whenRequiredStageMissing_throwsIllegalStateException() {
        // Arrange
        PipelinePlan plan = mock(PipelinePlan.class);
        when(plan.inputOrder()).thenReturn(List.of("stage1"));
        when(plan.outputOrder()).thenReturn(List.of("stage2"));

        StageRegistry registry = mock(StageRegistry.class);
        when(registry.contains("stage1")).thenReturn(true);
        when(registry.contains("stage2")).thenReturn(true);

        PipelineValidator validator = new PipelineValidator(List.of("requiredStage"));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.validate(plan, registry));
        assertEquals("Required stage missing in plan: requiredStage", exception.getMessage());
    }

    /**
     * Tests the {@code PipelineValidator#validate} method to ensure validation passes when all stage IDs are valid.
     */
    @Test
    void validate_whenAllStagesValid_completesSuccessfully() {
        // Arrange
        PipelinePlan plan = mock(PipelinePlan.class);
        when(plan.inputOrder()).thenReturn(List.of("stage1"));
        when(plan.outputOrder()).thenReturn(List.of("stage2"));

        StageRegistry registry = mock(StageRegistry.class);
        when(registry.contains("stage1")).thenReturn(true);
        when(registry.contains("stage2")).thenReturn(true);

        PipelineValidator validator = new PipelineValidator(List.of("stage1", "stage2"));

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(plan, registry));
    }

    /**
     * Tests the {@code PipelineValidator#validate} method when the registry is empty and required stages are missing.
     */
    @Test
    void validate_whenRegistryIsEmpty_throwsIllegalStateException() {
        // Arrange
        PipelinePlan plan = mock(PipelinePlan.class);
        when(plan.inputOrder()).thenReturn(List.of("stage1"));
        when(plan.outputOrder()).thenReturn(List.of("stage2"));

        StageRegistry registry = mock(StageRegistry.class);
        when(registry.contains("stage1")).thenReturn(false);
        when(registry.contains("stage2")).thenReturn(false);

        PipelineValidator validator = new PipelineValidator(List.of("stage1"));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.validate(plan, registry));
        assertEquals("Missing stage bean: stage1", exception.getMessage());
    }

    /**
     * Tests the {@code PipelineValidator#validate} method to ensure it throws an exception when the plan contains duplicate stage IDs.
     */
    @Test
    void validate_whenPlanContainsDuplicateStages_throwsIllegalStateException() {
        // Arrange
        PipelinePlan plan = mock(PipelinePlan.class);
        when(plan.inputOrder()).thenReturn(List.of("stage1", "stage1"));
        when(plan.outputOrder()).thenReturn(List.of("stage2"));

        StageRegistry registry = mock(StageRegistry.class);
        when(registry.contains("stage1")).thenReturn(true);
        when(registry.contains("stage2")).thenReturn(true);

        PipelineValidator validator = new PipelineValidator(List.of("stage1"));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.validate(plan, registry));
        assertEquals("Duplicate stage id: stage1", exception.getMessage());
    }

    /**
     * Tests the {@code PipelineValidator#validate} method to handle an empty plan gracefully.
     */
    @Test
    void validate_whenPlanIsEmpty_throwsIllegalStateException() {
        // Arrange
        PipelinePlan plan = mock(PipelinePlan.class);
        when(plan.inputOrder()).thenReturn(List.of());
        when(plan.outputOrder()).thenReturn(List.of());

        StageRegistry registry = mock(StageRegistry.class);

        PipelineValidator validator = new PipelineValidator(List.of("requiredStage"));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.validate(plan, registry));
        assertEquals("Required stage missing in plan: requiredStage", exception.getMessage());
    }
}