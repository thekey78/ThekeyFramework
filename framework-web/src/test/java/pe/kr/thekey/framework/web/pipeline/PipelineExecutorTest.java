package pe.kr.thekey.framework.web.pipeline;

import org.junit.jupiter.api.Test;
import pe.kr.thekey.framework.core.error.StandardException;
import pe.kr.thekey.framework.core.pipeline.Stage;
import pe.kr.thekey.framework.core.pipeline.StageContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PipelineExecutorTest {

    /**
     * Tests the {@code PipelineExecutor#execute} method with a single stage that is enabled
     * and satisfies the condition. The stage should be executed successfully.
     */
    @Test
    void execute_withEnabledStage_executesStage() {
        // Arrange
        StageContext mockContext = mock(StageContext.class);
        Stage mockStage = mock(Stage.class);
        StageDefinition mockDefinition = mock(StageDefinition.class);

        when(mockDefinition.enable()).thenReturn(true);
        when(mockDefinition.condition()).thenReturn(mock(StageDefinition.Condition.class));
        when(mockDefinition.condition().matches(mockContext)).thenReturn(true);

        ExecutableStage stage = new ExecutableStage(mockStage, mockDefinition);
        List<ExecutableStage> chain = List.of(stage);

        PipelineExecutor executor = new PipelineExecutor();

        // Act
        executor.execute(chain, mockContext);

        // Assert
        verify(mockStage).execute(mockContext);
    }

    /**
     * Tests the {@code PipelineExecutor#execute} method with a single stage that is disabled.
     * The stage should not be executed.
     */
    @Test
    void execute_withDisabledStage_skipsExecution() {
        // Arrange
        StageContext mockContext = mock(StageContext.class);
        Stage mockStage = mock(Stage.class);
        StageDefinition mockDefinition = mock(StageDefinition.class);

        when(mockDefinition.enable()).thenReturn(false);

        ExecutableStage stage = new ExecutableStage(mockStage, mockDefinition);
        List<ExecutableStage> chain = List.of(stage);

        PipelineExecutor executor = new PipelineExecutor();

        // Act
        executor.execute(chain, mockContext);

        // Assert
        verify(mockStage, never()).execute(mockContext);
    }

    /**
     * Tests the {@code PipelineExecutor#execute} method with a condition that does not match.
     * The stage should not be executed.
     */
    @Test
    void execute_withNonMatchingCondition_skipsExecution() {
        // Arrange
        StageContext mockContext = mock(StageContext.class);
        Stage mockStage = mock(Stage.class);
        StageDefinition mockDefinition = mock(StageDefinition.class);

        when(mockDefinition.enable()).thenReturn(true);
        when(mockDefinition.condition()).thenReturn(mock(StageDefinition.Condition.class));
        when(mockDefinition.condition().matches(mockContext)).thenReturn(false);

        ExecutableStage stage = new ExecutableStage(mockStage, mockDefinition);
        List<ExecutableStage> chain = List.of(stage);

        PipelineExecutor executor = new PipelineExecutor();

        // Act
        executor.execute(chain, mockContext);

        // Assert
        verify(mockStage, never()).execute(mockContext);
    }

    /**
     * Tests the {@code PipelineExecutor#execute} method when a stage throws an exception,
     * and its failure policy is FAIL_OPEN. Execution should continue.
     */
    @Test
    void execute_withFailOpenPolicy_continuesOnException() {
        // Arrange
        StageContext mockContext = mock(StageContext.class);
        Stage mockStage = mock(Stage.class);
        StageDefinition mockDefinition = mock(StageDefinition.class);

        when(mockDefinition.enable()).thenReturn(true);
        when(mockDefinition.condition()).thenReturn(mock(StageDefinition.Condition.class));
        when(mockDefinition.condition().matches(mockContext)).thenReturn(true);
        when(mockDefinition.failurePolicy()).thenReturn(StageDefinition.FailurePolicy.FAIL_OPEN);

        doThrow(new RuntimeException("Test Exception")).when(mockStage).execute(mockContext);

        ExecutableStage stage = new ExecutableStage(mockStage, mockDefinition);
        List<ExecutableStage> chain = List.of(stage);

        PipelineExecutor executor = new PipelineExecutor();

        // Act & Assert
        assertDoesNotThrow(() -> executor.execute(chain, mockContext));
    }

    /**
     * Tests the {@code PipelineExecutor#execute} method when a stage throws a {@code StandardException},
     * and its failure policy is FAIL_CLOSE. The exception should propagate.
     */
    @Test
    void execute_withFailClosePolicy_throwsStandardException() {
        // Arrange
        StageContext mockContext = mock(StageContext.class);
        Stage mockStage = mock(Stage.class);
        StageDefinition mockDefinition = mock(StageDefinition.class);

        when(mockDefinition.enable()).thenReturn(true);
        when(mockDefinition.condition()).thenReturn(mock(StageDefinition.Condition.class));
        when(mockDefinition.condition().matches(mockContext)).thenReturn(true);
        when(mockDefinition.failurePolicy()).thenReturn(StageDefinition.FailurePolicy.FAIL_CLOSE);

        StandardException exception = new StandardException("CODE", "Test Exception", null);
        doThrow(exception).when(mockStage).execute(mockContext);

        ExecutableStage stage = new ExecutableStage(mockStage, mockDefinition);
        List<ExecutableStage> chain = List.of(stage);

        PipelineExecutor executor = new PipelineExecutor();

        // Act & Assert
        StandardException thrown = assertThrows(StandardException.class, () -> executor.execute(chain, mockContext));
        assertEquals("CODE", thrown.getCode());
        assertEquals("Test Exception", thrown.getMessage());
    }

    /**
     * Tests the {@code PipelineExecutor#execute} method when a stage throws a generic exception,
     * and its failure policy is FAIL_CLOSE. The exception should be wrapped as a {@code StandardException}.
     */
    @Test
    void execute_withFailClosePolicy_wrapsGenericExceptionAsStandardException() {
        // Arrange
        StageContext mockContext = mock(StageContext.class);
        Stage mockStage = mock(Stage.class);
        StageDefinition mockDefinition = mock(StageDefinition.class);

        when(mockDefinition.enable()).thenReturn(true);
        when(mockDefinition.condition()).thenReturn(mock(StageDefinition.Condition.class));
        when(mockDefinition.condition().matches(mockContext)).thenReturn(true);
        when(mockDefinition.failurePolicy()).thenReturn(StageDefinition.FailurePolicy.FAIL_CLOSE);

        RuntimeException exception = new RuntimeException("Test Exception");
        doThrow(exception).when(mockStage).execute(mockContext);

        ExecutableStage stage = new ExecutableStage(mockStage, mockDefinition);
        List<ExecutableStage> chain = List.of(stage);

        PipelineExecutor executor = new PipelineExecutor();

        // Act & Assert
        StandardException thrown = assertThrows(StandardException.class, () -> executor.execute(chain, mockContext));
        assertEquals("PIPELINE_EXECUTION_ERROR", thrown.getCode());
        assertEquals("Test Exception", thrown.getMessage());
        assertSame(exception, thrown.getCause());
    }

    /**
     * Tests the {@code PipelineExecutor#execute} method with an empty list of stages.
     * The method should complete without exceptions.
     */
    @Test
    void execute_withEmptyChain_completesSuccessfully() {
        // Arrange
        StageContext mockContext = mock(StageContext.class);
        List<ExecutableStage> chain = List.of();

        PipelineExecutor executor = new PipelineExecutor();

        // Act & Assert
        assertDoesNotThrow(() -> executor.execute(chain, mockContext));
    }
}