package pe.kr.thekey.framework.core.error;

public interface ErrorMapper {
    StandardException map(Throwable ex);
    StandardException map(Throwable ex, String traceId, String requestId);
    int httpStatus(StandardException ex);
}
