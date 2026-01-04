package pe.kr.thekey.framework.web.condition;

import org.junit.jupiter.api.Test;
import pe.kr.thekey.framework.core.pipeline.StageContext;
import pe.kr.thekey.framework.web.pipeline.StageDefinition;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ConditionCompilerTest {

    /**
     * Test for ConditionCompiler.compile() when input is null.
     * Should return a condition that always evaluates to true.
     */
    @Test
    void testCompile_NullExpression_ReturnsAlwaysTrueCondition() {
        PrimitiveFunctions primitiveFunctions = mock(PrimitiveFunctions.class);
        ConditionCompiler compiler = new ConditionCompiler(primitiveFunctions);

        StageDefinition.Condition condition = compiler.compile(null);

        StageContext mockContext = mock(StageContext.class);
        assertTrue(condition.matches(mockContext));
    }

    /**
     * Test for ConditionCompiler.compile() when input is blank.
     * Should return a condition that always evaluates to true.
     */
    @Test
    void testCompile_BlankExpression_ReturnsAlwaysTrueCondition() {
        PrimitiveFunctions primitiveFunctions = mock(PrimitiveFunctions.class);
        ConditionCompiler compiler = new ConditionCompiler(primitiveFunctions);

        StageDefinition.Condition condition = compiler.compile("   ");

        StageContext mockContext = mock(StageContext.class);
        assertTrue(condition.matches(mockContext));
    }

    /**
     * Test for ConditionCompiler.compile() with a simple 'path' function.
     */
    @Test
    void testCompile_SimplePathFunction_ReturnsCorrectCondition() {
        PrimitiveFunctions primitiveFunctions = mock(PrimitiveFunctions.class);
        ConditionCompiler compiler = new ConditionCompiler(primitiveFunctions);

        when(primitiveFunctions.path(any(), eq("/test"))).thenReturn(true);

        StageDefinition.Condition condition = compiler.compile("path('/test')");

        StageContext mockContext = mock(StageContext.class);
        assertTrue(condition.matches(mockContext));

        verify(primitiveFunctions).path(mockContext, "/test");
    }

    /**
     * Test for ConditionCompiler.compile() with a logical AND between 'path' and 'header'.
     */
    @Test
    void testCompile_PathAndHeaderCondition_ReturnsCorrectCondition() {
        PrimitiveFunctions primitiveFunctions = mock(PrimitiveFunctions.class);
        ConditionCompiler compiler = new ConditionCompiler(primitiveFunctions);

        when(primitiveFunctions.path(any(), eq("/test"))).thenReturn(true);
        when(primitiveFunctions.headerExists(any(), eq("X-Custom"))).thenReturn(true);

        StageDefinition.Condition condition = compiler.compile("path('/test') && header('X-Custom')");

        StageContext mockContext = mock(StageContext.class);
        assertTrue(condition.matches(mockContext));

        verify(primitiveFunctions).path(mockContext, "/test");
        verify(primitiveFunctions).headerExists(mockContext, "X-Custom");
    }

    /**
     * Test for ConditionCompiler.compile() with a logical OR.
     */
    @Test
    void testCompile_LogicalOrCondition_ReturnsCorrectCondition() {
        PrimitiveFunctions primitiveFunctions = mock(PrimitiveFunctions.class);
        ConditionCompiler compiler = new ConditionCompiler(primitiveFunctions);

        when(primitiveFunctions.path(any(), eq("/test1"))).thenReturn(false);
        when(primitiveFunctions.path(any(), eq("/test2"))).thenReturn(true);

        StageDefinition.Condition condition = compiler.compile("path('/test1') || path('/test2')");

        StageContext mockContext = mock(StageContext.class);
        assertTrue(condition.matches(mockContext));

        verify(primitiveFunctions).path(mockContext, "/test1");
        verify(primitiveFunctions).path(mockContext, "/test2");
    }

    /**
     * Test for ConditionCompiler.compile() with NOT operator.
     */
    @Test
    void testCompile_NotOperator_ReturnsCorrectCondition() {
        PrimitiveFunctions primitiveFunctions = mock(PrimitiveFunctions.class);
        ConditionCompiler compiler = new ConditionCompiler(primitiveFunctions);

        when(primitiveFunctions.path(any(), eq("/restricted"))).thenReturn(false);

        StageDefinition.Condition condition = compiler.compile("!path('/restricted')");

        StageContext mockContext = mock(StageContext.class);
        assertTrue(condition.matches(mockContext));

        verify(primitiveFunctions).path(mockContext, "/restricted");
    }

    /**
     * Test for ConditionCompiler.compile() with 'headerValue' comparison.
     */
    @Test
    void testCompile_HeaderValueComparison_ReturnsCorrectCondition() {
        PrimitiveFunctions primitiveFunctions = mock(PrimitiveFunctions.class);
        ConditionCompiler compiler = new ConditionCompiler(primitiveFunctions);

        when(primitiveFunctions.header(any(), eq("Content-Type"))).thenReturn("application/json");

        StageDefinition.Condition condition = compiler.compile("headerValue('Content-Type') == 'application/json'");

        StageContext mockContext = mock(StageContext.class);
        assertTrue(condition.matches(mockContext));

        verify(primitiveFunctions).header(mockContext, "Content-Type");
    }

    /**
     * Test for ConditionCompiler.compile() with invalid syntax.
     * Should throw IllegalArgumentException.
     */
    @Test
    void testCompile_InvalidSyntax_ThrowsException() {
        PrimitiveFunctions primitiveFunctions = mock(PrimitiveFunctions.class);
        ConditionCompiler compiler = new ConditionCompiler(primitiveFunctions);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                compiler.compile("path('/test' && header('X-Custom')")
        );

        assertTrue(exception.getMessage().contains("Expected"));
    }
}