package pe.kr.thekey.framework.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import pe.kr.thekey.framework.core.context.RequestContext;
import pe.kr.thekey.framework.core.context.RequestContextFactory;
import pe.kr.thekey.framework.core.context.RequestContextHolder;
import pe.kr.thekey.framework.core.error.ErrorMapper;
import pe.kr.thekey.framework.core.pipeline.AttributeBag;
import pe.kr.thekey.framework.core.pipeline.Phase;
import pe.kr.thekey.framework.core.pipeline.StageContext;
import pe.kr.thekey.framework.web.pipeline.ExecutableStage;
import pe.kr.thekey.framework.web.pipeline.PipelineAssembler;
import pe.kr.thekey.framework.web.pipeline.PipelineExecutor;
import pe.kr.thekey.framework.web.servlet.ThekeyHttpServletRequest;
import pe.kr.thekey.framework.web.servlet.impl.ThekeyHttpServletRequestWrapper;
import pe.kr.thekey.framework.web.servlet.ThekeyHttpServletResponse;
import pe.kr.thekey.framework.web.servlet.impl.ThekeyHttpServletResponseWrapper;

import java.io.IOException;
import java.util.List;

public class FrameworkGatewayFilter extends OncePerRequestFilter {
    private final RequestContextFactory ctxFactory;
    private final RequestContextHolder ctxHolder;
    private final PipelineAssembler assembler;
    private final PipelineExecutor executor;
    private final ErrorMapper errorMapper;

    public FrameworkGatewayFilter(RequestContextFactory ctxFactory,
                                  RequestContextHolder ctxHolder,
                                  PipelineAssembler assembler,
                                  PipelineExecutor executor,
                                  ErrorMapper errorMapper) {
        this.ctxFactory = ctxFactory;
        this.ctxHolder = ctxHolder;
        this.assembler = assembler;
        this.executor = executor;
        this.errorMapper = errorMapper;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest _request,
                                    @NonNull HttpServletResponse _response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {
        ThekeyHttpServletRequest request = new ThekeyHttpServletRequestWrapper(_request);
        ThekeyHttpServletResponse response = new ThekeyHttpServletResponseWrapper(_response);

        RequestContext rc = ctxFactory.create(request);
        ctxHolder.set(rc);

        AttributeBag bag = new AttributeBag();
        StageContext sc = new DefaultStageContext(request, response, rc, bag);

        List<ExecutableStage> inputChain = assembler.assemble(Phase.INPUT);
        List<ExecutableStage> outputChain = assembler.assemble(Phase.OUTPUT);

        try {
            executor.execute(inputChain, sc);
            chain.doFilter(request, response);
            executor.execute(outputChain, sc);
        } catch (Throwable ex) {
            var err = errorMapper.map(ex, rc.traceId(), rc.requestId());
            response.setStatus(errorMapper.httpStatus(err));
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":\"" + err.getCode() + "\",\"message\":\"" + err.getMessage() + "\"}");
        } finally {
            ctxHolder.clear();
        }
    }

    static final class DefaultStageContext implements StageContext {
        private final ThekeyHttpServletRequest req;
        private final ThekeyHttpServletResponse res;
        private final RequestContext rc;
        private final AttributeBag bag;

        DefaultStageContext(ThekeyHttpServletRequest req, ThekeyHttpServletResponse res, RequestContext rc, AttributeBag bag) {
            this.req = req; this.res = res; this.rc = rc; this.bag = bag;
        }
        public ThekeyHttpServletRequest request() { return req; }
        public ThekeyHttpServletResponse response() { return res; }
        public RequestContext requestContext() { return rc; }
        public AttributeBag attributes() { return bag; }
    }
}
