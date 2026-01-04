package pe.kr.thekey.framework.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import pe.kr.thekey.framework.core.context.RequestContext;
import pe.kr.thekey.framework.core.context.RequestContextFactory;
import pe.kr.thekey.framework.core.context.RequestContextHolder;
import pe.kr.thekey.framework.core.error.ErrorMapper;
import pe.kr.thekey.framework.core.error.StandardException;
import pe.kr.thekey.framework.core.pipeline.Phase;
import pe.kr.thekey.framework.web.pipeline.ExecutableStage;
import pe.kr.thekey.framework.web.pipeline.PipelineAssembler;
import pe.kr.thekey.framework.web.pipeline.PipelineExecutor;
import pe.kr.thekey.framework.web.servlet.ThekeyHttpServletRequest;
import pe.kr.thekey.framework.web.servlet.ThekeyHttpServletResponse;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FrameworkGatewayFilterTest {

    @Test
    void doFilterInternal_successfulExecution() throws ServletException, IOException {
        // Setup dependencies
        RequestContextFactory ctxFactory = mock(RequestContextFactory.class);
        RequestContextHolder ctxHolder = mock(RequestContextHolder.class);
        PipelineAssembler assembler = mock(PipelineAssembler.class);
        PipelineExecutor executor = mock(PipelineExecutor.class);
        ErrorMapper errorMapper = mock(ErrorMapper.class);

        FrameworkGatewayFilter filter = new FrameworkGatewayFilter(ctxFactory, ctxHolder, assembler, executor, errorMapper);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        FilterChain mockChain = mock(FilterChain.class);

        RequestContext mockRequestContext = mock(RequestContext.class);
        when(ctxFactory.create(any(ThekeyHttpServletRequest.class))).thenReturn(mockRequestContext);

        List<ExecutableStage> inputStages = mock(List.class);
        List<ExecutableStage> outputStages = mock(List.class);
        when(assembler.assemble(Phase.INPUT)).thenReturn(inputStages);
        when(assembler.assemble(Phase.OUTPUT)).thenReturn(outputStages);

        // Execution
        filter.doFilterInternal(mockRequest, mockResponse, mockChain);

        // Verifications
        verify(ctxFactory).create(any(ThekeyHttpServletRequest.class));
        verify(ctxHolder).set(mockRequestContext);
        verify(executor).execute(inputStages, any());
        verify(mockChain).doFilter(any(ThekeyHttpServletRequest.class), any(ThekeyHttpServletResponse.class));
        verify(executor).execute(outputStages, any());
        verify(ctxHolder).clear();
    }

    @Test
    void doFilterInternal_errorHandling() throws ServletException, IOException {
        // Setup dependencies
        RequestContextFactory ctxFactory = mock(RequestContextFactory.class);
        RequestContextHolder ctxHolder = mock(RequestContextHolder.class);
        PipelineAssembler assembler = mock(PipelineAssembler.class);
        PipelineExecutor executor = mock(PipelineExecutor.class);
        ErrorMapper errorMapper = mock(ErrorMapper.class);

        FrameworkGatewayFilter filter = new FrameworkGatewayFilter(ctxFactory, ctxHolder, assembler, executor, errorMapper);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        FilterChain mockChain = mock(FilterChain.class);

        RequestContext mockRequestContext = mock(RequestContext.class);
        when(ctxFactory.create(any(ThekeyHttpServletRequest.class))).thenReturn(mockRequestContext);

        List<ExecutableStage> inputStages = mock(List.class);
        List<ExecutableStage> outputStages = mock(List.class);
        when(assembler.assemble(Phase.INPUT)).thenReturn(inputStages);
        when(assembler.assemble(Phase.OUTPUT)).thenReturn(outputStages);

        String mockTraceId = "trace123";
        String mockRequestId = "req123";
        when(mockRequestContext.traceId()).thenReturn(mockTraceId);
        when(mockRequestContext.requestId()).thenReturn(mockRequestId);

        StandardException mockError = mock(StandardException.class);
        when(mockError.getCode()).thenReturn("ERR001");
        when(mockError.getMessage()).thenReturn("Test error");
        when(errorMapper.map(any(), eq(mockTraceId), eq(mockRequestId))).thenReturn(mockError);
        when(errorMapper.httpStatus(mockError)).thenReturn(500);

        doThrow(new RuntimeException("Pipeline error")).when(executor).execute(inputStages, any());

        // Execution
        filter.doFilterInternal(mockRequest, mockResponse, mockChain);

        // Verifications
        verify(ctxFactory).create(any(ThekeyHttpServletRequest.class));
        verify(ctxHolder).set(mockRequestContext);
        verify(executor).execute(inputStages, any());
        verify(errorMapper).map(any(), eq(mockTraceId), eq(mockRequestId));
        assertEquals(500, mockResponse.getStatus());
        assertEquals("application/json", mockResponse.getContentType());
        assertEquals("{\"code\":\"ERR001\",\"message\":\"Test error\"}", mockResponse.getContentAsString());
        verify(ctxHolder).clear();
    }
}