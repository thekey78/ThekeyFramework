package pe.kr.thekey.framework.starter.web;

import pe.kr.thekey.framework.core.error.ErrorMapper;
import pe.kr.thekey.framework.core.error.StandardException;

public class DefaultErrorMapper implements ErrorMapper {
    @Override
    public StandardException map(Throwable ex) {
        return new StandardException("UNKNOWN_ERROR", ex.getMessage(), ex);
    }

    @Override
    public StandardException map(Throwable ex, String traceId, String requestId) {
        return new StandardException("UNKNOWN_ERROR", ex.getMessage(), traceId, requestId, ex);
    }

    @Override
    public int httpStatus(StandardException ex) {
        return ex.getStatusCode();
    }
}
