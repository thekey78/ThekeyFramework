package pe.kr.thekey.framework.starter.web;

import org.junit.jupiter.api.Test;
import pe.kr.thekey.framework.core.error.StandardException;

import static org.junit.jupiter.api.Assertions.*;

class DefaultErrorMapperTest {

    /**
     * Tests that the {@code map(Throwable ex)} method correctly wraps a {@code Throwable}
     * into a {@code StandardException} with the expected values.
     */
    @Test
    void map_withThrowable_shouldReturnStandardException() {
        // Arrange
        Throwable throwable = new RuntimeException("Test Exception");
        DefaultErrorMapper errorMapper = new DefaultErrorMapper();

        // Act
        StandardException result = errorMapper.map(throwable);

        // Assert
        assertNotNull(result);
        assertEquals("UNKNOWN_ERROR", result.getCode());
        assertEquals("Test Exception", result.getMessage());
        assertSame(throwable, result.getCause());
    }

    /**
     * Tests that the {@code map(Throwable ex, String traceId, String requestId)} method
     * correctly wraps a {@code Throwable} into a {@code StandardException} with the given trace ID
     * and request ID, along with the expected values.
     */
    @Test
    void map_withThrowableAndTraceAndRequestIds_shouldReturnStandardException() {
        // Arrange
        Throwable throwable = new RuntimeException("Test Exception");
        String traceId = "trace123";
        String requestId = "request123";
        DefaultErrorMapper errorMapper = new DefaultErrorMapper();

        // Act
        StandardException result = errorMapper.map(throwable, traceId, requestId);

        // Assert
        assertNotNull(result);
        assertEquals("UNKNOWN_ERROR", result.getCode());
        assertEquals("Test Exception", result.getMessage());
        assertSame(throwable, result.getCause());
        assertEquals(traceId, result.getTraceId());
        assertEquals(requestId, result.getRequestId());
    }

    /**
     * Tests that the {@code httpStatus(StandardException ex)} method returns the status code
     * of the given {@code StandardException}.
     */
    @Test
    void httpStatus_withStandardException_shouldReturnStatusCode() {
        // Arrange
        int expectedStatusCode = 404;
        StandardException ex = new StandardException("NOT_FOUND", "Resource not found", expectedStatusCode);
        DefaultErrorMapper errorMapper = new DefaultErrorMapper();

        // Act
        int result = errorMapper.httpStatus(ex);

        // Assert
        assertEquals(expectedStatusCode, result);
    }
}