package pe.kr.thekey.framework.starter.web;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pe.kr.thekey.framework.web.servlet.ThekeyHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DefaultRequestContextTest {

    /**
     * Tests the `principalId` method of the `DefaultRequestContext` class.
     * The `principalId` method retrieves the value of the "X-Principal-Id" HTTP header.
     */

    @Test
    void testPrincipalId_ReturnsHeaderValue() {
        // Arrange
        ThekeyHttpServletRequest mockedRequest = Mockito.mock(ThekeyHttpServletRequest.class);
        Mockito.when(mockedRequest.getHeader("X-Principal-Id")).thenReturn("user123");

        DefaultRequestContext context = new DefaultRequestContext(mockedRequest);

        // Act
        String principalId = context.principalId();

        // Assert
        assertEquals("user123", principalId);
    }

    @Test
    void testPrincipalId_HeaderNotSet_ReturnsNull() {
        // Arrange
        ThekeyHttpServletRequest mockedRequest = Mockito.mock(ThekeyHttpServletRequest.class);
        Mockito.when(mockedRequest.getHeader("X-Principal-Id")).thenReturn(null);

        DefaultRequestContext context = new DefaultRequestContext(mockedRequest);

        // Act
        String principalId = context.principalId();

        // Assert
        assertNull(principalId);
    }

    @Test
    void testPrincipalId_HeaderEmpty_ReturnsEmptyString() {
        // Arrange
        ThekeyHttpServletRequest mockedRequest = Mockito.mock(ThekeyHttpServletRequest.class);
        Mockito.when(mockedRequest.getHeader("X-Principal-Id")).thenReturn("");

        DefaultRequestContext context = new DefaultRequestContext(mockedRequest);

        // Act
        String principalId = context.principalId();

        // Assert
        assertEquals("", principalId);
    }
}