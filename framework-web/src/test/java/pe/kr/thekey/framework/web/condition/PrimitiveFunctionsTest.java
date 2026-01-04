package pe.kr.thekey.framework.web.condition;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pe.kr.thekey.framework.core.pipeline.StageContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrimitiveFunctionsTest {

    /**
     * Tests for the methodIn method from the PrimitiveFunctions class.
     * <p>
     * The method checks if the HTTP request method in the given StageContext matches
     * any of the provided method names, case-insensitively.
     */

    @Test
    void testMethodIn_WhenMethodMatchesSingle() {
        // Arrange
        StageContext context = Mockito.mock(StageContext.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(context.request()).thenReturn(request);
        Mockito.when(request.getMethod()).thenReturn("GET");

        PrimitiveFunctions functions = new PrimitiveFunctions();

        // Act
        boolean result = functions.methodIn(context, "GET");

        // Assert
        assertTrue(result);
    }

    @Test
    void testMethodIn_WhenMethodMatchesCaseInsensitive() {
        // Arrange
        StageContext context = Mockito.mock(StageContext.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(context.request()).thenReturn(request);
        Mockito.when(request.getMethod()).thenReturn("POST");

        PrimitiveFunctions functions = new PrimitiveFunctions();

        // Act
        boolean result = functions.methodIn(context, "post");

        // Assert
        assertTrue(result);
    }

    @Test
    void testMethodIn_WhenMethodDoesNotMatch() {
        // Arrange
        StageContext context = Mockito.mock(StageContext.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(context.request()).thenReturn(request);
        Mockito.when(request.getMethod()).thenReturn("PUT");

        PrimitiveFunctions functions = new PrimitiveFunctions();

        // Act
        boolean result = functions.methodIn(context, "POST");

        // Assert
        assertFalse(result);
    }

    @Test
    void testMethodIn_WhenMatchesMultipleMethods() {
        // Arrange
        StageContext context = Mockito.mock(StageContext.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(context.request()).thenReturn(request);
        Mockito.when(request.getMethod()).thenReturn("DELETE");

        PrimitiveFunctions functions = new PrimitiveFunctions();

        // Act
        boolean result = functions.methodIn(context, "GET", "POST", "DELETE");

        // Assert
        assertTrue(result);
    }

    @Test
    void testMethodIn_WhenEmptyMethodsProvided() {
        // Arrange
        StageContext context = Mockito.mock(StageContext.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(context.request()).thenReturn(request);
        Mockito.when(request.getMethod()).thenReturn("GET");

        PrimitiveFunctions functions = new PrimitiveFunctions();

        // Act
        boolean result = functions.methodIn(context);

        // Assert
        assertFalse(result);
    }
}